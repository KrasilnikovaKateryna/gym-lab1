package org.gym;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Coach {
    private String phone;
    private String name;
    private String specialization;
    private Map<LocalDateTime, String> trainingSchedule = new HashMap<>();

    @JsonCreator
    public Coach(@JsonProperty("phone") String phone, @JsonProperty("name") String name,
                 @JsonProperty("specialization") String specialization) {
        this.phone = phone;
        this.name = name;
        this.specialization = specialization;
    }

    public void scheduleSession(LocalDateTime dateTime, String visitorPhone) {
        if (trainingSchedule.containsKey(dateTime)) {
            throw new IllegalArgumentException("Coach " + name + " is already booked at " + dateTime);
        }
        trainingSchedule.put(dateTime, visitorPhone);
    }

    public void cancelSession(LocalDateTime dateTime) {
        if (!trainingSchedule.containsKey(dateTime)) {
            throw new IllegalArgumentException("There is no training session at this time.");
        }
        trainingSchedule.remove(dateTime);
    }

    public String showSchedule() {
        StringBuilder builder = new StringBuilder("Training schedule for " + name + ":\n");
        trainingSchedule.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> builder.append(entry.getKey())
                        .append(" → visitor=")
                        .append(entry.getValue())
                        .append("\n"));
        return builder.toString();
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public Map<LocalDateTime, String> getTrainingSchedule() {
        return trainingSchedule;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, specialization);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coach that)) return false;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public String toString() {
        return "Coach {name='" + name + '\''
                + ", specialization='" + specialization + '\''
                + ", scheduled=" + trainingSchedule.size()
                + '}';
    }
}
