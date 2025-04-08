package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.application.Application;

public class ReviewApp extends Application {
	private DatabaseHelper dbHelper = new DatabaseHelper();
	private Review currentReview;
	private User user = UserSession.getUser();
	
	@Override
    public void start(Stage primaryStage) {
        try {
            dbHelper.connectToDatabase();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to connect to database: " + e.getMessage());
            return;
        }
        
        // UI Elements
        Label titleLabel = new Label("Review CRUD Demo");
        TextField answerIdField = new TextField();
        answerIdField.setPromptText("Enter Answer ID");
        int reviewId;
        
        TextField contentField = new TextField();
        contentField.setPromptText("Enter review content");
        
        Button createButton = new Button("Create Review");
        Button readButton = new Button("Read Review");
        Button updateButton = new Button("Update Review");
        Button deleteButton = new Button("Delete Review");
        Button backButton = new Button("Back");
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        
        // Set up actions for each button
        
        // Create Answer
        createButton.setOnAction(e -> {
            try {
                // Parse questionId from text field
                int answerId = Integer.parseInt(answerIdField.getText().trim());
                String content = contentField.getText();
                currentReview = new Review( answerId, content, user.getId(), user.getUserName());
                currentReview.create(dbHelper);
                outputArea.appendText("Created: " + currentReview + "\n");
            } catch (NumberFormatException nfe) {
                outputArea.appendText("Invalid Answer ID. Please enter a valid integer.\n");
            } catch (Exception ex) {
                outputArea.appendText("Error creating answer: " + ex.getMessage() + "\n");
            }
        });
        
        // Read Answer
        readButton.setOnAction(e -> {
            if (currentReview != null) {
                try {
                    Review readReview = Review.read(dbHelper, currentReview.getId());
                    if (readReview != null) {
                        outputArea.appendText("Read: " + readReview + "\n");
                    } else {
                        outputArea.appendText("Review not found.\n");
                    }
                } catch (SQLException ex) {
                    outputArea.appendText("Error reading review: " + ex.getMessage() + "\n");
                }
            } else {
                outputArea.appendText("No review created yet.\n");
            }
        });
        
        // Update Review
        updateButton.setOnAction(e -> {
            if (!answerIdField.getText().isEmpty()) {
                try {
                    String newContent = contentField.getText();
                    Review old = Review.read(dbHelper, currentReview.getId());
                    old.update(dbHelper, newContent);
                    outputArea.appendText("Updated: " + old + "\n");
                } catch (Exception ex) {
                    outputArea.appendText("Error updating review: " + ex.getMessage() + "\n");
                }
            } else {
                outputArea.appendText("No review created to update.\n");
            }
        });
        
        // Delete Review
        deleteButton.setOnAction(e -> {
            if (!answerIdField.getText().isEmpty()) {
                try {
                	Review a = Review.read(dbHelper, currentReview.getId());
                	a.delete(dbHelper);
                    outputArea.appendText("Deleted review with id: " + a.getId() + "\n");
                    a = null;
                } catch (SQLException ex) {
                    outputArea.appendText("Error deleting review: " + ex.getMessage() + "\n");
                }
            } else {
                outputArea.appendText("No review created to delete.\n");
            }
        });
        
        
        
       
        
        backButton.setOnAction(e -> new AnswersApp().start(primaryStage));
        
        // Layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(
            titleLabel,
            new Label("Answer ID:"), answerIdField,
            new Label("Review Content:"), contentField,
            createButton, readButton, updateButton, deleteButton, 
            backButton, outputArea
        );
        
        
        
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("Answer CRUD Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Utility method to show an error alert.
     *
     * @param title   The title of the alert.
     * @param message The message to display.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
    
    /**
     * Main entry point for launching the AnswerApp.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

}
