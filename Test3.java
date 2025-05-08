import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Test3 {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket server = Network.get_server();
        System.out.println("Connected to server...");

        InputStream input = server.getInputStream();
        OutputStream output = server.getOutputStream();
        
        DataInputStream data_inp = new DataInputStream(input);
        DataOutputStream data_out = new DataOutputStream(output);
        data_out.writeUTF("file-tranfer");
        data_out.writeUTF("yope@com");
        data_out.flush();
        
        new Thread(()->{
            for(;;){
                try {
                    String action = data_inp.readUTF();
                    System.out.println(action);

                    if (action.equals("update-file-lists")) {
                        String split_text_1 = "!___This_Is_The_First_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                        String split_text_2 = "!___This_Is_The_Second_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";

                        String file_list = data_inp.readUTF();
                        String[] files = file_list.split(split_text_2);

                        for(String file : files){
                            String[] file_data = file.split(split_text_1);

                            String sender_email = file_data[0];
                            String sender_uname = file_data[1];
                            String file_id = file_data[2];
                            String file_name = file_data[3];
                            String upload_at = file_data[4];

                            System.out.println(sender_email + " | " + sender_uname + " | " + file_id + " | " + file_name + " | " + upload_at);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Scanner scan = new Scanner(System.in);
        System.out.print("Enetr option [U for upload | D for Download]: ");
        String option = scan.nextLine().toLowerCase().trim();

        if(option.equals("u")){
            System.out.print("Enter file path: ");
            String file_path = scan.nextLine().trim();
            String file_name = new File(file_path).getName();
            FileInputStream file_inp = new FileInputStream(file_path);

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
        }

        else if(option.equals("d")){
            System.out.print("Enetr file_id: ");
            String file_id = scan.nextLine().trim();
            System.out.print("Enter directory: ");
            String file_dir = scan.nextLine().trim();
            
            data_out.writeUTF("download-file");
            data_out.writeUTF(file_id);
            data_out.flush();
            
            String file_name = data_inp.readUTF();
            int file_size = data_inp.readInt();
            byte[] file_data = new byte[file_size];
            data_inp.readFully(file_data);

            FileOutputStream file_out = new FileOutputStream(file_dir + "/" + file_name);
            file_out.write(file_data);
            file_out.flush();
            file_out.close();

            data_out.writeUTF("new-chat-msg");
            data_out.writeUTF("Download: " + file_name);
            data_out.flush();
        }
    }
}
