import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProfilePage extends Application{
    @Override
    public void start(Stage stg) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/profile.fxml"));
        Parent root = loader.load();

        stg.setTitle("Real Time Chat Application");
        stg.setScene(new Scene(root));
        stg.setResizable(false);
        stg.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
