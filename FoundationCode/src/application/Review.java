package application;

import java.sql.*;

import databasePart1.DatabaseHelper;

public class Review implements Post{
    private int id;			// Unique identifier for this review
    private int answerId;	//ID of the answer this review is associated with
    private String content;	//Text/content of the review
    private int userId; 	//ID of the user this review is associated with
    private String userName; //Username of the reviewer

    // Constructor for a new Answer (id will be generated)
    public Review(int id, int answerId, String content, int userId, String userName) {
        if (!isValidContent(content)) {
            throw new IllegalArgumentException("Question content must be between 1 and 255 characters.");
        }
        this.id = id;
        this.content = content;
        this.answerId = answerId;
        this.userId = userId;
        this.userName = userName;
    }
    // Contructor for a new Answer (id not yet known)
    public Review(int answerId, String content, int userId, String userName) {
        if (!isValidContent(content)) {
            throw new IllegalArgumentException("Question content must be between 1 and 255 characters.");
        }
        this.content = content;
        this.answerId = answerId;
        this.userId = userId;
        this.userName = userName;
    }
    @Override
    //Checks if content is valid
    public boolean isValidContent(String content) {
    	return content != null && !content.trim().isEmpty() && content.length() <= 255;
    }
    /**
     * Inserts this Review into the database.
     *
     * @param dbHelper The DatabaseHelper instance providing the database connection.
     * @throws SQLException if an error occurs while inserting the answer.
     */
	@Override
	public void create(DatabaseHelper dbHelper) throws SQLException {
	    String sql = "INSERT INTO Reviews (answerId, content, userId, userName) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, this.answerId);
            pstmt.setString(2, this.content);
            pstmt.setInt(3,  this.userId);
            pstmt.setString(4, this.userName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating review failed, no rows affected.");
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
	@Override
	public void update(DatabaseHelper dbHelper, String newContent) throws SQLException {
        if (!isValidContent(newContent)) {
            throw new IllegalArgumentException("Question content must be between 1 and 255 characters.");
        }
        
	    String sql = "INSERT INTO PastReviews (answerId, content, userId, reviewId, userName) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, this.answerId);
            pstmt.setString(2, this.content);
            pstmt.setInt(3,  this.userId);
            pstmt.setInt(4,  this.id);
            pstmt.setString(5, this.userName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating past review failed, no rows affected.");
            }
 
        }
        
        String sql2 = "UPDATE Reviews SET content = ? WHERE id = ?";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql2)) {
            pstmt.setString(1, newContent);
            pstmt.setInt(2, this.id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating answer failed, no rows affected.");
            }
            this.content = newContent;
        }
		
	}
	@Override
	public void delete(DatabaseHelper dbHelper) throws SQLException {
        String sql = "DELETE FROM Reviews WHERE id = ?";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting answer failed, no rows affected.");
            }
        }
		
	}
	
	public static Review read(DatabaseHelper dbHelper, int reviewId) throws SQLException {
		String sql = "SELECT id, answerId, content, userId, userName FROM Reviews WHERE id = ?";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int answerId = rs.getInt("answerId");
                    String content = rs.getString("content");
                    int userId = rs.getInt("userId");
                    String userName = rs.getString("userName");
                    return new Review(id, answerId, content, userId, userName);
                } else {
                    return null; // Review not found
                }
            }
        }
	}
	
	@Override
	public int getId() {
		return id;
	}
	public int getAnswerId() {
		return answerId;
	}
	public int getUserId() {
		return userId;
	}
	@Override
	public String getContent() {
		return content;
	}
	
    @Override
    public String toString() {
        return "Review [id=" + id + ", answerId=" + answerId + ", content=" + content + ", userId=" + userId + ", userName=" + userName + "]";
    }

}
