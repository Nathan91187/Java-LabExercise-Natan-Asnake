package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MessageSaver {

    public static void save(String sender, String receiver, String type, String content) {
        String sql = "INSERT INTO messages(sender, receiver, type, content) VALUES (?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, type);
            ps.setString(4, content);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
