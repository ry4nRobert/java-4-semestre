package com.uniruy.appuser;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer idade;
    private String cpf;
    private String alergias;
    private String historicoCirurgias;
    private String observacoes;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Registro medico;

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

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getHistoricoCirurgias() {
        return historicoCirurgias;
    }

    public void setHistoricoCirurgias(String historicoCirurgias) {
        this.historicoCirurgias = historicoCirurgias;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Registro getMedico() {
        return medico;
    }

    public void setMedico(Registro medico) {
        this.medico = medico;
    }


private LocalDateTime dataCriacao = LocalDateTime.now();

public LocalDateTime getDataCriacao() {
    return dataCriacao;
}

public void setDataCriacao(LocalDateTime dataCriacao) {
    this.dataCriacao = dataCriacao;
}
}

