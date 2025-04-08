package org.gym;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;


public class Membership {
    private String visitorPhone;
    private String gymName;

    private MembershipDuration duration;
    private LocalDate startDate;
    private LocalDate endDate;

    @JsonIgnore
    public Membership(Visitor visitor, Gym gym, String durationLabel, LocalDate startDate) {
        this.visitorPhone = visitor.getPhone();
        this.gymName = gym.getName();
        this.duration = MembershipDuration.fromLabel(durationLabel);
        this.startDate = startDate;
        this.endDate = startDate.plusDays(this.duration.getDurationDays());

        visitor.addMembership(this);
    }

    @JsonCreator
    public Membership(@JsonProperty("visitor") String visitorPhone,
                      @JsonProperty("gym") String gymName,
                      @JsonProperty("duration") String durationLabel,
                      @JsonProperty("startDate") LocalDate startDate) {
        this.visitorPhone = visitorPhone;
        this.gymName = gymName;
        this.duration = MembershipDuration.fromLabel(durationLabel);
        this.startDate = startDate;
        this.endDate = startDate.plusDays(this.duration.getDurationDays());
    }

    public String getVisitorPhone() {
        return visitorPhone;
    }

    public String getGymName() {
        return gymName;
    }

    public MembershipDuration getDuration() {
        return duration;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return (startDate != null && endDate != null)
                && ( !today.isBefore(startDate) ) // today >= startDate
                && today.isBefore(endDate);       // today < endDate
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitorPhone, gymName, startDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Membership that)) return false;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public String toString() {
        return "Membership {hash=" + this.hashCode()
                + ", visitor=" + visitorPhone
                + ", gym=" + gymName
                + ", duration=" + duration.getLabel()
                + ", active=" + isActive()
                + ", startDate=" + startDate
                + ", endDate=" + endDate
                + '}';
    }
}
