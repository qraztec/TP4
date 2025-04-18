package application;

import databasePart1.DatabaseHelper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

/**
 * A JavaFX application that manages the UI for a single chat room,
 * allowing the current user to view and send chat messages.
 */
public class ChatRoomApp extends Application {
    private int chatRoomId;
    private DatabaseHelper dbHelper;
    private User user;

    /**
     * Default constructor for ChatRoomApp (primarily for testing via Application.launch()).
     */
    public ChatRoomApp() {
    }

    /**
     * Constructs a ChatRoomApp that references a specific chat room ID, as well as the
     * database helper and the currently logged-in user.
     *
     * @param chatRoomId the unique ID of the chat room
     * @param dbHelper   the DatabaseHelper instance for DB connectivity
     * @param user       the currently logged-in user
     */
    public ChatRoomApp(int chatRoomId, DatabaseHelper dbHelper, User user) {
        this.chatRoomId = chatRoomId;
        this.dbHelper = dbHelper;
        this.user = user;
    }

    /**
     * Starts the JavaFX UI for this ChatRoomApp, displaying a text area of existing messages
     * and a text field + button for sending new messages.
     *
     * @param primaryStage the main Stage object for this application
     */
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label titleLabel = new Label("Chat Room ID: " + chatRoomId);

        // TextArea to display messages
        TextArea messagesArea = new TextArea();
        messagesArea.setEditable(false);
        messagesArea.setPrefHeight(300);

        // Load and display existing messages
        loadMessages(messagesArea);

        // Input area for new message
        TextField messageField = new TextField();
        messageField.setPromptText("Type a message...");

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String content = messageField.getText().trim();
            if (!content.isEmpty()) {
                // Insert into DB
                try {
                    Chat chatMsg = new Chat(chatRoomId, user.getRole(), user.getUserName(), content);
                    chatMsg.create(dbHelper);
                    messageField.clear();
                    // Reload the messages
                    loadMessages(messagesArea);
                } catch (SQLException ex) {
                    showAlert("Error", "Failed to send message: " + ex.getMessage());
                }
            }
        });

        // Optionally, a "Close" or "Back" button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> primaryStage.close());

        root.getChildren().addAll(
            titleLabel,
            messagesArea,
            messageField,
            sendButton,
            closeButton
        );

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Chat Room " + chatRoomId);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Loads all messages belonging to the current chat room and displays them
     * in the provided messagesArea (TextArea).
     *
     * @param messagesArea the TextArea where existing chat messages will be displayed
     */
    private void loadMessages(TextArea messagesArea) {
        messagesArea.clear();
        try {
            List<Chat> chats = Chat.readAllForChatRoom(dbHelper, chatRoomId);
            for (Chat c : chats) {
                messagesArea.appendText(
                    String.format("%s (%s): %s\n", c.getUserName(), c.getRole(), c.getContent())
                );
            }
        } catch (SQLException ex) {
            messagesArea.appendText("Error loading messages: " + ex.getMessage());
        }
    }

    /**
     * Displays an alert dialog with the specified title and message.
     *
     * @param title the title of the alert
     * @param msg   the content message of the alert
     */
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
