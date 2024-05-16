package dev.rafaelreis.desafiovotacao.features.pauta.dto;

import dev.rafaelreis.desafiovotacao.model.enums.OpcaoVoto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class VotoDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long idPauta;
    private Long idAssociado;
    private OpcaoVoto opcao;
    private LocalDateTime dataRegistro;

    public VotoDto() {
    }

    public VotoDto(Long idPauta, Long idAssociado, OpcaoVoto opcao, LocalDateTime dataRegistro) {
        this.idPauta = idPauta;
        this.idAssociado = idAssociado;
        this.opcao = opcao;
        this.dataRegistro = dataRegistro;
    }

    public Long getIdPauta() {
        return idPauta;
    }

    public void setIdPauta(Long idPauta) {
        this.idPauta = idPauta;
    }

    public Long getIdAssociado() {
        return idAssociado;
    }

    public void setIdAssociado(Long idAssociado) {
        this.idAssociado = idAssociado;
    }

    public OpcaoVoto getOpcao() {
        return opcao;
    }

    public void setOpcao(OpcaoVoto opcao) {
        this.opcao = opcao;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public String toString() {
        return "VotoDto{" +
                "idPauta=" + idPauta +
                ", idAssociado=" + idAssociado +
                ", opcao=" + opcao +
                ", dataRegistro=" + dataRegistro +
                '}';
    }
}
