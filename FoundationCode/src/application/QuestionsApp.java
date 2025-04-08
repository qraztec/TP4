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
 * The QuestionsApp class demonstrates how to load and display all questions
 * from the database using the Questions collection manager.
 */
public class QuestionsApp extends Application {

    private DatabaseHelper dbHelper = new DatabaseHelper();
    private Questions questionsManager = new Questions();

    @Override
    public void start(Stage primaryStage) {
        try {
            // Connect to the database
            dbHelper.connectToDatabase();

            // Create UI elements
            Label titleLabel = new Label("All Questions");
            ListView<String> listView = new ListView<>();
            Button loadButton = new Button("Load Questions");
            TextField searchField = new TextField();
            searchField.setPromptText("Enter keyword to search");
            Button searchButton = new Button("Search");
            
            Button ToQOpt = new Button("Question Options");
            
            HBox buttons = new HBox(5, searchButton, ToQOpt);
            
            TextField idGet = new TextField();
            idGet.setMaxWidth(50);
            Button toQnA = new Button("toQnA");
            
            
            HBox toAnswers = new HBox(5, idGet, toQnA);
            
            //buttons.setMAxWidth();
            
            
            // button to go back to messages
            Button toMessages = new Button("Messages");
            
            
            // VBox layout for UI components
            VBox root = new VBox(10, titleLabel, loadButton, searchField, buttons, toMessages, toAnswers, listView);
            root.setPadding(new Insets(15));

            // Button action to load all questions
            loadButton.setOnAction(e -> {
                try {
                    questionsManager.loadAllQuestions(dbHelper);
                    // Clear the ListView and add loaded questions
                    listView.getItems().clear();
                    for (Question q : questionsManager.getQuestionList()) {
                        listView.getItems().add(q.toString());
                    }
                } catch (SQLException ex) {
                    showAlert("Error", "Failed to load questions: " + ex.getMessage());
                }
            });

            // Button action to search questions by keyword
            searchButton.setOnAction(e -> {
                String keyword = searchField.getText();
                List<Question> results = questionsManager.searchQuestions(keyword);
                listView.getItems().clear();
                if (results.isEmpty()) {
                    listView.getItems().add("No questions found with keyword: " + keyword);
                } else {
                    for (Question q : results) {
                        listView.getItems().add(q.toString());
                    }
                }
            });
            
            toQnA.setOnAction(e-> new AnswersApp().start(primaryStage));
            
            // Button to go to Question Options like creating/updating
            ToQOpt.setOnAction(e -> new QuestionApp(dbHelper).show(primaryStage));

            Scene scene = new Scene(root, 500, 400);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Questions Collection Demo");
            primaryStage.show();
            
            // Action for message button
            toMessages.setOnAction(e -> new MessagesApp(dbHelper).show(primaryStage));
            
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
     * Entry point for launching the QuestionsApp.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
