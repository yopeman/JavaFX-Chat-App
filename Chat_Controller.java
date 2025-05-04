import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Chat_Controller {
    private Stage stg;

    @FXML
    private VBox vb_chat_msg_list;

    @FXML
    private VBox vb_chat_user_list;

    @FXML
    protected void setStage(Stage stg){
        this.stg = stg;
    }

    @FXML
    protected void start_chat_page(){
        new Thread(()->{
            Socket server = Network.get_server();
            if(server == null){
                JOptionPane.showMessageDialog(null, "Server not connected!");
                return;
            }

            try {
                DataOutputStream data_out = new DataOutputStream(server.getOutputStream());
                DataInputStream data_inp = new DataInputStream(server.getInputStream());
                data_out.writeUTF("start-chat");
                data_out.writeUTF("yope@com"); //(User.get_email());
                data_out.flush();

                for(;;){
                    String action = data_inp.readUTF();

                    if (action.equals("update-user")) {
                        String user_info = data_inp.readUTF();
                        String[] users = user_info.split(";");

                        ArrayList<ChatUser> chat_users = new ArrayList<>();
                        for (String user : users) {
                            String[] user_data = user.split(",");
                            String user_id = user_data[0];
                            String uname = user_data[1];
                            String email = user_data[2];
                            String createdAt = user_data[3];
                            String updatedAt = user_data[4];

                            ChatUser chat_user = new ChatUser(user_id, uname, email, createdAt, updatedAt);
                            chat_users.add(chat_user);
                        }
                        set_chat_user_list(chat_users);
                    }
                    
                    // else if (action.equals("update-msg")) {
                    //     String msg_info = data_inp.readUTF();
                    //     String[] msg_data = msg_info.split(",");
                    //     String msg_id = msg_data[0];
                    //     String sender_id = msg_data[1];
                    //     String sender_uname = msg_data[2];
                    //     String msg = msg_data[3];
                    //     String createdAt = msg_data[4];

                    //     System.out.println("Message ID: " + msg_id);
                    //     System.out.println("Sender ID: " + sender_id);
                    //     System.out.println("Sender Username: " + sender_uname);
                    //     System.out.println("Message: " + msg);
                    //     System.out.println("Created At: " + createdAt);
                    // }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    @FXML
    protected void set_chat_user_list(ArrayList<ChatUser> users){
        Platform.runLater(()->{
            vb_chat_user_list.getChildren().clear();
            vb_chat_user_list.setSpacing(10);
            vb_chat_user_list.setStyle("-fx-background-color: #5E2BBD; -fx-padding: 10px; -fx-spacing: 10px;");
            for (ChatUser user : users) {
                Button btn = new Button("User " + user.uname);
                btn.setStyle("-fx-background-color: #09225B; -fx-background-radius: 15px; -fx-font-size: 14px; -fx-font-weight: bold");
                btn.setTextFill(javafx.scene.paint.Color.WHITE);
                btn.setAlignment(Pos.CENTER);
                btn.setOnMouseClicked(event -> {
                    JOptionPane.showMessageDialog(null, user.user_info(), "User Information", JOptionPane.INFORMATION_MESSAGE);
                });
                vb_chat_user_list.getChildren().add(btn);
            }
        });
    }

    @FXML
    void btn_profile(ActionEvent event) {

    }

    @FXML
    void icon_btn_file(MouseEvent event) {

    }

    @FXML
    void icon_btn_send(MouseEvent event) {

    }

    @FXML
    void tf_chat_input(ActionEvent event) {

    }

}
