package org.gym;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Coach хранит:
 *  - name, specialization
 *  - расписание: Map<LocalDateTime, Integer> (храним hashCode посетителя)
 */
public class Coach {
    private String name;
    private String specialization;

    /**
     * Ключ: время тренировки,
     * Значение: хэш-код (hashCode) Visitor-а.
     */
    private Map<LocalDateTime, Integer> trainingSchedule = new HashMap<>();

    public Coach(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
    }

    /**
     * Записать посетителя (visitorHash) на time.
     */
    public void scheduleSession(LocalDateTime dateTime, int visitorHash) {
        if (trainingSchedule.containsKey(dateTime)) {
            throw new IllegalArgumentException("Coach " + name + " is already booked at " + dateTime);
        }
        trainingSchedule.put(dateTime, visitorHash);
    }

    /**
     * Отменить запись (если есть).
     */
    public void cancelSession(LocalDateTime dateTime) {
        if (!trainingSchedule.containsKey(dateTime)) {
            throw new IllegalArgumentException("There is no training session at this time.");
        }
        trainingSchedule.remove(dateTime);
    }

    /**
     * Печать расписания (показываем только хэш посетителя).
     */
    public String showSchedule() {
        StringBuilder builder = new StringBuilder("Training schedule for " + name + ":\n");
        trainingSchedule.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> builder.append(entry.getKey())
                        .append(" → visitorHash=")
                        .append(entry.getValue())
                        .append("\n"));
        return builder.toString();
    }

    // Геттеры

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public Map<LocalDateTime, Integer> getTrainingSchedule() {
        return trainingSchedule;
    }

    /**
     * Переопределяем hashCode (используем поля name + specialization).
     * Это будет «уникальный ID» Coach-а.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, specialization);
    }

    /**
     * Coach считаются равными, если у них совпадает hashCode.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coach that)) return false;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public String toString() {
        return "Coach {hash=" + this.hashCode()
                + ", name='" + name + '\''
                + ", specialization='" + specialization + '\''
                + ", scheduled=" + trainingSchedule.size()
                + '}';
    }
}
