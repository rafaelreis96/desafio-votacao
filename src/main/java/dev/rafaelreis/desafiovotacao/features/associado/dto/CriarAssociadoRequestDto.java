package dev.rafaelreis.desafiovotacao.features.associado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CriarAssociadoRequestDto {

    public static final String NOME_OBRIGATORIO = "O campo nome e obrigatório";

    public static final String CPF_OBRIGATORIO = "O campo cpf e obrigatório";

    public static final String NOME_INVALIDO = "O campo nome deve ter no máximo 255 caracteres";

    public static final String CPF_INVALIDO = "O campo cpf deve ter 11 digitos";

    @NotBlank(message = NOME_OBRIGATORIO)
    @Size(max = 255, message = NOME_INVALIDO)
    private String nome;

    @NotBlank(message = CPF_OBRIGATORIO)
    @Pattern(regexp = "^\\d{11}$", message = CPF_INVALIDO)
    private String cpf;

    public CriarAssociadoRequestDto() {
    }

    public CriarAssociadoRequestDto(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return "CriarAssociadoRequestDto{" +
                "nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
