import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Authentication extends Application{
    @Override
    public void start(Stage stg) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/authentication.fxml"));
        Parent root = loader.load();

        Authentication_Controller controller = loader.getController();
        controller.setStage(stg);

        stg.setTitle("Real Time Chat Application");
        stg.setScene(new Scene(root));
        stg.setResizable(false);
        stg.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
