package com.example.doitnow.controller;

import com.example.doitnow.dto.CreateTaskDTO;
import com.example.doitnow.dto.TaskDTO;
import com.example.doitnow.service.TaskService;
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
    public TaskDTO createTask(@RequestBody CreateTaskDTO taskToCreate) {
        return this.taskService.save(taskToCreate);
    }

    @GetMapping
    public List<TaskDTO> getAllTasks() {
        return this.taskService.getAllTasks();
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable String id, @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(id, taskDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
    }

}
