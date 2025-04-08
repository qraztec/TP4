package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;

public class Reviewer {
    private String reviewerName;
    private Reviews reviews;
    private PrivateMessages privateMessages;

    public Reviewer(String reviewerName, DatabaseHelper dbHelper) {
        this.reviewerName = reviewerName;
        this.reviews = new Reviews();
        this.privateMessages = new PrivateMessages(dbHelper);
    }

   
    //Loads and displays all reviews written by this reviewer.
    public void loadMyReviews(DatabaseHelper dbHelper) throws SQLException {
        reviews.loadAllReviews(dbHelper);
        List<Review> myReviews = reviews.searchReviews(reviewerName);
        System.out.println("Reviews by reviewer " + reviewerName + ":");
        for (Review review : myReviews) {
            System.out.println(review);
        }
    }

    
    //Loads and displays private messages sent to this reviewer.
    public void viewPrivateMessages() {
        privateMessages.displayMessages(reviewerName);
    }

    
    //Sends a reply to a private message.
    public void replyToMessage(String recipient, String response) {
        privateMessages.sendMessage(reviewerName, recipient, response);
    }


    //Marks all messages as read.
    public void markMessagesAsRead() {
        privateMessages.markMessagesAsRead(reviewerName);
    }


    //Clears all private messages.
    public void clearMessages() {
        privateMessages.clearMessages(reviewerName);
    }


    //Displays the count of unread messages.
    public void displayUnreadMessageCount() {
        privateMessages.displayUnreadMessageCount(reviewerName);
    }

    public String getReviewerName() {
        return reviewerName;
    }
}