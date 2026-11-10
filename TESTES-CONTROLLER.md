# Testes com Mockito — AvaliacaoController

Arquivo: `src/test/java/com/example/pf_back/Avaliacao/AvaliacaoControllerTest.java`
As dependências já vêm pelos starters `-test` do `pom.xml` (JUnit 5, Mockito, AssertJ, MockMvc).

Rodar só este teste:

```bash
./mvnw test -Dtest=AvaliacaoControllerTest
```

Rodar todos:

```bash
./mvnw test
```

Testa o controller com o `AvaliacaoService` mockado. Usa `MockMvcBuilders.standaloneSetup`,
então não sobe o contexto do Spring e não passa pela segurança (JWT/roles) — só valida
rotas, status HTTP, JSON e a validação do `@Valid`.

> Os testes de `GET/PUT/DELETE /avaliacoes/{id}` supõem que o controller tenha estas rotas
> (hoje ele só tem `POST` e `GET /avaliacoes`):
>
> ```java
> @GetMapping("/{id}")
> public Avaliacao getAvaliacao(@PathVariable Integer id){
>     return avaliacaoService.getAvaliacao(id);
> }
>
> @PutMapping("/{id}")
> public Avaliacao putAvaliacao(@PathVariable Integer id, @RequestBody @Valid AvaliacaoDTO avaliacaoDTO){
>     return avaliacaoService.putAvaliacao(id, avaliacaoDTO);
> }
>
> @DeleteMapping("/{id}")
> @ResponseStatus(HttpStatus.NO_CONTENT)
> public void deleteAvaliacao(@PathVariable Integer id){
>     avaliacaoService.deleteAvaliacao(id);
> }
> ```

```java
package com.example.pf_back.Avaliacao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AvaliacaoControllerTest {

    @Mock
    private AvaliacaoService avaliacaoService;

    @InjectMocks
    private AvaliacaoController avaliacaoController;

    private MockMvc mockMvc;
    private Avaliacao avaliacao;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(avaliacaoController).build();

        avaliacao = new Avaliacao();
        avaliacao.setId(1);
        avaliacao.setEmailAvaliado("avaliado@insper.edu.br");
        avaliacao.setEmailAvaliador("avaliador@insper.edu.br");
        avaliacao.setComentarios(List.of("Muito bom"));
        avaliacao.setNota(9);
    }

    @Test
    void postAvaliacao_valido_deveRetornar201() throws Exception {
        when(avaliacaoService.postAvaliacao(any(AvaliacaoDTO.class))).thenReturn(avaliacao);

        String json = """
                {
                  "emailAvaliado": "avaliado@insper.edu.br",
                  "emailAvaliador": "avaliador@insper.edu.br",
                  "comentarios": ["Muito bom"],
                  "nota": 9
                }
                """;

        mockMvc.perform(post("/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.emailAvaliado").value("avaliado@insper.edu.br"))
                .andExpect(jsonPath("$.nota").value(9))
                .andExpect(jsonPath("$.comentarios[0]").value("Muito bom"));

        verify(avaliacaoService).postAvaliacao(any(AvaliacaoDTO.class));
    }

    @Test
    void postAvaliacao_notaMaiorQue10_deveRetornar400() throws Exception {
        String json = """
                {
                  "emailAvaliado": "avaliado@insper.edu.br",
                  "emailAvaliador": "avaliador@insper.edu.br",
                  "nota": 11
                }
                """;

        mockMvc.perform(post("/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(avaliacaoService);
    }

    @Test
    void postAvaliacao_emailInvalido_deveRetornar400() throws Exception {
        String json = """
                {
                  "emailAvaliado": "nao-e-email",
                  "emailAvaliador": "avaliador@insper.edu.br",
                  "nota": 5
                }
                """;

        mockMvc.perform(post("/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(avaliacaoService);
    }

    @Test
    void getAvaliacoes_deveRetornar200ComLista() throws Exception {
        when(avaliacaoService.getAvaliacoes()).thenReturn(List.of(avaliacao));

        mockMvc.perform(get("/avaliacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].emailAvaliador").value("avaliador@insper.edu.br"));

        verify(avaliacaoService).getAvaliacoes();
    }

    @Test
    void getAvaliacoes_semRegistros_deveRetornarListaVazia() throws Exception {
        when(avaliacaoService.getAvaliacoes()).thenReturn(List.of());

        mockMvc.perform(get("/avaliacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAvaliacao_idExistente_deveRetornar200() throws Exception {
        when(avaliacaoService.getAvaliacao(1)).thenReturn(avaliacao);

        mockMvc.perform(get("/avaliacoes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nota").value(9));

        verify(avaliacaoService).getAvaliacao(1);
    }

    @Test
    void getAvaliacao_idInexistente_deveRetornar404() throws Exception {
        when(avaliacaoService.getAvaliacao(99))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada"));

        mockMvc.perform(get("/avaliacoes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void putAvaliacao_valido_deveRetornar200() throws Exception {
        Avaliacao atualizada = new Avaliacao();
        atualizada.setId(1);
        atualizada.setEmailAvaliado("novo@insper.edu.br");
        atualizada.setEmailAvaliador("avaliador@insper.edu.br");
        atualizada.setComentarios(List.of("Melhorou"));
        atualizada.setNota(7);
        when(avaliacaoService.putAvaliacao(eq(1), any(AvaliacaoDTO.class))).thenReturn(atualizada);

        String json = """
                {
                  "emailAvaliado": "novo@insper.edu.br",
                  "emailAvaliador": "avaliador@insper.edu.br",
                  "comentarios": ["Melhorou"],
                  "nota": 7
                }
                """;

        mockMvc.perform(put("/avaliacoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAvaliado").value("novo@insper.edu.br"))
                .andExpect(jsonPath("$.nota").value(7));

        verify(avaliacaoService).putAvaliacao(eq(1), any(AvaliacaoDTO.class));
    }

    @Test
    void putAvaliacao_invalido_deveRetornar400() throws Exception {
        String json = """
                {
                  "emailAvaliado": "",
                  "emailAvaliador": "avaliador@insper.edu.br",
                  "nota": -1
                }
                """;

        mockMvc.perform(put("/avaliacoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(avaliacaoService);
    }

    @Test
    void putAvaliacao_idInexistente_deveRetornar404() throws Exception {
        when(avaliacaoService.putAvaliacao(eq(99), any(AvaliacaoDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada"));

        String json = """
                {
                  "emailAvaliado": "avaliado@insper.edu.br",
                  "emailAvaliador": "avaliador@insper.edu.br",
                  "nota": 5
                }
                """;

        mockMvc.perform(put("/avaliacoes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAvaliacao_idExistente_deveRetornar204() throws Exception {
        doNothing().when(avaliacaoService).deleteAvaliacao(1);

        mockMvc.perform(delete("/avaliacoes/1"))
                .andExpect(status().isNoContent());

        verify(avaliacaoService).deleteAvaliacao(1);
    }

    @Test
    void deleteAvaliacao_idInexistente_deveRetornar404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada"))
                .when(avaliacaoService).deleteAvaliacao(99);

        mockMvc.perform(delete("/avaliacoes/99"))
                .andExpect(status().isNotFound());
    }
}
```

> Os testes do service estão em `TESTES-SERVICE.md`.
