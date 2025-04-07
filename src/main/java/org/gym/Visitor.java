package org.gym;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Visitor {

    private String name;
    private List<Membership> memberships = new ArrayList<>();
    private List<GymVisitRecord> visits = new ArrayList<>();
    private Map<Coach, List<LocalDateTime>> bookedSessions = new HashMap<>();

    public Visitor(String name) {
        this.name = name;
    }
    public Visitor(String name, Membership membership) {
        this.name = name;
        this.memberships.add(membership);
    }

    public void addMembership(Membership membership) {
        if (memberships.contains(membership)) {
            throw new IllegalArgumentException("This membership already exists.");
        }
        this.memberships.add(membership);
    }

    public void visitGym(Gym gym) {
        visits.add(new GymVisitRecord(gym));
    }

    public void bookSession(Coach coach, LocalDateTime dateTime) {
        bookedSessions.computeIfAbsent(coach, k -> new ArrayList<>());

        if (bookedSessions.get(coach).contains(dateTime)) {
            throw new IllegalArgumentException("Coach " + coach.getName() + " is training at " + dateTime);
        }

        bookedSessions.get(coach).add(dateTime);
    }

    public void removeSession(Coach coach, LocalDateTime dateTime) {
        List<LocalDateTime> sessions = bookedSessions.get(coach);
        if (sessions != null) {
            sessions.remove(dateTime);
            if (sessions.isEmpty()) {
                bookedSessions.remove(coach);
            }
        }
    }

    public void showCoachSessions() {
        System.out.println("Training Sessions for " + name + ":");
        for (Map.Entry<Coach, List<LocalDateTime>> entry : bookedSessions.entrySet()) {
            System.out.println("- " + entry.getKey().getName() + ":");
            for (LocalDateTime dateTime : entry.getValue()) {
                System.out.println("  * " + dateTime);
            }
        }
    }

    public void showVisitHistory() {
        System.out.println("Visit history of " + name + ":");
        for (GymVisitRecord visit : visits) {
            System.out.println("- " + visit.getDateTime());
        }
    }

    public String getInfo() {
        return "Name: " + name + ", membership: " +
                (hasActiveMembership() ? "active" : "no");
    }

    public String getName() {
        return name;
    }

    public boolean hasActiveMembership() {
        return memberships.stream()
                .anyMatch(m -> m.getEndDate().isAfter(LocalDate.now()));
    }

    public boolean hasActiveMembership(Gym gym) {
        return memberships.stream().filter(m -> m.getGym().equals(gym))
                .anyMatch(m -> m.getEndDate().isAfter(LocalDate.now()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visitor visitor)) return false;
        return Objects.equals(name, visitor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Visitor {" +
                "name='" + name + '\'' +
                ", memberships=" + memberships.size() +
                ", visits=" + visits.size() +
                ", bookedSessions=" + bookedSessions.values().stream().mapToInt(List::size).sum() +
                '}';
    }
}
