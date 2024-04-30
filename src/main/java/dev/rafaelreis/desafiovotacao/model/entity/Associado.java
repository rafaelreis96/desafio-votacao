package dev.rafaelreis.desafiovotacao.model.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = Associado.TABLE_NAME)
public class Associado {

    public static final String TABLE_NAME = "associado";

    public static final String SEQUENCE_NAME = "seq_associado";

    public static final String TABLE_ID = "cod_associado";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @Column(name = TABLE_ID, nullable = false)
    private Long id;

    @Column(name = "nome", length = 255, nullable = false)
    private String nome;

    @Column(name = "cpf", length = 11, unique = true, nullable = false)
    private String cpf;

    public Associado() {

    }

    public Associado(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return "Associado{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Associado associado = (Associado) o;
        return Objects.equals(id, associado.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
