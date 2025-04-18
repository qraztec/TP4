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
 * A JavaFX application that displays all ChatRooms available to the currently logged-in user.
 * Staff users can create new ChatRooms by specifying the other user's ID, while instructors
 * can only view existing ChatRooms.
 */
public class ChatRoomListApp extends Application {
    private DatabaseHelper dbHelper;
    private ChatRoomList chatRoomsManager = new ChatRoomList();
    private User user; // from your domain

    /**
     * Default no-argument constructor for JavaFX Application compatibility.
     */
    public ChatRoomListApp() {
        // No-arg constructor
    }

    /**
     * Constructs a ChatRoomListApp with the given DatabaseHelper and User.
     *
     * @param dbHelper the DatabaseHelper instance for DB connectivity
     * @param user     the currently logged-in user
     */
    public ChatRoomListApp(DatabaseHelper dbHelper, User user) {
        this.dbHelper = dbHelper;
        this.user = user;
    }

    /**
     * Starts the JavaFX UI, loading all chat rooms from the database and then
     * displaying only those that match the current user's ID (userId1 or userId2).
     * Staff can optionally create new ChatRooms.
     *
     * @param primaryStage the primary Stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        // Create a vertical layout for everything
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Attempt to load chat rooms
        try {
            dbHelper.connectToDatabase();
            chatRoomsManager.loadAllChatRooms(dbHelper);

            // If user is staff, let them create new chat rooms
            if (user.getRole().equalsIgnoreCase("Staff")) {
                Label createLabel = new Label("Create New Chat Room (Staff Only):");
                TextField otherUserField = new TextField();
                otherUserField.setPromptText("Enter the other user's ID");

                Button createRoomButton = new Button("Create Chat Room");
                createRoomButton.setOnAction(e -> {
                    try {
                        int otherUserId = Integer.parseInt(otherUserField.getText().trim());
                        ChatRoom newRoom = new ChatRoom(user.getId(), otherUserId);
                        newRoom.create(dbHelper);
                        // Reload the chat rooms
                        chatRoomsManager.loadAllChatRooms(dbHelper);
                        // Possibly re-draw the list of buttons or just show a success
                        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                "Chat Room created with ID: " + newRoom.getId());
                        alert.showAndWait();
                    } catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "Failed to create chat room: " + ex.getMessage());
                        alert.showAndWait();
                    }
                });

                root.getChildren().addAll(createLabel, otherUserField, createRoomButton);
            }

            // Now show the user’s existing chat rooms
            List<ChatRoom> results = chatRoomsManager.searchChatRooms(user.getId());

            if (results.isEmpty()) {
                root.getChildren().add(new Label("No chat rooms found for your ID."));
            } else {
                for (ChatRoom c : results) {
                    Button cButton = new Button("Chat Room: " + c.getId() +
                        " (User1=" + c.getUserId1() + ", User2=" + c.getUserId2() + ")");
                    cButton.setOnAction(e -> {
                        // Open the ChatRoom in a new Stage:
                        ChatRoomApp chatRoomApp = new ChatRoomApp(c.getId(), dbHelper, user);
                        Stage chatStage = new Stage();
                        chatRoomApp.start(chatStage);
                    });
                    root.getChildren().add(cButton);
                }
            }
        } catch (SQLException ex) {
            root.getChildren().add(new Label("Database Error: " + ex.getMessage()));
        }

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Room List");
        primaryStage.show();
    }

    /**
     * Main method for launching this JavaFX application directly.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
