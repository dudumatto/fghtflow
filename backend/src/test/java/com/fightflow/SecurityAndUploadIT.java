package com.fightflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fightflow.dto.common.ApiResponse;
import com.fightflow.dto.documento.DocumentoResponse;
import com.fightflow.entity.Usuario;
import com.fightflow.security.JwtService;
import com.fightflow.security.UserPrincipal;
import com.fightflow.test.TestDataFactory;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAndUploadIT {

  @Autowired MockMvc mvc;
  @Autowired TestDataFactory factory;
  @Autowired JwtService jwtService;
  @Autowired ObjectMapper om;

  @Test
  void unauthorizedAccess_returns403() throws Exception {
    mvc.perform(get("/dashboard/atleta"))
        .andExpect(status().isForbidden());
  }

  @Test
  void uploadTooLarge_returns413() throws Exception {
    Usuario u = factory.createAtletaUser("u1@fightflow.test");
    String token = jwtService.generate(new UserPrincipal(u.getId(), u.getEmail(), u.getPasswordHash(), u.getRole(), u.getAcademia().getId()));

    byte[] tooBig = new byte[10 * 1024 * 1024 + 1];
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "a.pdf",
        "application/pdf",
        tooBig
    );

    mvc.perform(multipart("/documentos/upload")
            .file(file)
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isPayloadTooLarge());
  }

  @Test
  void invalidPreview_returns415() throws Exception {
    Usuario u = factory.createAtletaUser("u2@fightflow.test");
    String token = jwtService.generate(new UserPrincipal(u.getId(), u.getEmail(), u.getPasswordHash(), u.getRole(), u.getAcademia().getId()));

    MockMultipartFile file = new MockMultipartFile(
        "file",
        "doc.doc",
        "application/msword",
        "hello".getBytes(StandardCharsets.UTF_8)
    );

    MvcResult res = mvc.perform(multipart("/documentos/upload")
            .file(file)
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andReturn();

    ApiResponse<DocumentoResponse> wrapped = om.readValue(res.getResponse().getContentAsByteArray(),
        new TypeReference<ApiResponse<DocumentoResponse>>() {});
    assertThat(wrapped.success()).isTrue();
    DocumentoResponse doc = wrapped.data();
    assertThat(doc).isNotNull();
    assertThat(doc.id()).isNotNull();

    mvc.perform(get("/documentos/" + doc.id() + "/preview")
            .header("Authorization", "Bearer " + token)
            .accept(MediaType.APPLICATION_PDF))
        .andExpect(status().isUnsupportedMediaType());
  }
}
