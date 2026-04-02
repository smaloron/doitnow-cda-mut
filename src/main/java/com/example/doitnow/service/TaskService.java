package com.example.doitnow.service;

import com.example.doitnow.dto.CreateTaskDTO;
import com.example.doitnow.dto.StepDTO;
import com.example.doitnow.dto.TaskDTO;
import com.example.doitnow.exception.ResourceNotFoundException;
import com.example.doitnow.model.Priority;
import com.example.doitnow.model.Step;
import com.example.doitnow.model.Task;
import com.example.doitnow.model.User;
import com.example.doitnow.repository.TaskRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {


    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public String getCurrentUserId(){
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

        task.setSteps(toStepList(taskToCreate.getSteps()));
        task.setCompleted(false);
        task.refreshCompletionFromSteps();

        Task savedTask = taskRepository.save(task);

        return convertToDTO(savedTask);

    }

    public TaskDTO updateTask(String id, TaskDTO taskDTO) {
        this.taskRepository.findByIdAndUserId(id, getCurrentUserId()).orElseThrow(
                ()->new ResourceNotFoundException("La tâche " + id + " n'existe pas")
        );
        Task task = convertToEntity(taskDTO);
        task.setId(id); // Assure que l'ID est le bon

        // Mise à jour des steps, puis re-calcul de la complétion
        if (taskDTO.getSteps() != null) {
            task.setSteps(toStepList(taskDTO.getSteps()));
            task.refreshCompletionFromSteps();
        } else {
            // Pas de steps fournis : on respecte la valeur manuelle du DTO
            task.setCompleted(taskDTO.isCompleted());
        }

        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    /**
     * Bascule le statut completed d'un step identifié par son index (0-based).
     * Recalcule ensuite automatiquement la complétion de la tâche parente.
     *
     * @param taskId    identifiant de la tâche
     * @param stepIndex index du step dans la liste (0-based)
     * @return le TaskDTO mis à jour
     */
    public TaskDTO toggleStep(String taskId, int stepIndex) {
        Task task = taskRepository.findByIdAndUserId(taskId, getCurrentUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tâche non trouvée avec l'id : " + taskId));

        List<Step> steps = task.getSteps();
        if (steps == null || stepIndex < 0 || stepIndex >= steps.size()) {
            throw new ResourceNotFoundException(
                    "Step introuvable à l'index " + stepIndex + " pour la tâche : " + taskId);
        }

        Step step = steps.get(stepIndex);
        step.setCompleted(!step.isCompleted());

        // Recalcul automatique de la complétion de la tâche
        task.refreshCompletionFromSteps();

        TaskDTO result = convertToDTO(taskRepository.save(task));

        return result;
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

        dto.setSteps(toStepDTOList(task.getSteps()));
        dto.setStepsCompletionRate(task.getStepsCompletionRate());


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

    // Convertit une liste de StepDTO en une liste de Step
    private List<Step> toStepList(List<StepDTO> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream()
                .map(dto -> new Step(dto.getName(), dto.isCompleted()))
                .toList();
    }

    private List<StepDTO> toStepDTOList(List<Step> steps) {
        if (steps == null) return new ArrayList<>();
        return steps.stream()
                .map(s -> new StepDTO(s.getName(), s.isCompleted()))
                .toList();
    }
}
