package org.gym;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Gym {
    private int MAX_VISITORS = 999;
    private int MAX_COACHES = 100;

    private String name;
    private String address;
    private List<Visitor> visitors = new ArrayList<>();
    private List<Coach> coaches = new ArrayList<>();
    private List<Visit> visitHistory = new ArrayList<>();

    public Gym(@JsonProperty("name") String name, @JsonProperty("address") String address) {
        this.name = name;
        this.address = address;
    }

    public void addVisit(Visit visit) {
        long countVisits = visitHistory.stream().filter(v -> v.equals(visit)).count();
        if (countVisits != 0) {
            throw new IllegalArgumentException("You cannot add same visit twice");
        }
        visitHistory.add(visit);
    }

    public void addVisitor(Visitor visitor) {
        if (visitors.size() >= MAX_VISITORS) {
            throw new IllegalStateException("Max number of visitors is " + MAX_VISITORS);
        }
        visitors.add(visitor);
    }

    public List<Visitor> findVisitors(String name) {
        return visitors.stream().filter(v -> v.getName().equals(name)).toList();
    }

    public void deleteVisitor(String name) {
        List<Visitor> visitorsWithName = findVisitors(name);

        if (visitorsWithName.size() > 1) {
            throw new IllegalArgumentException("More than 1 visitor with this name were found");
        }
        if (visitorsWithName.isEmpty()) {
            throw new IllegalArgumentException("No visitors with this name were found");
        }
        visitors.remove(visitorsWithName.get(0));
    }

    public void deleteVisitor(Visitor visitor) {
        boolean removed = visitors.remove(visitor);
        if (!removed) {
            throw new IllegalArgumentException("Visitor not found in the gym");
        }
    }

    public void deleteVisitor(String name, int index) {
        List<Visitor> visitorsWithName = findVisitors(name);
        Visitor visitor = visitorsWithName.get(index);
        boolean removed = visitors.remove(visitor);
        if (!removed) {
            throw new IllegalArgumentException("Visitor not found in the gym");
        }
    }

    public void addCoach(Coach coach) {
        if (coaches.size() >= MAX_COACHES) {
            throw new IllegalStateException("Max number of coaches is " + MAX_COACHES);
        }
        coaches.add(coach);
    }

    public void deleteCoach(String name) {
        List<Coach> coachesWithName = coaches.stream()
                .filter(v -> v.getName().equals(name))
                .toList();

        if (coachesWithName.size() > 1) {
            throw new IllegalArgumentException("More than 1 coach with this name were found");
        }
        if (coachesWithName.isEmpty()) {
            throw new IllegalArgumentException("No coaches with this name were found");
        }
        coaches.remove(coachesWithName.get(0));
    }

    public void deleteCoach(Coach coach) {
        boolean removed = coaches.remove(coach);
        if (!removed) {
            throw new IllegalArgumentException("Coach not found in the gym");
        }
    }

    public List<Visit> getVisitHistory() {
        return visitHistory;
    }

    public List<Visitor> getVisitors() {
        return visitors;
    }

    public List<Coach> getCoaches() {
        return coaches;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @JsonIgnore
    public String getInfo() {
        return "Name: " + name + ", address: " + address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gym gym)) return false;
        return Objects.equals(name, gym.name) &&
                Objects.equals(address, gym.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }

    @Override
    public String toString() {
        return "Gym {name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", visitors=" + visitors.size() +
                ", coaches=" + coaches.size() +
                '}';
    }
}
