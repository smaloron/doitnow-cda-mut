package com.example.doitnow.dto;

public class CreateTaskDTO {
    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public CreateTaskDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CreateTaskDTO setDescription(String description) {
        this.description = description;
        return this;
    }
}
