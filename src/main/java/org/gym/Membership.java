package org.gym;

import java.time.LocalDate;
import java.util.Objects;

public class Membership {
    private Visitor owner;
    private Gym gym;
    private MembershipDuration duration;
    private LocalDate startDate;
    private LocalDate endDate;

    public Membership(Visitor owner, Gym gym, String duration, LocalDate startDate) {
        this.owner = owner;
        this.gym = gym;
        this.duration = MembershipDuration.fromLabel(duration);
        this.startDate = startDate;
        this.endDate = startDate.plusDays(this.duration.getDurationDays());
    }

    public MembershipDuration getDuration() {
        return duration;
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return (startDate != null && endDate != null) &&
                (today.isEqual(startDate) || today.isAfter(startDate)) &&
                today.isBefore(endDate);
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public Visitor getOwner() {
        return owner;
    }
    public Gym getGym() {
        return gym;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Membership membership)) return false;
        return Objects.equals(owner, membership.owner) &&
                Objects.equals(gym, membership.gym) && Objects.equals(startDate, membership.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, gym, startDate);
    }

    @Override
    public String toString() {
        return "Membership {" +
                "owner=" + owner.getName() +
                ", gym=" + gym.getName() +
                ", duration=" + duration.getLabel() +
                ", active=" + isActive() +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
