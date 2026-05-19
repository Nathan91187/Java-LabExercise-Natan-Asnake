package actions;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Receiver implements Runnable {

    DataInputStream in;
    VBox chat;

    public Receiver(DataInputStream in, VBox chat) {
        this.in = in;
        this.chat = chat;
    }

    public void run() {
        try {
            while (true) {

                String data = in.readUTF();

                if (data.equals("Image")) {

                    String sender = in.readUTF();
                    int size = in.readInt();

                    byte[] imageBytes = new byte[size];
                    in.readFully(imageBytes);

                    Platform.runLater(() -> {
                        Image image = new Image(new ByteArrayInputStream(imageBytes));
                        ImageView imageView = new ImageView(image);

                        imageView.setFitWidth(200);
                        imageView.setPreserveRatio(true);

                        Text label = new Text(sender + " sent an image");
                        label.setFont(Font.font(20));

                        chat.getChildren().addAll(label, imageView);
                    });

                } else {

                    String finalData = data;
                    Platform.runLater(() -> {
                        Text text = new Text(finalData);
                        text.setFont(Font.font(25));
                        chat.getChildren().add(text);
                    });
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}