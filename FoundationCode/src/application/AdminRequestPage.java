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
import java.util.ArrayList;
import java.util.List;

public class AdminRequestPage extends Application {
	
	private DatabaseHelper dbHelper = new DatabaseHelper();
	private ArrayList<AdminRequest> adminRequestList = new ArrayList<>();
	
	@Override
	public void start(Stage arg0) throws Exception {
		try {
			dbHelper.connectToDatabase();
			Label titleLabel = new Label("All Admin Requests");
			
		}
		
	}
	
}
