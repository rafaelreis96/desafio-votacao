package dev.rafaelreis.desafiovotacao.features.pauta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CriarPautaRequestDto {

    public static final String TITULO_OBRIGATORIO = "O campo titulo e obrigatório";

    public static final String DESCRICAO_OBRIGATORIA = "O campo descricao e obrigatório";

    public static final String TITULO_INVALIDO = "O campo titulo deve ter no máximo 255 caracteres";

    public static final String DESCRICAO_INVALIDO = "O campo descricao deve ter no máximo 512 caracteres";

    @NotBlank(message = TITULO_OBRIGATORIO)
    @Size(max = 255, message = TITULO_INVALIDO)
    String titulo;

    @NotBlank(message = DESCRICAO_OBRIGATORIA)
    @Size(max = 512, message = DESCRICAO_INVALIDO)
    String descricao;

    public CriarPautaRequestDto() {

    }

    public CriarPautaRequestDto(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "CriarPautaRequestDto{" +
                "titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
