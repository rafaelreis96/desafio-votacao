package dev.rafaelreis.desafiovotacao.features.pauta.mapper;

import dev.rafaelreis.desafiovotacao.features.associado.mapper.AssociadoMapper;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.CriarPautaRequestDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.PautaResponseDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.VotacaoResponseDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.VotoResponseDto;
import dev.rafaelreis.desafiovotacao.model.entity.Pauta;

import java.util.List;
import java.util.Objects;

public class PautaMapper {

    private PautaMapper() {

    }

    public static PautaResponseDto toDto(Pauta pauta) {
        VotacaoResponseDto votacaoResponseDto = null;
        List<VotoResponseDto> votos = null;

        if(Objects.nonNull(pauta.getVotacao())) {
            votacaoResponseDto = VotacaoMapper.toDto(pauta.getVotacao());

            if(Objects.nonNull(pauta.getVotacao().getVotos())) {
                votos = pauta.getVotacao().getVotos().stream()
                        .map( v -> new VotoResponseDto(v.getId(), AssociadoMapper.toDto(v.getAssociado()), v.getOpcao()))
                        .toList();
            }

            votacaoResponseDto.setVotos(votos);
        }

        PautaResponseDto pautaResponseDto = new PautaResponseDto(pauta.getId(), pauta.getTitulo(), pauta.getDescricao());
        pautaResponseDto.setVotacao(votacaoResponseDto);
        return pautaResponseDto;
    }

    public static Pauta toEntity(CriarPautaRequestDto request) {
        return new Pauta(request.getTitulo(), request.getDescricao());
    }


}
