package org.gym;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "dateTime")
public class Visit {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateTime;

    private Visitor visitor;
    private Gym gym;

    public Visit(@JsonProperty("visitor") Visitor visitor, @JsonProperty("gym") Gym gym) {
        this.dateTime = LocalDateTime.now();
        this.visitor = visitor;
        this.gym = gym;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Gym getGym() {
        return gym;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visit visit)) return false;
        return Objects.equals(dateTime, visit.dateTime)
                && Objects.equals(visitor, visit.visitor) && Objects.equals(gym, visit.gym);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, visitor);
    }

    @Override
    public String toString() {
        return "Date: " + dateTime + "Visitor: " + visitor.getName() + "Gym: " + gym.getInfo();
    }
}
