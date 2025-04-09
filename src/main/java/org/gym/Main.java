package org.gym;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Gym> gyms = new ArrayList<>();
    private static final JsonDataIO<Gym> gymIO = new JsonDataIO<>(Gym[].class);
    private static final JsonDataIO<Visitor> visitorIO = new JsonDataIO<>(Visitor[].class);
    private static final JsonDataIO<Coach> coachIO = new JsonDataIO<>(Coach[].class);
    private static final JsonDataIO<Visit> visitIO = new JsonDataIO<>(Visit[].class);
    private static final JsonDataIO<Membership> membershipIO = new JsonDataIO<>(Membership[].class);

    public static void main(String[] args) {
        System.out.println("Welcome to the Gym Management Console App!");

        while (true) {
            System.out.println("\nChoose an action:");
            System.out.println("1. Create Gym");
            System.out.println("2. Add Visitor");
            System.out.println("3. Add Coach");
            System.out.println("4. Remove Visitor");
            System.out.println("5. Remove Coach");
            System.out.println("6. Add Membership");
            System.out.println("7. Visit Gym");
            System.out.println("8. Book Training Session");
            System.out.println("9. Cancel Training Session (Coach)");
            System.out.println("10. Remove Session (Visitor)");
            System.out.println("11. Show Gym Info");
            System.out.println("12. Show All Visitors");
            System.out.println("13. Show All Coaches");
            System.out.println("14. Show Visit History (Gym)");
            System.out.println("15. Show Schedule (Coach)");
            System.out.println("16. Show Visitor Sessions");
            System.out.println("17. Show Visitor Visit History");
            System.out.println("18. Show Active Memberships (Visitor)");
            System.out.println("19. Check Active Membership (Visitor)");
            System.out.println("20. Export to JSON");
            System.out.println("21. Import from JSON");
            System.out.println("0. Exit");

            String choice = input("Enter your choice: ");
            if (choice == null) continue;
            switch (choice) {
                case "1" -> createGym();
                case "2" -> addVisitor();
                case "3" -> addCoach();
                case "4" -> removeVisitor();
                case "5" -> removeCoach();
                case "6" -> addMembership();
                case "7" -> visitGym();
                case "8" -> bookSession();
                case "9" -> cancelSession();
                case "10" -> removeSession();
                case "11" -> showGyms();
                case "12" -> showAllVisitors();
                case "13" -> showAllCoaches();
                case "14" -> showVisitHistory();
                case "15" -> showCoachSchedule();
                case "16" -> showVisitorSessions();
                case "17" -> showVisitorVisitHistory();
                case "18" -> showActiveMemberships();
                case "19" -> checkActiveMembership();
                case "20" -> exportData();
                case "21" -> importData();
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static String input(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            System.out.println("Empty input. Returning to main menu.");
            return null;
        }
        return value;
    }

    private static LocalDateTime inputDateTime(String prompt) {
        String dateStr = input(prompt);
        if (dateStr == null) return null;
        try {
            return LocalDateTime.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Returning to main menu.");
            return null;
        }
    }

    private static void showGyms() {
        if (gyms.isEmpty()) {
            System.out.println("No gyms available.");
        }
        for (int i = 0; i < gyms.size(); i++) {
            System.out.println((i + 1) + ". " + gyms.get(i).getName());
        }
    }

    private static Gym selectGym() {
        showGyms();
        int index = inputInt("Select gym number: ", 1, gyms.size());
        return index == -1 ? null : gyms.get(index - 1);
    }

    private static int inputInt(String prompt, int min, int max) {
        String inputStr = input(prompt);
        if (inputStr == null) return -1;
        try {
            int num = Integer.parseInt(inputStr);
            if (num < min || num > max) throw new NumberFormatException();
            return num;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Returning to main menu.");
            return -1;
        }
    }

    private static void createGym() {
        String name = input("Gym name: ");
        String address = input("Address: ");
        if (name == null || address == null) return;
        gyms.add(new Gym(name, address));
        System.out.println("Gym created.");
    }

    private static void addVisitor() {
        Gym gym = selectGym();
        if (gym == null) return;
        String phone = input("Visitor phone: ");
        String name = input("Visitor name: ");
        if (phone == null || name == null) return;
        gym.addVisitor(new Visitor(phone, name));
        System.out.println("Visitor added.");
    }

    private static void addCoach() {
        Gym gym = selectGym();
        if (gym == null) return;
        String phone = input("Coach phone: ");
        String name = input("Coach name: ");
        String spec = input("Specialization: ");
        if (phone == null || name == null || spec == null) return;
        gym.addCoach(new Coach(phone, name, spec));
        System.out.println("Coach added.");
    }

    private static void removeVisitor() {
        Gym gym = selectGym();
        if (gym == null) return;
        String phone = input("Visitor phone to remove: ");
        if (phone == null) return;
        try {
            gym.removeVisitor(phone);
            System.out.println("Visitor removed.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void removeCoach() {
        Gym gym = selectGym();
        if (gym == null) return;
        String phone = input("Coach phone to remove: ");
        if (phone == null) return;
        try {
            gym.removeCoach(phone);
            System.out.println("Coach removed.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addMembership() {
        Gym gym = selectGym();
        if (gym == null) return;
        String phone = input("Visitor phone: ");
        String duration = input("Membership duration (e.g. 1 month): ");
        if (phone == null || duration == null) return;
        Visitor v = gym.getAllVisitors().get(phone);
        if (v == null) {
            System.out.println("Visitor not found.");
            return;
        }
        new Membership(v, gym, duration, LocalDate.now());
        System.out.println("Membership added.");
    }

    private static void visitGym() {
        Gym gym = selectGym();
        if (gym == null) return;
        String phone = input("Visitor phone: ");
        if (phone == null) return;
        Visitor v = gym.getAllVisitors().get(phone);
        if (v == null) {
            System.out.println("Visitor not found.");
            return;
        }
        try {
            v.visitGym(gym);
            System.out.println("Visit recorded.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void bookSession() {
        Gym gym = selectGym();
        if (gym == null) return;
        String vPhone = input("Visitor phone: ");
        String cPhone = input("Coach phone: ");
        LocalDateTime dt = inputDateTime("Session datetime (e.g. 2025-04-07T15:00): ");
        if (vPhone == null || cPhone == null || dt == null) return;
        Visitor v = gym.getAllVisitors().get(vPhone);
        Coach c = gym.getAllCoaches().get(cPhone);
        if (v == null || c == null) {
            System.out.println("Visitor or Coach not found.");
            return;
        }
        v.bookSession(c, dt);
        System.out.println("Session booked.");
    }

    private static void cancelSession() {
        Gym gym = selectGym();
        if (gym == null) return;
        String cPhone = input("Coach phone: ");
        LocalDateTime dt = inputDateTime("Session datetime: ");
        if (cPhone == null || dt == null) return;
        Coach c = gym.getAllCoaches().get(cPhone);
        if (c == null) {
            System.out.println("Coach not found.");
            return;
        }
        c.cancelSession(dt);
        System.out.println("Session canceled.");
    }

    private static void removeSession() {
        Gym gym = selectGym();
        if (gym == null) return;
        String vPhone = input("Visitor phone: ");
        String cPhone = input("Coach phone: ");
        LocalDateTime dt = inputDateTime("Session datetime: ");
        if (vPhone == null || cPhone == null || dt == null) return;
        Visitor v = gym.getAllVisitors().get(vPhone);
        Coach c = gym.getAllCoaches().get(cPhone);
        if (v == null || c == null) {
            System.out.println("Visitor or Coach not found.");
            return;
        }
        v.removeSession(c, dt);
        System.out.println("Session removed.");
    }

    private static void showAllVisitors() {
        Gym gym = selectGym();
        if (gym == null) return;
        gym.getAllVisitors().values().forEach(System.out::println);
    }

    private static void showAllCoaches() {
        Gym gym = selectGym();
        if (gym == null) return;
        gym.getAllCoaches().values().forEach(System.out::println);
    }

    private static void showVisitHistory() {
        Gym gym = selectGym();
        if (gym == null) return;
        gym.getVisitHistory().forEach(System.out::println);
    }

    private static void showCoachSchedule() {
        Gym gym = selectGym();
        if (gym == null) return;
        String phone = input("Coach phone: ");
        if (phone == null) return;
        Coach c = gym.getAllCoaches().get(phone);
        if (c == null) {
            System.out.println("Coach not found.");
            return;
        }
        System.out.println(c.showSchedule());
    }

    private static void showVisitorSessions() {
        Gym gym = selectGym();
        if (gym == null) return;
        String phone = input("Visitor phone: ");
        if (phone == null) return;
        Visitor v = gym.getAllVisitors().get(phone);
        if (v == null) {
            System.out.println("Visitor not found.");
            return;
        }
        v.showSessions();
    }

    private static void showVisitorVisitHistory() {
        Gym gym = selectGym();
        if (gym == null) return;
        String phone = input("Visitor phone: ");
        if (phone == null) return;
        Visitor v = gym.getAllVisitors().get(phone);
        if (v == null) {
            System.out.println("Visitor not found.");
            return;
        }
        v.showVisitHistory();
    }

    private static void showActiveMemberships() {
        Gym gym = selectGym();
        if (gym == null) return;
        String phone = input("Visitor phone: ");
        if (phone == null) return;
        Visitor v = gym.getAllVisitors().get(phone);
        if (v == null) {
            System.out.println("Visitor not found.");
            return;
        }
        System.out.println(v.getActiveMemberships());
    }

    private static void checkActiveMembership() {
        Gym gym = selectGym();
        if (gym == null) return;
        String phone = input("Visitor phone: ");
        if (phone == null) return;
        Visitor v = gym.getAllVisitors().get(phone);
        if (v == null) {
            System.out.println("Visitor not found.");
            return;
        }
        System.out.println("Has active membership: " + v.hasActiveMembership());
    }

    private static void exportData() {
        System.out.println("Choose what to export:");
        System.out.println("1. Gyms");
        System.out.println("2. Visitors");
        System.out.println("3. Coaches");
        System.out.println("4. Visits");
        String choice = input("Enter choice: ");
        if (choice == null) return;
        String fileName = input("Enter file name: ");
        if (fileName == null) return;
        File file = new File(fileName);
        try {
            switch (choice) {
                case "1" -> gymIO.exportData(gyms, file);
                case "2" -> {
                    List<Visitor> allVisitors = new ArrayList<>();
                    for (Gym gym : gyms) {
                        allVisitors.addAll(gym.getAllVisitors().values());
                    }
                    visitorIO.exportData(allVisitors, file);
                }
                case "3" -> {
                    List<Coach> allCoaches = new ArrayList<>();
                    for (Gym gym : gyms) {
                        allCoaches.addAll(gym.getAllCoaches().values());
                    }
                    coachIO.exportData(allCoaches, file);
                }
                case "4" -> {
                    List<Visit> allVisits = new ArrayList<>();
                    for (Gym gym : gyms) {
                        allVisits.addAll(gym.getVisitHistory());
                    }
                    visitIO.exportData(allVisits, file);
                }
                default -> System.out.println("Invalid choice.");
            }
            System.out.println("Export successful.");
        } catch (IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }

    private static void importData() {
        System.out.println("Choose what to import:");
        System.out.println("1. Gyms");
        System.out.println("2. Visitors");
        System.out.println("3. Coaches");
        System.out.println("4. Visits");
        String choice = input("Enter choice: ");
        if (choice == null) return;
        String fileName = input("Enter file name: ");
        if (fileName == null) return;
        File file = new File(fileName);
        try {
            switch (choice) {
                case "1" -> {
                    gyms.addAll(gymIO.importData(file));
                }
                case "2" -> {
                    List<Visitor> vList = visitorIO.importData(file);
                    for (Visitor v : vList) {
                        gyms.get(0).addVisitor(v);
                    }
                }
                case "3" -> {
                    List<Coach> cList = coachIO.importData(file);
                    for (Coach c : cList) {
                        gyms.get(0).addCoach(c);
                    }
                }
                case "4" -> {
                    List<Visit> vList = visitIO.importData(file);
                    for (Visit v : vList) {
                        gyms.get(0).addVisit(v);
                    }
                }
                default -> System.out.println("Invalid choice.");
            }
            System.out.println("Import successful.");
        } catch (IOException | IndexOutOfBoundsException e) {
            System.out.println("Import failed: " + e.getMessage());
        }
    }
}
