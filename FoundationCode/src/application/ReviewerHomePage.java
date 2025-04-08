package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class ReviewerHomePage extends UserHomePage {

    public void show(Stage primaryStage, DatabaseHelper databaseHelper, User user) {

	    // Label to display Hello user
	    Label userLabel = new Label("Hello, Reviewer!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	  //  layout.getChildren().add(userLabel);
	    
	 // Logout button to return to login page
	    
	    Button toQuestion = new Button("Questions");
	    toQuestion.setOnAction(e -> new QuestionsApp().start(primaryStage));
	    
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> new UserLoginPage(databaseHelper).show(primaryStage));

        // Switch Role button (Only shown if user has multiple roles)
        Button switchRoleButton = new Button("Switch Role");
        switchRoleButton.setOnAction(e -> new WelcomeLoginPage(databaseHelper).show(primaryStage, user));
        switchRoleButton.setVisible(user.getRole().contains(",")); // Show only if multiple roles
        
	    HBox buttons = new HBox(5, toQuestion, logoutButton);
	    buttons.setMaxWidth(130);
       
        
        VBox layout = new VBox(10, userLabel, buttons, switchRoleButton);
        layout.setStyle("-fx-padding:20; -fx-alignment: center;");
        //layout.getChildren().addAll(buttons, logoutButton, switchRoleButton);
	    
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
    }
}