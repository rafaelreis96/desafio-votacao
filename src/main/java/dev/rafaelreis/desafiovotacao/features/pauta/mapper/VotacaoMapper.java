package dev.rafaelreis.desafiovotacao.features.pauta.mapper;

import dev.rafaelreis.desafiovotacao.features.pauta.dto.CriarVotacaoRequestDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.VotacaoResponseDto;
import dev.rafaelreis.desafiovotacao.model.entity.Votacao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class VotacaoMapper {

    private VotacaoMapper() {

    }

    public static Votacao toEntity(CriarVotacaoRequestDto request) {
        LocalDateTime dataEncerramento = null;

       if(Objects.nonNull(request.getDataEncerramento())) {
           try {
               dataEncerramento = LocalDateTime.parse(request.getDataEncerramento(),
                       DateTimeFormatter.ofPattern(CriarVotacaoRequestDto.PATTERN_DATA));
           } catch (DateTimeParseException e) {
               throw new IllegalArgumentException(CriarVotacaoRequestDto.FORMATO_DATA_INVALIDO);
           }
       }

        return new Votacao(null, dataEncerramento);
    }

    public static VotacaoResponseDto toDto(Votacao votacao) {
        return new VotacaoResponseDto(
                votacao.getId(),
                votacao.getDataAbertura(),
                votacao.getDataEncerramento());
    }
}
