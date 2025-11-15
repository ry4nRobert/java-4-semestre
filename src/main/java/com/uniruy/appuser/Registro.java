package com.uniruy.appuser;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.Set;
import java.util.HashSet;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Lob; 
import java.util.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    private String senha;

    private String resetToken;
    private LocalDateTime resetTokenExpiryDate;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fotoPerfil;

    @OrderBy("nome ASC")
    @ManyToMany
    @JoinTable(
        name = "registro_especialidades",
        joinColumns = @JoinColumn(name = "registro_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidade_id"))
    private Set<Especialidade> especialidades = new HashSet<>();


    public Registro() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public LocalDateTime getResetTokenExpiryDate() { return resetTokenExpiryDate; }
    public void setResetTokenExpiryDate(LocalDateTime resetTokenExpiryDate) { this.resetTokenExpiryDate = resetTokenExpiryDate; }

    public Set<Especialidade> getEspecialidades() { return especialidades; }
    public void setEspecialidades(Set<Especialidade> especialidades) { this.especialidades = especialidades; }

    public byte[] getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(byte[] fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public String getFotoPerfilBase64() {
        if (fotoPerfil == null || fotoPerfil.length == 0) return null;
        return Base64.getEncoder().encodeToString(fotoPerfil);
    }
}
