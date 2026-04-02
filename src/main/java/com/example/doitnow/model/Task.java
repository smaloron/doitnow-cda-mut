package com.example.doitnow.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;
    @Indexed
    private String title;
    private String description;
    private boolean completed;

    @Indexed
    private String userId;

    private Priority priority = Priority.MEDIUM;

    private List<String> tags;

    private LocalDate dueDate;

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    private List<Step> steps = new ArrayList<>();

    /**
     * Recalcule et applique l'auto-complétion.
     * Appelé après chaque modification des steps ou du statut.
     * - Si la liste de steps est vide, la complétion est gérée manuellement.
     * - Si tous les steps sont completed, la tâche devient automatiquement completed.
     * - Dès qu'un step est en attente, la tâche repasse à non-completed.
     */
    public void refreshCompletionFromSteps() {
        if (steps == null || steps.isEmpty()) {
            return; // Pas de steps : complétion manuelle
        }
        this.completed = steps.stream().allMatch(Step::isCompleted);
    }

    /**
     * Taux de réalisation basé sur le ratio de steps completed.
     * Retourne 0 si aucun step n'est défini, ou le pourcentage (0–100) sinon.
     */
    public double getStepsCompletionRate() {
        if (steps == null || steps.isEmpty()) {
            return 0.0;
        }
        long completedCount = steps.stream()
                .filter(Step::isCompleted)
                .count();
        return Math.round((double) completedCount / steps.size() * 10_000.0) / 100.0;
    }
}
