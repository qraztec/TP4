package application;

import java.util.ArrayList;

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

		// Instructors can create new requests
		
		if (user.getUser().getRole().equals("Instructor")) {
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
						show(primaryStage, dbHelper, dbHelper.listAdminRequests());
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
		}

		// Title label
		Label titleLabel = new Label("List of Admin Requests:");
		titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		layout.getChildren().add(titleLabel);

		for (String[] adminRequest : adminRequestsList) {
			if (adminRequest.length == 4) {
				Boolean isOpen = Boolean.parseBoolean(adminRequest[0]);
				String actionContent = adminRequest[1];
				String description = adminRequest[2];
				int requestId = Integer.parseInt(adminRequest[3]);

				Label descriptionLabel = new Label("Description: " + description);
				Label isOpenLabel = new Label("  Status: " + (isOpen ? "Open" : "Closed"));
				Label actionContentLabel = new Label("  Admin Action: " + actionContent);

				HBox requestBox = new HBox(10, descriptionLabel, isOpenLabel, actionContentLabel);
				requestBox.setStyle("-fx-padding: 5;");

				// Admin-specific controls for open requests
				if (user.getUser().getRole().equalsIgnoreCase("Admin") && isOpen) {
					TextField actionInput = new TextField();
					actionInput.setPromptText("Enter documented actions");
					actionInput.setMaxWidth(150);

					Button closeRequestButton = new Button("Close Request");
					closeRequestButton.setOnAction(e -> {
						String documentedAction = actionInput.getText().trim();
						dbHelper.closeAdminRequest(requestId, documentedAction);
						showAlert("Success", "Admin request closed.");
						show(primaryStage, dbHelper, dbHelper.listAdminRequests());
					});

					requestBox.getChildren().addAll(actionInput, closeRequestButton);
				}

				// Instructor-specific controls for closed requests
				if (user.getUser().getRole().equalsIgnoreCase("Instructor") && !isOpen) {
					TextField updateDescriptionInput = new TextField();
					updateDescriptionInput.setPromptText("Update description");
					updateDescriptionInput.setMaxWidth(150);

					Button reopenRequestButton = new Button("Reopen Request");
					reopenRequestButton.setOnAction(e -> {
						String newDescription = updateDescriptionInput.getText().trim();
						if (!newDescription.isEmpty()) {
							dbHelper.reopenAdminRequest(requestId, newDescription);
							showAlert("Success", "Admin request reopened with updated description.");
							show(primaryStage, dbHelper, dbHelper.listAdminRequests());
						} else {
							showAlert("Warning", "Please enter a new description.");
						}
					});

					requestBox.getChildren().addAll(updateDescriptionInput, reopenRequestButton);
				}

				layout.getChildren().add(requestBox);
			}
		}

		Scene userScene = new Scene(layout, 800, 400);
		primaryStage.setScene(userScene);
		primaryStage.setTitle("Admin Request List");
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
