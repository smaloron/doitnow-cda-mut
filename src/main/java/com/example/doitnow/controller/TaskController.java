package com.example.doitnow.controller;

import com.example.doitnow.dto.CreateTaskDTO;
import com.example.doitnow.dto.TaskDTO;
import com.example.doitnow.model.Priority;
import com.example.doitnow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public TaskDTO getTaskById(@PathVariable String id) {
        return this.taskService.findTaskById(id);
    }

    @PostMapping
    public TaskDTO createTask(@Valid @RequestBody CreateTaskDTO taskToCreate) {
        return this.taskService.save(taskToCreate);
    }

    @GetMapping
    public List<TaskDTO> getAllTasks(
            @RequestParam(required = false)
            Priority priority,
            @RequestParam(required = false)
            String tag
    ) {

        if (priority != null) {
            return ResponseEntity.ok(
                    taskService.getTasksByPriority(
                            priority)).getBody();
        }
        if (tag != null) {
            return ResponseEntity.ok(
                    taskService.getTasksByTag(tag)).getBody();
        }
        return ResponseEntity.ok(
                taskService.getAllTasks()).getBody();
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable String id, @Valid @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(id, taskDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDTO>>
    getOverdueTasks() {
        return ResponseEntity.ok(
                taskService.getOverdueTasks());
    }

}
