package dev.rafaelreis.desafiovotacao.features.associado.repository;

import com.github.javafaker.Faker;
import dev.rafaelreis.desafiovotacao.model.entity.Associado;
import dev.rafaelreis.desafiovotacao.util.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties="queue.voto=fl-voto")
class AssociadoRepositoryTest {

    @MockBean
    private AssociadoRepository associadoRepository;

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
    void deveRetornarVerdadeiroQuandoCpfJaExiste() {
        when(associadoRepository.existsByCpf(associado.getCpf())).thenReturn(true);

        boolean result = associadoRepository.existsByCpf(associado.getCpf());

        assertTrue(result);

        verify(associadoRepository).existsByCpf(associado.getCpf());
    }

    @Test
    void deveRetornarFalsoQuandoCpfNaoExiste() {
        when(associadoRepository.existsByCpf(associado.getCpf())).thenReturn(true);

        boolean result = associadoRepository.existsByCpf(associado.getCpf());

        assertTrue(result);

        verify(associadoRepository).existsByCpf(associado.getCpf());
    }
}