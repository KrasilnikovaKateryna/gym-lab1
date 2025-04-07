package org.gym;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Храним дату визита и hashCode посетителя, а также зал (gymHash).
 * Это аналог старого Visit(Visitor, Gym), но без рекурсии.
 */
public class Visit {
    private LocalDateTime dateTime;
    private int visitorHash;
    private int gymHash; // Укажем, в какой зал

    public Visit(Visitor visitor, Gym gym) {
        this.dateTime = LocalDateTime.now();
        this.visitorHash = visitor.hashCode();
        this.gymHash = gym.hashCode();
    }

    public Visit(LocalDateTime dateTime, int visitorHash, int gymHash) {
        this.dateTime = dateTime;
        this.visitorHash = visitorHash;
        this.gymHash = gymHash;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getVisitorHash() {
        return visitorHash;
    }

    public int getGymHash() {
        return gymHash;
    }

    /**
     * Для уникальности возьмём hash(dateTime, visitorHash, gymHash).
     */
    @Override
    public int hashCode() {
        return Objects.hash(dateTime, visitorHash, gymHash);
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
                + ", visitorHash=" + visitorHash
                + ", gymHash=" + gymHash
                + '}';
    }
}
