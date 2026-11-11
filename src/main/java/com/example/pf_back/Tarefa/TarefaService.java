package com.example.pf_back.Tarefa;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TarefaService {
    @Autowired
    private TarefaRepository tarefaRepository;
    public Tarefa postTarefa(TarefaDTO tarefaDTO){
        Tarefa tarefa = new Tarefa();
        tarefa.setPrioridade(tarefaDTO.getPrioridade());
        tarefa.setDescrição(tarefaDTO.getDescrição());
        tarefa.setEmail(tarefaDTO.getEmail());
        tarefa.setTítulo(tarefaDTO.getTítulo());
        tarefaRepository.save(tarefa);

        return tarefa;

    }


    public List<Tarefa> getTarefas(){
        return tarefaRepository.findAll();

    }
    public Tarefa getTarefa(Integer id){
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada"));
    }

    public void deleteTarefa(Integer id){
        Tarefa tarefa = getTarefa(id);
        tarefaRepository.delete(tarefa);
    }

}
