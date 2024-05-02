package dev.rafaelreis.desafiovotacao.features.pauta.integracao;

import com.github.javafaker.Faker;
import dev.rafaelreis.desafiovotacao.features.associado.dto.AssociadoResponseDto;
import dev.rafaelreis.desafiovotacao.features.associado.dto.CriarAssociadoRequestDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.*;
import dev.rafaelreis.desafiovotacao.model.enums.OpcaoVoto;
import dev.rafaelreis.desafiovotacao.util.TestUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment =  WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
class PautaTestIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static Faker faker;

    private static PautaResponseDto pautaResponseDto;

    private static AssociadoResponseDto associadoResponseDto;

    private static VotacaoResponseDto votacaoResponseDto;

    @BeforeAll
    static void beforeAll() {
        faker = TestUtils.fakerBR();
    }

    @Test
    @Order(1)
    void deveCriarPauta() {
        CriarPautaRequestDto request = new CriarPautaRequestDto(faker.lorem().sentence(), faker.lorem().paragraph());

        HttpEntity<CriarPautaRequestDto> httpEntity = new HttpEntity<>(request);

        ResponseEntity<PautaResponseDto> response = testRestTemplate
                .exchange("/api/v1/pautas", HttpMethod.POST, httpEntity, PautaResponseDto.class);

        pautaResponseDto = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(response.getHeaders().getLocation().getPath(), "/api/v1/pautas/" + pautaResponseDto.getId());
        assertEquals(pautaResponseDto.getTitulo(), request.getTitulo());
        assertEquals(pautaResponseDto.getDescricao(), request.getDescricao());
    }

    @Test
    @Order(2)
    void deveRecuperarPautaPeloId() {
        ResponseEntity<PautaResponseDto> response = testRestTemplate
                .exchange("/api/v1/pautas/" + pautaResponseDto.getId(), HttpMethod.GET, null, PautaResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getId(), pautaResponseDto.getId());
        assertEquals(response.getBody().getTitulo(), pautaResponseDto.getTitulo());
        assertEquals(response.getBody().getDescricao(), pautaResponseDto.getDescricao());
    }

    @Test
    @Order(3)
    void deveRecuperarPautas() {
        ResponseEntity<PautaResponseDto[]> response = testRestTemplate
                .exchange("/api/v1/pautas", HttpMethod.GET, null, PautaResponseDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Disabled
    @Order(4)
    void deveExcluirPauta() {
        ResponseEntity<Void> response = testRestTemplate
                .exchange("/api/v1/pautas/" + pautaResponseDto.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(5)
    void deveRetornar404QuandoRecuperarPautaPeloIdInexistente() {
        ResponseEntity<PautaResponseDto> response = testRestTemplate
                .exchange("/api/v1/pautas/" + faker.number().randomNumber(), HttpMethod.GET, null, PautaResponseDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(6)
    void deveAbrirVotacaoDePauta() {
        String dataEncerramento = LocalDateTime.now()
                .plusMinutes(30)
                .format(DateTimeFormatter.ofPattern(CriarVotacaoRequestDto.PATTERN_DATA));
        CriarVotacaoRequestDto request = new CriarVotacaoRequestDto(dataEncerramento);

        ResponseEntity<VotacaoResponseDto> response = testRestTemplate
                .exchange("/api/v1/pautas/" + pautaResponseDto.getId() + "/votacoes", HttpMethod.POST, new HttpEntity<>(request), VotacaoResponseDto.class);

        votacaoResponseDto = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(response.getHeaders().getLocation().getPath(), "/api/v1/pautas/" +  pautaResponseDto.getId() + "/votacoes/" + votacaoResponseDto.getId());
        assertTrue(votacaoResponseDto.getDataAbertura().isBefore(votacaoResponseDto.getDataEncerramento()));
    }

    @Test
    @Order(7)
    void deveRetornar400QuandoTentarAbrirVotacaoDePautaMaisDeUmaVez() {
        String dataEncerramento = LocalDateTime.now()
                .plusMinutes(30)
                .format(DateTimeFormatter.ofPattern(CriarVotacaoRequestDto.PATTERN_DATA));
        CriarVotacaoRequestDto request = new CriarVotacaoRequestDto(dataEncerramento);

        ResponseEntity<VotacaoResponseDto> response = testRestTemplate
                .exchange("/api/v1/pautas/" + pautaResponseDto.getId() + "/votacoes", HttpMethod.POST, new HttpEntity<>(request), VotacaoResponseDto.class);

        votacaoResponseDto = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(8)
    void deveCriarUmAssociadoParaVotar() {
        CriarAssociadoRequestDto request = new CriarAssociadoRequestDto(faker.name().firstName(), faker.number().digits(11));

        HttpEntity<CriarAssociadoRequestDto> httpEntity = new HttpEntity<>(request);

        ResponseEntity<AssociadoResponseDto> response = testRestTemplate
                .exchange("/api/v1/associados", HttpMethod.POST, httpEntity, AssociadoResponseDto.class);

        associadoResponseDto = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(response.getHeaders().getLocation().getPath(), "/api/v1/associados/" + associadoResponseDto.getId());
        assertEquals(associadoResponseDto.getNome(), request.getNome());
        assertEquals(associadoResponseDto.getCpf(), request.getCpf());
    }

    @Test
    @Order(9)
    void deveVotarNaPauta() {
        CriarVotoRequestDto request = new CriarVotoRequestDto(associadoResponseDto.getId(), OpcaoVoto.NAO);

        HttpEntity<CriarVotoRequestDto> httpEntity = new HttpEntity<>(request);

        ResponseEntity<Void> response = testRestTemplate
                .exchange("/api/v1/pautas/" + pautaResponseDto.getId() + "/votos", HttpMethod.POST, httpEntity, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(10)
    void deverRetornar400QuandoTentarVotarMaisDeUmaVez() {
        CriarVotoRequestDto request = new CriarVotoRequestDto(associadoResponseDto.getId(), OpcaoVoto.NAO);

        HttpEntity<CriarVotoRequestDto> httpEntity = new HttpEntity<>(request);

        ResponseEntity<Void> response = testRestTemplate
                .exchange("/api/v1/pautas/" + pautaResponseDto.getId() + "/votos", HttpMethod.POST, httpEntity, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
