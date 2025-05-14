import java.awt.HeadlessException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Authentication_Controller {
    private Stage stg;

    @FXML
    private TextField tf_login_email;

    @FXML
    private PasswordField tf_login_pswd;

    @FXML
    private TextField tf_register_email;

    @FXML
    private PasswordField tf_register_pswd;

    @FXML
    private TextField tf_register_uname;

    @FXML
    protected void setStage(Stage stg){
        this.stg = stg;
    }

    @FXML
    void btn_cancel_login(ActionEvent event) {
        tf_login_email.setText("");
        tf_login_pswd.setText("");
    }

    @FXML
    void btn_cancel_register(ActionEvent event) {
        tf_register_uname.setText("");
        tf_register_email.setText("");
        tf_register_pswd.setText("");
    }

    @FXML
    void btn_register(ActionEvent event) {
        new Thread(()->{
            try{
                String uname = tf_register_uname.getText();
                String email = tf_register_email.getText();
                String pswd = tf_register_pswd.getText();

                if(!Validate.is_valid_uname(uname) || !Validate.is_valid_email(email) || !Validate.is_valid_pswd(pswd)){
                    return;
                }
                pswd = Validate.hash_pswd(pswd);

                Socket server = Network.get_server();
                if(server == null){
                    return;
                }
                
                InputStream input = server.getInputStream();
                OutputStream output = server.getOutputStream();
                DataInputStream data_inp = new DataInputStream(input);
                DataOutputStream data_out = new DataOutputStream(output);

                data_out.writeUTF("register");
                data_out.writeUTF(uname);
                data_out.writeUTF(email);
                data_out.writeUTF(pswd);
                data_out.flush();

                if(data_inp.readBoolean()){
                    User.set_uname(uname);
                    User.set_email(email);
                    User.set_pswd(pswd);

                    data_inp.close();
                    data_out.close();
                    data_inp = null;
                    data_out = null;

                    Platform.runLater(()->{
                        try{
                            stg.close();
                            new ChatPage().start(new Stage());
                        } catch (Exception e) {
                            // e.printStackTrace();
                            ErrorHandler.write_log_file(e);
                        }
                    });
                }
                server.close();

            } catch (HeadlessException | IOException e1) {
                // e1.printStackTrace();
                ErrorHandler.write_log_file(e1);
            }
        }).start();
    }

    @FXML
    void btn_login(ActionEvent event) {
        new Thread(()->{
            try{
                String email = tf_login_email.getText();
                String pswd = tf_login_pswd.getText();

                if(!Validate.is_valid_email(email) || !Validate.is_valid_pswd(pswd)){
                    return;
                }
                pswd = Validate.hash_pswd(pswd);

                Socket server = Network.get_server();
                if(server == null){
                    return;
                }

                InputStream input = server.getInputStream();
                OutputStream output = server.getOutputStream();
                DataInputStream data_inp = new DataInputStream(input);
                DataOutputStream data_out = new DataOutputStream(output);

                data_out.writeUTF("login");
                data_out.writeUTF(email);
                data_out.writeUTF(pswd);
                data_out.flush();

                if(data_inp.readBoolean()){
                    User.set_email(email);
                    User.set_pswd(pswd);

                    data_inp.close();
                    data_out.close();
                    data_inp = null;
                    data_out = null;

                    Platform.runLater(()->{
                        try{
                            stg.close();
                            new ChatPage().start(new Stage());
                        } catch (Exception e) {
                            // e.printStackTrace();
                            ErrorHandler.write_log_file(e);
                        }
                    });
                }
                server.close();
            } catch (HeadlessException | IOException e1) {
                // e1.printStackTrace();
                ErrorHandler.write_log_file(e1);
            }
        }).start();
    }
}
