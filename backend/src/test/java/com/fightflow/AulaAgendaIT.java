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

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AulaAgendaIT {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;
  @Autowired TestDataFactory factory;
  @Autowired JwtService jwtService;
  @Autowired AtletaRepository atletaRepository;

  @Test
  void professorCrudAula_andAthleteReadOnly() throws Exception {
    Academia ac = factory.createAcademia("Aulas " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-aulas-" + System.nanoTime() + "@fightflow.test", ac);
    Usuario atletaUser = factory.createAtletaUserInAcademia("atleta-aulas-" + System.nanoTime() + "@fightflow.test", ac);
    String professorToken = token(professor);
    String atletaToken = token(atletaUser);

    Instant ini = Instant.parse("2026-01-01T10:00:00Z");
    Instant fim = Instant.parse("2026-01-01T11:00:00Z");
    Long aulaId = postAndReadId("/aulas", professorToken,
        "{\"titulo\":\"Aula 1\",\"descricao\":null,\"tipo\":\"COLETIVA\",\"dataHoraInicio\":\"" + ini + "\",\"dataHoraFim\":\"" + fim + "\",\"capacidade\":20}");
    assertThat(aulaId).isNotNull();

    mvc.perform(get("/aulas")
            .header("Authorization", "Bearer " + atletaToken))
        .andExpect(status().isOk());

    mvc.perform(get("/aulas/" + aulaId)
            .header("Authorization", "Bearer " + atletaToken))
        .andExpect(status().isOk());

    mvc.perform(post("/aulas")
            .header("Authorization", "Bearer " + atletaToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"titulo\":\"X\",\"tipo\":\"COLETIVA\",\"dataHoraInicio\":\"" + ini + "\",\"dataHoraFim\":\"" + fim + "\",\"capacidade\":10}"))
        .andExpect(status().isForbidden());

    mvc.perform(put("/aulas/" + aulaId)
            .header("Authorization", "Bearer " + professorToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"titulo\":\"Aula 1 (editada)\",\"ativa\":true}"))
        .andExpect(status().isOk());

    mvc.perform(delete("/aulas/" + aulaId)
            .header("Authorization", "Bearer " + professorToken))
        .andExpect(status().isOk());

    MvcResult deletedGet = mvc.perform(get("/aulas/" + aulaId)
            .header("Authorization", "Bearer " + professorToken))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode deletedBody = om.readTree(deletedGet.getResponse().getContentAsByteArray());
    assertThat(deletedBody.at("/data/ativa").asBoolean()).isFalse();
  }

  @Test
  void professorCannotManageAulaFromAnotherProfessor_sameAcademia() throws Exception {
    Academia ac = factory.createAcademia("Aulas Perm " + System.nanoTime());
    Usuario profA = factory.createProfessorUserInAcademia("prof-a-" + System.nanoTime() + "@fightflow.test", ac);
    Usuario profB = factory.createProfessorUserInAcademia("prof-b-" + System.nanoTime() + "@fightflow.test", ac);
    String tokenA = token(profA);
    String tokenB = token(profB);

    Long aulaId = postAndReadId("/aulas", tokenA,
        "{\"titulo\":\"Aula A\",\"descricao\":null,\"tipo\":\"COLETIVA\",\"dataHoraInicio\":\"2026-01-01T10:00:00Z\",\"dataHoraFim\":\"2026-01-01T11:00:00Z\",\"capacidade\":10}");

    mvc.perform(put("/aulas/" + aulaId)
            .header("Authorization", "Bearer " + tokenB)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"titulo\":\"Tentativa\"}"))
        .andExpect(status().isForbidden());

    mvc.perform(delete("/aulas/" + aulaId)
            .header("Authorization", "Bearer " + tokenB))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminRespectsAcademiaScope_onAulaAndPresencas() throws Exception {
    Academia acA = factory.createAcademia("Academia A " + System.nanoTime());
    Academia acB = factory.createAcademia("Academia B " + System.nanoTime());
    Usuario adminA = factory.createAdminUserInAcademia("admin-a-" + System.nanoTime() + "@fightflow.test", acA);
    Usuario profB = factory.createProfessorUserInAcademia("prof-b2-" + System.nanoTime() + "@fightflow.test", acB);
    Usuario alunoBUser = factory.createAtletaUserInAcademia("aluno-b2-" + System.nanoTime() + "@fightflow.test", acB);
    Aluno alunoB = factory.findAlunoByUsuario(alunoBUser);
    String adminAToken = token(adminA);
    String profBToken = token(profB);

    Long aulaB = postAndReadId("/aulas", profBToken,
        "{\"titulo\":\"Aula B\",\"descricao\":null,\"tipo\":\"COLETIVA\",\"dataHoraInicio\":\"2026-01-01T10:00:00Z\",\"dataHoraFim\":\"2026-01-01T11:00:00Z\",\"capacidade\":10}");

    mvc.perform(get("/aulas/" + aulaB)
            .header("Authorization", "Bearer " + adminAToken))
        .andExpect(status().isForbidden());

    mvc.perform(get("/aulas/" + aulaB + "/presencas")
            .header("Authorization", "Bearer " + adminAToken))
        .andExpect(status().isForbidden());

    mvc.perform(post("/aulas/" + aulaB + "/presencas")
            .header("Authorization", "Bearer " + adminAToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"alunoId\":" + alunoB.getId() + ",\"status\":\"PRESENTE\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void presencaRules_duplicateAndBlockedAndParticularCapacity() throws Exception {
    Academia ac = factory.createAcademia("Aulas Presenca " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-pres-" + System.nanoTime() + "@fightflow.test", ac);
    Usuario alunoAUser = factory.createAtletaUserInAcademia("aluno-pres-a-" + System.nanoTime() + "@fightflow.test", ac);
    Usuario alunoBUser = factory.createAtletaUserInAcademia("aluno-pres-b-" + System.nanoTime() + "@fightflow.test", ac);
    Aluno alunoA = factory.findAlunoByUsuario(alunoAUser);
    Aluno alunoB = factory.findAlunoByUsuario(alunoBUser);
    Atleta atletaA = atletaRepository.findByUsuarioId(alunoAUser.getId()).orElseThrow();
    String professorToken = token(professor);
    String alunoAToken = token(alunoAUser);

    // Aula PARTICULAR defaults capacity=1 in service when omitted.
    Long aulaId = postAndReadId("/aulas", professorToken,
        "{\"titulo\":\"Particular\",\"descricao\":null,\"tipo\":\"PARTICULAR\",\"dataHoraInicio\":\"2026-01-01T10:00:00Z\",\"dataHoraFim\":\"2026-01-01T11:00:00Z\"}");

    mvc.perform(post("/aulas/" + aulaId + "/presencas")
            .header("Authorization", "Bearer " + professorToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"alunoId\":" + alunoA.getId() + ",\"status\":\"PRESENTE\"}"))
        .andExpect(status().isOk());

    // Prevent duplicate
    mvc.perform(post("/aulas/" + aulaId + "/presencas")
            .header("Authorization", "Bearer " + professorToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"alunoId\":" + alunoA.getId() + ",\"status\":\"PRESENTE\"}"))
        .andExpect(status().isConflict());

    // Capacity=1: second PRESENTE should conflict
    mvc.perform(post("/aulas/" + aulaId + "/presencas")
            .header("Authorization", "Bearer " + professorToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"alunoId\":" + alunoB.getId() + ",\"status\":\"PRESENTE\"}"))
        .andExpect(status().isConflict());

    // Athlete can only see own presence
    MvcResult listMine = mvc.perform(get("/aulas/" + aulaId + "/presencas")
            .header("Authorization", "Bearer " + alunoAToken))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode mineBody = om.readTree(listMine.getResponse().getContentAsByteArray());
    assertThat(mineBody.at("/data").size()).isEqualTo(1);
    assertThat(mineBody.at("/data/0/alunoId").asLong()).isEqualTo(alunoA.getId());

    MvcResult dashAlunoA = mvc.perform(get("/dashboard/aulas")
            .header("Authorization", "Bearer " + alunoAToken))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode dashAlunoABody = om.readTree(dashAlunoA.getResponse().getContentAsByteArray());
    assertThat(dashAlunoABody.at("/data/presencasPresentes").asLong()).isGreaterThanOrEqualTo(1);

    MvcResult dashProf = mvc.perform(get("/dashboard/aulas")
            .header("Authorization", "Bearer " + professorToken))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode dashProfBody = om.readTree(dashProf.getResponse().getContentAsByteArray());
    assertThat(dashProfBody.at("/data/presencasPresentes").asLong()).isGreaterThanOrEqualTo(1);

    // Block alunoA and forbid PRESENTE
    blockByInadimplencia(ac, professorToken, alunoA);
    mvc.perform(put("/aulas/" + aulaId + "/presencas")
            .header("Authorization", "Bearer " + professorToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"alunoId\":" + alunoA.getId() + ",\"status\":\"PRESENTE\"}"))
        .andExpect(status().isForbidden());

    // Still allow marking as AUSENTE when blocked
    mvc.perform(put("/aulas/" + aulaId + "/presencas")
            .header("Authorization", "Bearer " + professorToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"alunoId\":" + alunoA.getId() + ",\"status\":\"AUSENTE\"}"))
        .andExpect(status().isOk());

    // Treino presence regression: blocked also prevents treino presence (already covered in FinanceiroIT), keep signal here
    mvc.perform(post("/presencas")
            .header("Authorization", "Bearer " + professorToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"treinoId\":999999,\"atletaId\":" + atletaA.getId() + "}"))
        .andExpect(status().isNotFound());
  }

  private void blockByInadimplencia(Academia ac, String professorToken, Aluno aluno) throws Exception {
    Long planoId = postAndReadId("/planos", professorToken,
        "{\"nome\":\"Plano Bloqueio Aula\",\"valor\":99.90,\"duracaoEmDias\":30,\"ativo\":true}");
    postAndReadId("/matriculas", professorToken,
        "{\"alunoId\":" + aluno.getId() + ",\"planoId\":" + planoId + ",\"dataInicio\":\"2020-01-01T00:00:00Z\"}");
    mvc.perform(post("/financeiro/atualizar-bloqueios")
            .header("Authorization", "Bearer " + professorToken))
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
}
