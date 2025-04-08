package application;

import databasePart1.DatabaseHelper;
import java.sql.*;

public class Messages {
	
	// Manages Database connections
	private DatabaseHelper dbHelper;
	private String studentUser;
	
	public Messages(String studentUser) {
		
		this.dbHelper = new DatabaseHelper();
		this.studentUser = studentUser;
		
		try {
			
			dbHelper.connectToDatabase();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	// Get the amount of unread messages
	public int getUnread(int questionId) {
		
		String query = "SELECT COUNT(*) FROM messages WHERE question_id = ? AND is_read = FALSE and recipient = ?";
		
		try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(query)) {
			
			pstmt.setInt(1, questionId);
			pstmt.setString(2, studentUser);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				
				return rs.getInt(1);
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return 0;
	}
	
	// gets the messages and marks them as read
	public String readMessages(int questionId) {
	    
	    StringBuilder messages = new StringBuilder();
	    
	    String query = "SELECT sender, content FROM messages WHERE question_id = ? AND recipient = ? ORDER BY id ASC"; 
	    
	    try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(query)) {
	        
	        pstmt.setInt(1, questionId);
	        pstmt.setString(2, studentUser);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            
	            messages.append("From: ").append(rs.getString("sender")).append("\n");
	            messages.append("Message: ").append(rs.getString("content")).append("\n");
	        }
	        
	        markMessagesRead(questionId); // Mark messages as read
	    } catch (SQLException e) {
	        
	        e.printStackTrace();
	    }
	    
	    return messages.toString();
	}
	
	// updates the messages in the database
	private void markMessagesRead(int questionId) {
		
		String query = "UPDATE messages SET is_read = TRUE WHERE question_id = ? AND recipient = ?";
		
		try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(query)) {
			
			pstmt.setInt(1, questionId);
			pstmt.setString(2, studentUser);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	// sends a message under a question and puts it into the database
	public void reply(int questionId, String recipient, String messageContent) {
		
		String query = "INSERT INTO messages (question_id, sender, recipient, content, is_read) VALUES (?, ?, ?, ?, FALSE)";
		
		try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(query)) {
			
			pstmt.setInt(1, questionId);
			pstmt.setString(2, studentUser);
			pstmt.setString(3, recipient);
			pstmt.setString(4, messageContent);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	// closes the connection between the database
	public void close() {
		
		try {
	        
			if (dbHelper.getConnection() != null && !dbHelper.getConnection().isClosed()) {
	            
				dbHelper.closeConnection();
	        }
			
	    } catch (SQLException e) {
	        
	    	e.printStackTrace();
	    }	
	}
	
}
