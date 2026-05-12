package com.fightflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fightflow.entity.Academia;
import com.fightflow.entity.Usuario;
import com.fightflow.repository.AcademiaRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AcademiaIT {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;
  @Autowired TestDataFactory factory;
  @Autowired JwtService jwtService;
  @Autowired AcademiaRepository academiaRepository;
  @Autowired ProfessorAcademiaRepository professorAcademiaRepository;

  @Test
  void professorCreatesAcademia_andBecomesResponsavel() throws Exception {
    Academia base = factory.createAcademia("Base " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-ac-" + System.nanoTime() + "@fightflow.test", base);

    MvcResult res = mvc.perform(post("/academias")
            .header("Authorization", "Bearer " + token(professor))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"Nova Academia\",\"endereco\":\"Rua 1\"}"))
        .andExpect(status().isOk())
        .andReturn();

    JsonNode body = body(res);
    Long academiaId = body.at("/data/id").asLong();
    assertThat(body.at("/data/professorResponsavelId").asLong()).isEqualTo(professor.getId());
    assertThat(professorAcademiaRepository.existsByProfessorIdAndAcademiaIdAndAtivoTrue(professor.getId(), academiaId))
        .isTrue();
  }

  @Test
  void professorListsOnlyOwnAcademias_andCannotAccessOtherProfessorAcademia() throws Exception {
    Academia acA = factory.createAcademia("Academia A " + System.nanoTime());
    Academia acB = factory.createAcademia("Academia B " + System.nanoTime());
    Usuario profA = factory.createProfessorUserInAcademia("prof-a-ac-" + System.nanoTime() + "@fightflow.test", acA);
    Usuario profB = factory.createProfessorUserInAcademia("prof-b-ac-" + System.nanoTime() + "@fightflow.test", acB);

    Long createdByA = createAcademia(profA, "Academia Extra A");

    MvcResult listRes = mvc.perform(get("/academias")
            .header("Authorization", "Bearer " + token(profA)))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode items = body(listRes).at("/data");
    assertThat(ids(items)).contains(acA.getId(), createdByA);
    assertThat(ids(items)).doesNotContain(acB.getId());

    mvc.perform(get("/academias/" + acB.getId())
            .header("Authorization", "Bearer " + token(profA)))
        .andExpect(status().isForbidden());

    mvc.perform(put("/academias/" + acB.getId())
            .header("Authorization", "Bearer " + token(profA))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"Tentativa\",\"endereco\":\"x\"}"))
        .andExpect(status().isForbidden());

    // Ensure professor B is still valid for his own academy through the legacy Usuario.academia fallback.
    mvc.perform(get("/academias/" + acB.getId())
            .header("Authorization", "Bearer " + token(profB)))
        .andExpect(status().isOk());
  }

  @Test
  void adminListsAllAcademias() throws Exception {
    Academia acA = factory.createAcademia("Admin A " + System.nanoTime());
    Academia acB = factory.createAcademia("Admin B " + System.nanoTime());
    Usuario admin = factory.createAdminUserInAcademia("admin-ac-" + System.nanoTime() + "@fightflow.test", acA);

    MvcResult res = mvc.perform(get("/academias")
            .header("Authorization", "Bearer " + token(admin)))
        .andExpect(status().isOk())
        .andReturn();

    assertThat(ids(body(res).at("/data"))).contains(acA.getId(), acB.getId());
  }

  @Test
  void alunoCannotCreateOrEditAcademia_andOnlySelectsOwnAcademia() throws Exception {
    Academia acA = factory.createAcademia("Aluno A " + System.nanoTime());
    Academia acB = factory.createAcademia("Aluno B " + System.nanoTime());
    Usuario aluno = factory.createAtletaUserInAcademia("aluno-ac-" + System.nanoTime() + "@fightflow.test", acA);

    mvc.perform(post("/academias")
            .header("Authorization", "Bearer " + token(aluno))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"Nao pode\"}"))
        .andExpect(status().isForbidden());

    mvc.perform(put("/academias/" + acA.getId())
            .header("Authorization", "Bearer " + token(aluno))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"Nao pode\",\"endereco\":null}"))
        .andExpect(status().isForbidden());

    MvcResult selectRes = mvc.perform(get("/academias/select")
            .header("Authorization", "Bearer " + token(aluno)))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode data = body(selectRes).at("/data");
    assertThat(ids(data)).containsExactly(acA.getId());
    assertThat(ids(data)).doesNotContain(acB.getId());
  }

  @Test
  void softDeleteSetsAtivoFalse_andSelectHidesInactive() throws Exception {
    Academia base = factory.createAcademia("Delete Base " + System.nanoTime());
    Usuario professor = factory.createProfessorUserInAcademia("prof-del-" + System.nanoTime() + "@fightflow.test", base);
    Long academiaId = createAcademia(professor, "Academia Inativar");

    mvc.perform(delete("/academias/" + academiaId)
            .header("Authorization", "Bearer " + token(professor)))
        .andExpect(status().isOk());

    Academia deleted = academiaRepository.findById(academiaId).orElseThrow();
    assertThat(deleted.isAtivo()).isFalse();

    MvcResult getRes = mvc.perform(get("/academias/" + academiaId)
            .header("Authorization", "Bearer " + token(professor)))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(body(getRes).at("/data/ativo").asBoolean()).isFalse();

    MvcResult selectRes = mvc.perform(get("/academias/select")
            .header("Authorization", "Bearer " + token(professor)))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(ids(body(selectRes).at("/data"))).doesNotContain(academiaId);
  }

  @Test
  void selectRespectsRoles() throws Exception {
    Academia acA = factory.createAcademia("Select A " + System.nanoTime());
    Academia acB = factory.createAcademia("Select B " + System.nanoTime());
    Usuario profA = factory.createProfessorUserInAcademia("prof-select-" + System.nanoTime() + "@fightflow.test", acA);
    Usuario admin = factory.createAdminUserInAcademia("admin-select-" + System.nanoTime() + "@fightflow.test", acA);

    MvcResult profSelect = mvc.perform(get("/academias/select")
            .header("Authorization", "Bearer " + token(profA)))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(ids(body(profSelect).at("/data"))).contains(acA.getId()).doesNotContain(acB.getId());

    MvcResult adminSelect = mvc.perform(get("/academias/select")
            .header("Authorization", "Bearer " + token(admin)))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(ids(body(adminSelect).at("/data"))).contains(acA.getId(), acB.getId());
  }

  private Long createAcademia(Usuario professor, String nome) throws Exception {
    MvcResult res = mvc.perform(post("/academias")
            .header("Authorization", "Bearer " + token(professor))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"" + nome + "\",\"endereco\":\"Rua Teste\"}"))
        .andExpect(status().isOk())
        .andReturn();
    return body(res).at("/data/id").asLong();
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
