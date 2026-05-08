package com.fightflow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fightflow.dto.atleta.AtletaProfileResponse;
import com.fightflow.dto.common.ApiResponse;
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
class AtletaAndRegisterIT {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;
  @Autowired TestDataFactory factory;
  @Autowired JwtService jwtService;

  @Test
  void getAtletasMe_authenticated_returns200_andProfileComplete() throws Exception {
    Usuario u = factory.createAtletaUser("me@fightflow.test");
    String token = jwtService.generate(new UserPrincipal(u.getId(), u.getEmail(), u.getPasswordHash(), u.getRole(), u.getAcademia().getId()));

    MvcResult res = mvc.perform(get("/atletas/me")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andReturn();

    ApiResponse<AtletaProfileResponse> body = om.readValue(res.getResponse().getContentAsByteArray(),
        new TypeReference<ApiResponse<AtletaProfileResponse>>() {});
    assertThat(body.success()).isTrue();
    assertThat(body.data()).isNotNull();
    assertThat(body.data().email()).isEqualTo("me@fightflow.test");
    assertThat(body.data().academiaId()).isNotNull();
  }

  @Test
  void registerDuplicate_returns409() throws Exception {
    String payload = "{\"email\":\"dup@fightflow.test\",\"password\":\"password123\",\"role\":\"ATLETA\",\"academiaNome\":\"Academia Dup\"}";

    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk());

    mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isConflict());
  }
}

