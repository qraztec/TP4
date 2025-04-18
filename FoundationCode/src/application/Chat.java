package application;

import databasePart1.DatabaseHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single message within a chat room, containing information about
 * the sender's role, username, the message content, and the chat room in which
 * it is posted.
 */
public class Chat {
    private int id;         // ID of the chat message
    private int roomId;     // Foreign key referencing the chat room ID
    private String role;    // Name of the role (e.g., Staff or Instructor)
    private String userName; // Username of the person who posted the message
    private String content;  // Text content of the chat message

    /**
     * Constructs a new Chat message without an explicit ID, to be used
     * when creating a new message in the database.
     *
     * @param roomId   the ID of the chat room this message is associated with
     * @param role     the role of the sender (e.g., Staff, Instructor)
     * @param userName the username of the sender
     * @param content  the content of the message
     */
    public Chat(int roomId, String role, String userName, String content) {
        this.roomId = roomId;
        this.role = role;
        this.userName = userName;
        this.content = content;
    }

    /**
     * Constructs a new Chat message with an explicit ID, typically used
     * when loading a message from the database.
     *
     * @param id       the unique ID of the chat message
     * @param roomId   the ID of the chat room this message is associated with
     * @param role     the role of the sender (e.g., Staff, Instructor)
     * @param userName the username of the sender
     * @param content  the content of the message
     */
    public Chat(int id, int roomId, String role, String userName, String content) {
        this.id = id;
        this.roomId = roomId;
        this.role = role;
        this.userName = userName;
        this.content = content;
    }

    /**
     * Returns the unique ID of this chat message.
     *
     * @return the chat message's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the chat room ID associated with this message.
     *
     * @return the chat room ID
     */
    public int getRoomId() {
        return roomId;
    }

    /**
     * Returns the role (Staff or Instructor) of the user who posted this message.
     *
     * @return the sender's role
     */
    public String getRole() {
        return role;
    }

    /**
     * Returns the username of the individual who posted this message.
     *
     * @return the sender's username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the content of this chat message.
     *
     * @return the text of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Inserts this Chat message into the database (creating a new record).
     * On success, the generated primary key is set on this Chat object's ID field.
     *
     * @param dbHelper the DatabaseHelper used to obtain a database connection
     * @throws SQLException if inserting the Chat message fails or no rows are affected
     */
    public void create(DatabaseHelper dbHelper) throws SQLException {
        String sql = "INSERT INTO Chats (roomId, role, userName, content) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, this.roomId);
            pstmt.setString(2, this.role);
            pstmt.setString(3, this.userName);
            pstmt.setString(4, this.content);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating chat failed, no rows affected.");
            }
            // Retrieve the auto-generated key (id)
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating chat failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Loads all Chat messages belonging to a specific chat room from the database.
     *
     * @param dbHelper the DatabaseHelper used to obtain a database connection
     * @param roomId   the ID of the chat room whose messages should be loaded
     * @return a List of Chat objects for the given chat room, ordered by ascending ID
     * @throws SQLException if a database access error occurs
     */
    public static List<Chat> readAllForChatRoom(DatabaseHelper dbHelper, int roomId) throws SQLException {
        List<Chat> list = new ArrayList<>();
        String sql = "SELECT id, roomId, role, userName, content FROM Chats WHERE roomId = ? ORDER BY id ASC";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int rid = rs.getInt("roomId");
                    String role = rs.getString("role");
                    String uname = rs.getString("userName");
                    String content = rs.getString("content");
                    Chat chatMsg = new Chat(id, rid, role, uname, content);
                    list.add(chatMsg);
                }
            }
        }
        return list;
    }
}
