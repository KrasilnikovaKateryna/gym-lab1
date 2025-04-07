package org.gym;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class GymTests {

    private Gym gym;
    private Visitor visitor;
    private Coach coach;

    @BeforeEach
    public void setUp() {
        gym = new Gym("Test Gym", "Main St.");
        visitor = new Visitor("John");
        coach = new Coach("Anna", "Cardio");
        gym.addVisitor(visitor);
        gym.addCoach(coach);
    }

    @Test
    public void testAddVisitor() {
        Visitor v = new Visitor("Mike");
        gym.addVisitor(v);
        assertTrue(gym.getVisitors().contains(v));
    }

    @Test
    public void testDeleteVisitorByName() {
        gym.deleteVisitor("John");
        assertFalse(gym.getVisitors().stream().anyMatch(v -> v.getName().equals("John")));
    }

    @Test
    public void testDeleteVisitorByNameNotFoundThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> gym.deleteVisitor("NotFoundName"));
        assertEquals("No visitors with this name were found", ex.getMessage());
    }

    @Test
    public void testDeleteVisitorByNameMoreThanOneFoundThrowsException() {
        Visitor visitor1 = new Visitor("Kate");
        Visitor visitor2 = new Visitor("Kate");
        gym.addVisitor(visitor1);
        gym.addVisitor(visitor2);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> gym.deleteVisitor("Kate"));
        assertEquals("More than 1 visitor with this name were found", ex.getMessage());
    }

    @Test
    public void testDeleteVisitorByObject() {
        gym.deleteVisitor(visitor);
        assertFalse(gym.getVisitors().contains(visitor));
    }

    @Test
    public void testDeleteVisitorByObjectNotFoundThrowsException() {
        Visitor v = new Visitor("NotFoundName");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> gym.deleteVisitor(v));
        assertEquals("Visitor not found in the gym", ex.getMessage());
    }


    @Test
    public void testAddCoach() {
        Coach c = new Coach("Bob", "Strength");
        gym.addCoach(c);
        assertTrue(gym.getCoaches().contains(c));
    }

    @Test
    public void testDeleteCoachByName() {
        gym.deleteCoach("Anna");
        assertFalse(gym.getCoaches().stream().anyMatch(c -> c.getName().equals("Anna")));
    }

    @Test
    public void testDeleteCoachByObject() {
        gym.deleteCoach(coach);
        assertFalse(gym.getCoaches().contains(coach));
    }

    @Test
    public void testDeleteCoachByObjectNotFoundThrowsException() {
        Coach c = new Coach("Ghost", "Shadow");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> gym.deleteCoach(c));
        assertEquals("Coach not found in the gym", ex.getMessage());
    }

    @Test
    public void testVisitGymWithMembership() {
        Membership m = new Membership(visitor, gym, "1 month", LocalDate.now().minusDays(1));

        visitor.visitGym(gym);

        assertEquals(1, gym.getVisitHistory().size());
        assertEquals(1, visitor.getActiveMemberships().size());
    }

    @Test
    public void testVisitGymWithoutMembershipThrowsException() {
        Exception ex = assertThrows(IllegalStateException.class, () -> visitor.visitGym(gym));
        assertEquals("Visitor does not have an active membership in this gym.", ex.getMessage());
    }

    @Test
    public void testBookSession() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        visitor.bookSession(coach, time);
        assertEquals(1, coach.getTrainingSchedule().size());
    }

    @Test
    public void testBookSessionTimeConflictThrowsException() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        visitor.bookSession(coach, time);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> visitor.bookSession(coach, time));
        assertTrue(ex.getMessage().contains("already booked"));
    }

    @Test
    public void testCancelSession() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        visitor.bookSession(coach, time);
        coach.cancelSession(time);
        assertEquals(0, coach.getTrainingSchedule().size());
    }

    @Test
    public void testCancelNonexistentSessionThrowsException() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> coach.cancelSession(time));
        assertEquals("There is no training session at this time.", ex.getMessage());
    }

    @Test
    public void testAddMembership() {
        Membership m = new Membership(visitor, gym, "1 month", LocalDate.now());
        assertTrue(visitor.getActiveMemberships().contains(m));
    }

    @Test
    public void testAddDuplicateMembershipThrowsException() {
        Membership m = new Membership(visitor, gym, "1 month", LocalDate.now());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> visitor.addMembership(m));
        assertEquals("This membership already exists.", ex.getMessage());
    }

    @Test
    public void testMembershipIsActive() {
        Membership m = new Membership(visitor, gym, "1 month", LocalDate.now().minusDays(5));
        assertTrue(m.isActive());
    }

    @Test
    public void testMembershipIsNotActive() {
        Membership m = new Membership(visitor, gym, "1 month", LocalDate.now().minusDays(40));
        assertFalse(m.isActive());
    }

    @Test
    public void testFromLabelValid() {
        MembershipDuration duration = MembershipDuration.fromLabel("1 month");
        assertEquals(MembershipDuration.ONE_MONTH, duration);
    }

    @Test
    public void testFromLabelInvalidThrows() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> MembershipDuration.fromLabel("2 months"));
        assertTrue(ex.getMessage().contains("Unexpected membership duration"));
    }

    @Test
    public void testVisitObjectCreationAndValues() {
        Visit visit = new Visit(visitor, gym);
        assertNotNull(visit.getDateTime());
        assertTrue(visit.toString().contains(visitor.getName()));
        assertTrue(visit.toString().contains(gym.getName()));
    }

    @Test
    public void testVisitEqualityWithSameData() {
        Visit visit1 = new Visit(visitor, gym);
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {}

        Visit visit2 = new Visit(visitor, gym);
        assertNotEquals(visit1, visit2);
    }

    @Test
    public void testGymVisitRecordCreation() {
        GymVisitRecord record = new GymVisitRecord(gym);
        assertNotNull(record.getDateTime());
        assertEquals(gym, record.getGym());
    }

    @Test
    public void testGymVisitRecordToStringIncludesGymName() {
        GymVisitRecord record = new GymVisitRecord(gym);
        assertTrue(record.toString().contains(gym.getName()));
    }

    @Test
    public void testGymVisitRecordEquality() {
        GymVisitRecord record1 = new GymVisitRecord(gym);

        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {}

        GymVisitRecord record2 = new GymVisitRecord(gym);

        assertNotEquals(record1, record2);
    }
}
