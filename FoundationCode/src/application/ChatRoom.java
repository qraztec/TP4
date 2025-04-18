package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import databasePart1.DatabaseHelper;

/**
 * Represents a private chat room between exactly two users, identified by their user IDs.
 */
public class ChatRoom {
    private int id;      // ID for the chatroom
    private int userId1; // ID of user 1 in the chat
    private int userId2; // ID of user 2 in the chat

    /**
     * Constructs a ChatRoom object without an explicit ID, used for creating a new room.
     *
     * @param userId1 the first user's ID in this chat room
     * @param userId2 the second user's ID in this chat room
     */
    public ChatRoom(int userId1, int userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    /**
     * Constructs a ChatRoom object with an existing ID, typically loaded from the database.
     *
     * @param id      the unique ID of the chat room
     * @param userId1 the first user's ID in this chat room
     * @param userId2 the second user's ID in this chat room
     */
    public ChatRoom(int id, int userId1, int userId2) {
        this.id = id;
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    /**
     * Gets the ID of this chat room.
     *
     * @return the chat room ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the user ID of the first user in this chat room.
     *
     * @return the first user's ID
     */
    public int getUserId1() {
        return userId1;
    }

    /**
     * Gets the user ID of the second user in this chat room.
     *
     * @return the second user's ID
     */
    public int getUserId2() {
        return userId2;
    }

    /**
     * Creates this ChatRoom in the database by inserting a new record with userId1 and userId2.
     * Automatically retrieves the generated ID (if successful).
     *
     * @param dbHelper the DatabaseHelper used to obtain a database connection
     * @throws SQLException if a database access error occurs or the insert fails
     */
    public void create(DatabaseHelper dbHelper) throws SQLException {
        String sql = "INSERT INTO ChatRooms (userId1, userId2) VALUES (?, ?)";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, this.userId1);
            pstmt.setInt(2, this.userId2);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating chat room failed, no rows affected.");
            }
            // Retrieve the auto-generated key (id)
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating chat room failed, no ID obtained.");
                }
            }
        }
    }
}
