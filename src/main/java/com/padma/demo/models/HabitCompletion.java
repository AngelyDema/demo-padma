package com.padma.demo.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.padma.demo.models.HabitHistory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "habit_completions")

public class HabitCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long completionId;

    private LocalDate dateCompleted;

    private String note;

    @ManyToOne
    @JoinColumn(name = "habit_history_id")
    private HabitHistory habitHistories;
}
