package application;

import databasePart1.DatabaseHelper;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ReviewTests {
    private static DatabaseHelper dbHelper;

    @BeforeAll
    static void setup() {
        dbHelper = new DatabaseHelper();
        try {
            dbHelper.connectToDatabase();
        } catch (SQLException e) {
            fail("DB Connection Error: " + e.getMessage());
        }
    }

    @AfterAll
    static void teardown() {
        dbHelper.closeConnection();
    }

    @Test
    void createReviewMin() {
        // Example test: trying to create a review with empty content. Should not be valid
        try {
            Review r = new Review(1, "", 2, "John"); // Assuming constructor (questionId, content, rating)
            r.create(dbHelper);
            r.delete(dbHelper);
            fail("Expected IllegalArgumentException for empty review content.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().equals("Question content must be between 1 and 255 characters."));
        } catch (SQLException se) {
            fail("Unexpected SQLException: " + se.getMessage());
        }
        
    }
    @Test
    void createReviewMax() {
        // Example test: trying to create a review with max content. Should not be valid
        try {
            Review r = new Review(1, "DFddfsdfasadsfasdfsadfasdfasdfasdfasdfdasdfasdfadsfasdfasdfasdfasdfasdfadfasdfasdfadsfasdfasdfjasdjfadskfjaksdjfkasjdkfajsdkfjasdfasdfjkasdjfkajsdkfjaksdjfkasdjfkajsdkfjaksdjfkajsdkfjaskdfjaksdjfkasdjfkasjdkfjasdkfjkasdjfasdfasdfasdfasdfasdfadsfasdfadsfasdfasdfasdfasdf", 2, "John"); // Assuming constructor (questionId, content, rating)
            r.create(dbHelper);
            r.delete(dbHelper);
            fail("Expected IllegalArgumentException for extremely large review content.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().equals("Question content must be between 1 and 255 characters."));
        } catch (SQLException se) {
            fail("Unexpected SQLException: " + se.getMessage());
        }
    }
    @Test
    void createReviewAvg() {
        // Example test: trying to create a review with average size content. Should be valid
        try {
            Review r = new Review(1, "I agree", 2, "John"); // Assuming constructor (questionId, content, rating)
            r.create(dbHelper);
            
            Review ra = Review.read(dbHelper, r.getId());
            assertTrue(ra.getContent().equals("I agree"));
            r.delete(dbHelper);
        } catch (IllegalArgumentException e) {
            fail("Should not have illegal argument exception");
        } catch (SQLException se) {
            fail("Unexpected SQLException: " + se.getMessage());
        }
    }
    
    @Test
    void updateReviewMin() {
    	//Example test: text should stay the same
    	try {
    		Review r = new Review(1, "Initial text", 2, "John");
    		r.create(dbHelper);
    		r.update(dbHelper, "Initial text");
    		Review ra = Review.read(dbHelper, r.getId());
    		assertTrue(ra.getContent().equals("Initial text"));
    		r.delete(dbHelper);
    	} catch (IllegalArgumentException e) {
    		fail("Should not have illegal argument exception");
    	} catch (SQLException se) {
    		fail("Unexpected SQLException: " + se.getMessage());
    	}
    }
    @Test
    void updateReviewAvg() {
    	//Example test: text should change
    	try {
    		Review r = new Review(1, "Initial text", 2, "John");
    		r.create(dbHelper);
    		r.update(dbHelper, "Updated Text");
    		Review ra = Review.read(dbHelper, r.getId());
    		assertTrue(ra.getContent().equals("Updated Text"));
    		r.delete(dbHelper);
    	} catch (IllegalArgumentException e) {
    		fail("Should not have illegal argument exception");
    	} catch (SQLException se) {
    		fail("Unexpected SQLException: " + se.getMessage());
    	}
    }
    @Test
    void updateReviewMax() {
    	//Example test: trying to update a review with max content. Should not be valid
    	try {
    		Review r = new Review(1, "Initial text", 2, "John");
    		r.create(dbHelper);
    		r.update(dbHelper, "DFddfsdfasadsfasdfsadfasdfasdfasdfasdfdasdfasdfadsfasdfasdfasdfasdfasdfadfasdfasdfadsfasdfasdfjasdjfadskfjaksdjfkasjdkfajsdkfjasdfasdfjkasdjfkajsdkfjaksdjfkasdjfkajsdkfjaksdjfkajsdkfjaskdfjaksdjfkasdjfkasjdkfjasdkfjkasdjfasdfasdfasdfasdfasdfadsfasdfadsfasdfasdfasdfasdf");
    		r.delete(dbHelper);
            fail("Expected IllegalArgumentException for extremely large review content.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().equals("Question content must be between 1 and 255 characters."));
        } catch (SQLException se) {
            fail("Unexpected SQLException: " + se.getMessage());
        }
    }
    @Test
    void deleteReview() {
    	//Example test: review should be successfully deleted
    	try {
    		Review r = new Review(1, "It works", 2, "John");
    		r.create(dbHelper);
    		r.delete(dbHelper);
    		Review ra = Review.read(dbHelper, r.getId());
            assertNull(ra);
        } catch (SQLException se) {
            fail("Unexpected SQLException: " + se.getMessage());
        }
    }
    @Test
    void pastReview() {
    	//Example test: past review should be readable
    	try {
    		Review r = new Review(1, "Initial text", 2, "John");
    		r.create(dbHelper);
    		r.update(dbHelper, "Updated Text");
    		PastReview ra = PastReview.read(dbHelper, r.getId());
    		r.delete(dbHelper);
    		assertTrue(ra.getContent().equals("Initial text"));
    		
    	} catch (IllegalArgumentException e) {
    		fail("Should not have illegal argument exception");
    	} catch (SQLException se) {
    		fail("Unexpected SQLException: " + se.getMessage());
    	}
    }
    
    

}
