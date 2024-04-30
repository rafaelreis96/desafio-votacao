package dev.rafaelreis.desafiovotacao.features.associado.service;

import com.github.javafaker.Faker;
import dev.rafaelreis.desafiovotacao.features.associado.repository.AssociadoRepository;
import dev.rafaelreis.desafiovotacao.model.entity.Associado;
import dev.rafaelreis.desafiovotacao.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
class AssociadoServiceTest {

    @Mock
    private AssociadoRepository associadoRepository;

    @InjectMocks
    private AssociadoService associadoService;

    private static Faker faker;

    private Associado associado;

    @BeforeAll
    static void beforeAll() {
        faker = TestUtils.fakerBR();
    }

    @BeforeEach
    void setUp() {
        associado = new Associado(faker.name().firstName(), faker.number().digits(11));
        associado.setId(faker.random().nextLong());
    }

    @Test
    void deverCriarAssociado() {
        when(associadoRepository.save(associado)).thenReturn(associado);

        Associado result = associadoService.criar(associado);

        Assertions.assertNotNull(result);

        verify(associadoRepository, times(1)).save(associado);
    }

    @Test
    void deverRetornarPautaPeloId() {
        when(associadoRepository.findById(associado.getId())).thenReturn(Optional.of(associado));

        Optional<Associado> result = associadoService.buscar(associado.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(associado.getId(), result.get().getId());

        verify(associadoRepository, times(1)).findById(associado.getId());
    }

    @Test
    void deverRetornarUmaListaDePautas() {
        List<Associado> associados = List.of(associado);

        when(associadoRepository.findAll()).thenReturn(associados);

        List<Associado> result = associadoService.listar();
        Assertions.assertEquals(associados.size(), result.size());

        verify(associadoRepository, times(1)).findAll();
    }

    @Test
    void deveExcluirPautaPeloId() {
        associadoService.excluir(associado.getId());
        verify(associadoRepository, times(1)).deleteById(associado.getId());
    }
}