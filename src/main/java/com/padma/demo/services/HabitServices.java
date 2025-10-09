package com.padma.demo.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.padma.demo.models.Habit;
import com.padma.demo.models.Usuario;

public class HabitServices {
    private String id;
    private Habit habit;
    private Usuario user;
    private LocalDate completedAt;
    private String notes;
    private LocalDate createdAt;
    private List<HabitServices> completions = new ArrayList<>();
    private int streak = 0;

    public HabitServices(String id, Habit habit, Usuario user, LocalDate completedAt, String notes) {
        this.id = id;
        this.habit = habit;
        this.user = user;
        this.completedAt = completedAt;
        this.notes = notes;
        this.createdAt = LocalDate.now();
    }

    public boolean validarNota() {
        return notes != null && !notes.trim().isEmpty();
    }


    // Método para calcular la racha actual
    public int calculateStreak() {
        if (completions.isEmpty()) return 0;

        completions.sort((a, b) -> b.getCompletedAt().compareTo(a.getCompletedAt()));
        LocalDate today = LocalDate.now();
        int newStreak = 0;  // Contador para la racha

        for (HabitServices completion : completions) {
            LocalDate completionDate = completion.getCompletedAt();
            if (completionDate.equals(today.minusDays(newStreak))) {
                newStreak++;
            } else if (completionDate.isBefore(today.minusDays(newStreak))) {
             newStreak = 1; //Se pone la racha a 1 si rompe el hábito
            }
        }
        this.streak = newStreak; // Actualizar la racha actual
        return newStreak;
    }


 //Para la barra de progreso: el logro se define por una meta de rachas cumplicas (cantidad definida por el usuario también)
    public double calcularPorcentajeLogro(int rachaMeta) {
    int rachaActual = calculateStreak();
    if (rachaMeta <= 0) return 0;

    double porcentaje = ((double) rachaActual / rachaMeta) * 100;
    if (porcentaje > 100) porcentaje = 100; // Limitar al 100%
    return porcentaje;
}

    public void resetStreak() {
        this.streak = 0;
    }

    // Getters y Setters
    public LocalDate getCompletedAt() { return completedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Habit getHabit() { return habit; }
    public void setHabit(Habit habit) { this.habit = habit; }
    public String getId() { return id; }
    public Usuario getUser() { return user; }
    public LocalDate getCreatedAt() { return createdAt; }
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
    public List<HabitServices> getCompletions() { return completions; }
    public void addCompletion(HabitServices completion) {
        completions.add(completion);
        completion.setHabit(this.habit); 
}
}
