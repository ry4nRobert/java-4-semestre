package com.uniruy.appuser;

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
    private String email;
    private String senha;
    private String codigoLogin;
    private String resetToken;
    private LocalDateTime resetTokenExpiryDate;

    public Registro() {
    }

    // GETTERS E SETTERS CORRETOS:
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCodigoLogin() {
        return codigoLogin;
    }

    public void setCodigoLogin(String codigoLogin) {
        this.codigoLogin = codigoLogin;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiryDate() {
        return resetTokenExpiryDate;
    }

    public void setResetTokenExpiryDate(LocalDateTime resetTokenExpiryDate) {
        this.resetTokenExpiryDate = resetTokenExpiryDate;
    }
}