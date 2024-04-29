package dev.rafaelreis.desafiovotacao.features.pauta.repository;

import dev.rafaelreis.desafiovotacao.model.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PautaRepostitory extends JpaRepository<Pauta, Long> {
}
