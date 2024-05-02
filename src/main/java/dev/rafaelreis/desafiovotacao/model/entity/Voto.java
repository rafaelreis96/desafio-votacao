package dev.rafaelreis.desafiovotacao.model.entity;

import dev.rafaelreis.desafiovotacao.model.enums.OpcaoVoto;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = Voto.TABLE_NAME)
public class Voto {

    public static final String TABLE_NAME = "voto";

    public static final String SEQUENCE_NAME = "seq_voto";

    public static final String TABLE_ID = "cod_voto";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @Column(name = "cod_voto", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = Votacao.TABLE_ID, nullable = false)
    private Votacao votacao;

    @OneToOne
    @JoinColumn(name = Associado.TABLE_ID, nullable = false)
    private Associado associado;

    @Enumerated(EnumType.STRING)
    @Column(name = "opcao", length = 30, nullable = false)
    private OpcaoVoto opcao;

    public Voto() {
    }

    public Voto(Votacao votacao, Associado associado, OpcaoVoto opcao) {
        this.votacao = votacao;
        this.associado = associado;
        this.opcao = opcao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Votacao getVotacao() {
        return votacao;
    }

    public void setVotacao(Votacao votacao) {
        this.votacao = votacao;
    }

    public Associado getAssociado() {
        return associado;
    }

    public void setAssociado(Associado associado) {
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
        return "Voto{" +
                "id=" + id +
                ", votacao=" + votacao +
                ", associado=" + associado +
                ", opcao=" + opcao +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voto voto = (Voto) o;
        return Objects.equals(id, voto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
