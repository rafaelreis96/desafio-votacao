package dev.rafaelreis.desafiovotacao.features.pauta.controller;

import com.github.javafaker.Faker;
import dev.rafaelreis.desafiovotacao.exception.BusinessException;
import dev.rafaelreis.desafiovotacao.exception.ResourceNotFoundException;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.CriarPautaRequestDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.CriarVotacaoRequestDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.CriarVotoRequestDto;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.VotacaoResponseDto;
import dev.rafaelreis.desafiovotacao.features.pauta.mapper.PautaMapper;
import dev.rafaelreis.desafiovotacao.features.pauta.mapper.VotacaoMapper;
import dev.rafaelreis.desafiovotacao.features.pauta.service.PautaService;
import dev.rafaelreis.desafiovotacao.model.entity.Pauta;
import dev.rafaelreis.desafiovotacao.model.entity.Votacao;
import dev.rafaelreis.desafiovotacao.model.enums.OpcaoVoto;
import dev.rafaelreis.desafiovotacao.util.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties="queue.voto=fl-voto")
@AutoConfigureMockMvc
class PautaControllerTest {

    ///@LocalServerPort
    //private int port;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PautaService pautaService;

    private static Faker faker;

    @BeforeAll
    static void beforeAll() {
        faker = TestUtils.fakerBR();
    }

    @Test
    void deverCriarUmaPauta() throws Exception {
        CriarPautaRequestDto request = new CriarPautaRequestDto(faker.lorem().sentence(), faker.lorem().paragraph());

        Pauta pauta = PautaMapper.toEntity(request);
        pauta.setId(123L);

        when(pautaService.criar(any(Pauta.class))).thenReturn(pauta);

        this.mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtils.toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(pautaService, times(1)).criar(any(Pauta.class));
    }

    @Test
    void deverRetornar400QuandoDadosDaPautaForemInvalidos() throws Exception {

        Map<CriarPautaRequestDto, ResultMatcher> testes = Map.of(
            new CriarPautaRequestDto(faker.lorem().sentence(), faker.lorem().fixedString(513)),
            jsonPath("$.errors.[*].message", hasItem(CriarPautaRequestDto.DESCRICAO_INVALIDO)),

            new CriarPautaRequestDto(faker.lorem().sentence(253), faker.lorem().fixedString(20)),
            jsonPath("$.errors.[*].message", hasItem(CriarPautaRequestDto.TITULO_INVALIDO)),

            new CriarPautaRequestDto( "", faker.lorem().fixedString(20)),
            jsonPath("$.errors.[*].message", hasItem(CriarPautaRequestDto.TITULO_OBRIGATORIO)),

            new CriarPautaRequestDto(faker.lorem().sentence(), ""),
            jsonPath("$.errors.[*].message", hasItem(CriarPautaRequestDto.DESCRICAO_OBRIGATORIA)),

            new CriarPautaRequestDto("", ""),
            jsonPath("$.errors.[*].message",
                    hasItems(
                            CriarPautaRequestDto.DESCRICAO_OBRIGATORIA,
                            CriarPautaRequestDto.TITULO_OBRIGATORIO))
        );


        when(pautaService.criar(any(Pauta.class))).thenReturn(null);

        for (var teste : testes.entrySet()) {
            this.mockMvc.perform(post("/api/v1/pautas")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(TestUtils.toJson(teste.getKey())))
                    .andExpect(status().isBadRequest())
                    .andExpect(teste.getValue());
        }

        verify(pautaService, never()).criar(any(Pauta.class));
    }

    @Test
    void deverRetornar400QuandoIDForInvalido() throws Exception {
        this.mockMvc.perform(get("/api/v1/pautas/{id}", "a")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deverRetornarNotFoundQuandoPautaNaoExiste() throws Exception {
        when(pautaService.buscar(anyLong())).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/api/v1/pautas/{id}", 123L)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

        verify(pautaService, times(1)).buscar(anyLong());
    }

    @Test
    void deverRetornarOkQuandoPautaExiste() throws Exception {
        Pauta pauta = new Pauta(faker.lorem().sentence(), faker.lorem().paragraph());
        pauta.setId(123L);

        when(pautaService.buscar(anyLong())).thenReturn(Optional.of(pauta));

        this.mockMvc.perform(get("/api/v1/pautas/{id}", pauta.getId())
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("id").value(pauta.getId()))
                .andExpect(jsonPath("titulo").value(pauta.getTitulo()))
                .andExpect(jsonPath("descricao").value(pauta.getDescricao()));

        verify(pautaService, times(1)).buscar(anyLong());
    }

    @Test
    void deveRetornarUmaListaDePautas() throws Exception {
        List<Pauta> pautas = IntStream.range(0, 3)
                .mapToObj(i -> {
                    Pauta pauta = new Pauta(faker.lorem().sentence(), faker.lorem().paragraph());
                    pauta.setId(i + 1L);
                    return pauta;
                }).collect(Collectors.toList());


        when(pautaService.listar()).thenReturn(pautas);

        this.mockMvc.perform(get("/api/v1/pautas")
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(pautas.get(0).getId()))
                .andExpect(jsonPath("$.[0].titulo").value(pautas.get(0).getTitulo()))
                .andExpect(jsonPath("$.[0].descricao").value(pautas.get(0).getDescricao()));

        verify(pautaService, times(1)).listar();
    }

    @Test
    void deveExcluirUmaPauta() throws Exception {
        Pauta pauta = new Pauta(faker.lorem().sentence(), faker.lorem().paragraph());
        pauta.setId(123L);

        when(pautaService.buscar(anyLong())).thenReturn(Optional.of(pauta));

        this.mockMvc.perform(delete("/api/v1/pautas/{id}", pauta.getId())
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());

        verify(pautaService, times(1)).excluir(anyLong());
    }


    @Test
    void deveRetornar200QuandoAbrirVotacaoParaUmaPauta() throws Exception {
        CriarVotacaoRequestDto request = new CriarVotacaoRequestDto();

        Votacao votacao = new Votacao(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));
        votacao.setId(1L);

        Pauta pauta = new Pauta(faker.lorem().sentence(), faker.lorem().paragraph());
        pauta.setId(123L);
        pauta.setVotacao(votacao);

        VotacaoResponseDto response = VotacaoMapper.toDto(votacao);

        when(pautaService.abrirVotacoes(pauta.getId(), VotacaoMapper.toEntity(request))).thenReturn(votacao);

        try(MockedStatic<VotacaoMapper> mapper = Mockito.mockStatic(VotacaoMapper.class)) {
            mapper.when(() -> VotacaoMapper.toDto(votacao)).thenReturn(response);
        }

        this.mockMvc.perform(post("/api/v1/pautas/{id}/votacoes", pauta.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtils.toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("id").value(votacao.getId()))
                .andExpect(jsonPath("dataAbertura").isNotEmpty())
                .andExpect(jsonPath("dataEncerramento").isNotEmpty());

        verify(pautaService, times(1)).abrirVotacoes(pauta.getId(), VotacaoMapper.toEntity(request));
    }


    @Test
    void deveRetornar404QuandoTentarAbrirVotacaoParaUmaPautaInexistente() throws Exception {
        CriarVotacaoRequestDto request = new CriarVotacaoRequestDto();

        Votacao votacao = new Votacao(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));
        votacao.setId(1L);

        String msgErro = "Pauta não encontrada";

        doThrow(new ResourceNotFoundException(msgErro))
                .when(pautaService).abrirVotacoes(anyLong(), any(Votacao.class));

        try(MockedStatic<VotacaoMapper> mapper = Mockito.mockStatic(VotacaoMapper.class)) {
            mapper.when(() -> VotacaoMapper.toEntity(request)).thenReturn(votacao);
        }

        this.mockMvc.perform(post("/api/v1/pautas/{id}/votacoes", 15689)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtils.toJson(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors.[*].message", hasItem(msgErro)));

        verify(pautaService, times(1)).abrirVotacoes(anyLong(), any(Votacao.class));
    }

    @Test
    void deveRetornar500QuandoOcorrerAlgumErrAoAbrirVotacao() throws Exception {
        CriarVotacaoRequestDto request = new CriarVotacaoRequestDto();

        Votacao votacao = new Votacao(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));
        votacao.setId(1L);

        String msgErro = "Erro inesperado";

        doThrow(new RuntimeException(msgErro))
                .when(pautaService).abrirVotacoes(anyLong(), any(Votacao.class));

        try(MockedStatic<VotacaoMapper> mapper = Mockito.mockStatic(VotacaoMapper.class)) {
            mapper.when(() -> VotacaoMapper.toEntity(request)).thenReturn(votacao);
        }

        this.mockMvc.perform(post("/api/v1/pautas/{id}/votacoes", 15689)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtils.toJson(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("errors.[*].message", hasItem(msgErro)));

        verify(pautaService, times(1)).abrirVotacoes(anyLong(), any(Votacao.class));
    }

    @Test
    void deveRetornar200QuandoVotarUmaPautaComSucesso() throws Exception {
        Long pautaId = 1L;
        CriarVotoRequestDto request = new CriarVotoRequestDto(1L, OpcaoVoto.SIM);

        doNothing().when(pautaService).votarPauta(anyLong(), anyLong(), any(OpcaoVoto.class));

        this.mockMvc.perform(post("/api/v1/pautas/{id}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtils.toJson(request)))
                .andExpect(status().isOk());

        verify(pautaService, times(1)).votarPauta(anyLong(), anyLong(), any(OpcaoVoto.class));
    }

    @Test
    void deveRetornar404QuandoPautaForInexistente() throws Exception {
        Long pautaId = 1L;
        CriarVotoRequestDto request = new CriarVotoRequestDto(1L, OpcaoVoto.SIM);
        String msgErro = "Pauta não encontrada";

        doThrow(new ResourceNotFoundException(msgErro))
                .when(pautaService).votarPauta(anyLong(), anyLong(), any(OpcaoVoto.class));

        this.mockMvc.perform(post("/api/v1/pautas/{id}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtils.toJson(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors.[*].message", hasItem(msgErro)));

        verify(pautaService, times(1)).votarPauta(anyLong(), anyLong(), any(OpcaoVoto.class));
    }

    @Test
    void deveRetornar400QuandoVotoForInvalido() throws Exception {
        Long pautaId = 1L;
        CriarVotoRequestDto request = new CriarVotoRequestDto(1L, OpcaoVoto.SIM);
        String msgErro = "Associado inexistente";

        doThrow(new BusinessException(msgErro))
                .when(pautaService).votarPauta(anyLong(), anyLong(), any(OpcaoVoto.class));

        this.mockMvc.perform(post("/api/v1/pautas/{id}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtils.toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors.[*].message", hasItem(msgErro)));

        verify(pautaService, times(1)).votarPauta(anyLong(), anyLong(), any(OpcaoVoto.class));
    }

    @Test
    void deveRetornar500QuandoOcorrerAlgumErrAoSalvarVoto() throws Exception {
        Long pautaId = 1L;
        CriarVotoRequestDto request = new CriarVotoRequestDto(1L, OpcaoVoto.SIM);
        String msgErro = "Erro inesperado";

        doThrow(new RuntimeException(msgErro))
                .when(pautaService).votarPauta(anyLong(), anyLong(), any(OpcaoVoto.class));

        this.mockMvc.perform(post("/api/v1/pautas/{id}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtils.toJson(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("errors.[*].message", hasItem(msgErro)));

        verify(pautaService, times(1)).votarPauta(anyLong(), anyLong(), any(OpcaoVoto.class));
    }

}