package com.example.pf_back.Avaliacao;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {
    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Avaliacao postAvaliacao(@RequestBody @Valid AvaliacaoDTO avaliacaoDTO){
        return avaliacaoService.postAvaliacao(avaliacaoDTO);
    }

    @GetMapping
    public List<Avaliacao> getAvaliacoes(){
        return avaliacaoService.getAvaliacoes();
    }

    // ===== Complemento: endpoints para o resto do service =====
    // Descomente conforme for precisar.

    // GET /avaliacoes/{id} -> busca uma avaliação pelo id (404 se não existir)
    // @GetMapping("/{id}")
    // public Avaliacao getAvaliacao(@PathVariable Integer id){
    //     return avaliacaoService.getAvaliacao(id);
    // }

    // PUT /avaliacoes/{id} -> atualiza uma avaliação existente
    // @PutMapping("/{id}")
    // public Avaliacao putAvaliacao(@PathVariable Integer id, @RequestBody @Valid AvaliacaoDTO avaliacaoDTO){
    //     return avaliacaoService.putAvaliacao(id, avaliacaoDTO);
    // }

    // DELETE /avaliacoes/{id} -> remove uma avaliação (responde 204 No Content)
    // @DeleteMapping("/{id}")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    // public void deleteAvaliacao(@PathVariable Integer id){
    //     avaliacaoService.deleteAvaliacao(id);
    // }

    // ----- Exemplos extras (precisam do método correspondente no service/repository) -----

    // GET /avaliacoes/avaliado/{email} -> avaliações recebidas por um usuário
    // @GetMapping("/avaliado/{email}")
    // public List<Avaliacao> getAvaliacoesPorAvaliado(@PathVariable String email){
    //     return avaliacaoService.getAvaliacoesPorAvaliado(email);
    // }
    // No service:
    // public List<Avaliacao> getAvaliacoesPorAvaliado(String email){
    //     return avaliacaoRepository.findByEmailAvaliado(email);
    // }

    // GET /avaliacoes/avaliador/{email} -> avaliações feitas por um usuário
    // @GetMapping("/avaliador/{email}")
    // public List<Avaliacao> getAvaliacoesPorAvaliador(@PathVariable String email){
    //     return avaliacaoService.getAvaliacoesPorAvaliador(email);
    // }
    // No service:
    // public List<Avaliacao> getAvaliacoesPorAvaliador(String email){
    //     return avaliacaoRepository.findByEmailAvaliador(email);
    // }

    // GET /avaliacoes/media/{email} -> média das notas recebidas por um usuário
    // @GetMapping("/media/{email}")
    // public Double getMediaNotas(@PathVariable String email){
    //     return avaliacaoService.getMediaNotas(email);
    // }
    // No service:
    // public Double getMediaNotas(String email){
    //     Double media = avaliacaoRepository.mediaNotasPorAvaliado(email);
    //     return media != null ? media : 0.0;
    // }

    // GET /avaliacoes?emailAvaliado=x  (filtro opcional via query param)
    // Obs: se usar este, substitua o getAvaliacoes() acima.
    // @GetMapping
    // public List<Avaliacao> getAvaliacoes(@RequestParam(required = false) String emailAvaliado){
    //     if (emailAvaliado != null) {
    //         return avaliacaoService.getAvaliacoesPorAvaliado(emailAvaliado);
    //     }
    //     return avaliacaoService.getAvaliacoes();
    // }
}
