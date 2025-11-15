package com.uniruy.appuser;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {
    
    Optional<Registro> findByEmail(String email);

    Optional<Registro> findByResetToken(String resetToken);
}
