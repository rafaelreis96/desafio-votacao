package dev.rafaelreis.desafiovotacao.features.pauta.service;

import dev.rafaelreis.desafiovotacao.exception.BusinessException;
import dev.rafaelreis.desafiovotacao.exception.ResourceNotFoundException;
import dev.rafaelreis.desafiovotacao.features.associado.service.AssociadoService;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.VotoDto;
import dev.rafaelreis.desafiovotacao.features.pauta.mq.VotacaoProducer;
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

    public static final String MSG_DATA_DE_ABERTURA_DEVE_SER_ANTERIOR_A_DATA_DE_ENCERRAMENTO = "Data de abertura deve ser anterior a data de encerramento.";

    public static final String MSG_VOTO_JA_REGISTRADO_PARA_ESTA_PAUTA = "Voto já registrado para esta pauta.";

    public static final String MSG_VOTACAO_DA_PAUTA_ENCERRADA = "Votação da pauta encerrada.";

    public static final String MSG_VOTACAO_DA_PAUTA_NAO_INICIADA = "Pauta ainda não possui uma sessão de votação.";

    public static final String MSG_ASSOCIADO_NAO_ENCONTRADO = "Associado não encontrado.";

    public static final String PAUTA_NAO_ENCONTRADA = "Pauta não encontrada.";

    public static final String MSG_OCORREU_UM_ERRO_A_REGISTRAR_O_VOTO = "Ocorreu um erro a registrar o voto.";

    public static final String MSG_OCORREU_UM_ERRO_AO_ABRIR_A_VOTACAO = "Ocorreu um erro ao abrir a votação.";

    public static final String MSG_PAUTA_JA_POSSUI_UMA_SESSAO_DE_VOTACAO = "Pauta já possui uma sessão de votação.";

    private final PautaRepostitory pautaRepostitory;

    private final VotacaoRepostitory votacaoRepostitory;

    private final VotoRepository votoRepository;

    private final AssociadoService associadoService;

    private final VotacaoProducer votoProdutor;

    public PautaService(PautaRepostitory pautaRepostitory,
                        VotacaoRepostitory votacaoRepostitory,
                        VotoRepository votoRepository,
                        AssociadoService associadoService,
                        VotacaoProducer votoProdutor) {
        this.pautaRepostitory = pautaRepostitory;
        this.votacaoRepostitory = votacaoRepostitory;
        this.votoRepository = votoRepository;
        this.associadoService = associadoService;
        this.votoProdutor = votoProdutor;
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
            throw new BusinessException(MSG_PAUTA_JA_POSSUI_UMA_SESSAO_DE_VOTACAO);
        }

        votacao.setDataAbertura(LocalDateTime.now());
        pauta.setVotacao(votacao);

        if(Objects.isNull(votacao.getDataEncerramento())) {
            votacao.setDataEncerramento(votacao.getDataAbertura().plusMinutes(1));
        }


        if(votacao.getDataAbertura().isAfter(votacao.getDataEncerramento())) {
            throw new BusinessException(MSG_DATA_DE_ABERTURA_DEVE_SER_ANTERIOR_A_DATA_DE_ENCERRAMENTO);
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
    public void votarPauta(Long idPauta, Long idAssociado, OpcaoVoto opcao) {
        final VotoDto votoDto = new VotoDto(idPauta, idAssociado, opcao, LocalDateTime.now());
        this.computarVoto(votoDto);
    }

    /**
     * Efetua o voto para uma determinada sessão de votação de forma assíncrona.
     *
     * @param  idPauta      O ID da pauta
     * @param  idAssociado  O ID do associado
     * @param  opcao        A opção de voto
     * @throws RuntimeException  Se ocorrer um erro ao salvar a sessão de votação
     */
    public void votarPautaAsync(Long idPauta, Long idAssociado, OpcaoVoto opcao) {
        final VotoDto votoDto = new VotoDto(idPauta, idAssociado, opcao, LocalDateTime.now());
        this.votoProdutor.enviar(votoDto);
    }

    /**
     * Computa o voto para uma determinada sessão de votação.
     *
     * @param  votoDto  o objeto de transferência de dados do voto contendo o ID da sessão de votação,
     *                  o ID do associado, a opção de voto e a data de registro
     * @throws ResourceNotFoundException    se a sessão de votação ou o associado não for encontrado
     * @throws BusinessException            se a sessão de votação para a pauta não tiver iniciado ou se já tiver
     *                                      encerrado, ou se o voto já tiver sido registrado para esta pauta
     * @throws RuntimeException             se ocorrer um erro ao registrar o voto
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void computarVoto(VotoDto votoDto) {
        final Pauta pauta = this.obterPautaEmVotacao(votoDto.getIdPauta())
                .orElseThrow(() -> new ResourceNotFoundException(PAUTA_NAO_ENCONTRADA));

        if(Objects.isNull(pauta.getVotacao())) {
            throw new BusinessException(MSG_VOTACAO_DA_PAUTA_NAO_INICIADA);
        }

        if(pauta.getVotacao().getDataEncerramento().isBefore(LocalDateTime.now())) {
            throw new BusinessException(MSG_VOTACAO_DA_PAUTA_ENCERRADA);
        }

        final Associado associado = this.associadoService.buscar(votoDto.getIdAssociado())
                .orElseThrow(() -> new ResourceNotFoundException(MSG_ASSOCIADO_NAO_ENCONTRADO));

        if(votoRepository.existsByAssociadoIdAndVotacaoId(associado.getId(), pauta.getVotacao().getId())) {
            throw new BusinessException(MSG_VOTO_JA_REGISTRADO_PARA_ESTA_PAUTA);
        }

        final Voto voto = new Voto(pauta.getVotacao(), associado, votoDto.getOpcao(), votoDto.getDataRegistro());

        try {
            this.votoRepository.save(voto);
        } catch (RuntimeException e) {
            LOGGER.error("Ocorreu um erro ao registrar o voto: {}, Erro: {}", voto, e.getMessage());
            throw new RuntimeException(MSG_OCORREU_UM_ERRO_A_REGISTRAR_O_VOTO);
        }
    }

}
