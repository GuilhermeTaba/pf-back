package com.example.pf_back.Tarefa;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String título;
    private String descrição;

    private Prioridade prioridade;
    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;
}
