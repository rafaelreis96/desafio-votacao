package dev.rafaelreis.desafiovotacao.features.pauta.service;

import com.github.javafaker.Faker;
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
import dev.rafaelreis.desafiovotacao.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class PautaServiceTest {

    @Mock
    private PautaRepostitory pautaRepository;

    @Mock
    private VotacaoRepostitory votacaoRepository;

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private PautaService pautaService;

    @Mock
    private AssociadoService associadoService;

    private static Faker faker;

    private Pauta pauta;

    private Votacao votacao;

    private Associado associado;

    private Voto voto;

    @BeforeAll
    static void beforeAll() {
        faker = TestUtils.fakerBR();
    }

    @BeforeEach
    void setUp() {
        pauta = new Pauta(faker.lorem().sentence(), faker.lorem().paragraph());
        pauta.setId(faker.random().nextLong());

        associado = new Associado(faker.name().firstName(), faker.number().digits(11));
        associado.setId(faker.random().nextLong());

        LocalDateTime dataAbertura = LocalDateTime.now();
        votacao = new Votacao(dataAbertura, dataAbertura.plusMinutes(1));
        votacao.setId(faker.random().nextLong());

        voto = new Voto(votacao, associado, OpcaoVoto.SIM);
        voto.setId(faker.random().nextLong());
    }

    @Test
    void deverCriarPauta() {
        when(pautaRepository.save(pauta)).thenReturn(pauta);

        Pauta result = pautaService.criar(pauta);

        Assertions.assertNotNull(result);

        verify(pautaRepository, times(1)).save(pauta);
    }

    @Test
    void deveRetornarPautaPeloId() {
        when(pautaRepository.findById(pauta.getId())).thenReturn(Optional.of(pauta));

        Optional<Pauta> result = pautaService.buscar(pauta.getId());

        Assertions.assertTrue(result.isPresent());
        assertEquals(pauta.getId(), result.get().getId());

        verify(pautaRepository, times(1)).findById(pauta.getId());
    }

    @Test
    void deveRetornarUmaListaDePautas() {
        List<Pauta> pautas = List.of(pauta);

        when(pautaRepository.findAll()).thenReturn(pautas);

        List<Pauta> result = pautaService.listar();
        assertEquals(pautas.size(), result.size());

        verify(pautaRepository, times(1)).findAll();
    }

    @Test
    void deveExcluirPautaPeloId() {
        pautaService.excluir(pauta.getId());
        verify(pautaRepository, times(1)).deleteById(pauta.getId());
    }

    @Test
    void deveImpedirAberturaDeVotacaoELancarResourceNotFoundExceptionQuandoNaoExistirPautaInformada() {
        when(pautaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> pautaService.abrirVotacoes(1L, votacao));

        verify(pautaRepository, times(0)).save(pauta);
    }

    @Test
    void deveImpedirAberturaDeVotacaoELancarBusinessExceptionQuandoPautaJaPossuirVotacao() {
        pauta.setVotacao(votacao);

        when(pautaRepository.findById(anyLong())).thenReturn(Optional.of(pauta));

        when(pautaService.obterPautaEmVotacao(anyLong())).thenReturn(Optional.of(pauta));

        assertThrows(BusinessException.class,
                () -> pautaService.abrirVotacoes(1L, votacao));

        verify(pautaRepository, times(1)).findById(anyLong());
    }

    @Test
    void deveDefinirDatasDeAberturaEncerramentoQuandoAbrirVotacaoEAmbasForemNulas() {
        votacao.setDataAbertura(null);
        votacao.setDataEncerramento(null);

        when(pautaRepository.findById(votacao.getId())).thenReturn(Optional.of(pauta));

        Votacao result = pautaService.abrirVotacoes(votacao.getId(), votacao);

        assertNotNull(result.getDataAbertura());
        assertNotNull(result.getDataEncerramento());
        assertTrue(result.getDataEncerramento().isAfter(result.getDataAbertura()));

        verify(pautaRepository, times(1)).save(pauta);
    }

    @Test
    void deveDefinirDataEncerramentoUmMinutoAposDataDeAberturaQuandoNaoInformada() {
        votacao.setDataEncerramento(null);

        when(pautaRepository.findById(votacao.getId())).thenReturn(Optional.of(pauta));

        Votacao result = pautaService.abrirVotacoes(votacao.getId(), votacao);

        assertNotNull(result.getDataEncerramento());
        assertTrue(result.getDataEncerramento().isAfter(result.getDataAbertura()));
        assertEquals(result.getDataEncerramento(), result.getDataAbertura().plusMinutes(1));

        verify(pautaRepository, times(1)).save(pauta);
    }

    @Test
    void deveLancarRuntimeExceptionQuandoOcorrerErroAoSalvarVotacao() {
        when(pautaRepository.findById(votacao.getId())).thenReturn(Optional.of(pauta));

        doThrow(new RuntimeException()).when(votacaoRepository).save(votacao);

        assertThrows(RuntimeException.class, () -> pautaService.abrirVotacoes(1L, votacao));

        verify(pautaRepository, times(1)).findById(anyLong());
        verify(votacaoRepository, never()).save(votacao);
    }

    @Test
    void deveRealizarVoto() {
        pauta.setVotacao(votacao);

        when(pautaRepository.findById(anyLong())).thenReturn(Optional.of(pauta));

        when(associadoService.buscar(anyLong())).thenReturn(Optional.of(associado));

        when(votoRepository.save(voto)).thenReturn(any(Voto.class));

        assertDoesNotThrow(() -> pautaService.votarPauta(1L, 1L, OpcaoVoto.SIM));

        verify(pautaRepository, times(1)).findById(anyLong());
        verify(votoRepository, times(1)).save(any(Voto.class));
    }

    @Test
    void deveImpedirAssociadoDeVotarQuandoJaTiverVotado() {
        pauta.setVotacao(votacao);

        when(pautaRepository.findById(anyLong())).thenReturn(Optional.of(pauta));

        when(votoRepository.existsByAssociadoIdAndVotacaoId(anyLong(), anyLong())).thenReturn(true);

        when(associadoService.buscar(anyLong())).thenReturn(Optional.of(associado));

        assertThrows(BusinessException.class, () -> pautaService.votarPauta(1L, 1L, OpcaoVoto.SIM));

        verify(pautaRepository, times(1)).findById(anyLong());
        verify(votoRepository, times(1)).existsByAssociadoIdAndVotacaoId(anyLong(), anyLong());
        verify(associadoService, times(1)).buscar(anyLong());
    }

    @Test
    void deveImpedirVotoELancarResourceNotFoundExceptionQuandoPautaEmVotacaoNaoExistir() {
        when(pautaService.obterPautaEmVotacao(anyLong())).thenReturn(Optional.empty());

        when(votoRepository.save(voto)).thenReturn(any(Voto.class));

        assertThrows(ResourceNotFoundException.class, () -> pautaService.votarPauta(1L, 1L, OpcaoVoto.SIM));

        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    void deveImpedirVotoELancarBusinessExceptionQuandoVotacaoEstiverEncerrada() {
        pauta.setVotacao(votacao);
        votacao.setDataEncerramento(votacao.getDataEncerramento().minusHours(1));

        when(pautaRepository.findById(anyLong())).thenReturn(Optional.of(pauta));

        when(associadoService.buscar(anyLong())).thenReturn(Optional.of(associado));

        assertThrows(BusinessException.class, () -> pautaService.votarPauta(1L, 1L, OpcaoVoto.SIM));

        when(pautaRepository.findById(anyLong())).thenReturn(Optional.of(pauta));
        verify(associadoService, times(1)).buscar(anyLong());
    }

    @Test
    void deveLancarRuntimeExceptionQuandoOcorrerErroAoSalvarVoto() {
        when(pautaRepository.findById(anyLong())).thenReturn(Optional.of(pauta));

        when(associadoService.buscar(anyLong())).thenReturn(Optional.of(associado));

        doThrow(new RuntimeException()).when(votoRepository).save(any(Voto.class));

        assertThrows(RuntimeException.class, () -> pautaService.votarPauta(1L, 1L, OpcaoVoto.SIM));

        verify(pautaRepository, times(1)).findById(anyLong());
        verify(associadoService, times(1)).buscar(anyLong());
        verify(votoRepository, never()).save(any(Voto.class));
    }

}