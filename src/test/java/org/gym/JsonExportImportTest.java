package org.gym;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class JsonExportImportTest {
    private ObjectMapper mockMapper;
    private JsonDataIO<Visitor> dataIO;
    private File mockFile;
    private List<Visitor> mockVisitors;
    private List<Visitor> visitors;

    @BeforeEach
    public void setup() {
        mockMapper = mock(ObjectMapper.class);
        dataIO = new JsonDataIO<>(Visitor[].class, mockMapper);
        mockFile = mock(File.class);
        mockVisitors = Arrays.asList(new Visitor("Alice"), new Visitor("Bob"));
        visitors = new ArrayList<>();
    }

    @Test
    public void testExportDataCallsWriteValue() throws IOException {
        dataIO.exportData(mockVisitors, mockFile);
        verify(mockMapper, times(1)).writeValue(mockFile, mockVisitors);
    }

    @Test
    public void testExportDataNoFilter() throws IOException {
        File tempFile = File.createTempFile("visitors", ".json");
        tempFile.deleteOnExit();

        visitors.add(new Visitor("1Name"));
        visitors.add(new Visitor("2Name"));
        JsonDataIO<Visitor> io = new JsonDataIO<>(Visitor[].class);
        io.exportData(visitors, tempFile);

        String actualJson = Files.readString(tempFile.toPath());

        String expectedJson = """
            [
              {
                "name": "1Name",
                "memberships": [],
                "bookedSessions": {},
                "visits": []
              },
              {
                "name": "2Name",
                "memberships": [],
                "bookedSessions": {},
                "visits": []
              }
            ]""";

        ObjectMapper mapper = new ObjectMapper();
        var actualNode = mapper.readTree(actualJson);
        var expectedNode = mapper.readTree(expectedJson);

        assertEquals(expectedNode, actualNode);
    }

    @Test
    public void testExportDataFilterName() throws IOException {
        File tempFile = File.createTempFile("visitors", ".json");
        tempFile.deleteOnExit();

        visitors.add(new Visitor("2Name"));
        visitors.add(new Visitor("1Name"));

        JsonDataIO<Visitor> io = new JsonDataIO<>(Visitor[].class);
        io.exportData(visitors, tempFile, Comparator.comparing(Visitor::getName));

        String actualJson = Files.readString(tempFile.toPath());

        String expectedJson = """
            [
              {
                "name": "1Name",
                "memberships": [],
                "visits": [],
                "bookedSessions": {}
              },
              {
                "name": "2Name",
                "memberships": [],
                "visits": [],
                "bookedSessions": {}
              }
            ]""";

        ObjectMapper mapper = new ObjectMapper();
        var actualNode = mapper.readTree(actualJson);
        var expectedNode = mapper.readTree(expectedJson);

        assertEquals(expectedNode, actualNode);
    }

    @Test
    public void testExportDataFullContent() throws IOException {
        File tempFile = File.createTempFile("visitors", ".json");
        tempFile.deleteOnExit();

        Gym gym = new Gym("Gym", "Address");
        LocalDate startDate = LocalDate.now();
        Visitor visitor = new Visitor("Name");
        gym.addVisit(new Visit(visitor, gym));
        new Membership(visitor, gym, "1 month", startDate);
        visitors.add(visitor);

        JsonDataIO<Visitor> io = new JsonDataIO<>(Visitor[].class);
        io.exportData(visitors, tempFile, Comparator.comparing(Visitor::getName));

        String actualJson = Files.readString(tempFile.toPath());

        LocalDateTime nowDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String now = nowDateTime.format(formatter);

        String expectedJson = String.format("""
            [
              {
                   "name": "Name",
                   "memberships": [
                     {
                       "startDate": "2025-04-07",
                       "owner": "Name",
                       "gym": {
                         "name": "Gym",
                         "address": "Address",
                         "visitors": ["Name"],
                         "coaches": [],
                         "visitHistory": [
                           {
                             "dateTime": "%s",
                             "visitor": "Name",
                             "gym": "Gym"
                           }
                         ]
                       },
                       "duration": "ONE_MONTH",
                       "endDate": "2025-05-07",
                       "active": true
                     }
                   ],
                   "visits": [],
                   "bookedSessions": {}
              }
            ]""", now);

        ObjectMapper mapper = new ObjectMapper();
        var actualNode = mapper.readTree(actualJson);
        var expectedNode = mapper.readTree(expectedJson);

        System.out.println(actualNode);

        assertEquals(expectedNode, actualNode);
    }

    @Test
    public void testExportDataThrowsIOException() throws IOException {
        doThrow(new IOException("Write error")).when(mockMapper).writeValue(any(File.class), any());

        assertThrows(IOException.class, () ->
                dataIO.exportData(mockVisitors, mockFile, Comparator.comparing(Visitor::getName)));
    }

    @Test
    public void testImportDataCallsReadValue() throws IOException {
        Visitor[] visitorArray = {new Visitor("Test")};
        when(mockMapper.readValue(mockFile, Visitor[].class)).thenReturn(visitorArray);

        List<Visitor> result = dataIO.importData(mockFile);
        verify(mockMapper, times(1)).readValue(mockFile, Visitor[].class);
        assert result.size() == 1;
    }

    @Test
    public void testImportDataReturnsListOfObjects() throws IOException {
        File tempFile = File.createTempFile("visitors_test", ".json");
        tempFile.deleteOnExit();

        String json = """
        [
          {
            "name": "Test1",
            "memberships": [],
            "bookedSessions": {},
            "visits": []
          },
          {
            "name": "Test2",
            "memberships": [],
            "bookedSessions": {},
            "visits": []
          }
        ]
        """;

        Files.writeString(tempFile.toPath(), json);

        JsonDataIO<Visitor> realIO = new JsonDataIO<>(Visitor[].class);

        List<Visitor> result = realIO.importData(tempFile);

        assertEquals(2, result.size());
        assertEquals("Test1", result.get(0).getName());
        assertEquals("Test2", result.get(1).getName());
    }

    @Test
    public void testImportDataThrowsIOException() throws IOException {
        when(mockMapper.readValue(mockFile, Visitor[].class)).thenThrow(new IOException("Read error"));

        assertThrows(IOException.class, () -> dataIO.importData(mockFile));
    }
}
