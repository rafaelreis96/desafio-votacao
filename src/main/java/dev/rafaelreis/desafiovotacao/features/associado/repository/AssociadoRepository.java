package dev.rafaelreis.desafiovotacao.features.associado.repository;

import dev.rafaelreis.desafiovotacao.model.entity.Associado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssociadoRepository  extends JpaRepository<Associado, Long> {

    boolean existsByCpf(String cpf);
}
