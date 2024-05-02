package dev.rafaelreis.desafiovotacao.features.pauta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;

public class CriarVotacaoRequestDto {

    public static final String FORMATO_DATA_INVALIDO = "Formato de data e hora inválido";

    public static final String REGEX_FORMATO_DATA = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";

    public static final String PATTERN_DATA = "yyyy-MM-dd HH:mm:ss";

    @Pattern(regexp = REGEX_FORMATO_DATA, message = FORMATO_DATA_INVALIDO)
    @JsonFormat(pattern = PATTERN_DATA)
    private String dataEncerramento;

    public CriarVotacaoRequestDto() {

    }

    public CriarVotacaoRequestDto(String dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public String getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(String dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    @Override
    public String toString() {
        return "CriarVotacaoRequestDto{" +
                "dataEncerramento=" + dataEncerramento +
                '}';
    }
}
