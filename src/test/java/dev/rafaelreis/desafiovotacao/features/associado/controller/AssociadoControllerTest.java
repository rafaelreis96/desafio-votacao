package dev.rafaelreis.desafiovotacao.features.associado.controller;

import com.github.javafaker.Faker;
import dev.rafaelreis.desafiovotacao.features.associado.dto.CriarAssociadoRequestDto;
import dev.rafaelreis.desafiovotacao.features.associado.mapper.AssociadoMapper;
import dev.rafaelreis.desafiovotacao.features.associado.service.AssociadoService;
import dev.rafaelreis.desafiovotacao.model.entity.Associado;
import dev.rafaelreis.desafiovotacao.util.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AssociadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssociadoService associadoService;

    private static Faker faker;

    private Associado associado;

    private CriarAssociadoRequestDto request;

    @BeforeAll
    static void beforeAll() {
        faker = TestUtils.fakerBR();
    }

    @BeforeEach
    void setUp() {
        request = new CriarAssociadoRequestDto(faker.name().firstName(), faker.number().digits(11));
        associado = new Associado(faker.name().firstName(), faker.number().digits(11));
    }

    @Test
    void deverCriarUmAssociado() throws Exception {
        Associado associado = AssociadoMapper.toEntity(request);
        associado.setId(123L);

        when(associadoService.criar(any(Associado.class))).thenReturn(associado);

        this.mockMvc.perform(post("/api/v1/associados")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtils.toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(associadoService, times(1)).criar(any(Associado.class));
    }

    @Test
    void deverRetornar400QuandoDadosDoAssociadoForemInvalidos() throws Exception {
        Map<CriarAssociadoRequestDto, ResultMatcher> testes = Map.of(
                new CriarAssociadoRequestDto("", faker.number().digits(11)),
                jsonPath("$.errors.[*].message", hasItem(CriarAssociadoRequestDto.NOME_OBRIGATORIO)),

                new CriarAssociadoRequestDto(faker.name().fullName(), ""),
                jsonPath("$.errors.[*].message", hasItem(CriarAssociadoRequestDto.CPF_OBRIGATORIO)),

                new CriarAssociadoRequestDto( faker.lorem().fixedString(258), faker.number().digits(11)),
                jsonPath("$.errors.[*].message", hasItem(CriarAssociadoRequestDto.NOME_INVALIDO)),

                new CriarAssociadoRequestDto(faker.name().fullName(), faker.number().digits(13)),
                jsonPath("$.errors.[*].message", hasItem(CriarAssociadoRequestDto.CPF_INVALIDO)),

                new CriarAssociadoRequestDto("", ""),
                jsonPath("$.errors.[*].message",
                        hasItems(CriarAssociadoRequestDto.NOME_OBRIGATORIO, CriarAssociadoRequestDto.CPF_OBRIGATORIO))
        );

        when(associadoService.criar(any(Associado.class))).thenReturn(null);

        for (var teste : testes.entrySet()) {
            this.mockMvc.perform(post("/api/v1/associados")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(TestUtils.toJson(teste.getKey())))
                    .andExpect(status().isBadRequest())
                    .andExpect(teste.getValue());
        }

        verify(associadoService, never()).criar(any(Associado.class));
    }

    @Test
    void deverRetornar400QuandoTituloDoAssociadoForInvalido() throws Exception {
        CriarAssociadoRequestDto requestInvalido =
                new CriarAssociadoRequestDto(faker.name().firstName(), faker.number().digits(15));

        when(associadoService.criar(any(Associado.class))).thenReturn(null);

        this.mockMvc.perform(post("/api/v1/associados")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtils.toJson(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors.[0].message").value(CriarAssociadoRequestDto.CPF_INVALIDO));

        verify(associadoService, never()).criar(any(Associado.class));
    }

    @Test
    void deverRetornar400QuandoIDForInvalido() throws Exception {
        this.mockMvc.perform(get("/api/v1/associados/{id}", "a")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deverRetornarNotFoundQuandoAssociadoNaoExiste() throws Exception {
        when(associadoService.buscar(anyLong())).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/api/v1/associados/{id}", 123L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

        verify(associadoService, times(1)).buscar(anyLong());
    }

    @Test
    void deverRetornarOkQuandoAssociadoExiste() throws Exception {
        associado.setId(123L);

        when(associadoService.buscar(anyLong())).thenReturn(Optional.of(associado));

        this.mockMvc.perform(get("/api/v1/associados/{id}", associado.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("id").value(associado.getId()))
                .andExpect(jsonPath("nome").value(associado.getNome()))
                .andExpect(jsonPath("cpf").value(associado.getCpf()));

        verify(associadoService, times(1)).buscar(anyLong());
    }

    @Test
    void deveRetornarUmaListaDeAssociados() throws Exception {
        List<Associado> associados = IntStream.range(0, 3)
                .mapToObj(i -> {
                    Associado ass = new Associado(faker.name().firstName(), faker.number().digits(11));
                    ass.setId(i + 1L);
                    return ass;
                }).toList();


        when(associadoService.listar()).thenReturn(associados);

        this.mockMvc.perform(get("/api/v1/associados")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(associados.get(0).getId()))
                .andExpect(jsonPath("$.[0].nome").value(associados.get(0).getNome()))
                .andExpect(jsonPath("$.[0].cpf").value(associados.get(0).getCpf()));

        verify(associadoService, times(1)).listar();
    }

    @Test
    void deveExcluirUmaPauta() throws Exception {
        associado.setId(123L);

        when(associadoService.buscar(anyLong())).thenReturn(Optional.of(associado));

        this.mockMvc.perform(delete("/api/v1/associados/{id}", associado.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());

        verify(associadoService, times(1)).excluir(anyLong());
    }


}