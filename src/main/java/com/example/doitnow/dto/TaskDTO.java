package com.example.doitnow.dto;

public class TaskDTO {

    private String id;
    private String title;
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
