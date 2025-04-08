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
public class AnswersApp extends Application {

    private DatabaseHelper dbHelper = new DatabaseHelper();
    private Answers answersManager = new Answers();
    private int index;
    UserSession user;

    public void start(Stage primaryStage) {
        try {
            // Connect to the database
            dbHelper.connectToDatabase();

            // Create UI elements
            Label titleLabel = new Label("All Answers For Question with ID: " );
            TextField idGet = new TextField();
            idGet.setMaxWidth(50);
            Button id = new Button("Get Question");
            TextArea QInfo = new TextArea();
            id.setOnAction(e -> {
            	if(!idGet.getText().isEmpty()) {
            		Question q;
            		QInfo.clear();
            		try {
						index = Integer.parseInt(idGet.getText());
						q = Question.read(dbHelper, index);
	            		QInfo.appendText(""+ q);
	            		answersManager.loadAllAnswers(dbHelper);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						QInfo.appendText("Question ID has no question");
					}
            	}
            });
            QInfo.setMaxHeight(25);
            QInfo.setMaxWidth(300);
            HBox question = new HBox(5, titleLabel, idGet);
            HBox GetInfo = new HBox(5, id, QInfo);
            VBox questionInfo = new VBox(10, question, GetInfo);
            ListView<String> listView = new ListView<>();
            Button loadButton = new Button("Load Answers");
            Button reviews = new Button("See Reviews");
            Button backButton = new Button("BackToQuestion");
            Button answerOptions = new Button("To Answer Options");
            Button toMessages = new Button("Messages");
            HBox buttons;
            if (user.getUser().getRole().equals("Reviewer")) {
            	Button makeReview = new Button("Make A Review");
            	buttons = new HBox(5, loadButton, reviews, answerOptions, makeReview, backButton, toMessages);
            	makeReview.setOnAction(e -> new ReviewApp().start(primaryStage));
            }
            else {
            	buttons = new HBox(5, loadButton, reviews, answerOptions, backButton, toMessages);
            }
            
            
            
            
            // Layout
            VBox root = new VBox(10, questionInfo, buttons, listView);
            root.setPadding(new Insets(15));

            // Load all answers button action
            loadButton.setOnAction(e -> {
            	System.out.println(index);
                List<Answer> results = answersManager.searchAnswersId(index);
                listView.getItems().clear();
                if (results.isEmpty()) {
                    listView.getItems().add("No answers found with question ID: " + index);
                } else {
                    for (Answer a : results) {
                        listView.getItems().add(a.toString());
                    }
                }
            });
            
            toMessages.setOnAction(e -> new MessagesApp(dbHelper).show(primaryStage));
            
            backButton.setOnAction(e -> new QuestionsApp().start(primaryStage));
            
            answerOptions.setOnAction(e -> new AnswerApp().start(primaryStage));
            
            reviews.setOnAction(e -> new ReviewsApp().start(primaryStage));

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
            primaryStage.setTitle("Answers Collection Demo");
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
