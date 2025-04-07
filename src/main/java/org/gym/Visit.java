package org.gym;

import java.time.LocalDateTime;
import java.util.Objects;

public class Visit {
    private LocalDateTime dateTime;
    private Visitor visitor;
    private Gym gym;

    public Visit(Visitor visitor, Gym gym) {
        this.dateTime = LocalDateTime.now();
        this.visitor = visitor;
        this.gym = gym;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
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
