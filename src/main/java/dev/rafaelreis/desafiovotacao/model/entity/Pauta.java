package dev.rafaelreis.desafiovotacao.model.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = Pauta.TABLE_NAME)
public class Pauta {

    public static final String TABLE_NAME = "pauta";

    public static final String SEQUENCE_NAME = "seq_pauta";

    public static final String TABLE_ID = "cod_pauta";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @Column(name = TABLE_ID, nullable = false)
    private Long id;
    private String titulo;
    private String descricao;

    @OneToOne
    @JoinColumn(name = Votacao.TABLE_ID)
    private Votacao votacao;

    public Pauta() {}

    public Pauta(String titulo, String descricao) {
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

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Votacao getVotacao() {
        return votacao;
    }

    public void setVotacao(Votacao votacao) {
        this.votacao = votacao;
    }

    @Override
    public String toString() {
        return "Pauta{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", votacao=" + votacao +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pauta pauta = (Pauta) o;
        return Objects.equals(id, pauta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
