package dev.rafaelreis.desafiovotacao.features.pauta.repository;

import com.github.javafaker.Faker;
import dev.rafaelreis.desafiovotacao.model.entity.Associado;
import dev.rafaelreis.desafiovotacao.model.entity.Votacao;
import dev.rafaelreis.desafiovotacao.util.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties="queue.voto=fl-voto")
class VotoRepositoryTest {

    @MockBean
    private VotoRepository votoRepository;


    private static Faker faker;

    private Votacao votacao;

    private Associado associado;

    @BeforeAll
    static void beforeAll() {
        faker = TestUtils.fakerBR();
    }

    @BeforeEach
    void setUp() {
        associado = new Associado(faker.name().firstName(), faker.number().digits(11));
        associado.setId(faker.random().nextLong());

        LocalDateTime dataAbertura = LocalDateTime.now();
        votacao = new Votacao(dataAbertura, dataAbertura.plusMinutes(1));
        votacao.setId(faker.random().nextLong());
    }

    @Test
    void deveRetornarVerdadeiroSeVotoExistir() {
        when(votoRepository.existsByAssociadoIdAndVotacaoId(associado.getId(), votacao.getId())).thenReturn(true);

        boolean result = votoRepository.existsByAssociadoIdAndVotacaoId(associado.getId(), votacao.getId());

        assertTrue(result);
        verify(votoRepository, times(1)).existsByAssociadoIdAndVotacaoId(associado.getId(), votacao.getId());
    }

    @Test
    void deveRetornarFalsoSeVotoNaoExistir() {
        when(votoRepository.existsByAssociadoIdAndVotacaoId(associado.getId(), votacao.getId())).thenReturn(false);

        boolean result = votoRepository.existsByAssociadoIdAndVotacaoId(associado.getId(), votacao.getId());

        assertFalse(result);
        verify(votoRepository, times(1)).existsByAssociadoIdAndVotacaoId(associado.getId(), votacao.getId());
    }

}