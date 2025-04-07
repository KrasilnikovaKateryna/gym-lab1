package org.gym;

import java.time.LocalDateTime;

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
    public String toString() {
        return "Date: " + dateTime + "Visitor: " + visitor.getName() + "Gym: " + gym.getInfo();
    }
}
