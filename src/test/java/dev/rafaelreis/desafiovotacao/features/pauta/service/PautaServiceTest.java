package dev.rafaelreis.desafiovotacao.features.pauta.service;

import com.github.javafaker.Faker;
import dev.rafaelreis.desafiovotacao.features.pauta.repository.PautaRepostitory;
import dev.rafaelreis.desafiovotacao.model.entity.Pauta;
import dev.rafaelreis.desafiovotacao.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
class PautaServiceTest {

    @Mock
    private PautaRepostitory pautaRepostitory;

    @InjectMocks
    private PautaService pautaService;

    private static Faker faker;

    private Pauta pauta;

    @BeforeAll
    static void beforeAll() {
        faker = TestUtils.fakerBR();
    }

    @BeforeEach
    void setUp() {
        pauta = new Pauta(faker.lorem().sentence(), faker.lorem().paragraph());
        pauta.setId(faker.random().nextLong());
    }

    void deverCriarPauta() {
        when(pautaRepostitory.save(pauta)).thenReturn(pauta);

        Pauta result = pautaService.criar(pauta);

        Assertions.assertNotNull(result);

        verify(pautaRepostitory, times(1)).save(pauta);
    }

    @Test
    void deverRetornarPautaPeloId() {
        when(pautaRepostitory.findById(pauta.getId())).thenReturn(Optional.of(pauta));

        Optional<Pauta> result = pautaService.buscar(pauta.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(pauta.getId(), result.get().getId());

        verify(pautaRepostitory, times(1)).findById(pauta.getId());
    }

    @Test
    void deverRetornarUmaListaDePautas() {
        List<Pauta> pautas = List.of(pauta);

        when(pautaRepostitory.findAll()).thenReturn(pautas);

        List<Pauta> result = pautaService.listar();
        Assertions.assertEquals(pautas.size(), result.size());

        verify(pautaRepostitory, times(1)).findAll();
    }

    @Test
    void deveExcluirPautaPeloId() {
        pautaService.excluir(pauta.getId());
        verify(pautaRepostitory, times(1)).deleteById(pauta.getId());
    }
}