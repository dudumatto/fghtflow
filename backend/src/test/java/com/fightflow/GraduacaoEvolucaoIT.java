package com.fightflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fightflow.entity.Academia;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.Usuario;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GraduacaoEvolucaoIT {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;
  @Autowired TestDataFactory factory;
  @Autowired JwtService jwtService;

  @Test
  void professorCanCreateGraduacaoAndEvolucao_sameAcademia_andDashboardWorks() throws Exception {
    Academia ac = factory.createAcademia("Grad Evo " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-grad-" + System.nanoTime() + "@fightflow.test", ac);
    Usuario alunoUser = factory.createAtletaUserInAcademia("aluno-grad-" + System.nanoTime() + "@fightflow.test", ac);
    Aluno aluno = factory.findAlunoByUsuario(alunoUser);
    String profToken = token(professor);
    String alunoToken = token(alunoUser);

    Long gradId = postAndReadId("/graduacoes", profToken,
        "{\"alunoId\":" + aluno.getId() + ",\"faixa\":\"BRANCA\",\"grau\":0,\"dataGraduacao\":\"2026-01-01T00:00:00Z\",\"observacao\":null}");
    assertThat(gradId).isNotNull();

    Long evoId = postAndReadId("/evolucoes", profToken,
        "{\"alunoId\":" + aluno.getId() + ",\"tipo\":\"TECNICA\",\"descricao\":\"Passagem de guarda\",\"data\":\"2026-01-02T00:00:00Z\"}");
    assertThat(evoId).isNotNull();

    MvcResult dashAluno = mvc.perform(get("/dashboard/evolucao/aluno/" + aluno.getId())
            .header("Authorization", "Bearer " + alunoToken))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode dashBody = om.readTree(dashAluno.getResponse().getContentAsByteArray());
    assertThat(dashBody.at("/data/alunoId").asLong()).isEqualTo(aluno.getId());
    assertThat(dashBody.at("/data/totalEvolucoes").asLong()).isGreaterThanOrEqualTo(1);
  }

  @Test
  void alunoCannotAccessOtherAlunoHistory_orDashboard() throws Exception {
    Academia ac = factory.createAcademia("IDOR Grad Evo " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-idor-" + System.nanoTime() + "@fightflow.test", ac);
    Usuario alunoAUser = factory.createAtletaUserInAcademia("aluno-idor-a-" + System.nanoTime() + "@fightflow.test", ac);
    Usuario alunoBUser = factory.createAtletaUserInAcademia("aluno-idor-b-" + System.nanoTime() + "@fightflow.test", ac);
    Aluno alunoB = factory.findAlunoByUsuario(alunoBUser);
    String profToken = token(professor);

    postAndReadId("/graduacoes", profToken,
        "{\"alunoId\":" + alunoB.getId() + ",\"faixa\":\"BRANCA\",\"grau\":0,\"dataGraduacao\":\"2026-01-01T00:00:00Z\",\"observacao\":null}");

    mvc.perform(get("/graduacoes")
            .param("alunoId", String.valueOf(alunoB.getId()))
            .header("Authorization", "Bearer " + token(alunoAUser)))
        .andExpect(status().isForbidden());

    mvc.perform(get("/evolucoes")
            .param("alunoId", String.valueOf(alunoB.getId()))
            .header("Authorization", "Bearer " + token(alunoAUser)))
        .andExpect(status().isForbidden());

    mvc.perform(get("/dashboard/evolucao/aluno/" + alunoB.getId())
            .header("Authorization", "Bearer " + token(alunoAUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  void professorAdminCannotAccessOtherAcademiaAluno() throws Exception {
    Academia acA = factory.createAcademia("Academia A " + System.nanoTime());
    Academia acB = factory.createAcademia("Academia B " + System.nanoTime());
    Usuario professorB = factory.createProfessorUserInAcademia("prof-b-" + System.nanoTime() + "@fightflow.test", acB);
    Usuario adminA = factory.createAdminUserInAcademia("admin-a-" + System.nanoTime() + "@fightflow.test", acA);
    Usuario alunoBUser = factory.createAtletaUserInAcademia("aluno-b-" + System.nanoTime() + "@fightflow.test", acB);
    Aluno alunoB = factory.findAlunoByUsuario(alunoBUser);

    mvc.perform(post("/graduacoes")
            .header("Authorization", "Bearer " + token(adminA))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"alunoId\":" + alunoB.getId() + ",\"faixa\":\"BRANCA\",\"grau\":0,\"dataGraduacao\":\"2026-01-01T00:00:00Z\",\"observacao\":null}"))
        .andExpect(status().isForbidden());

    mvc.perform(post("/evolucoes")
            .header("Authorization", "Bearer " + token(adminA))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"alunoId\":" + alunoB.getId() + ",\"tipo\":\"TECNICA\",\"descricao\":\"x\",\"data\":\"2026-01-02T00:00:00Z\"}"))
        .andExpect(status().isForbidden());

    mvc.perform(get("/dashboard/evolucao/aluno/" + alunoB.getId())
            .header("Authorization", "Bearer " + token(professorB)))
        .andExpect(status().isOk());

    mvc.perform(get("/dashboard/evolucao/aluno/" + alunoB.getId())
            .header("Authorization", "Bearer " + token(adminA)))
        .andExpect(status().isForbidden());
  }

  @Test
  void graduacaoForaDeOrdemRequiresObservacao() throws Exception {
    Academia ac = factory.createAcademia("Ordem " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-ordem-" + System.nanoTime() + "@fightflow.test", ac);
    Usuario alunoUser = factory.createAtletaUserInAcademia("aluno-ordem-" + System.nanoTime() + "@fightflow.test", ac);
    Aluno aluno = factory.findAlunoByUsuario(alunoUser);
    String token = token(professor);

    postAndReadId("/graduacoes", token,
        "{\"alunoId\":" + aluno.getId() + ",\"faixa\":\"BRANCA\",\"grau\":0,\"dataGraduacao\":\"2026-01-01T00:00:00Z\",\"observacao\":null}");

    // Should be BRANCA grau 1, but we try ROXA grau 0 without observacao => 400
    mvc.perform(post("/graduacoes")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"alunoId\":" + aluno.getId() + ",\"faixa\":\"ROXA\",\"grau\":0,\"dataGraduacao\":\"2026-02-01T00:00:00Z\",\"observacao\":null}"))
        .andExpect(status().isBadRequest());

    // With observacao => ok
    mvc.perform(post("/graduacoes")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"alunoId\":" + aluno.getId() + ",\"faixa\":\"ROXA\",\"grau\":0,\"dataGraduacao\":\"2026-02-01T00:00:00Z\",\"observacao\":\"caso especial\"}"))
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

