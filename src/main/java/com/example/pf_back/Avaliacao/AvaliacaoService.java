package com.example.pf_back.Avaliacao;
import com.example.pf_back.Avaliacao.Avaliacao;
import com.example.pf_back.Avaliacao.AvaliacaoDTO;
import com.example.pf_back.Avaliacao.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoService {
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;


    public Avaliacao postAvaliacao(AvaliacaoDTO avaliacaoDTO){
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setEmailAvaliador(avaliacaoDTO.getEmailAvaliador());
        avaliacao.setEmailAvaliado(avaliacaoDTO.getEmailAvaliado());
        avaliacao.setComentarios(avaliacaoDTO.getComentarios());
        avaliacao.setNota(avaliacaoDTO.getNota());
        avaliacaoRepository.save(avaliacao);

        return avaliacao;

    }
    public List<Avaliacao> getAvaliacoes(){
        return avaliacaoRepository.findAll();

    }
}
