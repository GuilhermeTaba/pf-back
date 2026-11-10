package com.example.pf_back.Avaliacao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoDTO {

    @NotBlank
    @Email
    private String emailAvaliado;

    @NotBlank
    @Email
    private String emailAvaliador;

    private List<String> comentarios;

    @NotNull
    @Min(0)
    @Max(10)
    private Integer nota;
}

