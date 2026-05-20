package gui;

import Database.HistoryFetcher;
import actions.Receiver;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.io.File;
import java.util.List;
public class ClientGui extends Application {
    private String user_name;
    private byte[] pendingImage;
    private Label imageStatus = new Label("");

    @Override
    public void start(Stage stage) {
        try {
            Socket socket = new Socket("localhost", 8080);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            VBox chat = new VBox(8);
            chat.setPadding(new Insets(10));
            ScrollPane scrollPane = new ScrollPane(chat);
            scrollPane.setFitToWidth(true);
            Receiver receiver = new Receiver(in, chat);
            new Thread(receiver).start();
            Label username = new Label("Username:");
            TextField usernameField = new TextField();
            Button loginButton = new Button("Start Chat");
            HBox loginBox = new HBox(10, username, usernameField, loginButton);
            loginBox.setAlignment(Pos.CENTER);
            loginBox.setPadding(new Insets(15));
            TextField messageField = new TextField();
            messageField.setPromptText("Type message...");
            Button broadcastBtn = new Button("All");
            Button privateBtn = new Button("Private");
            Button historyBtn = new Button("History");
            Image icon = new Image("/myIcon.png");
            ImageView iconView = new ImageView(icon);
            iconView.setFitWidth(18);
            iconView.setFitHeight(18);
            Button imageButton = new Button();
            imageButton.setGraphic(iconView);
            VBox sendOptions = new VBox(8, broadcastBtn, privateBtn, historyBtn);
            sendOptions.setAlignment(Pos.CENTER);
            VBox bottomBox = new VBox(8);
            HBox inputBar = new HBox(10, imageButton, messageField, sendOptions);
            inputBar.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(messageField, Priority.ALWAYS);
            bottomBox.getChildren().addAll(imageStatus, inputBar);
            bottomBox.setPadding(new Insets(10));
            BorderPane root = new BorderPane();
            root.setTop(loginBox);
            root.setCenter(scrollPane);
            root.setBottom(null);
            loginButton.setOnAction(e -> {
                user_name = usernameField.getText();
                if (user_name.isEmpty()) {
                    user_name = "Anonymous";
                }
                try {
                    out.writeUTF(user_name);
                    out.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                root.setBottom(bottomBox);
                root.setTop(null);
            });
            imageButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    try {
                        pendingImage = Files.readAllBytes(file.toPath());
                        imageStatus.setText("Image selected (ready to send)");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            broadcastBtn.setOnAction(e -> {
                try {
                    if (pendingImage != null) {
                        out.writeUTF("ImageBroadcast");
                        out.writeUTF(user_name);
                        out.writeInt(pendingImage.length);
                        out.write(pendingImage);
                        pendingImage = null;
                        imageStatus.setText("");
                    } else if (!messageField.getText().isEmpty()) {
                        out.writeUTF("Broadcast");
                        out.writeUTF(user_name + ": " + messageField.getText());
                        messageField.clear();
                    }
                    out.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            privateBtn.setOnAction(e -> {
                VBox privateBox = new VBox(5);
                TextField receiverField = new TextField();
                receiverField.setPromptText("Receiver username");
                Button sendPrivate = new Button("Send");
                HBox privateInput = new HBox(10, receiverField, sendPrivate);
                privateBox.getChildren().add(privateInput);
                sendPrivate.setOnAction(ev -> {
                    try {
                        if (pendingImage != null) {
                            out.writeUTF("ImagePrivate");
                            out.writeUTF(user_name);
                            out.writeUTF(receiverField.getText());
                            out.writeInt(pendingImage.length);
                            out.write(pendingImage);
                            pendingImage = null;
                            imageStatus.setText("");
                        } else {
                            out.writeUTF("Target");
                            out.writeUTF(receiverField.getText());
                            out.writeUTF(user_name + ": " + messageField.getText()
                            );
                            messageField.clear();
                        }
                        out.flush();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                bottomBox.getChildren().add(privateBox);
            });
            historyBtn.setOnAction(e -> {
                VBox historyBox = new VBox(10);
                historyBox.setPadding(new Insets(15));
                Button backButton = new Button("Back");
                List<String[]> history = HistoryFetcher.fetchHistory(user_name);
                for (String[] msg : history) {
                    String sender = msg[0];
                    String InReceiver = msg[1];
                    String type = msg[2];
                    String content = msg[3];
                    if (type.equals("TEXT")) {
                        Text text = new Text(sender + " -> " + InReceiver + ": " + content);
                        text.setFont(Font.font(18));
                        historyBox.getChildren().add(text);
                    }
                    else if (type.equals("IMAGE")) {
                        Text info = new Text(sender + " -> " + InReceiver + " (IMAGE)");
                        info.setFont(Font.font(18));
                        byte[] imageBytes = java.util.Base64.getDecoder().decode(content);
                        Image image = new Image( new ByteArrayInputStream(imageBytes));
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(200);
                        imageView.setPreserveRatio(true);
                        VBox imageContainer = new VBox(5);
                        imageContainer.getChildren().addAll(info, imageView);
                        historyBox.getChildren().add(imageContainer);
                    }
                }
                historyBox.getChildren().add(backButton);
                ScrollPane historyPane = new ScrollPane(historyBox);
                historyPane.setFitToWidth(true);
                root.setCenter(historyPane);
                backButton.setOnAction(ev -> {
                    root.setCenter(scrollPane);
                });
            });
            Scene scene = new Scene(root, 750, 500);
            stage.setScene(scene);
            stage.setTitle("Chat App");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}