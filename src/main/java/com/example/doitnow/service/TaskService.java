package com.example.doitnow.service;

import com.example.doitnow.dto.CreateTaskDTO;
import com.example.doitnow.dto.TaskDTO;
import com.example.doitnow.exception.ResourceNotFoundException;
import com.example.doitnow.model.Priority;
import com.example.doitnow.model.Task;
import com.example.doitnow.model.User;
import com.example.doitnow.repository.TaskRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {


    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private String getCurrentUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return user.getId();
    }

    public TaskDTO save(CreateTaskDTO taskToCreate) {
        Task task = new Task();
        task.setDescription(taskToCreate.getDescription());
        task.setTitle(taskToCreate.getTitle());
        task.setUserId(getCurrentUserId());
        task.setPriority(taskToCreate.getPriority());
        task.setTags(taskToCreate.getTags());
        task.setDueDate(taskToCreate.getDueDate());

        Task savedTask = taskRepository.save(task);

        return convertToDTO(savedTask);

    }

    public TaskDTO updateTask(String id, TaskDTO taskDTO) {
        this.taskRepository.findByIdAndUserId(id, getCurrentUserId()).orElseThrow(
                ()->new ResourceNotFoundException("La tâche " + id + " n'existe pas")
        );
        Task task = convertToEntity(taskDTO);
        task.setId(id); // Assure que l'ID est le bon
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findByUserId(getCurrentUserId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO findTaskById(String id) {
        return taskRepository.findByIdAndUserId(id, getCurrentUserId())
                .map(this::convertToDTO)
                .orElseThrow(()-> new ResourceNotFoundException("La tâche " + id + " n'existe pas"));
    }

    public void deleteTask(String id) {
        this.taskRepository.findByIdAndUserId(id, getCurrentUserId()).orElseThrow(
                ()->new ResourceNotFoundException("La tâche " + id + " n'existe pas")
        );
        taskRepository.deleteById(id);
    }

    public List<TaskDTO> getTasksByPriority(
            Priority priority) {
        return taskRepository
                .findByPriorityAndUserId(
                        priority, getCurrentUserId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByTag(String tag) {
        return taskRepository
                .findByTagsContainingAndUserId(
                        getCurrentUserId(), tag).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getOverdueTasks() {
        return taskRepository
                .findByCompletedFalseAndDueDateBeforeAndUserId(
                        LocalDate.now(), getCurrentUserId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.isCompleted());
        dto.setUserId(task.getUserId());
        dto.setPriority(task.getPriority());
        dto.setTags(task.getTags());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }

    private Task convertToEntity(TaskDTO dto) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCompleted(dto.isCompleted());
        task.setPriority(dto.getPriority());
        task.setTags(dto.getTags());
        task.setDueDate(dto.getDueDate());

        return task;
    }
}
