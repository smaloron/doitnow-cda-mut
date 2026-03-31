package com.example.doitnow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskDTO {


    private String id;

    @NotBlank(message = "Le titre ne peut être vide")
    @Size(min=3, max=100, message = "Le titre doit comporter entre 3 et 100 caractères")
    private String title;

    @Size(max=500, message = "La description ne peut dépasser 500 caractères")
    private String description;

    private boolean completed = false;

    public String getId() {
        return id;
    }

    public TaskDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public TaskDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TaskDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isCompleted() {
        return completed;
    }

    public TaskDTO setCompleted(boolean completed) {
        this.completed = completed;
        return this;
    }
}
