package org.gym;

import java.time.LocalDateTime;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GymVisitRecord visit)) return false;
        return Objects.equals(dateTime, visit.dateTime)
                && Objects.equals(gym, visit.gym);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, gym);
    }

    @Override
    public String toString() {
        return dateTime + " — " + gym.getName();
    }
}
