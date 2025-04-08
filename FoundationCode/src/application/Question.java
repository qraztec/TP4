package application;

import java.sql.*;
import databasePart1.DatabaseHelper;

public class Question implements Post{
    private int id;
    private String content;

    // Constructor for a new Question (id will be generated)
    public Question(String content) {
        if (!isValidContent(content)) {
            throw new IllegalArgumentException("Question content cannot be empty.");
        }
        this.content = content;
    }

    // Constructor for an existing Question (with id)
    public Question(int id, String content) {
        if (!isValidContent(content)) {
            throw new IllegalArgumentException("Question content cannot be empty.");
        }
        this.id = id;
        this.content = content;
    }

    // Getters and Setters
    @Override
    public int getId() {
        return id;
    }
    @Override
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        if (!isValidContent(content)) {
            throw new IllegalArgumentException("Question content cannot be empty.");
        }
        this.content = content;
    }

    // Simple validation method
    public boolean isValidContent(String content) {
        return content != null && !content.trim().isEmpty();
    }

    // -------------------------------
    // CRUD Operations using DatabaseHelper
    // -------------------------------

    // Create a new Question in the database.
    @Override
    public void create(DatabaseHelper dbHelper) throws SQLException {
        String query = "INSERT INTO Questions (content) VALUES (?)";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, content);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        }
    }

    // Read a Question from the database by id.
    
    public static Question read(DatabaseHelper dbHelper, int id) throws SQLException {
        String query = "SELECT * FROM Questions WHERE id = ?";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Question(rs.getInt("id"), rs.getString("content"));
            }
        }
        return null;
    }

    // Update an existing Question in the database.
    @Override
    public void update(DatabaseHelper dbHelper, String newContent) throws SQLException {
        if (!isValidContent(newContent)) {
            throw new IllegalArgumentException("New question content cannot be empty.");
        }
        String query = "UPDATE Questions SET content = ? WHERE id = ?";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(query)) {
            pstmt.setString(1, newContent);
            pstmt.setInt(2, this.id);
            pstmt.executeUpdate();
            this.content = newContent;
        }
    }

    // Delete a Question from the database.
    @Override
    public void delete(DatabaseHelper dbHelper) throws SQLException {
        String query = "DELETE FROM Questions WHERE id = ?";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public String toString() {
        return "Question #" + id + ": " + content;
    }
}
