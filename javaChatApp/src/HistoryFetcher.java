package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HistoryFetcher {
    public static List<String[]> fetchHistory(String user_name) {
        List<String[]> messages = new ArrayList<>();
        String sql = """
                SELECT sender, receiver,type, content
                FROM messages
                WHERE sender = ?
             
                """;
        try (
                Connection conn = DB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, user_name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String receiver = rs.getString("receiver");
                String type = rs.getString("type");
                String content = rs.getString("content");
                messages.add(new String[]{
                        sender,
                        receiver,
                        type,
                        content
                });
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }
}