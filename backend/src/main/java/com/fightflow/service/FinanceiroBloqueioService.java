package com.fightflow.service;

import com.fightflow.dto.financeiro.AtualizarBloqueiosResponse;
import com.fightflow.dto.financeiro.FinanceiroStatusResponse;
import com.fightflow.entity.Aluno;
import com.fightflow.entity.Matricula;
import com.fightflow.entity.MatriculaStatus;
import com.fightflow.entity.MensalidadeStatus;
import com.fightflow.entity.Role;
import com.fightflow.exception.BadRequestException;
import com.fightflow.exception.ForbiddenException;
import com.fightflow.repository.AlunoRepository;
import com.fightflow.repository.MatriculaRepository;
import com.fightflow.repository.MensalidadeRepository;
import com.fightflow.security.UserPrincipal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinanceiroBloqueioService {
  private final AlunoRepository alunoRepository;
  private final MatriculaRepository matriculaRepository;
  private final MensalidadeRepository mensalidadeRepository;
  private final int diasToleranciaInadimplencia;

  public FinanceiroBloqueioService(
      AlunoRepository alunoRepository,
      MatriculaRepository matriculaRepository,
      MensalidadeRepository mensalidadeRepository,
      @Value("${fightflow.financeiro.diasToleranciaInadimplencia:5}") int diasToleranciaInadimplencia
  ) {
    this.alunoRepository = alunoRepository;
    this.matriculaRepository = matriculaRepository;
    this.mensalidadeRepository = mensalidadeRepository;
    this.diasToleranciaInadimplencia = diasToleranciaInadimplencia;
  }

  @Transactional
  public AtualizarBloqueiosResponse atualizarBloqueios(UserPrincipal me) {
    requireStaffWithAcademia(me);
    int bloqueados = 0;
    int desbloqueados = 0;
    for (Aluno aluno : alunoRepository.findAllByAcademiaIdAndAtivoTrue(me.getAcademiaId())) {
      Resultado resultado = sincronizarBloqueio(aluno);
      if (resultado == Resultado.BLOQUEADO) bloqueados++;
      if (resultado == Resultado.DESBLOQUEADO) desbloqueados++;
    }
    return new AtualizarBloqueiosResponse(bloqueados, desbloqueados);
  }

  @Transactional(readOnly = true)
  public FinanceiroStatusResponse status(Aluno aluno) {
    boolean bloqueado = aluno != null && isBloqueado(aluno);
    return new FinanceiroStatusResponse(
        bloqueado,
        bloqueado ? "BLOQUEADO" : "EM_DIA",
        diasToleranciaInadimplencia
    );
  }

  @Transactional(readOnly = true)
  public boolean isBloqueado(Aluno aluno) {
    if (aluno == null || aluno.getId() == null) {
      return false;
    }
    return hasMatriculaBloqueada(aluno.getId()) || hasInadimplenciaBloqueante(aluno.getId());
  }

  @Transactional(readOnly = true)
  public void assertPodeRegistrarPresenca(Aluno aluno) {
    if (isBloqueado(aluno)) {
      throw new ForbiddenException("Aluno bloqueado por inadimplencia");
    }
  }

  @Transactional
  public void regularizarAlunoSePossivel(Aluno aluno) {
    if (aluno == null || aluno.getId() == null || hasInadimplenciaBloqueante(aluno.getId())) {
      return;
    }
    List<Matricula> bloqueadas = matriculaRepository.findAllByAlunoIdAndStatusForUpdate(aluno.getId(), MatriculaStatus.BLOQUEADA);
    bloqueadas.forEach(m -> m.setStatus(MatriculaStatus.ATIVA));
    matriculaRepository.saveAll(bloqueadas);
  }

  private Resultado sincronizarBloqueio(Aluno aluno) {
    boolean inadimplente = hasInadimplenciaBloqueante(aluno.getId());
    if (inadimplente) {
      List<Matricula> ativas = matriculaRepository.findAllByAlunoIdAndStatusForUpdate(aluno.getId(), MatriculaStatus.ATIVA);
      ativas.forEach(m -> m.setStatus(MatriculaStatus.BLOQUEADA));
      matriculaRepository.saveAll(ativas);
      return ativas.isEmpty() ? Resultado.NENHUM : Resultado.BLOQUEADO;
    }
    List<Matricula> bloqueadas = matriculaRepository.findAllByAlunoIdAndStatusForUpdate(aluno.getId(), MatriculaStatus.BLOQUEADA);
    bloqueadas.forEach(m -> m.setStatus(MatriculaStatus.ATIVA));
    matriculaRepository.saveAll(bloqueadas);
    return bloqueadas.isEmpty() ? Resultado.NENHUM : Resultado.DESBLOQUEADO;
  }

  private boolean hasMatriculaBloqueada(Long alunoId) {
    return matriculaRepository.existsByAlunoIdAndStatus(alunoId, MatriculaStatus.BLOQUEADA);
  }

  private boolean hasInadimplenciaBloqueante(Long alunoId) {
    return mensalidadeRepository.existsInadimplenciaBloqueante(
        alunoId,
        List.of(MensalidadeStatus.PENDENTE, MensalidadeStatus.ATRASADO),
        cutoff()
    );
  }

  private Instant cutoff() {
    return Instant.now().minus(diasToleranciaInadimplencia, ChronoUnit.DAYS);
  }

  private void requireStaffWithAcademia(UserPrincipal me) {
    if (me.getRole() != Role.PROFESSOR && me.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Only PROFESSOR/ADMIN can update financial blocks");
    }
    if (me.getAcademiaId() == null) {
      throw new BadRequestException("User has no academia");
    }
  }

  private enum Resultado {
    BLOQUEADO,
    DESBLOQUEADO,
    NENHUM
  }
}
