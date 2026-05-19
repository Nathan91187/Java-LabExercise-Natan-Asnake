package actors;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
            Server.addClients(user_name,this);
        while (true) {
                String command = in.readUTF();
                if(command.equals("Broadcast")){
                    String data = in.readUTF();
                Server.broadcast(data);
                } else if (command.equals("Target")) {
                    String receiver = in.readUTF();
                    String data = in.readUTF();
                    Server.oneVoneChat(data,receiver,user_name);
                }
                else if (command.equals("ImageBroadcast")) {
                    String sender = in.readUTF();
                    int size = in.readInt();
                    byte[] data = new byte[size];
                    in.readFully(data);

                    Server.broadcastImage(sender, data);
                }

                else if (command.equals("ImagePrivate")) {
                    String sender = in.readUTF();
                    String receiver = in.readUTF();
                    int size = in.readInt();
                    byte[] data = new byte[size];
                    in.readFully(data);

                    Server.oneVoneImage(sender, receiver, data);
                }
        }
        } catch (IOException e) {
            if(user_name != null){
                Server.removeOffline(user_name);
            }
        }
        }
}
