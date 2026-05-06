package com.fightflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fightflow.entity.Academia;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthAndRbacIT {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;
  @Autowired TestDataFactory factory;
  @Autowired JwtService jwtService;
  @Autowired AtletaRepository atletaRepository;

  @Test
  void invalidLogin_returns401() throws Exception {
    factory.createAtletaUser("login@fightflow.test");
    mvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"login@fightflow.test\",\"password\":\"wrongpass\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void roleBasedAccessDenial_returns403() throws Exception {
    Usuario atletaUser = factory.createAtletaUser("rbac@fightflow.test");
    String token = jwtService.generate(new UserPrincipal(atletaUser.getId(), atletaUser.getEmail(), atletaUser.getPasswordHash(),
        atletaUser.getRole(), atletaUser.getAcademia().getId()));

    mvc.perform(post("/treinos")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"startsAt\":\"2026-01-01T10:00:00Z\",\"titulo\":\"Treino\",\"descricao\":null}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void idorAttempt_accessOtherAthleteData_returns403() throws Exception {
    Academia ac = factory.createAcademia("Academia A");
    Usuario aUser = factory.createAtletaUserInAcademia("a@fightflow.test", ac);
    Usuario bUser = factory.createAtletaUserInAcademia("b@fightflow.test", ac);

    Atleta bAtleta = atletaRepository.findByUsuarioId(bUser.getId()).orElseThrow();

    String tokenA = jwtService.generate(new UserPrincipal(aUser.getId(), aUser.getEmail(), aUser.getPasswordHash(),
        aUser.getRole(), aUser.getAcademia().getId()));

    mvc.perform(get("/lutas")
            .param("atletaId", String.valueOf(bAtleta.getId()))
            .param("page", "0")
            .param("size", "10")
            .param("sort", "foughtAt,desc")
            .header("Authorization", "Bearer " + tokenA))
        .andExpect(status().isForbidden());
  }
}

