package com.fightflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fightflow.entity.Academia;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.Atleta;
import com.fightflow.entity.Usuario;
import com.fightflow.repository.AtletaRepository;
import com.fightflow.security.JwtService;
import com.fightflow.security.UserPrincipal;
import com.fightflow.test.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FinanceiroIT {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;
  @Autowired TestDataFactory factory;
  @Autowired JwtService jwtService;
  @Autowired AtletaRepository atletaRepository;

  @Test
  void professorCanManagePlanoMatriculaMensalidadePagamentoAndAtrasos() throws Exception {
    Academia ac = factory.createAcademia("Financeiro " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-fin-" + System.nanoTime() + "@fightflow.test", ac);
    Usuario atleta = factory.createAtletaUserInAcademia("aluno-fin-" + System.nanoTime() + "@fightflow.test", ac);
    Aluno aluno = factory.findAlunoByUsuario(atleta);
    String token = token(professor);

    Long planoId = postAndReadId("/planos", token,
        "{\"nome\":\"Mensal\",\"descricao\":\"Plano mensal\",\"valor\":199.90,\"duracaoEmDias\":30,\"ativo\":true}");

    Long matriculaId = postAndReadId("/matriculas", token,
        "{\"alunoId\":" + aluno.getId() + ",\"planoId\":" + planoId + ",\"dataInicio\":\"2026-01-01T00:00:00Z\"}");
    assertThat(matriculaId).isNotNull();

    MvcResult mensalidadesAutoRes = mvc.perform(get("/mensalidades")
            .param("alunoId", String.valueOf(aluno.getId()))
            .param("referencia", "matricula:" + matriculaId + ":primeira")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode mensalidadesAuto = om.readTree(mensalidadesAutoRes.getResponse().getContentAsByteArray());
    assertThat(mensalidadesAuto.at("/success").asBoolean()).isTrue();
    assertThat(mensalidadesAuto.at("/data/items").size()).isGreaterThanOrEqualTo(1);
    assertThat(mensalidadesAuto.at("/data/items/0/status").asText()).isEqualTo("PENDENTE");

    Long mensalidadeId = postAndReadId("/mensalidades", token,
        "{\"alunoId\":" + aluno.getId() + ",\"planoId\":" + planoId
            + ",\"valor\":199.90,\"vencimento\":\"2020-01-01T00:00:00Z\",\"status\":\"PENDENTE\"}");

    MvcResult atrasoRes = mvc.perform(post("/financeiro/atualizar-atrasos")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(om.readTree(atrasoRes.getResponse().getContentAsByteArray()).at("/data/mensalidadesAtualizadas").asInt())
        .isGreaterThanOrEqualTo(1);

    MvcResult pagamentoRes = mvc.perform(put("/mensalidades/" + mensalidadeId + "/pagar")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"metodoPagamento\":\"PIX\",\"referencia\":\"pix-test\"}"))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(om.readTree(pagamentoRes.getResponse().getContentAsByteArray()).at("/data/status").asText())
        .isEqualTo("PAGO");
  }

  @Test
  void professorCannotManageAlunoFromAnotherAcademia() throws Exception {
    Academia acA = factory.createAcademia("Academia A " + System.nanoTime());
    Academia acB = factory.createAcademia("Academia B " + System.nanoTime());
    Usuario professorB = factory.createProfessorUserInAcademia("prof-b-" + System.nanoTime() + "@fightflow.test", acB);
    Usuario atletaA = factory.createAtletaUserInAcademia("aluno-a-" + System.nanoTime() + "@fightflow.test", acA);
    Aluno alunoA = factory.findAlunoByUsuario(atletaA);
    String tokenB = token(professorB);

    Long planoB = postAndReadId("/planos", tokenB,
        "{\"nome\":\"Plano B\",\"valor\":150.00,\"duracaoEmDias\":30,\"ativo\":true}");

    mvc.perform(post("/matriculas")
            .header("Authorization", "Bearer " + tokenB)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"alunoId\":" + alunoA.getId() + ",\"planoId\":" + planoB + ",\"dataInicio\":\"2026-01-01T00:00:00Z\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void alunoCannotListOtherAlunoMensalidades() throws Exception {
    Academia ac = factory.createAcademia("Academia IDOR " + System.nanoTime());
    Usuario alunoAUser = factory.createAtletaUserInAcademia("aluno-idor-a-" + System.nanoTime() + "@fightflow.test", ac);
    Usuario alunoBUser = factory.createAtletaUserInAcademia("aluno-idor-b-" + System.nanoTime() + "@fightflow.test", ac);
    Aluno alunoB = factory.findAlunoByUsuario(alunoBUser);

    mvc.perform(get("/mensalidades")
            .param("alunoId", String.valueOf(alunoB.getId()))
            .header("Authorization", "Bearer " + token(alunoAUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  void bloqueioPorInadimplenciaImpedePresencaERegularizacaoDesbloqueia() throws Exception {
    Academia ac = factory.createAcademia("Bloqueio " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-block-" + System.nanoTime() + "@fightflow.test", ac);
    Usuario atletaUser = factory.createAtletaUserInAcademia("aluno-block-" + System.nanoTime() + "@fightflow.test", ac);
    Aluno aluno = factory.findAlunoByUsuario(atletaUser);
    Atleta atleta = atletaRepository.findByUsuarioId(atletaUser.getId()).orElseThrow();
    String professorToken = token(professor);
    String atletaToken = token(atletaUser);

    Long planoId = postAndReadId("/planos", professorToken,
        "{\"nome\":\"Plano Bloqueio\",\"valor\":99.90,\"duracaoEmDias\":30,\"ativo\":true}");
    postAndReadId("/matriculas", professorToken,
        "{\"alunoId\":" + aluno.getId() + ",\"planoId\":" + planoId + ",\"dataInicio\":\"2020-01-01T00:00:00Z\"}");

    Long mensalidadeId = firstMensalidadeId(professorToken, aluno.getId());
    Long treinoId = postAndReadId("/treinos", professorToken,
        "{\"startsAt\":\"2026-01-01T10:00:00Z\",\"titulo\":\"Treino Bloqueio\",\"descricao\":null}");

    MvcResult bloqueioRes = mvc.perform(post("/financeiro/atualizar-bloqueios")
            .header("Authorization", "Bearer " + professorToken))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(om.readTree(bloqueioRes.getResponse().getContentAsByteArray()).at("/data/alunosBloqueados").asInt())
        .isGreaterThanOrEqualTo(1);

    mvc.perform(post("/presencas")
            .header("Authorization", "Bearer " + professorToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"treinoId\":" + treinoId + ",\"atletaId\":" + atleta.getId() + "}"))
        .andExpect(status().isForbidden());

    MvcResult perfilBloqueado = mvc.perform(get("/atletas/me")
            .header("Authorization", "Bearer " + atletaToken))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(om.readTree(perfilBloqueado.getResponse().getContentAsByteArray()).at("/data/financeiro/bloqueado").asBoolean())
        .isTrue();

    mvc.perform(put("/mensalidades/" + mensalidadeId + "/pagar")
            .header("Authorization", "Bearer " + professorToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"metodoPagamento\":\"PIX\",\"referencia\":\"regularizado\"}"))
        .andExpect(status().isOk());

    MvcResult dashboardRegularizado = mvc.perform(get("/dashboard/atleta")
            .header("Authorization", "Bearer " + atletaToken))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(om.readTree(dashboardRegularizado.getResponse().getContentAsByteArray()).at("/data/financeiro/bloqueado").asBoolean())
        .isFalse();

    mvc.perform(post("/presencas")
            .header("Authorization", "Bearer " + professorToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"treinoId\":" + treinoId + ",\"atletaId\":" + atleta.getId() + "}"))
        .andExpect(status().isOk());
  }

  private Long postAndReadId(String path, String token, String payload) throws Exception {
    MvcResult res = mvc.perform(post(path)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode body = om.readTree(res.getResponse().getContentAsByteArray());
    return body.at("/data/id").asLong();
  }

  private String token(Usuario u) {
    return jwtService.generate(new UserPrincipal(u.getId(), u.getEmail(), u.getPasswordHash(), u.getRole(), u.getAcademia().getId()));
  }

  private Long firstMensalidadeId(String token, Long alunoId) throws Exception {
    MvcResult res = mvc.perform(get("/mensalidades")
            .param("alunoId", String.valueOf(alunoId))
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andReturn();
    return om.readTree(res.getResponse().getContentAsByteArray()).at("/data/items/0/id").asLong();
  }
}
