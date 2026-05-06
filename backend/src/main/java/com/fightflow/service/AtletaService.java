package com.fightflow.service;

import com.fightflow.dto.atleta.AtletaProfileResponse;
import com.fightflow.dto.atleta.AtletaUpdateRequest;
import com.fightflow.entity.Atleta;
import com.fightflow.entity.Role;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.exception.NotFoundException;
import com.fightflow.repository.AtletaRepository;
import com.fightflow.security.UserPrincipal;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AtletaService {
  private final AtletaRepository atletaRepository;

  public AtletaService(AtletaRepository atletaRepository) {
    this.atletaRepository = atletaRepository;
  }

  public AtletaProfileResponse getMe(UserPrincipal me) {
    requireAtleta(me);
    Atleta a = atletaRepository.findByUsuarioId(me.getId())
        .orElseThrow(() -> new NotFoundException("Atleta not found"));
    return toProfile(a);
  }

  @Transactional
  public AtletaProfileResponse updateMe(UserPrincipal me, AtletaUpdateRequest req) {
    requireAtleta(me);
    Atleta a = atletaRepository.findByUsuarioId(me.getId())
        .orElseThrow(() -> new NotFoundException("Atleta not found"));
    if (req.faixa() != null) a.setFaixa(req.faixa());
    if (req.peso() != null) a.setPeso(req.peso());
    if (req.categoria() != null) a.setCategoria(req.categoria());
    return toProfile(atletaRepository.save(a));
  }

  private void requireAtleta(UserPrincipal me) {
    if (me.getRole() != Role.ATLETA && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only ATLETA can access this resource");
    }
  }

  private AtletaProfileResponse toProfile(Atleta a) {
    Long academiaId = a.getAcademia() == null ? null : a.getAcademia().getId();
    return new AtletaProfileResponse(
        a.getId(),
        a.getUsuario().getId(),
        academiaId,
        a.getUsuario().getEmail(),
        a.getFaixa(),
        a.getPeso(),
        a.getCategoria()
    );
  }
}

