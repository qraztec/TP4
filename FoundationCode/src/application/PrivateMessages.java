package application;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

public class PrivateMessages {
    private DatabaseHelper dbHelper;

    public PrivateMessages(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // Method to send a message
    public void sendMessage(String sender, String recipient, String content) {
        try {
            dbHelper.sendMessage(sender, recipient, content);
            System.out.println("Message sent successfully!");
        } catch (SQLException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    // Method to retrieve messages for a specific user
    public void displayMessages(String recipient) {
        try {
            List<String> messages = dbHelper.getMessagesForUser(recipient);
            if (messages.isEmpty()) {
                System.out.println("No messages found for " + recipient);
            } else {
                System.out.println("Messages for " + recipient + ":");
                for (String message : messages) {
                    System.out.println(message);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving messages: " + e.getMessage());
        }
    }

    // Method to clear all messages for a recipient
    public void clearMessages(String recipient) {
        try {
            dbHelper.clearMessagesForUser(recipient);
            System.out.println("All messages cleared for " + recipient);
        } catch (SQLException e) {
            System.err.println("Error clearing messages: " + e.getMessage());
        }
    }

    // Method to mark all messages as read
    public void markMessagesAsRead(String recipient) {
        try {
            dbHelper.markMessagesAsRead(recipient);
            System.out.println("All messages marked as read for " + recipient);
        } catch (SQLException e) {
            System.err.println("Error marking messages as read: " + e.getMessage());
        }
    }

    // Method to get the count of unread messages
    public void displayUnreadMessageCount(String recipient) {
        try {
            int unreadCount = dbHelper.getUnreadMessageCount(recipient);
            System.out.println("Unread messages for " + recipient + ": " + unreadCount);
        } catch (SQLException e) {
            System.err.println("Error fetching unread message count: " + e.getMessage());
        }
    }

}