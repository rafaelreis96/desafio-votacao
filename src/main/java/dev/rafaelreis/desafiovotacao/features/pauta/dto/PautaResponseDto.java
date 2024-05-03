package dev.rafaelreis.desafiovotacao.features.pauta.dto;

public class PautaResponseDto {

    private Long id;
    private String titulo;
    private String descricao;
    private VotacaoResponseDto votacao;

    public PautaResponseDto() {

    }

    public PautaResponseDto(Long id, String titulo, String descricao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String nome) {
        this.titulo = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public VotacaoResponseDto getVotacao() {
        return votacao;
    }

    public void setVotacao(VotacaoResponseDto votacao) {
        this.votacao = votacao;
    }

    @Override
    public String toString() {
        return "PautaResponseDto{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", votacao=" + votacao +
                '}';
    }
}
