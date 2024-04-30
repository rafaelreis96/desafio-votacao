package dev.rafaelreis.desafiovotacao.features.associado.controller;

import dev.rafaelreis.desafiovotacao.exception.ResourceNotFoundException;
import dev.rafaelreis.desafiovotacao.features.associado.dto.AssociadoResponseDto;
import dev.rafaelreis.desafiovotacao.features.associado.dto.CriarAssociadoRequestDto;
import dev.rafaelreis.desafiovotacao.features.associado.mapper.AssociadoMapper;
import dev.rafaelreis.desafiovotacao.features.associado.service.AssociadoService;
import dev.rafaelreis.desafiovotacao.model.entity.Associado;
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
public class AssociadoController {

    private final AssociadoService associadoService;

    public AssociadoController(AssociadoService associadoService) {
        this.associadoService = associadoService;
    }

    @Operation(summary = "Cria um novo Associado", description = "Este endpoint cria um novo Associado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Associado criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "500", description = "Erro Interno.")
    })
    @PostMapping("/v1/associados")
    public ResponseEntity<AssociadoResponseDto> criar(
            @Parameter(description = "Dados do Associado") @Valid @RequestBody CriarAssociadoRequestDto request) {
        Associado associado = associadoService.criar(AssociadoMapper.toEntity(request));
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(associado.getId()).toUri();
        return ResponseEntity.created(uri).body(AssociadoMapper.toDto(associado));
    }

    @Operation(summary = "Busca um Associado pelo ID", description = "Este endpoint busca um Associado pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Associado encontrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Associado não encontrado.")
    })
    @GetMapping("/v1/associados/{id}")
    public ResponseEntity<AssociadoResponseDto> buscar(
            @Parameter(description = "ID do Associado") @PathVariable("id") Long id) {
        Associado associado = associadoService.buscar(id)
                .orElseThrow(() -> new ResourceNotFoundException("Associado não encontrado."));
        return ResponseEntity.ok(AssociadoMapper.toDto(associado));
    }

    @Operation(summary = "Lista todos os Associados", description = "Este endpoint lista todos os Associados.")
    @ApiResponse(responseCode = "200", description = "Associados listados com sucesso.")
    @GetMapping("/v1/associados")
    public ResponseEntity<List<AssociadoResponseDto>> listar() {
        List<Associado> associados = associadoService.listar();
        List<AssociadoResponseDto> associadosResponseDto = associados.stream()
                .map(AssociadoMapper::toDto)
                .toList();
        return ResponseEntity.ok(associadosResponseDto);
    }

    @Operation(summary = "Exclui um Associado pelo ID", description = "Este endpoint exclui um Associado pelo ID.")
    @ApiResponse(responseCode = "204", description = "Associado excluída com sucesso.")
    @DeleteMapping("/v1/associados/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID do Associado") @PathVariable("id") Long id) {
        associadoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}