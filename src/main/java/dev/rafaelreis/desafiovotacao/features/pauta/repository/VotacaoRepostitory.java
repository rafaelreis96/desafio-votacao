package dev.rafaelreis.desafiovotacao.features.pauta.repository;

import dev.rafaelreis.desafiovotacao.model.entity.Votacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotacaoRepostitory extends JpaRepository<Votacao, Long> {

}
