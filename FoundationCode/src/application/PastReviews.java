package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;

public class PastReviews {
	private List<PastReview> pastReviewList;
	public PastReviews() {
		pastReviewList = new ArrayList();
	}
    public void loadAllPastReviews(DatabaseHelper dbHelper) throws SQLException {
        String sql = "SELECT id, answerId, content, userId, userName, reviewId FROM PastReviews";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // Clear the current list before loading new data
            pastReviewList.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                int questionId = rs.getInt("answerId");
                String content = rs.getString("content");
                int userId = rs.getInt("userid");
                String userName = rs.getString("userName");
                int reviewId = rs.getInt("reviewId");
                // Create a new Answer object using the data from the database
                PastReview review = new PastReview(id, questionId, content, userId, userName, reviewId);
                pastReviewList.add(review);
            }
        }
    }
    public List<PastReview> searchPastReviewsId(int reviewId) {
    	System.out.println("sdfsfsdfsdfdsff");
    	List<PastReview> result = new ArrayList<>();
    	for(PastReview a : pastReviewList) {
    		if(a.getReviewId() == reviewId) {
    			result.add(a);
    		}
    	}
    	return result;
    }
}
