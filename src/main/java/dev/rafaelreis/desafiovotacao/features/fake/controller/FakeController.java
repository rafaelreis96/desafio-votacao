package dev.rafaelreis.desafiovotacao.features.fake.controller;

import com.github.javafaker.Faker;
import dev.rafaelreis.desafiovotacao.features.fake.dto.CpfValidoResponseDto;
import dev.rafaelreis.desafiovotacao.model.enums.OpcaoVoto;
import dev.rafaelreis.desafiovotacao.model.enums.StatusVoto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("api")
public class FakeController {

    public static final String TEXT_CSV_CHARSET_UTF_8 = "text/csv; charset=utf-8";

    private final Faker faker;

    public FakeController() {
        this.faker = new Faker(new Locale("pt-BR"));
    }

    @Operation(summary = "CPF aleatório", description = "Valida um CPF aleatóriamente.")
    @ApiResponse(responseCode = "200", description = "CPF validade com sucesso.")
    @GetMapping(value = "/v1/fake/cpf-aleatorio/{cpf}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CpfValidoResponseDto> gerarMassaPautas(@PathVariable("cpf") String cpf) {
        StatusVoto statusVoto = (faker.random().nextInt(0, 1) > 0) ? StatusVoto.ABLE_TO_VOTE : StatusVoto.UNABLE_TO_VOTE;
        CpfValidoResponseDto response = new CpfValidoResponseDto(statusVoto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Gera massa de associados", description = "Gera dados fake de associados.")
    @ApiResponse(responseCode = "200", description = "Massa de associados gerada com sucesso.")
    @GetMapping(value = "/v1/fake/associados/{quantidade}", produces = "text/csv")
    public StringBuilder gerarMassaAssociados(
            @PathVariable(value = "quantidade") int quantidade,
            HttpServletResponse response) {
        response.setContentType(TEXT_CSV_CHARSET_UTF_8);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=associados.csv");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < quantidade; i++) {
            sb.append(faker.name().firstName())
                    .append(";")
                    .append(faker.number().digits(11))
                    .append(System.lineSeparator());
        }

        return sb;
    }

    @Operation(summary = "Gera massa de pautas", description = "Gera dados fake de pautas.")
    @ApiResponse(responseCode = "200", description = "Massa de pautas gerada com sucesso.")
    @GetMapping(value = "/v1/fake/pautas/{quantidade}", produces = "text/csv")
    public StringBuilder gerarMassaPautas(
            @PathVariable(value = "quantidade") int quantidade,
            HttpServletResponse response) {
        response.setContentType(TEXT_CSV_CHARSET_UTF_8);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pautas.csv");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < quantidade; i++) {
            sb.append(faker.lorem().sentence())
                    .append(";")
                    .append(faker.lorem().paragraph())
                    .append(System.lineSeparator());
        }

        return sb;
    }

    @Operation(summary = "Gera massa de votos", description = "Gera dados fake de votos.")
    @ApiResponse(responseCode = "200", description = "Massa de votos gerada com sucesso.")
    @GetMapping(value = "/v1/fake/votos/{quantidade}", produces = "text/csv")
    public StringBuilder gerarMassaVotos(
            @PathVariable(value = "quantidade") int quantidade,
            HttpServletResponse response) {
        response.setContentType(TEXT_CSV_CHARSET_UTF_8);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=votos.csv");

        StringBuilder sb = new StringBuilder();

        for (long i = 1; i <= quantidade; i++) {
            OpcaoVoto opcao = (faker.random().nextInt(0, 1) > 0) ? OpcaoVoto.SIM : OpcaoVoto.NAO;
            sb.append(i).append(";").append(opcao).append(System.lineSeparator());
        }

        return sb;
    }

}
