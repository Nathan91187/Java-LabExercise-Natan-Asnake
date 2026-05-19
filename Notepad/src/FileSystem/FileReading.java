package FileSystem;
import java.io.*;
public class FileReading {
    public static String open(File f) {
        String line;
        StringBuilder data = new StringBuilder();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(f));
            while ((line = fileReader.readLine()) != null) {
                data.append(line);
            }
                return data.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
