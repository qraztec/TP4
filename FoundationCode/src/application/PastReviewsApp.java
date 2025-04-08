package application;

import databasePart1.DatabaseHelper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

/**
 * The AnswersApp class demonstrates how to load and display all answers
 * from the database using the Answers collection manager.
 */
public class PastReviewsApp extends Application {

    private DatabaseHelper dbHelper = new DatabaseHelper();
    private PastReviews reviewsManager = new PastReviews();
    private int index;
    UserSession user;

    public void start(Stage primaryStage) {
        try {
            // Connect to the database
            dbHelper.connectToDatabase();

            // Create UI elements
            Label titleLabel = new Label("All Past Reviews For Updated Review with ID: " );
            TextField idGet = new TextField();
            idGet.setMaxWidth(50);
            Button id = new Button("Get Review");
            TextArea aInfo = new TextArea();
            id.setOnAction(e -> {
            	if(!idGet.getText().isEmpty()) {
            		Review q;
            		aInfo.clear();
            		try {
						index = Integer.parseInt(idGet.getText());
						q = Review.read(dbHelper, index);
	            		aInfo.appendText(""+ q);
	            		reviewsManager.loadAllPastReviews(dbHelper);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						aInfo.appendText("Review ID has no past updates");
					}
            	}
            });
            aInfo.setMaxHeight(25);
            aInfo.setMaxWidth(300);
            HBox answer = new HBox(5, titleLabel, idGet);
            HBox GetInfo = new HBox(5, id, aInfo);
            VBox answerInfo = new VBox(10, answer, GetInfo);
            ListView<String> listView = new ListView<>();
            Button loadButton = new Button("Load Past Reviews");
      
            Button backButton = new Button("BackToReview");
            Button toMessages = new Button("Messages");
            HBox buttons;
            if (user.getUser().getRole().equals("Reviewer")) {
            	Button makeReview = new Button("Make A Review");
            	buttons = new HBox(5, loadButton, makeReview, backButton, toMessages);
            	makeReview.setOnAction(e -> new ReviewApp().start(primaryStage));
            }
            else {
            	buttons = new HBox(5, loadButton, backButton, toMessages);
            }
            
            
            
            
            // Layout
            VBox root = new VBox(10, answerInfo, buttons, listView);
            root.setPadding(new Insets(15));
            
            // Load all answers button action
            loadButton.setOnAction(e -> {
            	System.out.println(index);
                List<PastReview> results = reviewsManager.searchPastReviewsId(index);
                listView.getItems().clear();
                if (results.isEmpty()) {
                    listView.getItems().add("No reviews found with answer ID: " + index);
                } else {
                    for (PastReview a : results) {
                        listView.getItems().add(a.toString());
                    }
                }
            });
            
            toMessages.setOnAction(e -> new MessagesApp(dbHelper).show(primaryStage));
            
            backButton.setOnAction(e -> new ReviewsApp().start(primaryStage));
            

            // Search button action
         /*   searchButton.setOnAction(e -> {
                String keyword = searchField.getText();
                List<Answer> results = answersManager.searchAnswers(keyword);
                listView.getItems().clear();
                if (results.isEmpty()) {
                    listView.getItems().add("No answers found with keyword: " + keyword);
                } else {
                    for (Answer a : results) {
                        listView.getItems().add(a.toString());
                    }
                }
            });
          */
            
            Scene scene = new Scene(root, 500, 400);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Reviews Collection Demo");
            primaryStage.show();

        } catch (SQLException ex) {
            showAlert("Database Error", "Failed to connect: " + ex.getMessage());
        }
    }

    /**
     * Utility method to show an alert dialog.
     *
     * @param title   The title of the alert.
     * @param message The message content of the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    /**
     * Main entry point for launching the AnswersApp.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
