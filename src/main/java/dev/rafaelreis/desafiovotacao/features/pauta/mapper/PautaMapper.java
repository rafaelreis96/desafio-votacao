package dev.rafaelreis.desafiovotacao.features.pauta.mapper;

import dev.rafaelreis.desafiovotacao.features.pauta.dto.PautaResponseDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.CriarPautaRequestDto;
import dev.rafaelreis.desafiovotacao.model.entity.Pauta;

public class PautaMapper {

    private PautaMapper() {

    }

    public static PautaResponseDto toDto(Pauta pauta) {
        return new PautaResponseDto(pauta.getId(), pauta.getTitulo(), pauta.getDescricao());
    }

    public static Pauta toEntity(CriarPautaRequestDto request) {
        return new Pauta(request.getTitulo(), request.getDescricao());
    }


}
