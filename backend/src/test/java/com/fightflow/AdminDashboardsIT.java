package com.fightflow;

import com.fightflow.entity.Academia;
import com.fightflow.entity.Usuario;
import com.fightflow.security.JwtService;
import com.fightflow.security.UserPrincipal;
import com.fightflow.test.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.Matricula;
import com.fightflow.entity.MatriculaStatus;
import com.fightflow.entity.Mensalidade;
import com.fightflow.entity.MensalidadeStatus;
import com.fightflow.entity.Plano;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.MatriculaRepository;
import com.fightflow.repository.MensalidadeRepository;
import com.fightflow.repository.PlanoRepository;
import java.math.BigDecimal;
import java.time.Instant;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class AdminDashboardsIT {
  @Autowired MockMvc mvc;
  @Autowired TestDataFactory factory;
  @Autowired JwtService jwtService;
  @Autowired ObjectMapper om;
  @Autowired AlunoRepository alunoRepository;
  @Autowired PlanoRepository planoRepository;
  @Autowired MatriculaRepository matriculaRepository;
  @Autowired MensalidadeRepository mensalidadeRepository;

  @Test
  void professorCanAccessAdminFinanceiroAlunosDashboards_scopedByAcademia() throws Exception {
    Academia ac = factory.createAcademia("Dash " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-dash-" + System.nanoTime() + "@fightflow.test", ac);
    String token = token(professor);

    mvc.perform(get("/dashboard/admin").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    mvc.perform(get("/dashboard/financeiro").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    mvc.perform(get("/dashboard/alunos").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
  }

  @Test
  void adminCanAccessAdminFinanceiroAlunosDashboards() throws Exception {
    Academia ac = factory.createAcademia("Dash Admin " + System.nanoTime());
    Usuario admin = factory.createAdminUserInAcademia("admin-dash-" + System.nanoTime() + "@fightflow.test", ac);
    String token = token(admin);

    mvc.perform(get("/dashboard/admin").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    mvc.perform(get("/dashboard/financeiro").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    mvc.perform(get("/dashboard/alunos").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
  }

  @Test
  void atletaCannotAccessAdminDashboards() throws Exception {
    Usuario atleta = factory.createAtletaUser("atleta-dash-" + System.nanoTime() + "@fightflow.test");
    String token = token(atleta);

    mvc.perform(get("/dashboard/admin").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
    mvc.perform(get("/dashboard/financeiro").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
    mvc.perform(get("/dashboard/alunos").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
  }

  @Test
  void dashboardsAreScopedByAcademia_doNotCountOtherAcademiaData() throws Exception {
    Academia acA = factory.createAcademia("Dash A " + System.nanoTime());
    Academia acB = factory.createAcademia("Dash B " + System.nanoTime());
    Usuario professorA = factory.createProfessorUserInAcademia("prof-a-" + System.nanoTime() + "@fightflow.test", acA);
    Usuario alunoBUser = factory.createAtletaUserInAcademia("aluno-b-" + System.nanoTime() + "@fightflow.test", acB);
    Aluno alunoB = alunoRepository.findByUsuarioId(alunoBUser.getId()).orElseThrow();

    Plano planoB = new Plano();
    planoB.setAcademia(acB);
    planoB.setNome("Plano B");
    planoB.setDescricao(null);
    planoB.setValor(new BigDecimal("100.00"));
    planoB.setDuracaoEmDias(30);
    planoB.setAtivo(true);
    planoB = planoRepository.save(planoB);

    Matricula matB = new Matricula();
    matB.setAluno(alunoB);
    matB.setPlano(planoB);
    matB.setDataInicio(Instant.parse("2026-01-01T00:00:00Z"));
    matB.setStatus(MatriculaStatus.BLOQUEADA);
    matriculaRepository.save(matB);

    Mensalidade mensB = new Mensalidade();
    mensB.setAluno(alunoB);
    mensB.setPlano(planoB);
    mensB.setValor(new BigDecimal("100.00"));
    mensB.setVencimento(Instant.parse("2020-01-01T00:00:00Z"));
    mensB.setStatus(MensalidadeStatus.ATRASADO);
    mensalidadeRepository.save(mensB);

    String tokenA = token(professorA);

    JsonNode admin = body(getJson("/dashboard/admin", tokenA));
    // Academy A has no aluno/plano/matricula created here.
    assertThat(admin.at("/data/alunosAtivos").asLong()).isEqualTo(0);
    assertThat(admin.at("/data/planosAtivos").asLong()).isEqualTo(0);
    assertThat(admin.at("/data/matriculasBloqueadas").asLong()).isEqualTo(0);

    JsonNode fin = body(getJson("/dashboard/financeiro", tokenA));
    assertThat(fin.at("/data/mensalidadesAtrasadas").asLong()).isEqualTo(0);
    assertThat(fin.at("/data/totalAtrasado").asDouble()).isEqualTo(0.0);
    assertThat(fin.at("/data/alunosComInadimplenciaBloqueante").asLong()).isEqualTo(0);

    JsonNode alunos = body(getJson("/dashboard/alunos", tokenA));
    assertThat(alunos.at("/data/alunosAtivos").asLong()).isEqualTo(0);
    assertThat(alunos.at("/data/alunosComInadimplenciaBloqueante").asLong()).isEqualTo(0);
  }

  private String token(Usuario u) {
    return jwtService.generate(new UserPrincipal(u.getId(), u.getEmail(), u.getPasswordHash(), u.getRole(), u.getAcademia().getId()));
  }

  private byte[] getJson(String path, String token) throws Exception {
    return mvc.perform(get(path).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsByteArray();
  }

  private JsonNode body(byte[] bytes) throws Exception {
    return om.readTree(bytes);
  }
}
