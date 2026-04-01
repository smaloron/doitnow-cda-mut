package com.example.doitnow.repository;

import com.example.doitnow.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByCompleted(boolean completed);

    List<Task> findByTitleContainingIgnoreCase(String keyword);

}
