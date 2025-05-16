import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Profile_Controller {
    private Stage stg;

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField tf_pswd;

    @FXML
    private TextField tf_uname;

    @FXML
    protected void setStage(Stage stg){
        this.stg = stg;
    }

    @FXML
    void btn_about(ActionEvent event) {
        Platform.runLater(()->{
            try{
                stg.close();
                new AboutUs().start(new Stage());
            } catch (Exception e) {
                // e.printStackTrace();
                ErrorHandler.write_log_file(e);
            }
        });
    }

    @FXML
    void btn_cancel(ActionEvent event) {
        tf_uname.setText("");
        tf_email.setText("");
        tf_pswd.setText("");
    }

    @FXML
    void btn_delete(ActionEvent event) {
        new Thread(()->{
            try{
                Socket server = Network.get_server();
                if(server == null){
                    return;
                }
                
                InputStream input = server.getInputStream();
                OutputStream output = server.getOutputStream();
                DataInputStream data_inp = new DataInputStream(input);
                DataOutputStream data_out = new DataOutputStream(output);

                data_out.writeUTF("delete-account");
                data_out.writeUTF(User.get_email());
                data_out.flush();

                if(data_inp.readBoolean()){
                    JOptionPane.showMessageDialog(null, "Successfully delete account!");
           
                    if (User.logout()) {
                        Platform.runLater(()->{
                            try{
                                stg.close();
                                new Authentication().start(new Stage());
                            } catch (Exception e) {
                                // e.printStackTrace();
                                ErrorHandler.write_log_file(e);
                            }
                        });
                    }
                }
                
                server.close();

            } catch (Exception e1) {
                // e1.printStackTrace();
                ErrorHandler.write_log_file(e1);
            }
        }).start();
    }

    @FXML
    void btn_go_back(ActionEvent event) {
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

    @FXML
    void btn_logout(ActionEvent event) {
        if (User.logout()) {
            Platform.runLater(()->{
                try{
                    stg.close();
                    new Authentication().start(new Stage());
                } catch (Exception e) {
                    // e.printStackTrace();
                    ErrorHandler.write_log_file(e);
                }
            });
        }
    }

    @FXML
    void btn_update(ActionEvent event) {
        new Thread(()->{
            try{
                Socket server = Network.get_server();
                if(server == null){
                    return;
                }
                
                InputStream input = server.getInputStream();
                OutputStream output = server.getOutputStream();
                DataInputStream data_inp = new DataInputStream(input);
                DataOutputStream data_out = new DataOutputStream(output);

                String uname = tf_uname.getText();
                String email = tf_email.getText();
                String pswd = tf_pswd.getText();

                boolean bool1 = uname.isEmpty() ? false : Validate.is_valid_uname(uname);
                if(bool1){
                    data_out.writeUTF("update-uname");
                    data_out.writeUTF(User.get_email());
                    data_out.writeUTF(uname);
                    data_out.flush();

                    if(data_inp.readBoolean()){
                        JOptionPane.showMessageDialog(null, "Successfully update username!");
                        User.set_uname(uname);
                        tf_uname.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "Sorry username are not updated!");
                    }
                }

                boolean bool2 = email.isEmpty() ? false : Validate.is_valid_email(email);
                if(bool2){
                    data_out.writeUTF("update-email");
                    data_out.writeUTF(User.get_email());
                    data_out.writeUTF(email);
                    data_out.flush();

                    if(data_inp.readBoolean()){
                        JOptionPane.showMessageDialog(null, "Successfully update email!");
                        User.set_email(email);
                        tf_email.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "Sorry email are not updated!");
                    }
                }
                
                boolean bool3 = pswd.isEmpty() ? false : Validate.is_valid_pswd(pswd);
                if(bool3){
                    pswd = Validate.hash_pswd(pswd);
                    data_out.writeUTF("update-pswd");
                    data_out.writeUTF(User.get_email());
                    data_out.writeUTF(pswd);
                    data_out.flush();

                    if(data_inp.readBoolean()){
                        JOptionPane.showMessageDialog(null, "Successfully update password!");
                        User.set_pswd(pswd);
                        tf_pswd.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "Sorry password are not updated!");
                    }
                }
                
                server.close();

            } catch (Exception e1) {
                // e1.printStackTrace();
                ErrorHandler.write_log_file(e1);
            }
        }).start();
    }
}
