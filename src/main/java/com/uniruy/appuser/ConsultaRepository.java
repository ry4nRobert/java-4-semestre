package com.uniruy.appuser;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    void deleteByPacienteId(Long pacienteId);

    List<Consulta> findByMedicoAndDataHoraBetween(Registro medico, LocalDateTime start, LocalDateTime end);
}