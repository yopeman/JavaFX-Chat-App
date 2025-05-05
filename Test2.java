import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Test2 {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket server = Network.get_server();
        System.out.println("Connected to server...");

        InputStream input = server.getInputStream();
        OutputStream output = server.getOutputStream();
        
        DataInputStream data_inp = new DataInputStream(input);
        DataOutputStream data_out = new DataOutputStream(output);
        data_out.writeUTF("start-chat");
        data_out.writeUTF("yope@com");
        data_out.flush();

        if(data_inp.readUTF().equals("update-user")){
            System.out.println(
                data_inp.readUTF()
            );
        }

        if(data_inp.readUTF().equals("load-chat")){
            String split_text_1 = "!___This_Is_The_First_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
            String split_text_2 = "!___This_Is_The_Second_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
            
            String chat_info = data_inp.readUTF();
            String[] chats = chat_info.split(split_text_2);

            for (String chat : chats) {
                String[] chat_data = chat.split(split_text_1);
                String sender_uname = chat_data[0];
                String chat_msg = chat_data[1];
                String send_at = chat_data[2];

                System.out.println("\n\n\n---------------------------");
                System.out.println("Sender username: " + sender_uname);
                System.out.println("Chat message: " + chat_msg);
                System.out.println("Send at: " + send_at);
                System.out.println("---------------------------\n\n\n");
            }
        }

        new Thread(()->{
            String user_chat_msg_input;
            for(;;){
                user_chat_msg_input = JOptionPane.showInputDialog("Enter your message:", "Some massege ...");
                if(user_chat_msg_input != null && !user_chat_msg_input.isEmpty()){
                    try {
                        data_out.writeUTF("new-chat-msg");
                        data_out.writeUTF(user_chat_msg_input);
                        data_out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try{ Thread.sleep(3000); } catch(Exception e){}
            }
        }).start();
        
        for(;;){
            String split_text_1 = "!___This_Is_The_First_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
            
            if (data_inp.readUTF().equals("add-new-chat-msg")) {
                String chat = data_inp.readUTF();
                String[] chat_data = chat.split(split_text_1);
                String sender_uname = chat_data[0];
                String chat_msg = chat_data[1];
                String send_at = chat_data[2];

                System.out.println("Sender username: " + sender_uname);
                System.out.println("Chat message: " + chat_msg);
                System.out.println("Send at: " + send_at);
                System.out.println("\n");
            }
        }
    }
}
