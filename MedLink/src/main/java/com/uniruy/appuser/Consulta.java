package com.uniruy.appuser;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Lob;

@Entity // Diz ao Spring que esta classe é uma tabela no banco
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Guarda a data E a hora da consulta
    private LocalDateTime dataHora; 
    
    @Lob // Para textos longos (igual ao BLOB de foto)
    private String observacoes; // Notas sobre a consulta
    
    // --- RELACIONAMENTOS ---

    // @ManyToOne: "Muitas" consultas podem pertencer a "Um" paciente.
    @ManyToOne 
    @JoinColumn(name = "paciente_id", nullable = false) // Chave estrangeira
    private Paciente paciente;

    // @ManyToOne: "Muitas" consultas podem pertencer a "Um" médico (Registro).
    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false) // Chave estrangeira
    private Registro medico;

    
    // --- Getters e Setters ---
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Registro getMedico() {
        return medico;
    }

    public void setMedico(Registro medico) {
        this.medico = medico;
    }
}