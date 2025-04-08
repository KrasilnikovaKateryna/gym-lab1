package org.gym;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class JsonExportImportTest {

    private ObjectMapper mockMapper;
    private JsonDataIO<Visitor> dataIOWithMock;
    private File mockFile;

    private List<Visitor> mockVisitors;
    private List<Visitor> realVisitors;

    @BeforeEach
    public void setup() {
        mockMapper = mock(ObjectMapper.class);
        dataIOWithMock = new JsonDataIO<>(Visitor[].class, mockMapper);

        mockFile = mock(File.class);

        mockVisitors = new ArrayList<>();
        mockVisitors.add(new Visitor("+3801111111", "Alice"));
        mockVisitors.add(new Visitor("+3801111112", "Bob"));

        realVisitors = new ArrayList<>();
    }


    @Test
    void testExportData() throws IOException {
        dataIOWithMock.exportData(mockVisitors, mockFile);

        verify(mockMapper, times(1))
                .writeValue(mockFile, mockVisitors);
    }

    @Test
    void testExportDataWithSortingMapperWriteValueCalled() throws IOException {
        dataIOWithMock.exportData(mockVisitors, mockFile, Comparator.comparing(Visitor::getPhone));

        verify(mockMapper, times(1))
                .writeValue(eq(mockFile), anyList());
    }

    @Test
    void testExportData_WhenMapperThrowsException() throws IOException {
        doThrow(new IOException("Write error"))
                .when(mockMapper)
                .writeValue(any(File.class), any());

        assertThrows(IOException.class, () ->
                dataIOWithMock.exportData(mockVisitors, mockFile)
        );
    }


    @Test
    void testExportDataNoSorting() throws IOException {
        File tempFile = File.createTempFile("visitors_no_sort", ".json");
        tempFile.deleteOnExit();

        realVisitors.add(new Visitor("+3801111111", "1Name"));
        realVisitors.add(new Visitor("+3801111112", "2Name"));

        JsonDataIO<Visitor> io = new JsonDataIO<>(Visitor[].class);
        io.exportData(realVisitors, tempFile);

        String actualJson = Files.readString(tempFile.toPath());

        assertTrue(actualJson.contains("\"name\" : \"1Name\""), actualJson);
        assertTrue(actualJson.contains("\"name\" : \"2Name\""), actualJson);
    }

    @Test
    void testExportDataWithSorting() throws IOException {
        File tempFile = File.createTempFile("visitors_sort", ".json");
        tempFile.deleteOnExit();

        realVisitors.add(new Visitor("+3801111111", "Zebra"));
        realVisitors.add(new Visitor("+3801111112", "Alpha"));

        JsonDataIO<Visitor> io = new JsonDataIO<>(Visitor[].class);
        io.exportData(realVisitors, tempFile, Comparator.comparing(Visitor::getName));

        String actualJson = Files.readString(tempFile.toPath());

        int posAlpha  = actualJson.indexOf("\"name\" : \"Alpha\"");
        int posZebra = actualJson.indexOf("\"name\" : \"Zebra\"");
        assertTrue(posAlpha < posZebra, "Alpha should appear before Zebra in JSON");
    }

    @Test
    void testExportDataWithMembership() throws IOException {
        File tempFile = File.createTempFile("visitors_membership", ".json");
        tempFile.deleteOnExit();

        Gym g = new Gym("TestGym", "Somewhere");
        Visitor v = new Visitor("+3801111111", "Name");
        new Membership(v, g, "1 month", LocalDate.of(2025, 4, 7));
        realVisitors.add(v);

        JsonDataIO<Visitor> io = new JsonDataIO<>(Visitor[].class);
        io.exportData(realVisitors, tempFile);

        String actualJson = Files.readString(tempFile.toPath());
        System.out.println(actualJson);

        assertTrue(actualJson.contains("\"duration\" : \"ONE_MONTH\""), actualJson);
        assertTrue(actualJson.contains("\"startDate\" : \"2025-04-07\""), actualJson);
    }


    @Test
    void testImportDataMapperReadValueCalled() throws IOException {
        Visitor[] visitorArray = { new Visitor("+3801111111", "MockUser") };
        when(mockMapper.readValue(mockFile, Visitor[].class)).thenReturn(visitorArray);

        List<Visitor> result = dataIOWithMock.importData(mockFile);

        verify(mockMapper, times(1))
                .readValue(mockFile, Visitor[].class);
        assertEquals(1, result.size());
        assertEquals("MockUser", result.get(0).getName());
        assertEquals("+3801111111", result.get(0).getPhone());
    }

    @Test
    void testImportDataWhenMapperThrowsException() throws IOException {
        when(mockMapper.readValue(mockFile, Visitor[].class))
                .thenThrow(new IOException("Read error"));

        assertThrows(IOException.class, () -> dataIOWithMock.importData(mockFile));
    }


    @Test
    void testImportData() throws IOException {
        File tempFile = File.createTempFile("import_visitors", ".json");
        tempFile.deleteOnExit();

        String json = """
        [
          {
            "phone": "+3801111111",
            "name": "Test1",
            "memberships": [],
            "visits": [],
            "bookedSessions": {}
          },
          {
            "phone": "+380222222222",
            "name": "Test2",
            "memberships": [],
            "visits": [],
            "bookedSessions": {}
          }
        ]
        """;
        Files.writeString(tempFile.toPath(), json);

        JsonDataIO<Visitor> realIO = new JsonDataIO<>(Visitor[].class);

        List<Visitor> result = realIO.importData(tempFile);
        assertEquals(2, result.size());
        assertEquals("Test1", result.get(0).getName());
        assertEquals("+3801111111", result.get(0).getPhone());
        assertEquals("Test2", result.get(1).getName());
        assertEquals("+380222222222", result.get(1).getPhone());
    }

    @Test
    void testImportDataParseMemberships() throws IOException {
        File tempFile = File.createTempFile("import_memberships", ".json");
        tempFile.deleteOnExit();

        String json = """
        [
          {
            "phone": "+38011111111",
            "name": "HasMembership",
            "memberships": [
              {
                "visitorPhone": "+38011111111",
                "gymName": "GymName",
                "duration": "ONE_MONTH",
                "startDate": "2025-04-07",
                "endDate": "2025-05-07"
              }
            ],
            "visits": [],
            "bookedSessions": {}
          }
        ]
        """;

        Files.writeString(tempFile.toPath(), json);

        JsonDataIO<Visitor> realIO = new JsonDataIO<>(Visitor[].class);
        List<Visitor> result = realIO.importData(tempFile);

        assertEquals(1, result.size());
        Visitor got = result.get(0);
        assertEquals("HasMembership", got.getName());
        assertEquals("+38011111111", got.getPhone());
        assertEquals(1, got.getMemberships().size());

        Membership mem = got.getMemberships().get(0);
        assertEquals("ONE_MONTH", mem.getDuration().name());
        assertEquals(LocalDate.of(2025, 4, 7), mem.getStartDate());
        assertEquals(LocalDate.of(2025, 5, 7), mem.getEndDate());
    }

    @Test
    void testImportDataEmptyFileThrowsException() throws IOException {
        File tempFile = File.createTempFile("empty", ".json");
        tempFile.deleteOnExit();

        JsonDataIO<Visitor> realIO = new JsonDataIO<>(Visitor[].class);

        assertThrows(IOException.class, () -> realIO.importData(tempFile));
    }

    // Import/export of each entity

    @Test
    void testExportCoaches(@TempDir File tempDir) throws IOException {
        File outFile = new File(tempDir, "coaches.json");

        Coach c1 = new Coach("+380222222222", "John", "Cardio");
        Coach c2 = new Coach("+380333333333", "Eve",  "Yoga");
        c1.scheduleSession(LocalDateTime.now().plusDays(1), "+380222222222");
        c2.scheduleSession(LocalDateTime.now().plusDays(2), "+380333333333");

        JsonDataIO<Coach> coachIO = new JsonDataIO<>(Coach[].class);
        coachIO.exportData(new ArrayList<>(List.of(c1, c2)), outFile, Comparator.comparing(Coach::getName));

        assertTrue(outFile.exists() && outFile.length() > 0);
        String json = Files.readString(outFile.toPath());
        assertTrue(json.contains("\"name\" : \"Eve\""), json);
    }

    @Test
    void testImportCoaches(@TempDir File tempDir) throws IOException {
        File inFile = new File(tempDir, "coaches_import.json");

        String json = """
        [
          {
            "name": "AlphaCoach",
            "specialization": "Strength",
            "trainingSchedule": {}
          },
          {
            "name": "BetaCoach",
            "specialization": "Cardio",
            "trainingSchedule": {}
          }
        ]
        """;

        Files.writeString(inFile.toPath(), json);

        JsonDataIO<Coach> coachIO = new JsonDataIO<>(Coach[].class);
        List<Coach> list = coachIO.importData(inFile);

        assertEquals(2, list.size());
        assertEquals("AlphaCoach", list.get(0).getName());
        assertEquals("Strength",   list.get(0).getSpecialization());
        assertEquals("BetaCoach",  list.get(1).getName());
    }


    @Test
    void testExportGyms(@TempDir File tempDir) throws IOException {
        File outFile = new File(tempDir, "gyms_export.json");

        Gym g1 = new Gym("FitGym", "Address1");
        Gym g2 = new Gym("PowerGym", "Address2");
        g1.addVisitor(new Visitor("+3801111111", "Alice"));
        g2.addCoach(new Coach("+380222222222", "Bob", "Crossfit"));

        JsonDataIO<Gym> gymIO = new JsonDataIO<>(Gym[].class);
        gymIO.exportData(List.of(g1, g2), outFile);

        assertTrue(outFile.exists() && outFile.length() > 0);
        String content = Files.readString(outFile.toPath());

        assertTrue(content.contains("FitGym"), content);
        assertTrue(content.contains("PowerGym"), content);
    }

    @Test
    void testImportGyms(@TempDir File tempDir) throws IOException {
        File inFile = new File(tempDir, "gyms_import.json");

        String json = """
        [
          {
            "name" : "TestGymA",
            "address" : "Street A",
            "visitHistory" : [],
            "coaches" : {},
            "visitors" : { "+380888888888" : "Visitor" }
          },
          {
            "name" : "TestGymB",
            "address" : "Street B",
            "visitHistory" : [],
            "coaches" : { "+380777777777" : "Trainer" },
            "visitors" : {}
          }
        ]
        """;
        Files.writeString(inFile.toPath(), json);

        JsonDataIO<Gym> gymIO = new JsonDataIO<>(Gym[].class);
        List<Gym> list = gymIO.importData(inFile);

        for (Gym g : list) {
            System.out.println(g.toString());
        }

        assertEquals(2, list.size());
        assertEquals("TestGymA", list.get(0).getName());
        assertEquals("Street A", list.get(0).getAddress());
        assertNotNull(list.get(0).getAllVisitors().get("+380888888888"));
        assertNotNull(list.get(1).getAllCoaches().get("+380777777777"));
    }


    @Test
    void testExportMemberships(@TempDir File tempDir) throws IOException {
        File outFile = new File(tempDir, "membership_export.json");

        Membership m1 = new Membership("+380888888888", "GymName", "ONE_MONTH",
                LocalDate.of(2025, 4, 1));
        Membership m2 = new Membership("+3801111111", "GymName", "THREE_MONTHS",
                LocalDate.of(2025, 6, 1));


        JsonDataIO<Membership> memIO = new JsonDataIO<>(Membership[].class);
        memIO.exportData(List.of(m1, m2), outFile);

        String outJson = Files.readString(outFile.toPath());
        assertTrue(outJson.contains("\"visitorPhone\" : \"+380888888888\""), outJson);
        assertTrue(outJson.contains("THREE_MONTHS"), outJson);
    }

    @Test
    void testImportMemberships(@TempDir File tempDir) throws IOException {
        File inFile = new File(tempDir, "membership_import.json");

        String json = """
        [
          {
            "visitorPhone": "+380888888888",
            "gymName": "GymName1",
            "duration": "ONE_MONTH",
            "startDate": "2025-01-01",
            "endDate": "2025-02-01"
          },
          {
            "visitorPhone": "+380222222222",
            "gymName": "GymName2",
            "duration": "THREE_MONTHS",
            "startDate": "2025-03-01",
            "endDate": "2025-06-01"
          }
        ]
        """;
        Files.writeString(inFile.toPath(), json);

        JsonDataIO<Membership> memIO = new JsonDataIO<>(Membership[].class);
        List<Membership> list = memIO.importData(inFile);

        assertEquals(2, list.size());
        assertEquals("+380888888888", list.get(0).getVisitorPhone());
        assertEquals(MembershipDuration.ONE_MONTH, list.get(0).getDuration());
        assertEquals("+380222222222", list.get(1).getVisitorPhone());
        assertEquals(MembershipDuration.THREE_MONTHS, list.get(1).getDuration());
    }


    @Test
    void testExportVisits(@TempDir File tempDir) throws IOException {
        File outFile = new File(tempDir, "visits_export.json");

        // Два визита
        Visit v1 = new Visit(LocalDateTime.of(2025, 4, 10, 9, 0), "+3801111111");
        Visit v2 = new Visit(LocalDateTime.now(), "+380222222222");

        JsonDataIO<Visit> visitIO = new JsonDataIO<>(Visit[].class);
        visitIO.exportData(List.of(v1, v2), outFile);

        String text = Files.readString(outFile.toPath());
        assertTrue(text.contains("\"visitorPhone\" : \"+3801111111\""), text);
        assertTrue(text.contains("\"visitorPhone\" : \"+380222222222\""), text);
    }

    @Test
    void testImportVisits(@TempDir File tempDir) throws IOException {
        File inFile = new File(tempDir, "visits_import.json");

        String json = """
        [
          {
            "dateTime": "2025-04-10T09:00:00",
            "visitorPhone": "+380222222222"
          },
          {
            "dateTime": "2025-04-11T10:30:00",
            "visitorPhone": "+380888888888"
          }
        ]
        """;
        Files.writeString(inFile.toPath(), json);

        JsonDataIO<Visit> visitIO = new JsonDataIO<>(Visit[].class);
        List<Visit> visits = visitIO.importData(inFile);

        assertEquals(2, visits.size());
        assertEquals("+380222222222", visits.get(0).getVisitorPhone());
        assertEquals(LocalDateTime.of(2025,4,11,10,30), visits.get(1).getDateTime());
    }


    @Test
    void testExportVisitors(@TempDir File tempDir) throws IOException {
        File outFile = new File(tempDir, "visitors_export.json");

        Visitor vi1 = new Visitor("+3801111111", "Alice");
        Visitor vi2 = new Visitor("+3801111112", "Bob");

        new Membership(vi1, new Gym("G1","Addr1"), "1 month",
                LocalDate.of(2025,5,5));

        JsonDataIO<Visitor> visitorIO = new JsonDataIO<>(Visitor[].class);
        visitorIO.exportData(List.of(vi1, vi2), outFile);

        String text = Files.readString(outFile.toPath());
        assertTrue(text.contains("\"name\" : \"Alice\""), text);
        assertTrue(text.contains("\"memberships\" :"), text);
    }

    @Test
    void testImportVisitors(@TempDir File tempDir) throws IOException {
        File inFile = new File(tempDir, "visitors_import.json");

        String json = """
        [
          {
            "phone": "+3801111111",
            "name": "VName1",
            "memberships": [],
            "visits": [],
            "bookedSessions": {}
          },
          {
            "phone": "+380222222222",
            "name": "VName2",
            "memberships": [
              {
                "visitorPhone": "+380222222222",
                "gymName": "GymName",
                "duration": "ONE_MONTH",
                "startDate": "2025-05-05",
                "endDate": "2025-06-04"
              }
            ],
            "visits": [],
            "bookedSessions": {}
          }
        ]
        """;
        Files.writeString(inFile.toPath(), json);

        JsonDataIO<Visitor> visitorIO = new JsonDataIO<>(Visitor[].class);
        List<Visitor> visitors = visitorIO.importData(inFile);

        assertEquals(2, visitors.size());
        assertEquals("VName1", visitors.get(0).getName());
        assertEquals("+3801111111", visitors.get(0).getPhone());
        assertEquals("VName2", visitors.get(1).getName());
        assertEquals("+380222222222", visitors.get(1).getPhone());
        assertEquals(1, visitors.get(1).getMemberships().size());
        assertEquals("+380222222222", visitors.get(1).getMemberships().get(0).getVisitorPhone());
    }
}
