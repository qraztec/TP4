package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays a home page for a staff user with various options such as
 * viewing questions, accessing private messages, logging out, and
 * switching roles (if applicable).
 */
public class StaffHomePage {

    /**
     * Shows the staff home page on the provided Stage. The page offers
     * navigation to questions, private messages, logout, and role switching.
     *
     * @param primaryStage   the JavaFX Stage on which this UI is displayed
     * @param databaseHelper the DatabaseHelper for connecting to the database
     * @param user           the currently logged-in staff user
     */
    public void show(Stage primaryStage, DatabaseHelper databaseHelper, User user) {
        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display Hello user
        Label userLabel = new Label("Hello, Staff!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        layout.getChildren().add(userLabel);

        // Button to go to Questions
        Button toQuestion = new Button("Questions");
        toQuestion.setOnAction(e -> new QuestionsApp().start(primaryStage));

        // Button to display staff/instructor messages
        Button pmButton = new Button("Private Messages");
        pmButton.setOnAction(e -> {
            // Pass the DB helper and the current user to ChatRoomListApp
            ChatRoomListApp chatRoomListApp = new ChatRoomListApp(databaseHelper, user);
            chatRoomListApp.start(primaryStage);
        });

        // Logout button to return to login page
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> new UserLoginPage(databaseHelper).show(primaryStage));

        // Switch Role button (Only shown if user has multiple roles)
        Button switchRoleButton = new Button("Switch Role");
        switchRoleButton.setOnAction(e -> new WelcomeLoginPage(databaseHelper).show(primaryStage, user));
        switchRoleButton.setVisible(user.getRole().contains(",")); // Show only if multiple roles

        layout.getChildren().addAll(toQuestion, pmButton, logoutButton, switchRoleButton);

        Scene userScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(userScene);
        primaryStage.setTitle("Staff Home Page");
    }
}
