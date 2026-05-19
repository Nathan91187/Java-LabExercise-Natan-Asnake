package FileSystem;
import java.io.*;
public class FileWriting {
    public static String saveAs(File file, String data){
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(data);
            fileWriter.close();
            return "file written successfully";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void save(String data, String fileName){
        try{
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
