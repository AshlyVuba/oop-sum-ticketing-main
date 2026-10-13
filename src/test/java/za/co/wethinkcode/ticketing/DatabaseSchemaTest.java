package za.co.wethinkcode.ticketing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseSchemaTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = Database.connect(":memory:");
        DatabaseSchema.createSchema(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    // --- Q5.1 columns and types ---

    @Test
    void venuesTable_hasCorrectColumnsAndTypes() throws SQLException {
        assertEquals(Map.of(
                "id", "INTEGER",
                "name", "TEXT",
                "city", "TEXT"), columnTypes("venues"));
    }

    @Test
    void eventsTable_hasCorrectColumnsAndTypes() throws SQLException {
        assertEquals(Map.of(
                "id", "INTEGER",
                "venue_id", "INTEGER",
                "title", "TEXT",
                "starts_at", "TEXT",
                "capacity", "INTEGER"), columnTypes("events"));
    }

    @Test
    void ticketsTable_hasCorrectColumnsAndTypes() throws SQLException {
        assertEquals(Map.of(
                "id", "INTEGER",
                "event_id", "INTEGER",
                "seat_label", "TEXT",
                "price", "REAL",
                "scanned_at", "TEXT"), columnTypes("tickets"));
    }

    // --- Q5.2 primary keys and NOT NULL ---

    @Test
    void everyTableHasIdAsItsOnlyPrimaryKey() throws SQLException {
        for (String table : new String[]{"venues", "events", "tickets"}) {
            Map<String, Integer> pk = columnFlags(table, "pk");
            assertFalse(pk.isEmpty(), "the " + table + " table does not exist yet");
            for (Map.Entry<String, Integer> column : pk.entrySet()) {
                assertEquals(column.getKey().equals("id"), column.getValue() > 0,
                        table + "." + column.getKey() + " primary key flag");
            }
        }
    }

    @Test
    void venuesTable_notNullIsAppliedAsInTheErd() throws SQLException {
        Map<String, Integer> notNull = columnFlags("venues", "notnull");
        assertEquals(1, notNull.get("name"));
        assertEquals(1, notNull.get("city"));
    }

    @Test
    void eventsTable_notNullIsAppliedAsInTheErd() throws SQLException {
        Map<String, Integer> notNull = columnFlags("events", "notnull");
        assertEquals(1, notNull.get("venue_id"));
        assertEquals(1, notNull.get("title"));
        assertEquals(1, notNull.get("starts_at"));
        assertEquals(1, notNull.get("capacity"));
    }

    @Test
    void ticketsTable_notNullIsAppliedAsInTheErd_andScannedAtIsOptional() throws SQLException {
        Map<String, Integer> notNull = columnFlags("tickets", "notnull");
        assertEquals(1, notNull.get("event_id"));
        assertEquals(1, notNull.get("seat_label"));
        assertEquals(1, notNull.get("price"));
        assertEquals(0, notNull.get("scanned_at"), "a ticket has not been scanned until the event, so scanned_at is optional");
    }

    // --- Q5.3 foreign keys ---

    @Test
    void eventsTable_referencesVenues() throws SQLException {
        assertEquals(Map.of("venue_id", "venues.id"), foreignKeys("events"));
    }

    @Test
    void ticketsTable_referencesEvents() throws SQLException {
        assertEquals(Map.of("event_id", "events.id"), foreignKeys("tickets"));
    }

    // --- Q5.4 composite UNIQUE and CHECK ---

    @Test
    void aSeatCanOnlyBeSoldOncePerEvent() throws SQLException {
        seedVenueAndEvents();
        insertTicket(1, 1, "A1");
        assertThrows(SQLException.class, () -> insertTicket(2, 1, "A1"));
    }

    @Test
    void theSameSeatCanBeSoldForADifferentEvent() throws SQLException {
        seedVenueAndEvents();
        insertTicket(1, 1, "A1");
        insertTicket(2, 2, "A1");
        assertEquals(2, count("tickets"));
    }

    @Test
    void differentSeatsCanBeSoldForTheSameEvent() throws SQLException {
        seedVenueAndEvents();
        insertTicket(1, 1, "A1");
        insertTicket(2, 1, "A2");
        assertEquals(2, count("tickets"));
    }

    @Test
    void anEventNeedsACapacityGreaterThanZero() throws SQLException {
        insertVenue(1);
        assertThrows(SQLException.class, () -> insertEvent(1, 1, 0));
        assertThrows(SQLException.class, () -> insertEvent(2, 1, -50));
    }

    @Test
    void anEventWithAPositiveCapacityIsAccepted() throws SQLException {
        insertVenue(1);
        insertEvent(1, 1, 500);
        assertEquals(1, count("events"));
    }

    // --- helpers ---

    private void seedVenueAndEvents() throws SQLException {
        insertVenue(1);
        insertEvent(1, 1, 500);
        insertEvent(2, 1, 800);
    }

    private void insertVenue(int id) throws SQLException {
        try (PreparedStatement s = connection.prepareStatement(
                "INSERT INTO venues (id, name, city) VALUES (?, 'Ellis Park', 'Johannesburg')")) {
            s.setInt(1, id);
            s.executeUpdate();
        }
    }

    private void insertEvent(int id, int venueId, int capacity) throws SQLException {
        try (PreparedStatement s = connection.prepareStatement(
                "INSERT INTO events (id, venue_id, title, starts_at, capacity) VALUES (?, ?, 'Derby day', '2026-11-01 19:30', ?)")) {
            s.setInt(1, id);
            s.setInt(2, venueId);
            s.setInt(3, capacity);
            s.executeUpdate();
        }
    }

    private void insertTicket(int id, int eventId, String seat) throws SQLException {
        try (PreparedStatement s = connection.prepareStatement(
                "INSERT INTO tickets (id, event_id, seat_label, price) VALUES (?, ?, ?, 350.0)")) {
            s.setInt(1, id);
            s.setInt(2, eventId);
            s.setString(3, seat);
            s.executeUpdate();
        }
    }

    private int count(String table) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            assertTrue(resultSet.next());
            return resultSet.getInt(1);
        }
    }

    private Map<String, String> foreignKeys(String table) throws SQLException {
        Map<String, String> references = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA foreign_key_list(" + table + ")")) {
            while (resultSet.next()) {
                references.put(resultSet.getString("from"),
                        resultSet.getString("table") + "." + resultSet.getString("to"));
            }
        }
        return references;
    }

    private Map<String, String> columnTypes(String table) throws SQLException {
        Map<String, String> types = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (resultSet.next()) {
                types.put(resultSet.getString("name"), resultSet.getString("type").toUpperCase());
            }
        }
        return types;
    }

    private Map<String, Integer> columnFlags(String table, String flagColumn) throws SQLException {
        Map<String, Integer> flags = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (resultSet.next()) {
                flags.put(resultSet.getString("name"), resultSet.getInt(flagColumn));
            }
        }
        return flags;
    }
}
