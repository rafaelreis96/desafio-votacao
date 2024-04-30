package dev.rafaelreis.desafiovotacao.features.associado.mapper;

import dev.rafaelreis.desafiovotacao.features.associado.dto.AssociadoResponseDto;
import dev.rafaelreis.desafiovotacao.features.associado.dto.CriarAssociadoRequestDto;
import dev.rafaelreis.desafiovotacao.model.entity.Associado;

public class AssociadoMapper {

    private AssociadoMapper() {
    }

    public static Associado toEntity(CriarAssociadoRequestDto request) {
        return new Associado(request.getNome(), request.getCpf());
    }

    public static AssociadoResponseDto toDto(Associado associado) {
        return new AssociadoResponseDto(associado.getId(), associado.getNome(), associado.getCpf());
    }
}
