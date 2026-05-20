package actors;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server{
    static Map <String, ClientHandler> clients = new HashMap<>();
    static List <String> onlineUsers = new ArrayList<>();
    public static void addClients(String user, ClientHandler clientHandler){
        clients.put(user,clientHandler);
    }
    public static void broadcast(String msg) throws IOException {
        for(ClientHandler c: clients.values()){
            c.out.writeUTF(msg);
            c.out.flush();
        }
    }
    public static void oneVoneChat(String msg, String user_name, String me) throws IOException {
        if(clients.containsKey(user_name)){
            clients.get(user_name).out.writeUTF(msg);
            clients.get(me).out.writeUTF(msg);
            clients.get(user_name).out.flush();
            clients.get(me).out.flush();
        }
        else {
            clients.get(me).out.writeUTF("User " + user_name + " Not Online!");
            clients.get(me).out.flush();
        }
    }
    public static void broadcastImage(String sender, byte[] data) throws IOException {
        for (ClientHandler c : clients.values()) {
            c.out.writeUTF("Image");
            c.out.writeUTF(sender);
            c.out.writeInt(data.length);
            c.out.write(data);
            c.out.flush();
        }
    }
    public static void oneVoneImage(String sender, String receiver, byte[] data) throws IOException {
        if (clients.containsKey(receiver)) {
            clients.get(receiver).out.writeUTF("Image");
            clients.get(receiver).out.writeUTF(sender);
            clients.get(receiver).out.writeInt(data.length);
            clients.get(receiver).out.write(data);
            clients.get(receiver).out.flush();
        } else {
            clients.get(sender).out.writeUTF("User " + receiver + " Not Online!");
            clients.get(sender).out.flush();
        }
    }
    public  static void removeOffline(String user_name){
        clients.remove(user_name);
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true){
            Socket socket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(socket);
            new Thread(handler).start();
        }
    }


}