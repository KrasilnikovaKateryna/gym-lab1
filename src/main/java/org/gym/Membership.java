package org.gym;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Membership:
 *  - хранит visitorHash, gymHash
 *  - duration, startDate, endDate
 *  - без рекурсии (только int-хэши вместо ссылок на объекты)
 */
public class Membership {
    private int visitorHash;
    private int gymHash; // Чтобы знать, к какому залу относится абонемент

    private MembershipDuration duration;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Конструктор: строим на основе Visitor, Gym, параметров абонемента.
     * Сразу добавляем membership в Visitor (чтобы логика была похожа на «старую»).
     */
    public Membership(Visitor visitor, Gym gym, String durationLabel, LocalDate startDate) {
        this.visitorHash = visitor.hashCode();
        this.gymHash = gym.hashCode();
        this.duration = MembershipDuration.fromLabel(durationLabel);
        this.startDate = startDate;
        this.endDate = startDate.plusDays(this.duration.getDurationDays());

        // После создания добавим этот membership в visitor
        visitor.addMembership(this);
    }

    public Membership(int visitorHash, int gymHash, String durationLabel, LocalDate startDate) {
        this.visitorHash = visitorHash;
        this.gymHash = gymHash;
        this.duration = MembershipDuration.fromLabel(durationLabel);
        this.startDate = startDate;
        this.endDate = startDate.plusDays(this.duration.getDurationDays());
    }

    public int getVisitorHash() {
        return visitorHash;
    }

    public int getGymHash() {
        return gymHash;
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

    /**
     * Переопределяем hashCode как уникальный ID Membership-а
     * (на основе visitorHash, gymHash и startDate).
     */
    @Override
    public int hashCode() {
        return Objects.hash(visitorHash, gymHash, startDate);
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
                + ", visitorHash=" + visitorHash
                + ", gymHash=" + gymHash
                + ", duration=" + duration.getLabel()
                + ", active=" + isActive()
                + ", startDate=" + startDate
                + ", endDate=" + endDate
                + '}';
    }
}
