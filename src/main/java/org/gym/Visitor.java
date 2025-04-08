package org.gym;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class Visitor {
    private String phone;
    private String name;
    private List<Membership> memberships = new ArrayList<>();
    private List<LocalDateTime> visits = new ArrayList<>();
    private Map<String, List<LocalDateTime>> bookedSessions = new HashMap<>();

    @JsonCreator
    public Visitor(@JsonProperty("phone") String phone, @JsonProperty("name") String name) {
        this.phone = phone;
        this.name = name;
    }

    public boolean hasActiveMembership(String gymName) {
        return memberships.stream()
                .anyMatch(m -> Objects.equals(m.getGymName(), gymName) && m.isActive());
    }

    public List<Membership> getActiveMemberships() {
        return memberships.stream()
                .filter(Membership::isActive)
                .collect(Collectors.toList());
    }

    public void addMembership(Membership membership) {
        if (memberships.contains(membership)) {
            throw new IllegalArgumentException("This membership already exists.");
        }
        memberships.add(membership);
    }

    public void visitGym(Gym gym) {
        if (!hasActiveMembership(gym.getName())) {
            throw new IllegalStateException("Visitor does not have an active membership in this gym.");
        }

        Visit fullVisit = new Visit(this);
        gym.addVisit(fullVisit);
        visits.add(LocalDateTime.now());
    }

    public void bookSession(Coach coach, LocalDateTime dateTime) {
        List<LocalDateTime> sessions = bookedSessions.computeIfAbsent(coach.getPhone(), k -> new ArrayList<>());
        if (sessions.contains(dateTime)) {
            throw new IllegalArgumentException("Coach " + coach.getName()
                    + " is already booked at " + dateTime + " by this visitor.");
        }

        coach.scheduleSession(dateTime, this.getPhone());
        sessions.add(dateTime);
    }

    public void removeSession(Coach coach, LocalDateTime dateTime) {
        List<LocalDateTime> sessions = bookedSessions.get(coach.getPhone());
        if (sessions != null) {
            if (sessions.isEmpty()) {
                throw new IllegalArgumentException("This visitor has no sessions with coach " + coach.getPhone());
            }
            sessions.remove(dateTime);
            coach.cancelSession(dateTime);
        }
    }

    public void showSessions() {
        System.out.println("Training Sessions for " + phone + ":");
        for (Map.Entry<String, List<LocalDateTime>> entry : bookedSessions.entrySet()) {
            String cPhone = entry.getKey();
            System.out.println("- coachPhone=" + cPhone + ":");
            for (LocalDateTime dt : entry.getValue()) {
                System.out.println("  * " + dt);
            }
        }
    }

    public void showVisitHistory() {
        System.out.println("Visit history of " + phone + ":");
        for (LocalDateTime visit : visits) {
            System.out.println("- " + visit);
        }
    }

    public boolean hasActiveMembership() {
        return memberships.stream()
                .anyMatch(Membership::isActive);
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public List<LocalDateTime> getVisits() {
        return visits;
    }

    public Map<String, List<LocalDateTime>> getBookedSessions() {
        return bookedSessions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone);
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
                + ", name='" + phone + '\''
                + ", memberships=" + memberships.size()
                + ", visits=" + visits.size()
                + ", bookedSessions=" + bookedSessions.values().stream().mapToInt(List::size).sum()
                + '}';
    }
}
