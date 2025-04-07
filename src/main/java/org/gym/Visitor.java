package org.gym;

import com.fasterxml.jackson.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Visitor {

    private String name;

    private List<Membership> memberships = new ArrayList<>();

    private List<GymVisitRecord> visits = new ArrayList<>();
    private Map<String, List<LocalDateTime>> bookedSessions = new HashMap<>();

    public Visitor(@JsonProperty("name") String name) {
        this.name = name;
    }

    public Visitor(String name, Membership membership) {
        this.name = name;
        this.memberships.add(membership);
    }

    public String getName() {
        return name;
    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public List<GymVisitRecord> getVisits() {
        return visits;
    }

    public Map<String, List<LocalDateTime>> getBookedSessions() {
        return bookedSessions;
    }

    public void addMembership(Membership membership) {
        if (memberships.contains(membership)) {
            throw new IllegalArgumentException("This membership already exists.");
        }
        this.memberships.add(membership);
    }

    private Stream<Membership> _getActiveMemberships() {
        return memberships.stream()
                .filter(Membership::isActive);
    }

    @JsonIgnore
    public List<Membership> getActiveMemberships() {
        return memberships.stream()
                .filter(Membership::isActive).toList();
    }

    public void visitGym(Gym gym) {
        boolean hasActiveMembershipInThisGym = _getActiveMemberships()
                .anyMatch(m -> m.getGym().equals(gym));

        if (!hasActiveMembershipInThisGym) {
            throw new IllegalStateException("Visitor does not have an active membership in this gym.");
        }

        Visit fullVisit = new Visit(this, gym);
        gym.addVisit(fullVisit);
        visits.add(new GymVisitRecord(gym));
    }

    public void bookSession(Coach coach, LocalDateTime dateTime) {
        String coachName = coach.getName();
        bookedSessions.computeIfAbsent(coachName, k -> new ArrayList<>());

        if (bookedSessions.get(coachName).contains(dateTime)) {
            throw new IllegalArgumentException("Coach " + coachName + " is already booked at " + dateTime + " by this visitor.");
        }

        coach.scheduleSession(dateTime, this);
        bookedSessions.get(coachName).add(dateTime);
    }

    public void removeSession(Coach coach, LocalDateTime dateTime) {
        String coachName = coach.getName();
        List<LocalDateTime> sessions = bookedSessions.get(coachName);
        if (sessions != null) {
            sessions.remove(dateTime);
            if (sessions.isEmpty()) {
                bookedSessions.remove(coachName);
            }
        }
    }

    public void showSessions() {
        System.out.println("Training Sessions for " + name + ":");
        for (Map.Entry<String, List<LocalDateTime>> entry : bookedSessions.entrySet()) {
            System.out.println("- " + entry.getKey() + ":");
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

    @JsonIgnore
    public String getInfo() {
        return "Name: " + name + ", membership: " +
                (hasActiveMembership() ? "active" : "no");
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
