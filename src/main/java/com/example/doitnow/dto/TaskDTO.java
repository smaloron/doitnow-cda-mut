package com.example.doitnow.dto;

import com.example.doitnow.model.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TaskDTO {


    private String id;

    @NotBlank(message = "Le titre ne peut être vide")
    @Size(min=3, max=100, message = "Le titre doit comporter entre 3 et 100 caractères")
    private String title;

    @Size(max=500, message = "La description ne peut dépasser 500 caractères")
    private String description;

    private boolean completed = false;

    private String userId;

    @NotNull(message = "La priorité est obligatoire")
    private Priority priority = Priority.MEDIUM;

    private List<String> tags;

    @FutureOrPresent(message = "La date d'échéance ne peut être dans le passé")
    private LocalDate dueDate;

}
