package dev.rafaelreis.desafiovotacao.features.associado.service;

import dev.rafaelreis.desafiovotacao.exception.BusinessException;
import dev.rafaelreis.desafiovotacao.features.associado.repository.AssociadoRepository;
import dev.rafaelreis.desafiovotacao.model.entity.Associado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssociadoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssociadoService.class);

    private final AssociadoRepository associadoRepository;

    public AssociadoService(AssociadoRepository associadoRepository) {
        this.associadoRepository = associadoRepository;
    }

    public Associado criar(Associado entity) {
        LOGGER.debug("Criando associado: {}", entity);

        if(associadoRepository.existsByCpf(entity.getCpf())) {
            throw new BusinessException("Associado com o CPF informado já existe");
        }

        return associadoRepository.save(entity);
    }

    public Optional<Associado> buscar(Long id) {
        return associadoRepository.findById(id);
    }

    public List<Associado> listar() {
        return associadoRepository.findAll();
    }

    public void excluir(Long id) {
        LOGGER.debug("Excluindo associado ID: {}", id);
        associadoRepository.deleteById(id);
    }
}
