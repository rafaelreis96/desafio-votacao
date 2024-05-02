package dev.rafaelreis.desafiovotacao.features.pauta.mapper;

import dev.rafaelreis.desafiovotacao.features.pauta.dto.CriarVotacaoRequestDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.VotacaoResponseDto;
import dev.rafaelreis.desafiovotacao.model.entity.Votacao;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VotacaoMapperTest {

    @Test
    void deveMapearCriarVotacaoRequestDtoParaEntidade() {
        String dataEncerramentoRequestFmt = LocalDateTime.now().format(DateTimeFormatter.ofPattern(CriarVotacaoRequestDto.PATTERN_DATA));
        CriarVotacaoRequestDto request = new CriarVotacaoRequestDto(dataEncerramentoRequestFmt);

        Votacao votacao = VotacaoMapper.toEntity(request);

        String dataEncerramentoExpected = votacao.getDataEncerramento().format(DateTimeFormatter.ofPattern(CriarVotacaoRequestDto.PATTERN_DATA));

        assertEquals(dataEncerramentoExpected, dataEncerramentoRequestFmt);
    }

    @Test
    void deveMapearEntidadeParaVotacaoResponseDto() {
        Votacao votacao = new Votacao();
        votacao.setId(123L);
        votacao.setDataEncerramento(LocalDateTime.now());

        VotacaoResponseDto dto = VotacaoMapper.toDto(votacao);

        assertEquals(dto.getId(), votacao.getId());
        assertEquals(dto.getDataAbertura(), votacao.getDataAbertura());
        assertEquals(dto.getDataEncerramento(), votacao.getDataEncerramento());
    }

}