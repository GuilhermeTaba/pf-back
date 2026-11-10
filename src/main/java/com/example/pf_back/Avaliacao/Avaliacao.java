package com.example.pf_back.Avaliacao;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String emailAvaliado;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String emailAvaliador;

    @ElementCollection
    private List<String> comentarios;

    @NotNull
    @Min(0)
    @Max(10)
    private Integer nota;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataAvaliacao;


}
