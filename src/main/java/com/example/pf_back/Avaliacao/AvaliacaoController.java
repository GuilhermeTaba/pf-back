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
}
