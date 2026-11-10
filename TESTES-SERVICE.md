# Testes com Mockito — AvaliacaoService

Arquivo: `src/test/java/com/example/pf_back/Avaliacao/AvaliacaoServiceTest.java`
As dependências já vêm pelos starters `-test` do `pom.xml` (JUnit 5, Mockito, AssertJ, MockMvc).

Rodar só este teste:

```bash
./mvnw test -Dtest=AvaliacaoServiceTest
```

Rodar todos:

```bash
./mvnw test
```

Testa o service isolado: o `AvaliacaoRepository` é um mock, sem banco.

```java
package com.example.pf_back.Avaliacao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private AvaliacaoDTO dto;
    private Avaliacao avaliacao;

    @BeforeEach
    void setUp() {
        dto = new AvaliacaoDTO("avaliado@insper.edu.br", "avaliador@insper.edu.br",
                List.of("Muito bom"), 9);

        avaliacao = new Avaliacao();
        avaliacao.setId(1);
        avaliacao.setEmailAvaliado("avaliado@insper.edu.br");
        avaliacao.setEmailAvaliador("avaliador@insper.edu.br");
        avaliacao.setComentarios(List.of("Muito bom"));
        avaliacao.setNota(9);
    }

    @Test
    void postAvaliacao_deveSalvarComOsDadosDoDTO() {
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Avaliacao resultado = avaliacaoService.postAvaliacao(dto);

        ArgumentCaptor<Avaliacao> captor = ArgumentCaptor.forClass(Avaliacao.class);
        verify(avaliacaoRepository).save(captor.capture());
        Avaliacao salva = captor.getValue();

        assertThat(salva.getEmailAvaliado()).isEqualTo("avaliado@insper.edu.br");
        assertThat(salva.getEmailAvaliador()).isEqualTo("avaliador@insper.edu.br");
        assertThat(salva.getComentarios()).containsExactly("Muito bom");
        assertThat(salva.getNota()).isEqualTo(9);
        assertThat(resultado).isSameAs(salva);
    }

    @Test
    void getAvaliacoes_deveRetornarTodas() {
        when(avaliacaoRepository.findAll()).thenReturn(List.of(avaliacao));

        List<Avaliacao> resultado = avaliacaoService.getAvaliacoes();

        assertThat(resultado).hasSize(1).containsExactly(avaliacao);
        verify(avaliacaoRepository).findAll();
    }

    @Test
    void getAvaliacoes_semRegistros_deveRetornarListaVazia() {
        when(avaliacaoRepository.findAll()).thenReturn(List.of());

        assertThat(avaliacaoService.getAvaliacoes()).isEmpty();
    }

    @Test
    void getAvaliacao_idExistente_deveRetornar() {
        when(avaliacaoRepository.findById(1)).thenReturn(Optional.of(avaliacao));

        Avaliacao resultado = avaliacaoService.getAvaliacao(1);

        assertThat(resultado).isSameAs(avaliacao);
    }

    @Test
    void getAvaliacao_idInexistente_deveLancar404() {
        when(avaliacaoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> avaliacaoService.getAvaliacao(99))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void putAvaliacao_idExistente_deveAtualizarCampos() {
        AvaliacaoDTO novo = new AvaliacaoDTO("novo@insper.edu.br", "outro@insper.edu.br",
                List.of("Melhorou"), 7);
        when(avaliacaoRepository.findById(1)).thenReturn(Optional.of(avaliacao));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Avaliacao resultado = avaliacaoService.putAvaliacao(1, novo);

        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getEmailAvaliado()).isEqualTo("novo@insper.edu.br");
        assertThat(resultado.getEmailAvaliador()).isEqualTo("outro@insper.edu.br");
        assertThat(resultado.getComentarios()).containsExactly("Melhorou");
        assertThat(resultado.getNota()).isEqualTo(7);
        verify(avaliacaoRepository).save(avaliacao);
    }

    @Test
    void putAvaliacao_idInexistente_deveLancar404ENaoSalvar() {
        when(avaliacaoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> avaliacaoService.putAvaliacao(99, dto))
                .isInstanceOf(ResponseStatusException.class);
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void deleteAvaliacao_idExistente_deveDeletar() {
        when(avaliacaoRepository.findById(1)).thenReturn(Optional.of(avaliacao));

        avaliacaoService.deleteAvaliacao(1);

        verify(avaliacaoRepository).delete(avaliacao);
    }

    @Test
    void deleteAvaliacao_idInexistente_deveLancar404ENaoDeletar() {
        when(avaliacaoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> avaliacaoService.deleteAvaliacao(99))
                .isInstanceOf(ResponseStatusException.class);
        verify(avaliacaoRepository, never()).delete(any());
    }
}
```


> Os testes do controller estão em `TESTES-CONTROLLER.md`.
