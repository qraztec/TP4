package databasePart1;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import application.User;

/**
 * The DatabaseHelper class is responsible for managing the connection to the
 * database, performing operations such as user registration, login validation,
 * and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	private Connection connection = null;
	private Statement statement = null;
	// PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			// You can use this command to clear the database and restart from fresh.
			// statement.execute("DROP ALL OBJECTS");

			createTables(); // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, " + "password VARCHAR(255), " + "role VARCHAR(255), "
				+ "OTP VARCHAR(10), " + "isUSED BOOLEAN DEFAULT TRUE)";
		statement.execute(userTable);

		// Now ensure the new columns exist too
		// (H2 allows "IF NOT EXISTS" with ADD COLUMN, which helps avoid errors if it
		// already exists)
		String addOtpColumn = "ALTER TABLE cse360users ADD COLUMN IF NOT EXISTS OTP VARCHAR(10)";
		String addIsUsedColumn = "ALTER TABLE cse360users ADD COLUMN IF NOT EXISTS isUSED BOOLEAN DEFAULT TRUE";
		statement.execute(addOtpColumn);
		statement.execute(addIsUsedColumn);

		// Create the invitation codes table
		// Columns Added:
		// - Get the time when the code is first created
		// - Expires column to ensure the code is used within a certain amount of time
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes (" + "code VARCHAR(10) PRIMARY KEY, "
				+ "isUsed BOOLEAN DEFAULT FALSE, " + "startTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "expires TIMESTAMP)";
		statement.execute(invitationCodesTable);

		// Add new columns if not present
		statement.execute(
				"ALTER TABLE InvitationCodes ADD COLUMN IF NOT EXISTS startTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
		statement.execute("ALTER TABLE InvitationCodes ADD COLUMN IF NOT EXISTS expires TIMESTAMP");
		
		//create answer table
		String answersTable = "CREATE TABLE IF NOT EXISTS Answers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "questionId INT, " +
                "content VARCHAR(255) NOT NULL, " +
                "answered BOOLEAN DEFAULT FALSE" +
                ")";
        statement.execute(answersTable);
        
        String chatsTable = "CREATE TABLE IF NOT EXISTS Chats(" +
        		"id INT AUTO_INCREMENT PRIMARY KEY, " +
        		"roomId INT, " +
        		"role VARCHAR(255) NOT NULL, " +
        		"userName VARCHAR(255) NOT NULL, " +
        		"content VARCHAR(255) NOT NULL" +
        		")";
        statement.execute(chatsTable);
        
        String chatRoomsTable = "CREATE TABLE IF NOT EXISTS ChatRooms(" +
        		"id INT AUTO_INCREMENT PRIMARY KEY, " +
        		"userId1 INT, " +
        		"userId2 INT" +
        		")";
        statement.execute(chatRoomsTable);

        //Create question table
		String questionsTable = "CREATE TABLE IF NOT EXISTS Questions (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "content VARCHAR(255) NOT NULL)";
		statement.execute(questionsTable);
		
		String reviewsTable = "CREATE TABLE IF NOT EXISTS Reviews(" +
				"id INT AUTO_INCREMENT PRIMARY KEY, " +
				"answerId INT, " +
				"content VARCHAR(255) NOT NULL, " +
				"userId INT, " +
				"userName VARCHAR(255) NOT NULL" +
				")";
		statement.execute(reviewsTable);
		
		String pastReviewsTable = "CREATE TABLE IF NOT EXISTS PastReviews(" +
				"id INT AUTO_INCREMENT PRIMARY KEY, " +
				"answerId INT, " +
				"content VARCHAR(255) NOT NULL, " +
				"userId INT," +
				"userName VARCHAR(255) NOT NULL, " +
				"reviewId INT" +
				
				")";
		statement.execute(pastReviewsTable);
		 // Create an admin requests table
		String adminRequestsTable = "CREATE TABLE IF NOT EXISTS AdminRequests(" +
				"id INT AUTO_INCREMENT PRIMARY KEY, " +
				"isOpen BOOLEAN NOT NULL, " +
				"actionContent VARCHAR(255), " +
				"description VARCHAR(255) NOT NULL" +
				")";
		statement.execute(adminRequestsTable);
		
		// Create a Messages Table
		String messagesTable = "CREATE TABLE IF NOT EXISTS Messages (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "question_id INT NOT NULL, sender VARCHAR(255) NOT NULL, recipient VARCHAR(255) NOT NULL, "
				+ "content TEXT NOT NULL, is_read BOOLEAN DEFAULT FALSE)";
		statement.execute(messagesTable);
	}
	
	public void sendMessage(String sender, String recipient, String content) throws SQLException {
	    if (content == null || content.trim().isEmpty()) {
	        throw new IllegalArgumentException("Message content cannot be empty.");
	    }

	    String query = "INSERT INTO Messages (question_id, sender, recipient, content, is_read) VALUES (?, ?, ?, ?, FALSE)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, 0); // Assuming question_id is not used for now
	        pstmt.setString(2, sender);
	        pstmt.setString(3, recipient);
	        pstmt.setString(4, content);
	        pstmt.executeUpdate();
	        //System.out.println("Debug: Message inserted with is_read = FALSE");
	    }
	}
	
	// Fetches messages from users
	public List<String> getMessagesForUser(String recipient) throws SQLException {
	    String query = "SELECT sender, content FROM Messages WHERE recipient = ?";
	    List<String> messages = new ArrayList<>();

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, recipient);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            String sender = rs.getString("sender");
	            String content = rs.getString("content");
	            messages.add("From " + sender + ": " + content);
	        }
	    }
	    return messages;
	}
	
	// Clear Messages
	public void clearMessagesForUser(String recipient) throws SQLException {
	    String query = "DELETE FROM Messages WHERE recipient = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, recipient);
	        int rowsDeleted = pstmt.executeUpdate();
	        //System.out.println("Debug: Deleted " + rowsDeleted + " old messages for recipient: " + recipient);
	    }
	}
	
	// Mark messages as read
	public void markMessagesAsRead(String recipient) throws SQLException {
	    String query = "UPDATE Messages SET is_read = TRUE WHERE recipient = ? AND is_read = FALSE";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, recipient);
	        pstmt.executeUpdate();
	    }
	}
	
	// Get amount of unread messages
	public int getUnreadMessageCount(String recipient) throws SQLException {
	    //System.out.println("Debug: Running query to count unread messages for recipient: " + recipient);

	    String query = "SELECT COUNT(*) FROM Messages WHERE recipient = ? AND is_read = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, recipient);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            int count = rs.getInt(1);
	            //System.out.println("Debug: Actual unread message count in DB: " + count);
	            return count;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return 0;
	}
	
	// Check for messages
	public boolean doesMessageExist(String recipient, String messageContent) throws SQLException {
		String query = "SELECT COUNT(*) FROM Messages WHERE recipient = ? AND content = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, recipient);
	        stmt.setString(2, messageContent);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt(1) > 0; // If count > 0, message exists
	            }
	        }
	    }
	    return false;
	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Check if OTP is empty
	public boolean isOTPDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM One_Time_Passwords";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND (password = ? OR (OTP IS NOT NULL AND isUsed = FALSE)) AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String OTPass = rs.getString("OTP");
					boolean used = rs.getBoolean("isUsed");
					if (OTPass != null && !used) {
						validateOTP(OTPass);
					}

					return true;
				}
			}
		}
		return false;
	}

	// Deletes a user from database
	public void deleteUser(String userName) {
		String query = "DELETE FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Lists all users in database
	public ArrayList<String[]> listUsers() {
		String query = "SELECT userName, role FROM cse360users";
		ArrayList<String[]> returnList = new ArrayList<>();
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String user = rs.getString("userName");
				String role = rs.getString("role");
				String[] returnString = { user, role };
				returnList.add(returnString);
			}
			return returnList;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<String[]> listAdminRequests() {
		String query = "SELECT isOpen, actionContent, description, id from AdminRequests";
		ArrayList<String[]> returnList = new ArrayList<>();
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				boolean isOpen = rs.getBoolean("isOpen");
				String actionContent = rs.getString("actionContent");
				String description = rs.getString("description");
				int id = rs.getInt("id");
				String[] returnString = {String.valueOf(isOpen), actionContent, description, Integer.toString(id)};
				returnList.add(returnString);
			}
			return returnList;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String[] getAdminRequest(int id) {
	    String query = 
	        "SELECT isOpen, actionContent, description, id " +
	        "FROM AdminRequests " +
	        "WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            String isOpen        = Boolean.toString(rs.getBoolean("isOpen"));
	            String actionContent = rs.getString("actionContent");
	            String description   = rs.getString("description");
	          
	            return new String[] { isOpen, actionContent, description, String.valueOf(id)};
	        } else {
	            return null;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	public void closeAdminRequest(int id, String actionContent) {
		String query = "UPDATE AdminRequests SET isOpen = false, actionContent = ? where id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, actionContent);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void reopenAdminRequest(int id, String newDescription) {
	    String query = "UPDATE AdminRequests SET isOpen = true, description = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newDescription);
	        pstmt.setInt(2, id);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	


	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // If an error occurs, assume user doesn't exist
	}
	
	public int getUserID(String userName, String password) {
		String query = "SELECT id FROM cse360users WHERE userName = ? AND (password = ? OR (OTP IS NOT NULL AND isUsed = FALSE))";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, password);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("id");

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return -1;
		
	}

	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
		String query = "SELECT role FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("role"); // Return the role if user exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // If no user exists or an error occurs
	}

	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
		String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
		// QUERY ADDED:
		// - Added the current time through CURRENT_TIMESTAMP
		// - USED DATE_ADD to add 5 minutes to current time for expired time
		String query = "INSERT INTO InvitationCodes (code, startTime, expires) VALUES (?, CURRENT_TIMESTAMP, DATEADD(MINUTE, 5, CURRENT_TIMESTAMP))";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return code;
	}

	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
		String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE AND expires > CURRENT_TIMESTAMP";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// Mark the code as used
				markInvitationCodeAsUsed(code);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
		String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Generates a new OTP code and inserts it into the database.
	public String generateOTPCode(String username) {
		String OTPass = UUID.randomUUID().toString().substring(0, 8); // Generate a random 4-character code
		String query = "UPDATE cse360users SET OTP = ?, isUSED = FALSE WHERE userName = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, OTPass);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return OTPass;
	}

	// Validates an invitation code to check if it is unused.
	public boolean validateOTP(String OTPass) {
		String query = "SELECT * FROM cse360users WHERE OTP = ? AND isUSED = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, OTPass);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// Mark the code as used
				String UName = rs.getString("userName");
				OTPNotEmpty(UName);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Marks the invitation code as used in the database.
	private void OTPNotEmpty(String userName) {
		String query = "UPDATE cse360users SET OTP = NULL, isUSED = TRUE where userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	// new ones

	// Checks if the user has a specific role
	public boolean userHasRole(String userName, String role) {
		String query = "SELECT role FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				List<String> roles = Arrays.asList(rs.getString("role").split(","));
				return roles.contains(role);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Adds a role to a user
	public void addUserRole(String userName, String newRole) {
		String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			String existingRoles = getUserRole(userName);
			String updatedRoles = existingRoles.isEmpty() ? newRole : existingRoles + "," + newRole;
			pstmt.setString(1, updatedRoles);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Removes a role from a user
	public void removeUserRole(String userName, String removeRole) {
		String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			String existingRoles = getUserRole(userName);
			List<String> roles = new ArrayList<>(Arrays.asList(existingRoles.split(",")));
			roles.remove(removeRole);
			String updatedRoles = String.join(",", roles);
			pstmt.setString(1, updatedRoles);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Checks if the user is the last Admin
	public boolean isLastAdmin(String userName) {
		String query = "SELECT COUNT(*) FROM cse360users WHERE role LIKE '%Admin%'";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) == 1) {
				return userHasRole(userName, "Admin");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Connection getConnection() {
		return connection;
	}

}