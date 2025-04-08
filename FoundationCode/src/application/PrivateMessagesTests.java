package application;

import databasePart1.DatabaseHelper;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PrivateMessagesTests {
    private static DatabaseHelper dbHelper;
    private static PrivateMessages pm;

    @BeforeAll
    static void setup() {
        dbHelper = new DatabaseHelper();
        try {
            dbHelper.connectToDatabase();
            pm = new PrivateMessages(dbHelper);
        } catch (SQLException e) {
            fail("DB Connection Error: " + e.getMessage());
        }
    }

    @AfterAll
    static void teardown() {
        dbHelper.closeConnection();
    }

    @Test
    void testSendAndRetrieveMessage() {
        try {
            pm.sendMessage("Reviewer1", "User1", "Hello from test!");
            List<String> messages = dbHelper.getMessagesForUser("User1");
            assertTrue(messages.stream().anyMatch(m -> m.contains("Hello from test!")));
        } catch (SQLException e) {
            fail("Unexpected SQL error: " + e.getMessage());
        }
    }

    @Test
    void testClearMessages() {
        try {
            pm.sendMessage("Reviewer2", "UserToClear", "Message to delete");
            pm.clearMessages("UserToClear");
            List<String> messages = dbHelper.getMessagesForUser("UserToClear");
            assertTrue(messages.isEmpty());
        } catch (SQLException e) {
            fail("SQL Error during clearMessages: " + e.getMessage());
        }
    }

    @Test
    void testMarkMessagesAsRead() {
        try {
            pm.sendMessage("Reviewer3", "UserToRead", "Unread test message");
            pm.markMessagesAsRead("UserToRead");
            int count = dbHelper.getUnreadMessageCount("UserToRead");
            assertEquals(0, count);
        } catch (SQLException e) {
            fail("SQL Error in markMessagesAsRead: " + e.getMessage());
        }
    }

    @Test
    void testUnreadMessageCount() {
        try {
            pm.sendMessage("Reviewer4", "UserUnread", "Unread test");
            int count = dbHelper.getUnreadMessageCount("UserUnread");
            assertTrue(count > 0);
        } catch (SQLException e) {
            fail("SQL Error in unreadMessageCount: " + e.getMessage());
        }
    }
}