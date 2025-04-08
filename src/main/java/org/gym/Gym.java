package org.gym;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class Gym {
    private static final int MAX_VISITORS = 999;
    private static final int MAX_COACHES = 100;

    private String name;
    private String address;

    @JsonIgnore
    private Map<String, Visitor> allVisitors = new HashMap<>();

    @JsonIgnore
    private  Map<String, Coach> allCoaches = new HashMap<>();

    private List<Visit> visitHistory = new ArrayList<>();

    @JsonCreator
    public Gym(@JsonProperty("name") String name, @JsonProperty("address") String address) {
        this.name = name;
        this.address = address;
    }

    public void addVisit(Visit visit) {
        if (visitHistory.contains(visit)) {
            throw new IllegalArgumentException("You cannot add the same visit twice");
        }
        visitHistory.add(visit);
    }

    public void addVisitor(Visitor visitor) {
        if (allVisitors.size() >= MAX_VISITORS) {
            throw new IllegalStateException("Max number of visitors is " + MAX_VISITORS);
        }
        if (allVisitors.get(visitor.getPhone()) != null) {
            throw new IllegalArgumentException("Visitor with that phone " + visitor.getPhone() + " already exists");
        }
        allVisitors.put(visitor.getPhone(), visitor);
    }

    public void addCoach(Coach coach) {
        if (allCoaches.size() >= MAX_COACHES) {
            throw new IllegalStateException("Max number of coaches is " + MAX_VISITORS);
        }
        if (allCoaches.get(coach.getPhone()) != null) {
            throw new IllegalArgumentException("Visitor with that phone " + coach.getPhone() + " already exists");
        }
        allCoaches.put(coach.getPhone(), coach);
    }

    public void removeVisitor(String phone) {
        Visitor removed = allVisitors.remove(phone);
        if (removed == null) {
            throw new IllegalArgumentException("Visitor with that phone " + phone + " not found");
        }
    }

    public void removeCoach(String phone) {
        Coach removed = allCoaches.remove(phone);
        if (removed == null) {
            throw new IllegalArgumentException("Coach with that phone " + phone + " not found");
        }
    }

    @JsonProperty("visitors")
    public Map<String, String> getVisitorsPhonesNames() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, Visitor> entry : allVisitors.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getName());
        }
        return map;
    }

    @JsonProperty("visitors")
    public void setVisitorsPhonesNames(Map<String, String> visitorsMap) {
        for (Map.Entry<String, String> entry : visitorsMap.entrySet()) {
            String phone = entry.getKey();
            String name = entry.getValue();
            Visitor v = new Visitor(phone, name);
            allVisitors.put(phone, v);
        }
    }

    @JsonProperty("coaches")
    public Map<String, String> getCoachesPhonesNames() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, Coach> entry : allCoaches.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getName());
        }
        return map;
    }

    @JsonProperty("coaches")
    public void setCoachesPhonesNames(Map<String, String> coachesMap) {
        for (Map.Entry<String, String> entry : coachesMap.entrySet()) {
            String phone = entry.getKey();
            String name = entry.getValue();
            Coach c = new Coach(phone, name, "Unknown");
            allCoaches.put(phone, c);
        }
    }

    public Map<String, Visitor> getAllVisitors() {
        return allVisitors;
    }

    public Map<String, Coach> getAllCoaches() {
        return allCoaches;
    }

    public List<Visit> getVisitHistory() {
        return visitHistory;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @JsonIgnore
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
                + ", visitorsCount=" + allVisitors.size()
                + ", coachesCount=" + allCoaches.size()
                + '}';
    }
}
