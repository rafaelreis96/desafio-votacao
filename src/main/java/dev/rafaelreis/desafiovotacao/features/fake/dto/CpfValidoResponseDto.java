package dev.rafaelreis.desafiovotacao.features.fake.dto;

import dev.rafaelreis.desafiovotacao.model.enums.StatusVoto;

public class CpfValidoResponseDto {

    private StatusVoto status;

    public CpfValidoResponseDto() {
    }

    public CpfValidoResponseDto(StatusVoto status) {
        this.status = status;
    }

    public StatusVoto getStatus() {
        return status;
    }

    public void setStatus(StatusVoto status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CpfValidoResponseDto{" +
                "status=" + status +
                '}';
    }
}
