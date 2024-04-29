package dev.rafaelreis.desafiovotacao.features.pauta.controller;

import dev.rafaelreis.desafiovotacao.exception.ResourceNotFoundException;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.CriarPautaRequestDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.PautaResponseDto;
import dev.rafaelreis.desafiovotacao.features.pauta.mapper.PautaMapper;
import dev.rafaelreis.desafiovotacao.features.pauta.service.PautaService;
import dev.rafaelreis.desafiovotacao.model.entity.Pauta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE)
public class PautaController {

    private final PautaService pautaService;

    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @Operation(summary = "Cria uma nova Pauta", description = "Este endpoint cria uma nova Pauta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "500", description = "Erro Interno.")
    })
    @PostMapping("/v1/pautas")
    public ResponseEntity<PautaResponseDto> criar(
            @Parameter(description = "Dados da Pauta") @Valid @RequestBody CriarPautaRequestDto request) {
        Pauta pauta = pautaService.criar(PautaMapper.toEntity(request));
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(pauta.getId()).toUri();
        return ResponseEntity.created(uri).body(PautaMapper.toDto(pauta));
    }

    @Operation(summary = "Busca uma Pauta pelo ID", description = "Este endpoint busca uma Pauta pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pauta encontrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada.")
    })
    @GetMapping("/v1/pautas/{id}")
    public ResponseEntity<PautaResponseDto> buscar(
        @Parameter(description = "ID da Pauta") @PathVariable("id") Long id) {
        Pauta pauta = pautaService.buscar(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada."));
        return ResponseEntity.ok(PautaMapper.toDto(pauta));
    }

    @Operation(summary = "Lista todas as Pautas", description = "Este endpoint lista todas as Pautas.")
    @ApiResponse(responseCode = "200", description = "Pautas listadas com sucesso.")
    @GetMapping("/v1/pautas")
    public ResponseEntity<List<PautaResponseDto>> listar() {
        List<Pauta> pautas = pautaService.listar();
        List<PautaResponseDto> pautasResponseDto = pautas.stream()
                .map(PautaMapper::toDto)
                .toList();
        return ResponseEntity.ok(pautasResponseDto);
    }

    @Operation(summary = "Exclui uma Pauta pelo ID", description = "Este endpoint exclui uma Pauta pelo ID.")
    @ApiResponse(responseCode = "204", description = "Pauta excluída com sucesso.")
    @DeleteMapping("/v1/pautas/{id}")
    public ResponseEntity<Void> excluir(@Parameter(description = "ID da Pauta") @PathVariable("id") Long id) {
        pautaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}