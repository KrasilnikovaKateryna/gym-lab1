package org.gym;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;


public class Visit {
    private LocalDateTime dateTime;
    private String visitorPhone;

    @JsonIgnore
    public Visit(Visitor visitor) {
        this.dateTime = LocalDateTime.now();
        this.visitorPhone = visitor.getPhone();
    }

    @JsonCreator
    public Visit(@JsonProperty("dateTime") LocalDateTime dateTime,
                 @JsonProperty("visitorHash") String visitorPhone) {
        this.dateTime = dateTime;
        this.visitorPhone = visitorPhone;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getVisitorPhone() {
        return visitorPhone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, visitorPhone);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visit that)) return false;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public String toString() {
        return "Visit {hash=" + this.hashCode()
                + ", dateTime=" + dateTime
                + ", visitor=" + visitorPhone
                + '}';
    }
}
