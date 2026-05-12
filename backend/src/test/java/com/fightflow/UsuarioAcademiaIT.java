package com.fightflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fightflow.entity.Academia;
import com.fightflow.entity.Usuario;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.ProfessorAcademiaRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioAcademiaIT {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;
  @Autowired TestDataFactory factory;
  @Autowired JwtService jwtService;
  @Autowired AlunoRepository alunoRepository;
  @Autowired ProfessorAcademiaRepository professorAcademiaRepository;

  @Test
  void professorCreatesAlunoOnlyInManagedAcademia_andListsScoped() throws Exception {
    Academia acA = factory.createAcademia("Aluno Scope A " + System.nanoTime());
    Academia acB = factory.createAcademia("Aluno Scope B " + System.nanoTime());
    Usuario profA = factory.createProfessorUserInAcademia("prof-aluno-a-" + System.nanoTime() + "@fightflow.test", acA);
    Usuario profB = factory.createProfessorUserInAcademia("prof-aluno-b-" + System.nanoTime() + "@fightflow.test", acB);

    Long alunoA = createAluno(profA, acA.getId(), "aluno-a-" + System.nanoTime() + "@fightflow.test");
    Long alunoB = createAluno(profB, acB.getId(), "aluno-b-" + System.nanoTime() + "@fightflow.test");

    mvc.perform(post("/alunos")
            .header("Authorization", "Bearer " + token(profA))
            .contentType(MediaType.APPLICATION_JSON)
            .content(alunoPayload(acB.getId(), "blocked-" + System.nanoTime() + "@fightflow.test")))
        .andExpect(status().isForbidden());

    MvcResult listRes = mvc.perform(get("/alunos")
            .header("Authorization", "Bearer " + token(profA)))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(ids(body(listRes).at("/data"))).contains(alunoA).doesNotContain(alunoB);

    mvc.perform(get("/alunos/" + alunoB)
            .header("Authorization", "Bearer " + token(profA)))
        .andExpect(status().isForbidden());
  }

  @Test
  void professorCreatesAtletaOnlyInManagedAcademia_andAtletaCannotCreateUsers() throws Exception {
    Academia acA = factory.createAcademia("Atleta Scope A " + System.nanoTime());
    Academia acB = factory.createAcademia("Atleta Scope B " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-atleta-" + System.nanoTime() + "@fightflow.test", acA);
    Usuario atletaUser = factory.createAtletaUserInAcademia("atleta-auth-" + System.nanoTime() + "@fightflow.test", acA);

    MvcResult created = mvc.perform(post("/atletas")
            .header("Authorization", "Bearer " + token(professor))
            .contentType(MediaType.APPLICATION_JSON)
            .content(atletaPayload(acA.getId(), "competidor-" + System.nanoTime() + "@fightflow.test")))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(body(created).at("/data/academiaId").asLong()).isEqualTo(acA.getId());

    mvc.perform(post("/atletas")
            .header("Authorization", "Bearer " + token(professor))
            .contentType(MediaType.APPLICATION_JSON)
            .content(atletaPayload(acB.getId(), "blocked-atleta-" + System.nanoTime() + "@fightflow.test")))
        .andExpect(status().isForbidden());

    mvc.perform(post("/alunos")
            .header("Authorization", "Bearer " + token(atletaUser))
            .contentType(MediaType.APPLICATION_JSON)
            .content(alunoPayload(acA.getId(), "blocked-aluno-" + System.nanoTime() + "@fightflow.test")))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminCreatesProfessorWithInitialAcademia_andProfessorSelectIsScoped() throws Exception {
    Academia acA = factory.createAcademia("Professor Select A " + System.nanoTime());
    Academia acB = factory.createAcademia("Professor Select B " + System.nanoTime());
    Usuario admin = factory.createAdminUserInAcademia("admin-prof-" + System.nanoTime() + "@fightflow.test", acA);
    Usuario profA = factory.createProfessorUserInAcademia("prof-select-a-" + System.nanoTime() + "@fightflow.test", acA);
    Usuario profB = factory.createProfessorUserInAcademia("prof-select-b-" + System.nanoTime() + "@fightflow.test", acB);

    MvcResult created = mvc.perform(post("/professores")
            .header("Authorization", "Bearer " + token(admin))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"novo-prof-" + System.nanoTime() + "@fightflow.test\",\"password\":\"password123\",\"academiaId\":" + acA.getId() + "}"))
        .andExpect(status().isOk())
        .andReturn();
    Long novoProfessorId = body(created).at("/data/usuarioId").asLong();
    assertThat(professorAcademiaRepository.existsByProfessorIdAndAcademiaIdAndAtivoTrue(novoProfessorId, acA.getId())).isTrue();

    MvcResult selectProfA = mvc.perform(get("/professores/select")
            .header("Authorization", "Bearer " + token(profA)))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(ids(body(selectProfA).at("/data"))).contains(profA.getId()).doesNotContain(profB.getId());

    MvcResult selectAdmin = mvc.perform(get("/professores/select")
            .header("Authorization", "Bearer " + token(admin)))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(ids(body(selectAdmin).at("/data"))).contains(profA.getId(), profB.getId(), novoProfessorId);
  }

  @Test
  void softDeleteAlunoSetsAtivoFalse_andSelectHidesInactive() throws Exception {
    Academia ac = factory.createAcademia("Aluno Delete " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-delete-aluno-" + System.nanoTime() + "@fightflow.test", ac);
    Long alunoId = createAluno(professor, ac.getId(), "delete-aluno-" + System.nanoTime() + "@fightflow.test");

    mvc.perform(delete("/alunos/" + alunoId)
            .header("Authorization", "Bearer " + token(professor)))
        .andExpect(status().isOk());

    assertThat(alunoRepository.findById(alunoId).orElseThrow().isAtivo()).isFalse();
    MvcResult selectRes = mvc.perform(get("/alunos/select")
            .header("Authorization", "Bearer " + token(professor)))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(ids(body(selectRes).at("/data"))).doesNotContain(alunoId);
  }

  private Long createAluno(Usuario professor, Long academiaId, String email) throws Exception {
    MvcResult res = mvc.perform(post("/alunos")
            .header("Authorization", "Bearer " + token(professor))
            .contentType(MediaType.APPLICATION_JSON)
            .content(alunoPayload(academiaId, email)))
        .andExpect(status().isOk())
        .andReturn();
    return body(res).at("/data/id").asLong();
  }

  private String alunoPayload(Long academiaId, String email) {
    return "{\"nome\":\"Aluno Teste\",\"email\":\"" + email + "\",\"password\":\"password123\",\"academiaId\":" + academiaId + ",\"faixaAtual\":\"branca\",\"grauAtual\":1}";
  }

  private String atletaPayload(Long academiaId, String email) {
    return "{\"nome\":\"Atleta Teste\",\"email\":\"" + email + "\",\"password\":\"password123\",\"academiaId\":" + academiaId + ",\"faixa\":\"azul\",\"grauAtual\":2,\"peso\":78.5,\"categoria\":\"leve\"}";
  }

  private JsonNode body(MvcResult res) throws Exception {
    return om.readTree(res.getResponse().getContentAsByteArray());
  }

  private java.util.List<Long> ids(JsonNode array) {
    java.util.ArrayList<Long> ids = new java.util.ArrayList<>();
    array.forEach(node -> ids.add(node.at("/id").asLong()));
    return ids;
  }

  private String token(Usuario u) {
    return jwtService.generate(new UserPrincipal(u.getId(), u.getEmail(), u.getPasswordHash(), u.getRole(), u.getAcademia().getId()));
  }
}
