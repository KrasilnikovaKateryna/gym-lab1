package org.gym;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Coach {
    private String name;
    private String specialization;
    private Map<LocalDateTime, Visitor> trainingSchedule = new HashMap<>();

    public Coach(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void scheduleSession(LocalDateTime dateTime, Visitor visitor) {
        if (trainingSchedule.containsKey(dateTime)) {
            throw new IllegalArgumentException("Coach " + name + " is already booked at " + dateTime);
        }
        trainingSchedule.put(dateTime, visitor);
    }

    public void cancelSession(LocalDateTime dateTime) {
        Visitor visitor = trainingSchedule.remove(dateTime);
        if (visitor != null) {
            visitor.removeSession(this, dateTime);
        } else {
            throw new IllegalArgumentException("There is no training session at this time.");
        }
    }

    public void showSchedule() {
        System.out.println("Training schedule for " + name + ":");
        trainingSchedule.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.println(entry.getKey() + " → " + entry.getValue().getName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coach coach)) return false;
        return Objects.equals(name, coach.name) &&
                Objects.equals(specialization, coach.specialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, specialization);
    }
}
