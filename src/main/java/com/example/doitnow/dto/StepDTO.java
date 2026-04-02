package com.example.doitnow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepDTO {

    @NotBlank(message = "Le nom de l'étape est obligatoire.")
    @Size(min = 1, max = 200, message = "Le nom de l'étape ne peut pas dépasser 200 caractères.")
    private String name;

    private boolean completed = false;
}
