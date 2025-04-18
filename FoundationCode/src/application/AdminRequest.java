package application;

import databasePart1.DatabaseHelper;
import java.sql.*;

public class AdminRequest {
	private int id;
	private boolean isOpen;
	private String actionContent;
	private String description;
	
	public AdminRequest(boolean isOpen, String actionContent, String description) {
		this.isOpen = isOpen;
		this.actionContent = actionContent;
		this.description = description;
	}
	
	public boolean getIsOpen() {
		return this.isOpen;
	}
	
	public String getActionContent() {
		return this.actionContent;
	}
	
	public void createRequest(DatabaseHelper dbHelper) throws SQLException {
        String sql = "INSERT INTO AdminRequests (isOpen, actionContent, description) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setBoolean(1, this.isOpen);
            pstmt.setString(2, this.actionContent);
            pstmt.setString(3,  this.description);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating answer failed, no rows affected.");
            }
            // Retrieve the auto-generated key (id)
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating answer failed, no ID obtained.");
                }
            }
        }
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void updateDescripion() {
		
	}
	
}
