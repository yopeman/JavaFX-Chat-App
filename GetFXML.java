import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetFXML{
    private static String get_fxml_string(String file_path){
        try {
            return new String(Files.readAllBytes(Paths.get(file_path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream get_fxml(String file_name){
        String fxml_string = get_fxml_string(file_name);
        InputStream fxml_input_stream = new ByteArrayInputStream(fxml_string.getBytes());
        return fxml_input_stream;
    }

    public static void main(String[] args) {
        System.out.println(GetFXML.class.getResource("/image/about_img.jpg"));
    }
}