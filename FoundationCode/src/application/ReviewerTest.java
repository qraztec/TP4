package application;

import databasePart1.DatabaseHelper;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ReviewerTest {
    private static DatabaseHelper dbHelper;
    private static Reviewer reviewer;

    @BeforeAll
    static void setup() {
        dbHelper = new DatabaseHelper();
        try {
            dbHelper.connectToDatabase();
            reviewer = new Reviewer("TestReviewer", dbHelper);
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    @AfterAll
    static void teardown() {
        dbHelper.closeConnection();
    }

    @Test
    void testReplyToMessage() {
        try {
            reviewer.replyToMessage("TestRecipient", "Test reply message");
            List<String> messages = dbHelper.getMessagesForUser("TestRecipient");
            assertTrue(messages.stream().anyMatch(msg -> msg.contains("Test reply message")));
        } catch (SQLException e) {
            fail("SQL error during replyToMessage: " + e.getMessage());
        }
    }

    @Test
    void testMarkMessagesAsRead() {
        try {
            reviewer.replyToMessage("ReadUser", "Mark this as read");
            reviewer.markMessagesAsRead();
            int unreadCount = dbHelper.getUnreadMessageCount("TestReviewer");
            assertEquals(0, unreadCount);
        } catch (SQLException e) {
            fail("SQL error during markMessagesAsRead: " + e.getMessage());
        }
    }

    @Test
    void testDisplayUnreadMessageCount() {
        try {
            reviewer.replyToMessage("UnreadUser", "You have a new message!");
            int count = dbHelper.getUnreadMessageCount("UnreadUser");
            assertTrue(count > 0);
        } catch (SQLException e) {
            fail("SQL error during unreadMessageCount: " + e.getMessage());
        }
    }

    @Test
    void testLoadMyReviews() {
        try {
            reviewer.loadMyReviews(dbHelper);
            assertTrue(true);
        } catch (SQLException e) {
            fail("SQL error during loadMyReviews: " + e.getMessage());
        }
    }

}