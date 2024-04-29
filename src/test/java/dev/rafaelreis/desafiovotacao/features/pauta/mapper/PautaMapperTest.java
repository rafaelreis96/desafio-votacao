package dev.rafaelreis.desafiovotacao.features.pauta.mapper;

import com.github.javafaker.Faker;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.CriarPautaRequestDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.PautaResponseDto;
import dev.rafaelreis.desafiovotacao.model.entity.Pauta;
import dev.rafaelreis.desafiovotacao.util.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PautaMapperTest {

    private static Faker faker;

    @BeforeAll
    static void beforeAll() {
        faker = TestUtils.fakerBR();
    }

    @Test
    void deveMapearEntidadeParaPautaResponseDto() {
        Pauta pauta = new Pauta(faker.lorem().sentence(), faker.lorem().paragraph());
        pauta.setId(123L);

        PautaResponseDto dto = PautaMapper.toDto(pauta);

        assertEquals(dto.getId(), pauta.getId());
        assertEquals(dto.getTitulo(), pauta.getTitulo());
        assertEquals(dto.getDescricao(), pauta.getDescricao());
    }

    @Test
    void deveMapearCriarPautaRequestDtoParaEntidade() {
        CriarPautaRequestDto request = new CriarPautaRequestDto(faker.lorem().sentence(), faker.lorem().paragraph());

        Pauta pauta = PautaMapper.toEntity(request);

        assertNull(pauta.getId());
        assertEquals(pauta.getTitulo(), request.getTitulo());
        assertEquals(pauta.getDescricao(), request.getDescricao());
    }
}