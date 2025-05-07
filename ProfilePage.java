import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProfilePage extends Application{
    @Override
    public void start(Stage stg) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(GetFXML.get_fxml("fxml/profile.fxml"));
        
        Profile_Controller controller = loader.getController();
        controller.setStage(stg);

        stg.setTitle("Real Time Chat Application");
        stg.setScene(new Scene(root));
        stg.setResizable(false);
        stg.setOnCloseRequest(event -> Platform.exit());
        stg.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
