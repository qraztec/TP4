package application;

import databasePart1.DatabaseHelper;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A collection of JUnit tests for the Chat class, verifying creation
 * and retrieval of chat messages under various conditions (minimum,
 * average, maximum content length, etc.).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatTests {

    private static DatabaseHelper dbHelper;

    /**
     * Initializes the DatabaseHelper and sets up any needed test data.
     * Runs once before all tests.
     */
    @BeforeAll
    public static void setUpClass() throws SQLException {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
        // Optionally clear or prepare the Chats table if needed
        // e.g., dbHelper.execute("DELETE FROM Chats");
    }

    /**
     * Closes the database connection after all tests complete.
     */
    @AfterAll
    public static void tearDownClass() {
        dbHelper.closeConnection();
    }

    /**
     * Test 1: Create a Chat message with minimal content (e.g., 1 character).
     */
    @Test
    @Order(1)
    public void testCreateChatMinimumContent() throws SQLException {
        String minContent = "A"; // minimal content
        Chat chat = new Chat(1, "Staff", "staffUser", minContent);
        chat.create(dbHelper);

        assertTrue(chat.getId() > 0, 
            "Chat ID should be > 0 after insertion. Minimal content should still be valid.");
    }

    /**
     * Test 2: Create a Chat message with average content (e.g., ~20 characters).
     */
    @Test
    @Order(2)
    public void testCreateChatAverageContent() throws SQLException {
        String avgContent = "This is average text";
        Chat chat = new Chat(2, "Instructor", "instructorUser", avgContent);
        chat.create(dbHelper);

        assertTrue(chat.getId() > 0,
            "Chat ID should be > 0 after insertion. Average content should be valid.");
    }

    /**
     * Test 3: Create a Chat message with maximum content (testing boundary length).
     * Adjust length as needed if your DB field has a specific max length.
     */
    @Test
    @Order(3)
    public void testCreateChatMaximumContent() throws SQLException {
        // Suppose the content column in DB can handle 255 chars.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            sb.append("X");
        }
        String maxContent = sb.toString();

        Chat chat = new Chat(3, "Staff", "maxContentUser", maxContent);
        chat.create(dbHelper);

        assertTrue(chat.getId() > 0,
            "Chat ID should be > 0 after insertion. Max content within DB limit should succeed.");
    }

    /**
     * Test 4: Read all chats for a given chat room ID.
     * Ensures the method returns a list, even if empty, without throwing an exception.
     */
    @Test
    @Order(4)
    public void testReadAllForChatRoom() throws SQLException {
        int testRoomId = 1;
        List<Chat> chats = Chat.readAllForChatRoom(dbHelper, testRoomId);

        // We don't know exactly how many chats are in room #1, but we can check for no errors.
        assertNotNull(chats, "Should return a non-null list of Chats.");
        // Optionally check size or content if you inserted known data.
    }


}
