package dev.rafaelreis.desafiovotacao.features.associado.mapper;

import com.github.javafaker.Faker;
import dev.rafaelreis.desafiovotacao.features.associado.dto.AssociadoResponseDto;
import dev.rafaelreis.desafiovotacao.features.associado.dto.CriarAssociadoRequestDto;
import dev.rafaelreis.desafiovotacao.model.entity.Associado;
import dev.rafaelreis.desafiovotacao.util.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssociadoMapperTest {

    private static Faker faker;

    @BeforeAll
    static void beforeAll() {
        faker = TestUtils.fakerBR();
    }

    @Test
    void deveMapearCriarAssociadoRequestDtoParaEntidade() {
        CriarAssociadoRequestDto request =
                new CriarAssociadoRequestDto(faker.name().firstName(), faker.number().digits(11));

        Associado associado = AssociadoMapper.toEntity(request);

        assertEquals(request.getNome(), associado.getNome());
        assertEquals(request.getCpf(), associado.getCpf());
    }

    @Test
    void deveMapearEntidadeParaAssociadoResponseDto() {
        Associado associado = new Associado(faker.name().firstName(), faker.number().digits(11));
        associado.setId(123L);

        AssociadoResponseDto responseDto = AssociadoMapper.toDto(associado);

        assertEquals(associado.getId(), responseDto.getId());
        assertEquals(associado.getNome(), responseDto.getNome());
        assertEquals(associado.getCpf(), responseDto.getCpf());
    }

}