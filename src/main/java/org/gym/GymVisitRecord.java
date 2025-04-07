package org.gym;

import com.fasterxml.jackson.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "dateTime")
public class GymVisitRecord {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateTime;

    private Gym gym;

    public GymVisitRecord(@JsonProperty("gym") Gym gym) {
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
