import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class AboutUs_Controller {
    private Stage stg;

    @FXML
    protected void setStage(Stage stg){
        this.stg = stg;
    }

    @FXML
    void btn_go_back(ActionEvent event) {
        Platform.runLater(()->{
            try{
                stg.close();
                new ProfilePage().start(new Stage());
            } catch (Exception e) {
                // e.printStackTrace();
                ErrorHandler.write_log_file(e);
            }
        });
    }
}
