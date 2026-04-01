package com.example.doitnow.service;

import com.example.doitnow.dto.CreateTaskDTO;
import com.example.doitnow.dto.TaskDTO;
import com.example.doitnow.exception.ResourceNotFoundException;
import com.example.doitnow.model.Task;
import com.example.doitnow.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {


    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskDTO save(CreateTaskDTO taskToCreate) {
        Task task = new Task();
        task.setDescription(taskToCreate.getDescription());
        task.setTitle(taskToCreate.getTitle());

        Task savedTask = taskRepository.save(task);

        return convertToDTO(savedTask);

    }

    public TaskDTO updateTask(String id, TaskDTO taskDTO) {
        this.taskRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("La tâche " + id + " n'existe pas")
        );
        Task task = convertToEntity(taskDTO);
        task.setId(id); // Assure que l'ID est le bon
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO findTaskById(String id) {
        return taskRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(()-> new ResourceNotFoundException("La tâche " + id + " n'existe pas"));
    }

    public void deleteTask(String id) {
        this.taskRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("La tâche " + id + " n'existe pas")
        );
        taskRepository.deleteById(id);
    }


    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.isCompleted());
        return dto;
    }

    private Task convertToEntity(TaskDTO dto) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCompleted(dto.isCompleted());
        return task;
    }
}
