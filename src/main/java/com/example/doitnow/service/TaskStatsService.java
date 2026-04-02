package com.example.doitnow.service;

import com.example.doitnow.dto.TaskStats;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TaskStatsService {

    private final MongoTemplate mongoTemplate;

    public TaskStatsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public TaskStats getStatsForUserId(String userId) {
        TaskStats stats = new TaskStats();

        // Compte du nombre total de tâches
        Query q = new Query(
                Criteria.where("userId").is(userId)
        );
        long total = mongoTemplate.count(q, "tasks");
        stats.setTotal(total);

        // Compte du nombre de tâches terminées
        Query q2 = new Query(
                Criteria.where("userId").is(userId).and("completed").is(true)
        );
        long completed = mongoTemplate.count(q2, "tasks");
        stats.setCompleted(completed);

        stats.setPending(total - completed);

        // Compte du nombre de tâches en retard
        Query q3 = new Query(
                Criteria.where("userId").is(userId)
                        .and("completed").is(false)
                        .and("dueDate").lt(new java.util.Date())
        );
        long overdue = mongoTemplate.count(q3, "tasks");
        stats.setOverdue(overdue);

        if (total > 0) {
            stats.setCompletionRate(Math.round(
                    10000.0 * (double) completed / total)/ 100.0
            );
        }

        stats.setTaskByPriority(getTaskCountByPriority(userId));

        stats.setTaskByTag(getTaskCountByTag(userId));

        return stats;
    }

    Map<String, Long> getTaskCountByPriority(String userId) {
        Aggregation byPriority = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(userId)),
                Aggregation.group("priority").count().as("count"),
                Aggregation.project("count").and("_id").as("priority")
        );

        AggregationResults<org.bson.Document> results = mongoTemplate.aggregate(byPriority, "tasks",
                org.bson.Document.class);

        Map<String, Long> tasksByPriority = new HashMap<>();

        for (org.bson.Document doc : results) {
            tasksByPriority.put(doc.getString("priority"), doc.get("count",Number.class).longValue());
        }
        return tasksByPriority;
    }

    Map<String, Long> getTaskCountByTag(String userId) {
        Aggregation byTag = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(userId)),
                Aggregation.unwind("$tags"),
                Aggregation.group("tags").count().as("count"),
                Aggregation.project("count").and("_id").as("tag")
        );

        AggregationResults<org.bson.Document> results = mongoTemplate.aggregate(byTag, "tasks",
                org.bson.Document.class);

        Map<String, Long> tasksByTag = new HashMap<>();

        for (org.bson.Document doc : results) {
            tasksByTag.put(doc.getString("tag"), doc.get("count",Number.class).longValue());
        }
        return tasksByTag;
    }
}
