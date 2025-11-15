package com.uniruy.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; 

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    
 List<Paciente> findByMedicoId(Long medicoId);
 Long countByMedicoId(Long medicoId);
 Long countByMedicoIdAndDataCriacaoBetween(Long medicoId, LocalDateTime start, LocalDateTime end);
    Optional<Paciente> findByCpf(String cpf); 
    
    List<Paciente> findByMedicoIdAndNomeContainingIgnoreCaseOrMedicoIdAndCpf(
        Long medicoId1, String queryNome,
        Long medicoId2, String queryCpf
    );
}