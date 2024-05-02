package dev.rafaelreis.desafiovotacao.features.pauta.dto;

import java.time.LocalDateTime;

public class VotacaoResponseDto {
    private Long id;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataEncerramento;

    public VotacaoResponseDto() {

    }

    public VotacaoResponseDto(Long id, LocalDateTime dataAbertura, LocalDateTime dataEncerramento) {
        this.id = id;
        this.dataAbertura = dataAbertura;
        this.dataEncerramento = dataEncerramento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDateTime dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    @Override
    public String toString() {
        return "VotacaoResponseDto{" +
                "id=" + id +
                ", dataAbertura=" + dataAbertura +
                ", dataEncerramento=" + dataEncerramento +
                '}';
    }
}
