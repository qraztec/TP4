package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import databasePart1.DatabaseHelper;

public class PastReview {
	private int id;
	private int answerId;
	private String content;
	private int userId;
	private String userName;
	private int reviewId;
	
	
	public PastReview(int id, int answerId, String content, int userId, String userName, int reviewId) {

        this.id = id;
        this.content = content;
        this.answerId = answerId;
        this.userId = userId;
        this.userName = userName;
        this.reviewId = reviewId;
        
	}
	
	public static PastReview read(DatabaseHelper dbHelper, int reviewId) throws SQLException {
		String sql = "SELECT id, answerId, content, userId, userName FROM PastReviews WHERE reviewId = ?";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int answerId = rs.getInt("answerId");
                    String content = rs.getString("content");
                    int userId = rs.getInt("userId");
                    String userName = rs.getString("userName");
                    return new PastReview(id, answerId, content, userId, userName, reviewId);
                } else {
                    return null; // Review not found
                }
            }
        }
	}
	
	public String getContent() {
		return content;
	}
	
	public int getAnswerId() {
		return this.answerId;
	}
	
	public int getUserId() {
		return this.userId;
	}
	
	public int getReviewId() {
		return this.reviewId;
	}
	

    public String toString() {
        return "Review [id=" + id + ", answerId=" + answerId + ", content=" + content + ", userId=" + userId + ", userName=" + userName + ", reviewId=" + "reviewId" + "]";
    }
}
