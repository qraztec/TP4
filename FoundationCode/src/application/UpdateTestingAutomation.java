



package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;

/**
 * Automated testing for update functionality in Question and Answer classes.
 * <p>
 * This class connects to the database and performs basic automated test cases
 * to ensure that updating questions and answers works correctly.
 * </p>
 * 
 * @author Gerum
 * @since 1.0
 */
public class UpdateTestingAutomation {
	static int numPassed = 0; //counter of the number of passed tests
	static int numFailed = 0; //counter of the number of failed tests
	private static Question currentQuestion; //Question class for question test
	private static Answer currentAnswer; //Answer class for answer test cases
	private static DatabaseHelper dbHelper = new DatabaseHelper(); //Database helper class
    /**
     * Main test runner for update testing automation. Connects to the database
     * and executes test cases for question and answer updates.
     * 
     * @param args The command-line arguments passed to the program.
     */
	public static void main(String[] args) {
		try {
			dbHelper.connectToDatabase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("___________________");
		System.out.println("\nTesting Automation");
		
		// This is not covered by javadoc
		performQuestionCase(1, "First question", "Updated Question", true);
		// This is a properly written positive test. Updates answered status to true and must be equal to true
		performAnsweredCase(3, false, true, true);
		//This is a properly written positive test. Answer's updated contents must match "Updated answer"
		performAnswerCase(2, "First answer", "Updated answer", true);
		
		
		
	}

	private static void performQuestionCase(int testCase, String initialText, String updatedText, boolean expectedPass) {
		System.out.println("_____________________");
		System.out.println("Input: \"" + initialText + "\"");
		System.out.println("Test Case #: \"" + testCase + "\"");
		System.out.println("Expected Update: \"" + updatedText + "\"");
		System.out.println("______________________");
		
		//Call the Question class to make and then update a question.
		try {
			//dbHelper.connectToDatabase();
			currentQuestion = new Question(initialText);
			currentQuestion.create(dbHelper);
			currentQuestion.update(dbHelper,  updatedText);
			Question q = Question.read(dbHelper, currentQuestion.getId());
			
			String compareText = q.getContent();
			System.out.println(compareText);
			if (expectedPass) {
				if (compareText != updatedText) {
					System.out.println("**Failure** Question Test failed but it was supposed to succeed");
				}
				else {
					System.out.println("**Success** Question Test succeeded and it was supposed to succeed");
				}
			}
			else {
				if (compareText != updatedText) {
					System.out.println("**Success** Question Test failed and it was supposed to fail");
				}
				else {
					System.out.println("**Failure** Question Test succeeded but it was supposed to fail");
				}
			}
			currentQuestion.delete(dbHelper);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Question Test failed");
		}
	}
	/**
     * Performs a test case for updating an answer.
     *
     * @param testCase     The test case number.
     * @param initialText  The original content of the answer.
     * @param updatedText  The updated content for the answer.
     * @param expectedPass Whether the update is expected to succeed.
     */
	private static void performAnswerCase(int testCase, String initialText, String updatedText, boolean expectedPass) {
		/************** Display an individual test case header **************/
		System.out.println("_____________________");
		System.out.println("Input: \"" + initialText + "\"");
		System.out.println("Test Case #: \"" + testCase + "\"");
		System.out.println("Expected Update: \"" + updatedText + "\"");
		System.out.println("______________________");
		
		try {
			//dbHelper.connectToDatabase();
			/************** Create and update answer content **************/
			currentAnswer = new Answer(1, initialText, false);
			currentAnswer.create(dbHelper);
			currentAnswer.update(dbHelper,  updatedText);
			Answer a = Answer.read(dbHelper, currentAnswer.getId());
			String compareText = a.getContent();
			if (expectedPass) {
				if (compareText != updatedText) {
					System.out.println("**Failure** Answer Test failed but it was supposed to succeed");
				}
				else {
					// If expected pass is true and it was supposed to pass, then it succeeds
					System.out.println("**Success** Answer Test succeeded and it was supposed to succeed");
				}
			}
			else {
				if (compareText != updatedText) {
					System.out.println("**Success** Answer Test failed and it was supposed to fail");
				}
				else {
					System.out.println("**Failure** Answer Test succeeded but it was supposed to fail");
				}
			}
			currentAnswer.delete(dbHelper);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Answer Test failed");
		}
	}
	/**
     * Performs a test case for updating answered status.
     *
     * @param testCase     The test case number.
     * @param initialAnswered  The original answered status.
     * @param updatedAnswered  The updated answered status.
     * @param expectedPass Whether the update is expected to succeed.
     */
	private static void performAnsweredCase(int testCase, boolean initialAnswered, boolean updatedAnswered, boolean expectedPass) {
		/************** Display an individual test case header **************/
		System.out.println("_____________________");
		System.out.println("Input: \"" + String.valueOf(initialAnswered) + "\"");
		System.out.println("Test Case #: \"" + testCase + "\"");
		System.out.println("Expected Update: \"" + String.valueOf(updatedAnswered) + "\"");
		System.out.println("______________________");
		
		try {
			/************** Create and update answered status **************/
			//dbHelper.connectToDatabase();
			currentAnswer = new Answer(1, "Initial Answer", initialAnswered);
			currentAnswer.create(dbHelper);
			currentAnswer.updateAnswered(dbHelper,  updatedAnswered);
			Answer a = Answer.read(dbHelper, currentAnswer.getId());
			boolean compareAnswered = a.getAnswered();
			if (expectedPass) {
				
				if (compareAnswered != updatedAnswered) {
					System.out.println("**Failure** Answered Test failed but it was supposed to succeed");
				}
				// If the test case expected the test to pass then it passes
				else {
					System.out.println("**Success** Answered Test succeeded and it was supposed to succeed");
				}
			}
			else {
				if (compareAnswered != updatedAnswered) {
					System.out.println("**Success** Answered Test failed and it was supposed to fail");
				}
				else {
					System.out.println("**Failure** Answered Test succeeded but it was supposed to fail");
				}
			}
			currentAnswer.delete(dbHelper);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Answered Test failed");
		}
	}
	
}
