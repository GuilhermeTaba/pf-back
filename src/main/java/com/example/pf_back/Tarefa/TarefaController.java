package com.example.pf_back.Tarefa;



import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {
    @Autowired
    private TarefaService tarefaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tarefa postTarefa(@RequestBody @Valid TarefaDTO tarefaDTO){
        return tarefaService.postTarefa(tarefaDTO);
    }
    @GetMapping
    public List<Tarefa> getTarefas(){
        return tarefaService.getTarefas();
    }
     @DeleteMapping("/{id}")
     @ResponseStatus(HttpStatus.NO_CONTENT)
     public void deleteAvaliacao(@PathVariable Integer id){
         tarefaService.deleteTarefa(id);
     }

}
