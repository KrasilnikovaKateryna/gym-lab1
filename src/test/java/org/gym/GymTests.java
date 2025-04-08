package org.gym;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class GymTests {

    private Visitor visitor;
    private Coach coach;
    private Gym gym;

    private String visitorPhone;
    private String coachPhone;

    @BeforeEach
    void setUp() {
        visitor = new Visitor("+3801111111", "John");
        coach = new Coach("+380222222222", "Alice", "Yoga");
        gym = new Gym("Test Gym", "Main Street");

        visitorPhone = visitor.getPhone();
        coachPhone = coach.getPhone();
    }

    @Test
    void testVisitorAddMembershipAndGetActiveMemberships() {
        assertTrue(visitor.getActiveMemberships().isEmpty());
        assertFalse(visitor.hasActiveMembership());

        Membership mem = new Membership(visitor, gym, "1 month", LocalDate.now());

        assertTrue(visitor.getMemberships().contains(mem));
        assertTrue(visitor.hasActiveMembership());
        assertEquals(1, visitor.getActiveMemberships().size());
    }

    @Test
    void testVisitorAddSameMembershipTwiceThrows() {
        new Membership(visitor, gym, "1 month", LocalDate.now());
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Membership(visitor, gym, "1 month", LocalDate.now())
        );
        assertTrue(ex.getMessage().contains("This membership already exists."));
    }

    @Test
    void testVisitorVisitGymThrowsIfNoActiveMembership() {
        Exception ex = assertThrows(IllegalStateException.class, () -> visitor.visitGym(gym));
        assertEquals("Visitor does not have an active membership in this gym.", ex.getMessage());
    }

    @Test
    void testVisitorVisitGym() {
        new Membership(visitor, gym, "1 month", LocalDate.now());

        visitor.visitGym(gym);

        assertEquals(1, gym.getVisitHistory().size());
        Visit v = gym.getVisitHistory().get(0);
        assertEquals(visitorPhone, v.getVisitorPhone());
    }

    @Test
    void testVisitorBookSession() {
        gym.addVisitor(visitor);
        gym.addCoach(coach);

        LocalDateTime time = LocalDateTime.now().plusDays(1);
        visitor.bookSession(coach, time);

        assertEquals(visitorPhone, coach.getTrainingSchedule().get(time));
    }

    @Test
    void testVisitorRemoveSession() {
        gym.addVisitor(visitor);
        gym.addCoach(coach);

        LocalDateTime time = LocalDateTime.now().plusDays(1);
        visitor.bookSession(coach, time);


        visitor.removeSession(coach, time);
        assertFalse(coach.getTrainingSchedule().containsKey(time));
    }

    @Test
    void testVisitorBookSessionTimeConflict() {
        gym.addVisitor(visitor);
        gym.addCoach(coach);

        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        visitor.bookSession(coach, sameTime);
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                visitor.bookSession(coach, sameTime));
        assertTrue(ex.getMessage().contains("already booked"));
    }

    @Test
    void testCoachScheduleAndCancelSession() {
        LocalDateTime dt = LocalDateTime.now().plusDays(2);

        coach.scheduleSession(dt, visitorPhone);
        assertEquals(visitorPhone, coach.getTrainingSchedule().get(dt));

        coach.cancelSession(dt);
        assertFalse(coach.getTrainingSchedule().containsKey(dt));
    }

    @Test
    void testCoachCancelNoSuchSessionThrows() {
        LocalDateTime dt = LocalDateTime.now().plusDays(1);
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                coach.cancelSession(dt));
        assertEquals("There is no training session at this time.", ex.getMessage());
    }

    @Test
    void testGymAddVisitorAndCoach() {
        assertTrue(gym.getAllVisitors().isEmpty());
        assertTrue(gym.getAllCoaches().isEmpty());

        gym.addVisitor(visitor);
        gym.addCoach(coach);

        assertNotNull(gym.getAllVisitors().get(visitorPhone));
        assertNotNull(gym.getAllCoaches().get(coachPhone));
    }

    @Test
    void testGymAddVisit() {
        Visit visit = new Visit(visitor);
        gym.addVisit(visit);

        assertEquals(1, gym.getVisitHistory().size());
        assertEquals(visit, gym.getVisitHistory().get(0));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                gym.addVisit(visit));
        assertTrue(ex.getMessage().contains("cannot add the same visit twice"));
    }

    @Test
    void testMembershipIsActive() {
        Membership membership = new Membership(visitor, gym, "1 month", LocalDate.now());
        assertTrue(membership.isActive());
    }

    @Test
    void testMembershipExpired() {
        Membership membership = new Membership(visitor, gym, "1 month", LocalDate.now().minusDays(40));
        assertFalse(membership.isActive());
    }

    @Test
    void testMembershipDurationFromLabelValid() {
        MembershipDuration md = MembershipDuration.fromLabel("3 months");
        assertEquals(MembershipDuration.THREE_MONTHS, md);
        assertEquals(90, md.getDurationDays());
    }

    @Test
    void testMembershipDurationFromLabelInvalid() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                MembershipDuration.fromLabel("2 months"));
        assertTrue(ex.getMessage().contains("Unexpected membership duration"));
    }

}
