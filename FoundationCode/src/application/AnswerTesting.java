package application;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

/**
 * AnswerTesting
 * 
 * A simple automated test suite for testing CRUD operations on the Answer class.
 * This class is modeled after the PasswordEvaluationTestingAutomation example.
 */

public class AnswerTesting {

    static int numPassed = 0;  // Counter for passed tests
    static int numFailed = 0;  // Counter for failed tests

    public static void main(String[] args) {
        System.out.println("______________________________________");
        System.out.println("\nAnswer Testing Automation");

        // Test case 1: Create a valid answer, then read and delete it.
        performTestCase(1, 1, "This is a valid answer", true);

        // Test case 2: Try to create an answer with invalid content (empty string).
        performTestCase(2, 1, "", false);

        // Test case 3: Create a valid answer and then update its content.
        performUpdateTestCase(3, 1, "Initial valid answer", "Updated valid answer", true);

        // Test case 4: Create a valid answer and delete it.
        performDeleteTestCase(4, 1, "Answer to be deleted", true);

        // Test case 5: Search answers by question ID.
        performSearchByQuestionIdTestCase(5, 1, true);

        System.out.println("____________________________________________________________________________");
        System.out.println("\nNumber of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
    }

    /**
     * This method tests creating, reading, and deleting an Answer.
     * 
     * @param testCase     The test case number.
     * @param questionId   The question ID associated with the answer.
     * @param content      The content for the answer.
     * @param expectedPass true if the test is expected to pass; false otherwise.
     */
    private static void performTestCase(int testCase, int questionId, String content, boolean expectedPass) {
        System.out.println("____________________________________________________________________________\nTest case: " + testCase);
        System.out.println("Input content: \"" + content + "\"");

        DatabaseHelper dbHelper = new DatabaseHelper();
        try {
            dbHelper.connectToDatabase();
        } catch (SQLException e) {
            System.out.println("DB Connection Error: " + e.getMessage());
            numFailed++;
            return;
        }

        try {
            // Attempt to create the answer.
            Answer a = new Answer(questionId, content, false);
            a.create(dbHelper);

            // If creation succeeded but we expected failure, flag a test failure.
            if (!expectedPass) {
                System.out.println("***Failure***: An answer was created with invalid content.");
                numFailed++;
                // Cleanup if needed.
                a.delete(dbHelper);
            } else {
                // Otherwise, attempt to read back the answer.
                Answer readA = Answer.read(dbHelper, a.getId());
                if (readA != null && readA.getContent().equals(content)) {
                    System.out.println("***Success***: Answer created and read successfully.");
                    numPassed++;
                } else {
                    System.out.println("***Failure***: The read answer does not match the created one.");
                    numFailed++;
                }
                // Cleanup: delete the answer.
                a.delete(dbHelper);
            }
        } catch (IllegalArgumentException e) {
            // Expected if content is invalid.
            if (!expectedPass) {
                System.out.println("***Success***: Invalid answer was rejected: " + e.getMessage());
                numPassed++;
            } else {
                System.out.println("***Failure***: Valid answer was rejected: " + e.getMessage());
                numFailed++;
            }
        } catch (SQLException se) {
            System.out.println("***Failure***: SQL error: " + se.getMessage());
            numFailed++;
        } finally {
            dbHelper.closeConnection();
        }
    }

    /**
     * This method tests updating an Answer.
     * 
     * @param testCase       The test case number.
     * @param questionId     The question ID associated with the answer.
     * @param initialContent The initial content of the answer.
     * @param updatedContent The new content to update the answer with.
     * @param expectedPass   true if the update is expected to succeed; false otherwise.
     */
    private static void performUpdateTestCase(int testCase, int questionId, String initialContent, String updatedContent, boolean expectedPass) {
        System.out.println("____________________________________________________________________________\nTest case: " + testCase + " (Update Test)");
        System.out.println("Initial content: \"" + initialContent + "\"");
        System.out.println("Updated content: \"" + updatedContent + "\"");

        DatabaseHelper dbHelper = new DatabaseHelper();
        try {
            dbHelper.connectToDatabase();
        } catch (SQLException e) {
            System.out.println("DB Connection Error: " + e.getMessage());
            numFailed++;
            return;
        }

        try {
            // Create the answer.
            Answer a = new Answer(questionId, initialContent, false);
            a.create(dbHelper);

            // Attempt to update the answer.
            a.update(dbHelper, updatedContent);

            // Read back the updated answer.
            Answer updatedA = Answer.read(dbHelper, a.getId());
            if (updatedA != null && updatedA.getContent().equals(updatedContent)) {
                System.out.println("***Success***: Answer updated successfully.");
                numPassed++;
            } else {
                System.out.println("***Failure***: Answer update failed. Updated content does not match.");
                numFailed++;
            }
            // Cleanup.
            a.delete(dbHelper);
        } catch (IllegalArgumentException e) {
            if (!expectedPass) {
                System.out.println("***Success***: Invalid update was rejected: " + e.getMessage());
                numPassed++;
            } else {
                System.out.println("***Failure***: Valid update was rejected: " + e.getMessage());
                numFailed++;
            }
        } catch (SQLException se) {
            System.out.println("***Failure***: SQL error: " + se.getMessage());
            numFailed++;
        } finally {
            dbHelper.closeConnection();
        }
    }

    /**
     * This method tests deleting an Answer.
     * 
     * @param testCase     The test case number.
     * @param questionId   The question ID associated with the answer.
     * @param content      The content of the answer.
     * @param expectedPass true if the test is expected to pass; false otherwise.
     */
    private static void performDeleteTestCase(int testCase, int questionId, String content, boolean expectedPass) {
        System.out.println("____________________________________________________________________________\nTest case: " + testCase + " (Delete Test)");
        System.out.println("Content: \"" + content + "\"");

        DatabaseHelper dbHelper = new DatabaseHelper();
        try {
            dbHelper.connectToDatabase();
        } catch (SQLException e) {
            System.out.println("DB Connection Error: " + e.getMessage());
            numFailed++;
            return;
        }

        try {
            // Create the answer.
            Answer a = new Answer(questionId, content, false);
            a.create(dbHelper);

            // Delete the answer.
            a.delete(dbHelper);

            // Try to read the deleted answer.
            Answer deletedA = Answer.read(dbHelper, a.getId());
            if (deletedA == null) {
                System.out.println("***Success***: Answer deleted successfully.");
                numPassed++;
            } else {
                System.out.println("***Failure***: Answer was not deleted.");
                numFailed++;
            }
        } catch (SQLException se) {
            System.out.println("***Failure***: SQL error: " + se.getMessage());
            numFailed++;
        } finally {
            dbHelper.closeConnection();
        }
    }

    /**
     * This method tests searching answers by question ID.
     * 
     * @param testCase     The test case number.
     * @param questionId   The question ID to search answers by.
     * @param expectedPass true if the test is expected to pass; false otherwise.
     */
    private static void performSearchByQuestionIdTestCase(int testCase, int questionId, boolean expectedPass) {
        System.out.println("____________________________________________________________________________\nTest case: " + testCase + " (Search by Question ID Test)");
        System.out.println("Question ID: " + questionId);

        DatabaseHelper dbHelper = new DatabaseHelper();
        try {
            dbHelper.connectToDatabase();
        } catch (SQLException e) {
            System.out.println("DB Connection Error: " + e.getMessage());
            numFailed++;
            return;
        }

        try {
            // Load answers and search by question ID.
            Answers answers = new Answers();
            answers.loadAllAnswers(dbHelper);
            List<Answer> searchResults = answers.searchAnswersId(questionId);

            if (!searchResults.isEmpty() && expectedPass) {
                System.out.println("***Success***: Found answers with question ID: " + questionId);
                numPassed++;
            } else if (searchResults.isEmpty() && !expectedPass) {
                System.out.println("***Success***: No answers found as expected for question ID: " + questionId);
                numPassed++;
            } else {
                System.out.println("***Failure***: Unexpected result during search by question ID.");
                numFailed++;
            }
        } catch (SQLException se) {
            System.out.println("***Failure***: SQL error: " + se.getMessage());
            numFailed++;
        } finally {
            dbHelper.closeConnection();
        }
    }
}