package dev.rafaelreis.desafiovotacao.features.pauta.repository;

import dev.rafaelreis.desafiovotacao.model.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByAssociadoIdAndVotacaoId(Long  idAssociado, Long idVotacao);
}
