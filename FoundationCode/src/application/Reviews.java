package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;

public class Reviews {

    // List to hold all Answer objects
    private List<Review> reviewList;

    /**
     * Constructs an empty Answers container.
     */
    public Reviews() {
        reviewList = new ArrayList<>();
    }

    /**
     * Loads all answers from the database into the local list.
     *
     * @param dbHelper the DatabaseHelper instance providing the database connection.
     * @throws SQLException if an error occurs during database access.
     */
    public void loadAllReviews(DatabaseHelper dbHelper) throws SQLException {
        String sql = "SELECT id, answerId, content, userId, userName FROM Reviews";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // Clear the current list before loading new data
            reviewList.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                int questionId = rs.getInt("answerId");
                String content = rs.getString("content");
                int userId = rs.getInt("userid");
                String userName = rs.getString("userName");
                // Create a new Answer object using the data from the database
                Review review = new Review(id, questionId, content, userId, userName);
                reviewList.add(review);
            }
        }
    }

    /**
     * Searches for answers that contain the specified keyword.
     *
     * @param keyword the keyword to search for.
     * @return a list of Answer objects whose content contains the keyword.
     */
    public List<Review> searchReviews(String keyword) {
        List<Review> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Review a : reviewList) {
            if (a.getContent().toLowerCase().contains(lowerKeyword)) {
                result.add(a);
            }
        }
        return result;
    }
    
    public List<Review> searchReviewsId(int id) {
    	System.out.println("sdfsfsdfsdfdsff");
    	List<Review> result = new ArrayList<>();
    	for(Review a : reviewList) {
    		if(a.getAnswerId() == id) {
    			result.add(a);
    		}
    	}
    	return result;
    }

    /**
     * Adds an Answer to the local list.
     * Note: This method only adds the answer to the list. To persist it in the database,
     *       call the create() method on the Answer object.
     *
     * @param a the Answer object to add.
     */
    public void addReview(Review a) {
        reviewList.add(a);
    }

    /**
     * Removes an Answer from the local list.
     * Note: This method only removes the answer from the local list. To delete it from the database,
     *       call the delete() method on the Answer object.
     *
     * @param a the Answer object to remove.
     */
    public void removeAnswer(Review a) {
        reviewList.remove(a);
    }

    /**
     * Retrieves the local list of all loaded answers.
     *
     * @return the list of Answer objects.
     */
    public List<Review> getreviewList() {
        return reviewList;
    }
}
