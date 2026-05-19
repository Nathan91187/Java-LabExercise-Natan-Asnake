package GUI;

import FileSystem.FileReading;
import FileSystem.FileWriting;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Gui extends Application {

    private boolean saved = false;
    String savedPath = "";

    @Override
    public void start(Stage stage) {

        Button open = new Button("Open");
        Button save = new Button("Save");
        Button saveAs = new Button("Save As");
        Button newFile = new Button("New File");
        ToolBar toolBar = new ToolBar(open, save, saveAs, newFile);
        TextArea textArea = new TextArea();
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(toolBar);
        borderPane.setCenter(textArea);
        Scene scene = new Scene(borderPane, 700, 500);
        stage.setScene(scene);
        stage.setTitle("Notepad");
        stage.show();
        save.setOnAction(e -> {
            String data = textArea.getText();
            if (saved) {
                FileWriting.save(data, savedPath);
            }
            else {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Text Files", "*.txt")
                );
                File fileC = fileChooser.showSaveDialog(stage);
                if (fileC != null) {
                    FileWriting.saveAs(fileC, data);
                    savedPath = fileC.getAbsolutePath();
                    saved = true;
                }
            }
        });
        newFile.setOnAction(e -> {
            textArea.clear();
            saved = false;
        });
        saveAs.setOnAction(e -> {
            String data = textArea.getText();
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt")
            );
            File fileC = fileChooser.showSaveDialog(stage);
            if (fileC != null) {
                FileWriting.saveAs(fileC, data);
                savedPath = fileC.getAbsolutePath();
                saved = true;
            }
        });
        open.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt" )
            );
            File fileO = fileChooser.showOpenDialog(stage);
            if (fileO != null) {
                savedPath = fileO.getAbsolutePath();
                saved = true;
                String data = FileReading.open(fileO);
                textArea.setText(data);
            }
        });
    }
    public static void main(String args[]) {
        launch(args);
    }
}
