package dev.rafaelreis.desafiovotacao.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = Votacao.TABLE_NAME)
public class Votacao {

    public static final String TABLE_NAME = "votacao";

    public static final String SEQUENCE_NAME = "seq_votacao";

    public static final String TABLE_ID = "cod_votacao";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @Column(name = TABLE_ID, nullable = false)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_abertura")
    private LocalDateTime dataAbertura;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_encerramento")
    private LocalDateTime dataEncerramento;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = Votacao.TABLE_ID)
    private List<Voto> votos;

    public Votacao() {

    }

    public Votacao(LocalDateTime dataAbertura, LocalDateTime dataEncerramento) {
        this.dataAbertura = dataAbertura;
        this.dataEncerramento = dataEncerramento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDateTime dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public List<Voto> getVotos() {
        return votos;
    }

    public void setVotos(List<Voto> votos) {
        this.votos = votos;
    }

    @Override
    public String toString() {
        return "Votacao{" +
                "id=" + id +
                ", votos=" + votos +
                ", dataAbertura=" + dataAbertura +
                ", dataEncerramento=" + dataEncerramento +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Votacao votacao = (Votacao) o;
        return Objects.equals(id, votacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
