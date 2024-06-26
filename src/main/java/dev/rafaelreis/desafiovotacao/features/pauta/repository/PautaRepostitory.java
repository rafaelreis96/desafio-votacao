package dev.rafaelreis.desafiovotacao.features.pauta.repository;

import dev.rafaelreis.desafiovotacao.model.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PautaRepostitory extends JpaRepository<Pauta, Long> {
}
