package dev.rafaelreis.desafiovotacao.features.pauta.service;

import dev.rafaelreis.desafiovotacao.model.entity.Pauta;
import dev.rafaelreis.desafiovotacao.features.pauta.repository.PautaRepostitory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PautaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepostitory pautaRepostitory;

    public PautaService(PautaRepostitory pautaRepostitory) {
        this.pautaRepostitory = pautaRepostitory;
    }

    public Pauta criar(Pauta pauta) {
        LOGGER.debug("Criando pauta: {}", pauta);
        return this.pautaRepostitory.save(pauta);
    }

    public Optional<Pauta> buscar(Long id) {
        return this.pautaRepostitory.findById(id);
    }

    public List<Pauta> listar() {
        return this.pautaRepostitory.findAll();
    }

    public void excluir(Long id) {
        LOGGER.debug("Excluindo pauta ID: {}", id);
        this.pautaRepostitory.deleteById(id);
    }
}
