package application;

public class UserSession {
	public static User loggedInUser;
	
	public static void setUser(User user) {
		loggedInUser = user;
	}
	
	public static User getUser() {
		return loggedInUser;
	}
	
	public static void clear() {
		loggedInUser = null;
	}
}
