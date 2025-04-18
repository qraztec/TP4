package application;

import databasePart1.DatabaseHelper;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MessagesTest is a JUnit test class for verifying message-related features:
 * sending messages, handling invalid content, reading messages, tracking
 * unread counts, and properly closing the database connection.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessagesTest {

    private static DatabaseHelper dbHelper;

    /**
     * Establishes the database connection before any tests are run.
     */
    @BeforeAll
    public static void setUpClass() throws SQLException {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
    }

    /**
     * Closes the database connection after all tests have completed.
     */
    @AfterAll
    public static void tearDownClass() {
        dbHelper.closeConnection();
    }

    /**
     * Test 1: Test sending a valid message.
     * Expects the insert to succeed, and the message to exist in the DB.
     */
    @Test
    @Order(1)
    public void testSendValidMessage() {
        String sender = "senderUser";
        String recipient = "recipientUser";
        String content = "This is a test message.";
        try {
            dbHelper.sendMessage(sender, recipient, content);
            boolean messageExists = dbHelper.doesMessageExist(recipient, content);
            assertTrue(messageExists, "Message should be inserted and found in the database.");
        } catch (SQLException e) {
            fail("Exception during valid message creation: " + e.getMessage());
        }
    }

    /**
     * Test 2: Test sending a message with invalid content (empty string).
     * Expects the DB or code to reject it or throw an exception.
     */
    @Test
    @Order(2)
    public void testSendInvalidMessage() {
        String sender = "senderUser";
        String recipient = "recipientUser";
        String content = ""; // invalid
        try {
            dbHelper.sendMessage(sender, recipient, content);
            boolean messageExists = dbHelper.doesMessageExist(recipient, content);
            fail("Expected an exception for invalid content, but the DB accepted it.");
        } catch (SQLException | IllegalArgumentException e) {
            // This is success: we expected an exception or some failure
            assertTrue(true, "Invalid message was rejected as expected: " + e.getMessage());
        }
    }

    /**
     * Test 3: Test reading messages for a user.
     * Expects the method to return non-empty if messages exist.
     */
    @Test
    @Order(3)
    public void testReadingMessages() {
        String recipient = "recipientUser";
        try {
            // Insert a message to ensure it exists
            dbHelper.sendMessage("senderUser", recipient, "Reading test message");
            String messages = dbHelper.getMessagesForUser(recipient).toString();
            assertNotNull(messages, "Messages string should not be null.");
            assertFalse(messages.isEmpty(), "Messages string should not be empty if a message was inserted.");
        } catch (SQLException e) {
            fail("Exception during message reading: " + e.getMessage());
        }
    }

    /**
     * Test 4: Test retrieving unread messages count.
     * Clears old messages for the user, then inserts known messages, checks the count.
     */
    @Test
    @Order(4)
    public void testUnreadMessagesCount() {
        String recipient = "recipientUser";
        try {
            // Clear all previous messages
            dbHelper.clearMessagesForUser(recipient);

            // Insert exactly 2 new messages
            dbHelper.sendMessage("senderUser", recipient, "First unread message");
            dbHelper.sendMessage("senderUser", recipient, "Second unread message");

            int unreadCount = dbHelper.getUnreadMessageCount(recipient);
            assertEquals(2, unreadCount, "Expected exactly 2 unread messages for this user.");
        } catch (SQLException e) {
            fail("Exception during unread messages count check: " + e.getMessage());
        }
    }

    /**
     * Test 5: Tests disconnecting from the database by closing the connection
     * and then verifying no exception is thrown. (In a real scenario, you might
     * re-check the connection state.)
     */
    @Test
    @Order(5)
    public void testCloseConnection() {
        // We'll re-open the DB to test closing it again
        DatabaseHelper tempHelper = new DatabaseHelper();
        try {
            tempHelper.connectToDatabase();
            tempHelper.closeConnection();
            // If we got here, it presumably closed without error
            assertTrue(true, "Database connection closed successfully without exception.");
        } catch (SQLException e) {
            fail("Exception during connection close: " + e.getMessage());
        }
    }
}
