package actors;

import Database.MessageSaver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Base64;

public class ClientHandler implements Runnable {
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    String user_name;
    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            user_name = in.readUTF();
            Server.addClients(user_name, this);
            while (true) {
                String command = in.readUTF();
                if (command.equals("Broadcast")) {
                    String data = in.readUTF();
                    Server.broadcast(data);
                    MessageSaver.save(user_name, "All", "TEXT", data);}
                else if (command.equals("Target")) {
                    String receiver = in.readUTF();
                    String data = in.readUTF();
                    Server.oneVoneChat(data, receiver, user_name);
                    MessageSaver.save(user_name, receiver, "TEXT", data);}
                else if (command.equals("ImageBroadcast")) {
                    String sender = in.readUTF();
                    int size = in.readInt();
                    byte[] data = new byte[size];
                    in.readFully(data);
                    Server.broadcastImage(sender, data);
                    String imageData = Base64.getEncoder().encodeToString(data);
                    MessageSaver.save(user_name, "All", "IMAGE", imageData);}
                else if (command.equals("ImagePrivate")) {
                    String sender = in.readUTF();
                    String receiver = in.readUTF();
                    int size = in.readInt();
                    byte[] data = new byte[size];
                    in.readFully(data);
                    Server.oneVoneImage(sender, receiver, data);
                    String imageData = Base64.getEncoder().encodeToString(data);
                    MessageSaver.save(user_name, receiver, "IMAGE", imageData);
                }
            }
        } catch (IOException e) {
            if (user_name != null) {
                Server.removeOffline(user_name);
            }
        }
    }
}