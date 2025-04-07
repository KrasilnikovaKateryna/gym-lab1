package org.gym;

import java.time.LocalDateTime;

public class GymVisitRecord {
    private LocalDateTime dateTime;
    private Gym gym;

    public GymVisitRecord(Gym gym) {
        this.dateTime = LocalDateTime.now();
        this.gym = gym;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Gym getGym() {
        return gym;
    }

    @Override
    public String toString() {
        return dateTime + " — " + gym.getName();
    }
}
