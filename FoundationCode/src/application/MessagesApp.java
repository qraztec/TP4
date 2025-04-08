package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class MessagesApp {

    private DatabaseHelper dbHelper;
    private String currentUser = "studentUser";  

    public MessagesApp(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void show(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label titleLabel = new Label("Messages for Question");
        TextField questionIdField = new TextField();
        questionIdField.setPromptText("Enter Question ID");

        TextArea messagesArea = new TextArea();
        messagesArea.setEditable(false);
        
        TextField messageField = new TextField();
        messageField.setPromptText("Type your message");

        Button loadMessagesButton = new Button("Load Messages");
        Button sendMessageButton = new Button("Send Message");
        Button backButton = new Button("Back");

        // Load Messages button action
        loadMessagesButton.setOnAction(e -> {
            try {
                int questionId = Integer.parseInt(questionIdField.getText().trim());
                loadMessages(questionId, messagesArea);
            } catch (NumberFormatException nfe) {
                messagesArea.appendText("Invalid Question ID. Please enter a valid integer.\n");
            } catch (SQLException ex) {
                messagesArea.appendText("Error loading messages: " + ex.getMessage() + "\n");
            }
        });

        // Send Message button action
        sendMessageButton.setOnAction(e -> {
            try {
                int questionId = Integer.parseInt(questionIdField.getText().trim());
                String messageContent = messageField.getText();
                sendMessage(questionId, messageContent);
                messagesArea.appendText("You: " + messageContent + "\n");
                messageField.clear();
            } catch (NumberFormatException nfe) {
                messagesArea.appendText("Invalid Question ID.\n");
            } catch (SQLException ex) {
                messagesArea.appendText("Error sending message: " + ex.getMessage() + "\n");
            }
        });

        // Back button action
        backButton.setOnAction(e -> {
            // Go back to the main menu or a previous screen
            new QuestionApp(dbHelper).show(stage);  
        });

        root.getChildren().addAll(
            titleLabel,
            new Label("Question ID:"), questionIdField,
            loadMessagesButton, sendMessageButton, new Label("Messages:"), messagesArea,
            new Label("Message Content:"), messageField, backButton
        );

        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Messages for Question");
        stage.setScene(scene);
        stage.show();
    }

    // Load messages related to a specific question
    private void loadMessages(int questionId, TextArea messagesArea) throws SQLException {
        String query = "SELECT sender, content FROM messages WHERE question_id = ? ORDER BY id ASC"; 
        try (var pstmt = dbHelper.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            var rs = pstmt.executeQuery();
            messagesArea.clear();

            while (rs.next()) {
                String sender = rs.getString("sender");
                String content = rs.getString("content");
                messagesArea.appendText(sender + ": " + content + "\n");
            }

            if (!rs.isBeforeFirst()) {
                messagesArea.appendText("No messages found for this question.\n");
            }

        } catch (SQLException e) {
            throw new SQLException("Error loading messages: " + e.getMessage());
        }
    }

    // Send a new message to the teacher
    private void sendMessage(int questionId, String content) throws SQLException {
        String query = "INSERT INTO messages (question_id, sender, recipient, content, is_read) VALUES (?, ?, ?, ?, FALSE)";
        try (var pstmt = dbHelper.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            pstmt.setString(2, currentUser);  
            pstmt.setString(3, "teacher");    
            pstmt.setString(4, content);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error sending message: " + e.getMessage());
        }
    }
}