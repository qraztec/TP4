package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;

/**
 * Maintains a list of ChatRoom objects, providing functionality to load them
 * from the database and search them by user ID.
 */
public class ChatRoomList {
    private List<ChatRoom> chatRoomList;

    /**
     * Constructs a new, empty ChatRoomList.
     */
    public ChatRoomList() {
        chatRoomList = new ArrayList<>();
    }

    /**
     * Loads all chat rooms from the database into this list, replacing any existing data.
     *
     * @param dbHelper the DatabaseHelper used for database operations
     * @throws SQLException if a database access error occurs
     */
    public void loadAllChatRooms(DatabaseHelper dbHelper) throws SQLException {
        String sql = "SELECT id, userId1, userId2 FROM ChatRooms";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // Clear the current list before loading new data
            chatRoomList.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                int userId1 = rs.getInt("userId1");
                int userId2 = rs.getInt("userId2");
                // Create a new ChatRoom object using the data from the database
                ChatRoom chatRoom = new ChatRoom(id, userId1, userId2);
                chatRoomList.add(chatRoom);
            }
        }
    }

    /**
     * Searches for all ChatRooms that include the specified userId.
     * A ChatRoom matches if userId is either userId1 or userId2.
     *
     * @param userId the user ID to search for
     * @return a list of ChatRooms matching the user's ID
     */
    public List<ChatRoom> searchChatRooms(int userId) {
        List<ChatRoom> result = new ArrayList<>();
        for (ChatRoom c : chatRoomList) {
            if (c.getUserId1() == userId || c.getUserId2() == userId) {
                result.add(c);
            }
        }
        return result;
    }
}
