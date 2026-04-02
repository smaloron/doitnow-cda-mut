package com.example.doitnow.dto;

import lombok.Data;
import java.util.Map;

@Data
public class TaskStats {

    private long total;
    private long completed;
    private long pending;
    private long overdue;
    private double completionRate = 0.0;
    private Map<String, Long> taskByPriority;
    private Map<String, Long> taskByTag;
}
