package com.uniruy.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<Paciente> findByMedicoId(Long medicoId);
    Long countByMedicoId(Long medicoId);
}