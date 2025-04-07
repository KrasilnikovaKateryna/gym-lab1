package org.gym;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Gym:
 *  - name, address
 *  - списки хэшей посетителей (visitorHashes) и тренеров (coachHashes)
 *  - история визитов (List<Visit>)
 */
public class Gym {
    private static final int MAX_VISITORS = 999;
    private static final int MAX_COACHES = 100;

    private String name;
    private String address;

    // Храним hashCode посетителей
    private List<Integer> visitorHashes = new ArrayList<>();
    // Храним hashCode тренеров
    private List<Integer> coachHashes = new ArrayList<>();
    // История визитов
    private List<Visit> visitHistory = new ArrayList<>();

    public Gym(String name, String address) {
        this.name = name;
        this.address = address;
    }

    /**
     * Добавить визит в историю.
     * (В каждом Visit хранится visitorHash, gymHash).
     */
    public void addVisit(Visit visit) {
        if (visitHistory.contains(visit)) {
            throw new IllegalArgumentException("You cannot add the same visit twice");
        }
        visitHistory.add(visit);
    }

    /**
     * Зарегистрировать посетителя (visitor.hashCode()) в зале.
     */
    public void addVisitor(Visitor visitor) {
        int vHash = visitor.hashCode();
        if (visitorHashes.size() >= MAX_VISITORS) {
            throw new IllegalStateException("Max number of visitors is " + MAX_VISITORS);
        }
        if (!visitorHashes.contains(vHash)) {
            visitorHashes.add(vHash);
        }
    }

    /**
     * Зарегистрировать тренера.
     */
    public void addCoach(Coach coach) {
        int cHash = coach.hashCode();
        if (coachHashes.size() >= MAX_COACHES) {
            throw new IllegalStateException("Max number of coaches is " + MAX_COACHES);
        }
        if (!coachHashes.contains(cHash)) {
            coachHashes.add(cHash);
        }
    }

    /**
     * Удалить посетителя по хэш-коду (если надо).
     */
    public void removeVisitor(int visitorHash) {
        visitorHashes.remove(Integer.valueOf(visitorHash));
    }

    /**
     * Удалить тренера по хэш-коду.
     */
    public void removeCoach(int coachHash) {
        coachHashes.remove(Integer.valueOf(coachHash));
    }

    // == Геттеры/Сеттеры ==
    public List<Visit> getVisitHistory() {
        return visitHistory;
    }

    public List<Integer> getVisitorHashes() {
        return visitorHashes;
    }

    public List<Integer> getCoachHashes() {
        return coachHashes;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getInfo() {
        return "Gym: " + name + ", address: " + address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gym that)) return false;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public String toString() {
        return "Gym {hash=" + this.hashCode()
                + ", name='" + name + '\''
                + ", address='" + address + '\''
                + ", visitorsCount=" + visitorHashes.size()
                + ", coachesCount=" + coachHashes.size()
                + '}';
    }
}
