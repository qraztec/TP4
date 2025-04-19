package application;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import databasePart1.DatabaseHelper;

class AdminRequestTests {
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
    
    //Tests if i can correctly get the right admin request info
	@Test
	void testGetAdminRequest() {
		try {
			AdminRequest ar = new AdminRequest(true, "", "Remove this account");
			ar.createRequest(dbHelper);
			String[] adminRequest = dbHelper.getAdminRequest(ar.getId());
			assertEquals(true, Boolean.parseBoolean(adminRequest[0]));
			assertEquals("", adminRequest[1]);
			assertEquals("Remove this account", adminRequest[2]);
			ar.deleteRequest(dbHelper);
		}
		 catch (SQLException se) {
	            fail("Unexpected SQLException: " + se.getMessage());
	     }
	}

}
