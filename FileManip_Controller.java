import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileManip_Controller {
    private Stage stg;
    DataOutputStream data_out;
    DataInputStream data_inp;

    @FXML
    private ScrollPane sp_for_file_list;

    @FXML
    private VBox vb_file_list;

    @FXML
    protected void setStage(Stage stg){
        this.stg = stg;
    }

    @FXML
    protected void start_file_manip(){
        new Thread(()->{
            Socket server = Network.get_server();
            if(server == null){
                return;
            }

            try {
                this.data_out = new DataOutputStream(server.getOutputStream());
                this.data_inp = new DataInputStream(server.getInputStream());
                data_out.writeUTF("file-tranfer");
                data_out.writeUTF(User.get_email());
                data_out.flush();

                for(;;){
                    Platform.runLater(()->{
                        try {
                            if (data_inp.readUTF().equals("update-file-lists")) {
                                String split_text_1 = "!___This_Is_The_First_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                                String split_text_2 = "!___This_Is_The_Second_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";

                                String file_list;
                                file_list = data_inp.readUTF();
                                // System.out.println(file_list);
                            
                                String[] files = file_list.split(split_text_2);

                                vb_file_list.getChildren().clear();
                                vb_file_list.setSpacing(15.0);
                                for(String file : files){
                                    String[] file_data = file.split(split_text_1);

                                    String sender_email = file_data[0];
                                    String sender_uname = file_data[1];
                                    String file_id = file_data[2];
                                    String file_name = file_data[3];
                                    String upload_at = file_data[4];
                                    
                                    Button btn = new Button(file_id + ". " + file_name);
                                    btn.setStyle("-fx-background-color: orange; -fx-background-radius: 15px; -fx-font-size: 14px;");
                                    btn.setTextFill(Color.BLACK);
                                    btn.setOnMouseClicked(event -> {
                                    String file_info = "Sender Username: " + sender_uname + "\n" +
                                                        "Sender Email: " + sender_email + "\n" +
                                                        "File ID: " + file_id + "\n" +
                                                        "File Name: " + file_name + "\n" +
                                                        "Upload At: " + upload_at;

                                        if(JOptionPane.showConfirmDialog(null, file_info, "File Information", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                                            file_download(file_id);
                                        }
                                    });
                                    vb_file_list.getChildren().add(btn);
                                    sp_for_file_list.setVvalue(1.0);
                                    // System.out.println("\t #Added!");
                                    
                                    // System.out.println("File List Updated");
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void btn_upload(ActionEvent event) {
        FileChooser file_chooser = new FileChooser();
        file_chooser.setTitle("File Upload");
        File file = file_chooser.showOpenDialog(stg);
        String file_name = file.getName();

        if(file != null){
            try {
                FileInputStream file_inp = new FileInputStream(file);
                data_out.writeUTF("upload-file");
                data_out.writeUTF(file_name);
                data_out.flush();
                
                byte[] buffer = new byte[4096];
                int read_byte;

                while ((read_byte = file_inp.read(buffer)) != -1) {
                    data_out.write(buffer, 0, read_byte);
                }
                data_out.flush();
                file_inp.close();

                data_out.writeUTF("new-chat-msg");
                data_out.writeUTF("Upload: " + file_name);
                data_out.flush();

                JOptionPane.showMessageDialog(null, "Successfully Uploaded: " + file_name, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btn_download(ActionEvent event) {
        String file_id = JOptionPane.showInputDialog("Enter file ID:");
        file_download(file_id);
    }

    @FXML
    protected void file_download(String file_id){
        try {
            data_out.writeUTF("download-file");
            data_out.writeUTF(file_id);
            data_out.flush();
            
            String file_name = data_inp.readUTF();
            FileChooser file_chooser = new FileChooser();
            file_chooser.setTitle("File Download");
            file_chooser.setInitialFileName(file_name);
            File file = file_chooser.showSaveDialog(stg);
  
            int file_size = data_inp.readInt();
            byte[] file_data = new byte[file_size];
            data_inp.readFully(file_data);

            if(file != null){
                FileOutputStream file_out = new FileOutputStream(file);
                file_out.write(file_data);
                file_out.flush();
                file_out.close();

                data_out.writeUTF("new-chat-msg");
                data_out.writeUTF("Download: " + file_name);
                data_out.flush();

                JOptionPane.showMessageDialog(null, "Successfully Download: " + file_name, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void go_to_chat_page(MouseEvent event) {
        Platform.runLater(()->{
            try{
                stg.close();
                new ChatPage().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
