package com.uniruy.appuser;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.Set;
import java.util.HashSet;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Lob; // 1. IMPORT ADICIONADO
import java.util.Base64; // 1. IMPORT ADICIONADO

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
@Column(unique = true, nullable = false)
private String codigoLogin;
private String resetToken;
private LocalDateTime resetTokenExpiryDate;

// --- 2. ALTERAÇÃO DO CAMPO 'fotoPerfil' ---
@Lob // Informa que é um "Large Object" (Objeto Grande)
@Column(columnDefinition = "LONGBLOB") // Define o tipo de coluna no MySQL
private byte[] fotoPerfil; // O tipo foi alterado de String para byte[]


@OrderBy("nome ASC")
@ManyToMany
@JoinTable(
name = "registro_especialidades", 
joinColumns = @JoinColumn(name = "registro_id"), 
inverseJoinColumns = @JoinColumn(name = "especialidade_id")) 
private Set<Especialidade> especialidades = new HashSet<>();


public Registro() {
}

// ... (Getters e Setters de id, nome, email, etc. continuam iguais) ...

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

public Set<Especialidade> getEspecialidades() {
return especialidades;
}

public void setEspecialidades(Set<Especialidade> especialidades) {
this.especialidades = especialidades;
}

// --- 3. ALTERAÇÃO DOS GETTERS E SETTERS DA FOTO ---
public byte[] getFotoPerfil() { // O tipo de retorno mudou
return fotoPerfil;
}

public void setFotoPerfil(byte[] fotoPerfil) { // O parâmetro mudou
this.fotoPerfil = fotoPerfil;
}

// --- 4. MÉTODO NOVO PARA O HTML LER A FOTO ---
// Este método converte os bytes da foto em texto (Base64) que o HTML entende
public String getFotoPerfilBase64() {
if (fotoPerfil == null || fotoPerfil.length == 0) {
return null; // Não há foto
}
// Converte o array de bytes em uma String Base64
return Base64.getEncoder().encodeToString(fotoPerfil);
}
}