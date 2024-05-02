package dev.rafaelreis.desafiovotacao.features.pauta.dto;

import dev.rafaelreis.desafiovotacao.model.enums.OpcaoVoto;
import jakarta.validation.constraints.NotNull;

public class CriarVotoRequestDto {

    public static final String MSG_ID_ASSOCIADO_NAO_INFORMADO = "ID do Associado deve ser informado";

    public static final String MSG_OPCAO_NAO_INFORMADA = "Opção de voto deve ser informada";

    @NotNull(message = MSG_ID_ASSOCIADO_NAO_INFORMADO)
    private Long idAssociado;

    @NotNull(message = MSG_OPCAO_NAO_INFORMADA)
    private OpcaoVoto opcao;

    public CriarVotoRequestDto() {
    }

    public CriarVotoRequestDto(Long idAssociado, OpcaoVoto opcao) {
        this.idAssociado = idAssociado;
        this.opcao = opcao;
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

    @Override
    public String toString() {
        return "CriarVotoRequestDto{" +
                "idAssociado=" + idAssociado +
                ", opcao=" + opcao +
                '}';
    }
}
