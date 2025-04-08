package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;

public interface Post {
	public void create(DatabaseHelper dbHelper) throws SQLException;
	public void update(DatabaseHelper dbHelper, String newContent) throws SQLException;
	public void delete(DatabaseHelper dbHelper) throws SQLException;
	public int getId();
	public String getContent();
	public String toString();
	public boolean isValidContent(String content);
}


