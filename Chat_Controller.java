import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Chat_Controller {
    private Stage stg;
    DataOutputStream data_out;
    DataInputStream data_inp;

    @FXML
    private ScrollPane sp_for_user_list;
    @FXML
    private ScrollPane sp_for_msg_list;

    @FXML
    private TextField tf_chat_input;

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
                return;
            }

            try {
                this.data_out = new DataOutputStream(server.getOutputStream());
                this.data_inp = new DataInputStream(server.getInputStream());
                data_out.writeUTF("start-chat");
                data_out.writeUTF(User.get_email());
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

                        if(data_inp.readUTF().equals("load-chat")){
                            String split_text_1 = "!___This_Is_The_First_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                            String split_text_2 = "!___This_Is_The_Second_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                            
                            String chat_info = data_inp.readUTF();
                            String[] chats = chat_info.split(split_text_2);

                            for (String chat : chats) {
                                String[] chat_data = chat.split(split_text_1);
                                // System.out.println("\n\n\n" + chat_data.length + "\n\n\n");

                                String sender_email = chat_data[0];
                                String sender_uname = chat_data[1];
                                String chat_msg = chat_data[2];
                                String send_at = chat_data[3];
                                set_chat_msg_list(sender_email, sender_uname, chat_msg, send_at);
                            }
                        }
                    }

                    else if(action.equals("add-new-chat-msg")){
                        String split_text_1 = "!___This_Is_The_First_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                        String chat = data_inp.readUTF();
                        String[] chat_data = chat.split(split_text_1);

                        String sender_email = chat_data[0];
                        String sender_uname = chat_data[1];
                        String chat_msg = chat_data[2];
                        String send_at = chat_data[3];
                        set_chat_msg_list(sender_email, sender_uname, chat_msg, send_at);
                    }
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
            vb_chat_user_list.setSpacing(15.0);

            for (ChatUser user : users) {
                Button btn = new Button(user.uname);
                btn.setStyle("-fx-background-color: #09225B; -fx-background-radius: 15px; -fx-font-size: 14px; -fx-font-weight: bold");
                btn.setTextFill(Color.WHITE);
                btn.setOnMouseClicked(event -> {
                    JOptionPane.showMessageDialog(null, user.user_info(), "User Information", JOptionPane.INFORMATION_MESSAGE);
                });
                vb_chat_user_list.getChildren().add(btn);
                sp_for_user_list.setVvalue(1.0);
            }
        });
    }

    @FXML
    protected void set_chat_msg_list(String sender_email, String sender_uname, String chat_msg, String send_at){
        Platform.runLater(()->{
            vb_chat_msg_list.setSpacing(15.0);
            Text txt = new Text(sender_uname + ":" + "\n" + chat_msg + "\n" + "[On: " + send_at + " ]");
            TextFlow lbl = new TextFlow(txt);
            lbl.setOnMouseClicked(event -> {
                String chat_info = "Sender Username: " + sender_uname + "\n" +
                                    "Sender Email: " + sender_email + "\n" +
                                    "Send At: " + send_at;
                JOptionPane.showMessageDialog(null, chat_info, "Chat Message Information", JOptionPane.INFORMATION_MESSAGE);
            });

            if(sender_email.equals(User.get_email())){
                lbl.setStyle("-fx-background-color:  yellow; -fx-background-radius: 10px; -fx-padding: 10px; -fx-text-alignment: right");
            } else {
                lbl.setStyle("-fx-background-color:  orange; -fx-background-radius: 10px; -fx-padding: 10px; -fx-text-alignment: left");
            }
            
            vb_chat_msg_list.getChildren().add(lbl);
            sp_for_msg_list.setVvalue(1.0);
        });
    }

    @FXML
    void icon_btn_file(MouseEvent event) {
        Platform.runLater(()->{
            try{
                stg.close();
                new FileManip().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void icon_btn_profile(MouseEvent event) {
        Platform.runLater(()->{
            try{
                stg.close();
                new ProfilePage().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void icon_btn_send(MouseEvent event) {
        send_msg_text();
    }

    @FXML
    void tf_chat_msg_input_entered(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER){
            send_msg_text();
        }
    }

    @FXML
    protected void send_msg_text(){
        new Thread(()->{
            String user_chat_msg_input = tf_chat_input.getText();
            if(user_chat_msg_input != null && !user_chat_msg_input.isEmpty()){
                try {
                    data_out.writeUTF("new-chat-msg");
                    data_out.writeUTF(user_chat_msg_input);
                    data_out.flush();

                    Platform.runLater(()->{
                        tf_chat_input.setText("");
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
