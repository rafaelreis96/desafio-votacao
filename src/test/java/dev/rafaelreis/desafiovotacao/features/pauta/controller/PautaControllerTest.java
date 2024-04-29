package dev.rafaelreis.desafiovotacao.features.pauta.controller;

import com.github.javafaker.Faker;
import dev.rafaelreis.desafiovotacao.features.pauta.dto.CriarPautaRequestDto;
import dev.rafaelreis.desafiovotacao.features.pauta.mapper.PautaMapper;
import dev.rafaelreis.desafiovotacao.features.pauta.service.PautaService;
import dev.rafaelreis.desafiovotacao.model.entity.Pauta;
import dev.rafaelreis.desafiovotacao.util.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

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
    void deverRetornar400QuandoTituloDaPautaForInvalida() throws Exception {

        CriarPautaRequestDto request = new CriarPautaRequestDto(
                faker.lorem().sentence(), faker.lorem().fixedString(513));

        when(pautaService.criar(any(Pauta.class))).thenReturn(null);

        this.mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtils.toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors.[0].message").value(CriarPautaRequestDto.DESCRICAO_INVALIDO));

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
}