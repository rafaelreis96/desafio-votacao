package dev.rafaelreis.desafiovotacao.features.pauta.service;

import dev.rafaelreis.desafiovotacao.exception.BusinessException;
import dev.rafaelreis.desafiovotacao.exception.ResourceNotFoundException;
import dev.rafaelreis.desafiovotacao.features.associado.service.AssociadoService;
import dev.rafaelreis.desafiovotacao.features.pauta.repository.PautaRepostitory;
import dev.rafaelreis.desafiovotacao.features.pauta.repository.VotacaoRepostitory;
import dev.rafaelreis.desafiovotacao.features.pauta.repository.VotoRepository;
import dev.rafaelreis.desafiovotacao.model.entity.Associado;
import dev.rafaelreis.desafiovotacao.model.entity.Pauta;
import dev.rafaelreis.desafiovotacao.model.entity.Votacao;
import dev.rafaelreis.desafiovotacao.model.entity.Voto;
import dev.rafaelreis.desafiovotacao.model.enums.OpcaoVoto;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class PautaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PautaService.class);

    public static final String MSG_VOTO_JA_REGISTRADO_PARA_ESTA_PAUTA = "Voto já registrado para esta pauta.";

    public static final String MSG_VOTACAO_DA_PAUTA_ENCERRADA = "Votação da pauta encerrada.";

    public static final String MSG_ASSOCIADO_NAO_ENCONTRADO = "Associado não encontrado.";

    public static final String PAUTA_NAO_ENCONTRADA = "Pauta não encontrada.";

    public static final String MSG_OCORREU_UM_ERRO_A_REGISTRAR_O_VOTO = "Ocorreu um erro a registrar o voto.";

    public static final String MSG_OCORREU_UM_ERRO_AO_ABRIR_A_VOTACAO = "Ocorreu um erro ao abrir a votação.";

    private final PautaRepostitory pautaRepostitory;

    private final VotacaoRepostitory votacaoRepostitory;

    private final VotoRepository votoRepository;

    private final AssociadoService associadoService;

    public PautaService(PautaRepostitory pautaRepostitory,
                        VotacaoRepostitory votacaoRepostitory,
                        VotoRepository votoRepository,
                        AssociadoService associadoService) {
        this.pautaRepostitory = pautaRepostitory;
        this.votacaoRepostitory = votacaoRepostitory;
        this.votoRepository = votoRepository;
        this.associadoService = associadoService;
    }

    @Transactional(Transactional.TxType.REQUIRED)
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

    @Transactional(Transactional.TxType.REQUIRED)
    public void excluir(Long id) {
        LOGGER.debug("Excluindo pauta ID: {}", id);
        this.pautaRepostitory.deleteById(id);
    }

    @Cacheable(value = "pautaCache")
    public Optional<Pauta> obterPautaEmVotacao(Long id) {
        return pautaRepostitory.findById(id);
    }

    /**
     * Abre uma sessão de votação para uma determinada pauta.
     *
     * @param  idPauta  O ID da pauta
     * @param  votacao  A sessão de votação a ser aberta
     * @return          A sessão de votação aberta
     * @throws ResourceNotFoundException    Se a pauta não for encontrada
     * @throws BusinessException            Se a pauta já tiver uma sessão de votação aberta
     * @throws RuntimeException             Se ocorrer um erro ao salvar a sessão de votação
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Votacao abrirVotacoes(Long idPauta, Votacao votacao) {
        final Pauta pauta = this.obterPautaEmVotacao(idPauta)
                .orElseThrow(() -> new ResourceNotFoundException(PAUTA_NAO_ENCONTRADA));

        if(Objects.nonNull(pauta.getVotacao())) {
            throw new BusinessException("Pauta já possui uma sessão de votação.");
        }

        votacao.setDataAbertura(LocalDateTime.now());
        pauta.setVotacao(votacao);

        if(Objects.isNull(votacao.getDataEncerramento())) {
            votacao.setDataEncerramento(votacao.getDataAbertura().plusMinutes(1));
        }

        if(votacao.getDataAbertura().isAfter(votacao.getDataEncerramento())) {
            throw new BusinessException("Data de abertura deve ser anterior a data de encerramento.");
        }

        try {
            this.votacaoRepostitory.save(votacao);
            this.pautaRepostitory.save(pauta);
        } catch (RuntimeException e) {
            LOGGER.error("Ocorreu um erro ao abrir votação: {}, Erro: {}", votacao, e.getMessage());
            throw new RuntimeException(MSG_OCORREU_UM_ERRO_AO_ABRIR_A_VOTACAO);
        }

        LOGGER.debug("Votação de pauta aberta com sucesso: {}", votacao);

        return votacao;
    }

    /**
     * Efetua o voto para uma determinada sessão de votação.
     *
     * @param  idPauta      O ID da pauta
     * @param  idAssociado  O ID do associado
     * @param  opcao        A opção de voto
     * @throws RuntimeException  Se ocorrer um erro ao salvar a sessão de votação
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void votarPauta(Long idPauta, Long idAssociado, OpcaoVoto opcao) {
        final Pauta pauta = this.obterPautaEmVotacao(idPauta)
                .orElseThrow(() -> new ResourceNotFoundException(PAUTA_NAO_ENCONTRADA));

        final Associado associado = this.associadoService.buscar(idAssociado)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_ASSOCIADO_NAO_ENCONTRADO));

        if(pauta.getVotacao().getDataEncerramento().isBefore(LocalDateTime.now())) {
            throw new BusinessException(MSG_VOTACAO_DA_PAUTA_ENCERRADA);
        }

        if(votoRepository.existsByAssociadoIdAndVotacaoId(associado.getId(), pauta.getVotacao().getId())) {
            throw new BusinessException(MSG_VOTO_JA_REGISTRADO_PARA_ESTA_PAUTA);
        }

        final Voto voto = new Voto(pauta.getVotacao(), associado, opcao);

        try {
            this.votoRepository.save(voto);
        } catch (RuntimeException e) {
            LOGGER.error("Ocorreu um erro ao registrar o voto: {}, Erro: {}", voto, e.getMessage());
            throw new RuntimeException(MSG_OCORREU_UM_ERRO_A_REGISTRAR_O_VOTO);
        }
    }


}
