package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;




public class AdminRequestList {
	UserSession user;
	public void show(Stage primaryStage, DatabaseHelper dbHelper, ArrayList<String[]> adminRequestsList) {
		VBox layout = new VBox(10);
		layout.setStyle("-fx-alignment: top-center; -fx-padding: 20;");
		TextField adminRequestInput = new TextField();
		adminRequestInput.setPromptText("Enter new admin request");
		adminRequestInput.setMaxWidth(300);
		Button createRequestButton = new Button("Create new request");
	    createRequestButton.setOnAction(e -> {
	        String descriptionRequest = adminRequestInput.getText().trim();
	        if (!descriptionRequest.isEmpty()) {
	            try {
	                AdminRequest ar = new AdminRequest(true, "", descriptionRequest);
	                ar.createRequest(dbHelper);
	                showAlert("Success", "Admin request created.");
	                show(primaryStage, dbHelper, dbHelper.listAdminRequests()); // refresh
	            } catch (Exception ex) {
	                showAlert("Error", "Failed to create admin request: " + ex.getMessage());
	            }
	        } else {
	            showAlert("Warning", "Please enter a request description.");
	        }
	    });
	    
	    HBox topSection = new HBox(10, adminRequestInput, createRequestButton);
	    topSection.setStyle("-fx-alignment: center;");
	    layout.getChildren().add(topSection);
	    
        Label titleLabel = new Label("List of Admin Requests:");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.getChildren().add(titleLabel);
        //Loop through admin request list for list view
        for (String[] adminRequest : adminRequestsList) {
        	if (adminRequest.length == 4) {
        		//Labels to for description, isOpen status, and action content
        		Boolean isOpen = Boolean.parseBoolean(adminRequest[0]);
        		String actionContent = adminRequest[1];
        		String description = adminRequest[2];
        		Label descriptionLabel = new Label("Description: " + description);
        		Label isOpenLabel = new Label("  Status: " + (isOpen ? "Open" : "Closed"));
        		Label actionContentLabel = new Label("  Admin Action: " + actionContent);
        		
        		//Text field for admins documenting actions
        		TextField actionInput = new TextField();
        		actionInput.setPromptText("Enter documented actions");
        		actionInput.setMaxWidth(100);
        		
        		//Button to officially close admin request
        		Button closeRequestButton = new Button("Close Request");
        		
        		closeRequestButton.setOnAction( e -> {
        			String documentedAction = actionInput.getText().trim();
        			dbHelper.closeAdminRequest(Integer.parseInt(adminRequest[3]), documentedAction);
        			showAlert("Sucess", "admin request closed");
        			show(primaryStage, dbHelper, dbHelper.listAdminRequests()); // refresh
        		});
        		

                HBox requestBox = new HBox(10, descriptionLabel, isOpenLabel, actionContentLabel, actionInput, closeRequestButton);
                requestBox.setStyle("-fx-padding: 5;");
                layout.getChildren().add(requestBox);
        		
        	}
        }
    	Scene userScene = new Scene(layout, 800, 400);
    	primaryStage.setScene(userScene);
    	primaryStage.setTitle("Admin Request List");
	}
	

    // Helper function to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
