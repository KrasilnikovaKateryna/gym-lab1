package org.gym;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Visitor:
 *  - name
 *  - хранит списки своих абонементов (Membership) — без рекурсии
 *  - хранит даты визитов
 *  - хранит карту (coachHash -> List<LocalDateTime>) для забронированных тренировок
 */
public class Visitor {
    private String name;

    /**
     * Вместо "List<Membership>" ссылающегося на Visitor
     * хранится список Membership-объектов, которые, в свою очередь,
     * содержат только hashCode (visitorHash/gymHash).
     */
    private List<Membership> memberships = new ArrayList<>();

    /**
     * Список дат посещения (LocalDateTime).
     */
    private List<LocalDateTime> visits = new ArrayList<>();

    /**
     * Забронированные сессии: coachHash -> список дат
     */
    private Map<Integer, List<LocalDateTime>> bookedSessions = new HashMap<>();

    public Visitor(String name) {
        this.name = name;
    }

    /**
     * Проверяем, есть ли активный абонемент в данном зале (gym.hashCode()).
     */
    public boolean hasActiveMembership(int gymHash) {
        return memberships.stream()
                .anyMatch(m -> m.getGymHash() == gymHash && m.isActive());
    }

    /**
     * Возвращаем список всех абонементов, которые ещё активны.
     */
    public List<Membership> getActiveMemberships() {
        return memberships.stream()
                .filter(Membership::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Добавляем абонемент к списку (чтобы не было дубликатов).
     */
    public void addMembership(Membership membership) {
        if (memberships.contains(membership)) {
            throw new IllegalArgumentException("This membership already exists.");
        }
        memberships.add(membership);
    }

    /**
     * Посещение зала: проверяем, есть ли активный абонемент,
     * если да — создаём новый Visit и добавляем его в gym.
     */
    public void visitGym(Gym gym) {
        int gymHash = gym.hashCode();
        if (!hasActiveMembership(gymHash)) {
            throw new IllegalStateException("Visitor does not have an active membership in this gym.");
        }
        // Регистрируем визит
        Visit fullVisit = new Visit(this, gym);
        gym.addVisit(fullVisit);
        visits.add(LocalDateTime.now());
    }

    /**
     * Записаться на тренировку к coach.
     * Ставим запись у coach (scheduleSession), и в bookedSessions (по coach.hashCode()).
     */
    public void bookSession(Coach coach, LocalDateTime dateTime) {
        int coachHash = coach.hashCode();
        List<LocalDateTime> sessions = bookedSessions.computeIfAbsent(coachHash, k -> new ArrayList<>());
        if (sessions.contains(dateTime)) {
            throw new IllegalArgumentException("Coach " + coach.getName()
                    + " is already booked at " + dateTime + " by this visitor.");
        }
        // Записываемся у coach (visitor.hashCode())
        coach.scheduleSession(dateTime, this.hashCode());
        // Добавляем дату у себя
        sessions.add(dateTime);
    }

    /**
     * Удалить запись на тренировку, если нужно.
     * (Coach при cancelSession сам не вызывает у visitor removeSession,
     *  но можно вручную).
     */
    public void removeSession(Coach coach, LocalDateTime dateTime) {
        int coachHash = coach.hashCode();
        List<LocalDateTime> sessions = bookedSessions.get(coachHash);
        if (sessions != null) {
            sessions.remove(dateTime);
            if (sessions.isEmpty()) {
                bookedSessions.remove(coachHash);
            }
        }
    }

    /**
     * Показать все тренировки (coachHash -> список дат).
     */
    public void showSessions() {
        System.out.println("Training Sessions for " + name + ":");
        for (Map.Entry<Integer, List<LocalDateTime>> entry : bookedSessions.entrySet()) {
            int cHash = entry.getKey();
            System.out.println("- coachHash=" + cHash + ":");
            for (LocalDateTime dt : entry.getValue()) {
                System.out.println("  * " + dt);
            }
        }
    }

    /**
     * Показать историю визитов (LocalDateTime).
     */
    public void showVisitHistory() {
        System.out.println("Visit history of " + name + ":");
        for (LocalDateTime visit : visits) {
            System.out.println("- " + visit);
        }
    }

    /**
     * Есть ли *хотя бы один* действующий абонемент (в любом зале).
     */
    public boolean hasActiveMembership() {
        return memberships.stream()
                .anyMatch(Membership::isActive);
    }

    // == Геттеры, equals/hashCode, toString ==

    public String getName() {
        return name;
    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public List<LocalDateTime> getVisits() {
        return visits;
    }

    public Map<Integer, List<LocalDateTime>> getBookedSessions() {
        return bookedSessions;
    }

    /**
     * Уникальный hash (на основе имени).
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visitor that)) return false;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public String toString() {
        return "Visitor {hash=" + this.hashCode()
                + ", name='" + name + '\''
                + ", memberships=" + memberships.size()
                + ", visits=" + visits.size()
                + ", bookedSessions=" + bookedSessions.values().stream().mapToInt(List::size).sum()
                + '}';
    }
}
