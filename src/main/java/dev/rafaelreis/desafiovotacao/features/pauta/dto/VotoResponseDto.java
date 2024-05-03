package dev.rafaelreis.desafiovotacao.features.pauta.dto;

import dev.rafaelreis.desafiovotacao.features.associado.dto.AssociadoResponseDto;
import dev.rafaelreis.desafiovotacao.model.enums.OpcaoVoto;

public class VotoResponseDto {

    private Long id;
    private AssociadoResponseDto associado;
    private OpcaoVoto opcao;

    public VotoResponseDto() {

    }

    public VotoResponseDto(Long id, AssociadoResponseDto associado, OpcaoVoto opcao) {
        this.id = id;
        this.associado = associado;
        this.opcao = opcao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssociadoResponseDto getAssociado() {
        return associado;
    }

    public void setAssociado(AssociadoResponseDto associado) {
        this.associado = associado;
    }

    public OpcaoVoto getOpcao() {
        return opcao;
    }

    public void setOpcao(OpcaoVoto opcao) {
        this.opcao = opcao;
    }

    @Override
    public String toString() {
        return "VotoResponseDto{" +
                "id=" + id +
                ", associado=" + associado +
                ", opcao=" + opcao +
                '}';
    }
}
