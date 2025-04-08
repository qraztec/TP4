package application;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;

/**
 * AutomatedTesting is used to test the Messages portion in the 
 * DataBaseHelper. The tests check if the message is being sent or its validity,
 * if the message is being read, if its marked as unread, and if the database
 * connection is being disconnected.
 */
public class MessagesTest {

    static int numPassed = 0;  // Counter for passed tests
    static int numFailed = 0;  // Counter for failed tests

    public static void main(String[] args) {
        System.out.println("______________________________________");
        System.out.println("\nMessages Testing Automation");

        // Test case 1: Test sending a valid message.
        performCreateTestCase(1, "senderUser", "recipientUser", "This is a test message.", true);

        // Test case 2: Test sending a message with invalid content (empty string).
        performCreateTestCase(2, "senderUser", "recipientUser", "", false);

        // Test case 3: Test reading messages.
        performReadTestCase(3, "recipientUser", true);

        // Test case 4: Test retrieving unread messages count.
        performUnreadTestCase(4, "recipientUser", 2, true);

        // Test case 5: Test closing the connection properly.
        performCloseTestCase(5, true);

        System.out.println("____________________________________________________________________________");
        System.out.println("\nNumber of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
    }

    /**
     * Tests sending a message
     * @param testCase: The test case number
     * @param sender: The sender of the message
     * @param recipient: The recipient of the message
     * @param messageContent: The content of the message
     * @param expectedPass true if the test is expected to pass; false otherwise
     */
    private static void performCreateTestCase(int testCase, String sender, String recipient, String messageContent, boolean expectedPass) {
        System.out.println("____________________________________________________________________________\nTest case: " + testCase);
        System.out.println("Message content: \"" + messageContent + "\"");

        DatabaseHelper dbHelper = new DatabaseHelper();

        try {
            dbHelper.connectToDatabase();
            dbHelper.sendMessage(sender, recipient, messageContent);

            // Check if the message was inserted correctly
            boolean messageExists = dbHelper.doesMessageExist(recipient, messageContent);
            if (messageExists) {
                if (expectedPass) {
                    System.out.println("***Success***: Message sent and found successfully.");
                    numPassed++;
                } else {
                    System.out.println("***Failure***: Message was unexpectedly accepted.");
                    numFailed++;
                }
            } else {
                if (expectedPass) {
                    System.out.println("***Failure***: Message was not found after sending.");
                    numFailed++;
                } else {
                    System.out.println("***Success***: Invalid message was rejected as expected.");
                    numPassed++;
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            if (!expectedPass) {
                System.out.println("***Success***: Invalid message was rejected: " + e.getMessage());
                numPassed++;
            } else {
                System.out.println("***Failure***: Exception during message creation: " + e.getMessage());
                numFailed++;
            }
        } finally {
            dbHelper.closeConnection();
        }
    }

    /**
     * Tests reading messages for a user
     * @param testCase: The test case number
     * @param recipient: The recipient whose messages are to be read
     * @param expectedPass true if the test is expected to pass; false otherwise
     */
    private static void performReadTestCase(int testCase, String recipient, boolean expectedPass) {
        System.out.println("____________________________________________________________________________\nTest case: " + testCase);

        DatabaseHelper dbHelper = new DatabaseHelper();

        try {
            dbHelper.connectToDatabase();
            String messages = dbHelper.getMessagesForUser(recipient).toString();

            if (messages != null && !messages.isEmpty()) {
                if (expectedPass) {
                    System.out.println("***Success***: Messages read successfully.");
                    numPassed++;
                } else {
                    System.out.println("***Failure***: Messages should not have been found.");
                    numFailed++;
                }
            } else {
                if (expectedPass) {
                    System.out.println("***Failure***: No messages found when there should be some.");
                    numFailed++;
                } else {
                    System.out.println("***Success***: No messages found as expected.");
                    numPassed++;
                }
            }
        } catch (SQLException e) {
            System.out.println("***Failure***: Exception during message reading: " + e.getMessage());
            numFailed++;
        } finally {
            dbHelper.closeConnection();
        }
    }

    /**
     * Tests retrieving unread messages for a user.
     * @param testCase: The test case number
     * @param recipient: The recipient whose unread messages count is to be checked
     * @param expectedCount: The expected count of unread messages
     * @param expectedPass true if the test is expected to pass; false otherwise
     */
    private static void performUnreadTestCase(int testCase, String recipient, int expectedCount, boolean expectedPass) {
        System.out.println("____________________________________________________________________________\nTest case: " + testCase);

        DatabaseHelper dbHelper = new DatabaseHelper();

        try {
            dbHelper.connectToDatabase();

            // Clear all previous messages for this recipient before testing
            dbHelper.clearMessagesForUser(recipient);
            //System.out.println("Debug: Cleared old messages before test");

            // Insert exactly 2 new messages
            dbHelper.sendMessage("senderUser", recipient, "First unread message");
            dbHelper.sendMessage("senderUser", recipient, "Second unread message");

            int unreadCount = dbHelper.getUnreadMessageCount(recipient);
            //System.out.println("Debug: Retrieved unread message count = " + unreadCount);

            if (unreadCount == expectedCount) {
                if (expectedPass) {
                    System.out.println("***Success***: Correct unread message count.");
                    numPassed++;
                } else {
                    System.out.println("***Failure***: Unread message count should not match.");
                    numFailed++;
                }
            } else {
                if (expectedPass) {
                    System.out.println("***Failure***: Expected " + expectedCount + " but found " + unreadCount);
                    numFailed++;
                } else {
                    System.out.println("***Success***: Unread message count was expected to be different.");
                    numPassed++;
                }
            }
        } catch (SQLException e) {
            System.out.println("***Failure***: Exception during unread message count check: " + e.getMessage());
            numFailed++;
        } finally {
            dbHelper.closeConnection();
        }
    }


    /**
     * Tests disconnecting from the database
     * @param testCase: The test case number
     * @param expectedPass true if the test is expected to pass; false otherwise
     */
    private static void performCloseTestCase(int testCase, boolean expectedPass) {
        System.out.println("____________________________________________________________________________\nTest case: " + testCase);

        DatabaseHelper dbHelper = new DatabaseHelper();

        try {
            dbHelper.connectToDatabase();
            dbHelper.closeConnection();
            System.out.println("***Success***: Database connection closed successfully.");
            numPassed++;
        } catch (Exception e) {
            System.out.println("***Failure***: Exception during connection close: " + e.getMessage());
            numFailed++;
        }
    }
}