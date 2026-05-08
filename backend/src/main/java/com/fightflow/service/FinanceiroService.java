package com.fightflow.service;

import com.fightflow.dto.financeiro.AtualizarAtrasosResponse;
import com.fightflow.entity.Mensalidade;
import com.fightflow.entity.MensalidadeStatus;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.repository.MensalidadeRepository;
import com.fightflow.repository.spec.MensalidadeSpecs;
import com.fightflow.security.UserPrincipal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinanceiroService {
  private final MensalidadeRepository mensalidadeRepository;

  public FinanceiroService(MensalidadeRepository mensalidadeRepository) {
    this.mensalidadeRepository = mensalidadeRepository;
  }

  @Transactional
  public AtualizarAtrasosResponse atualizarAtrasos(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can update overdue mensalidades");
    }
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }

    Instant now = Instant.now();
    Specification<Mensalidade> spec = Specification.where(MensalidadeSpecs.academiaId(me.getAcademiaId()))
        .and(MensalidadeSpecs.status(MensalidadeStatus.PENDENTE))
        .and(MensalidadeSpecs.vencimentoTo(now));
    List<Mensalidade> atrasadas = mensalidadeRepository.findAll(spec);
    atrasadas.forEach(m -> m.setStatus(MensalidadeStatus.ATRASADO));
    mensalidadeRepository.saveAll(atrasadas);
    return new AtualizarAtrasosResponse(atrasadas.size());
  }
}

