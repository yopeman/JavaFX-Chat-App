import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
        
        for(;;){
            if (data_inp.readUTF().equals("update-user")) {
                String user_info = data_inp.readUTF();
                String[] users = user_info.split(";");

                for (String user : users) {
                    String[] user_data = user.split(",");
                    String user_id = user_data[0];
                    String uname = user_data[1];
                    String email = user_data[2];
                    String createdAt = user_data[3];
                    String updatedAt = user_data[4];

                    System.out.println("User ID: " + user_id);
                    System.out.println("Username: " + uname);
                    System.out.println("Email: " + email);
                    System.out.println("Created At: " + createdAt);
                    System.out.println("Updated At: " + updatedAt);
                }
                System.out.println("--------------------------------------------------");
                System.out.println("--------------------------------------------------");
                System.out.println("--------------------------------------------------");
            }
        }
    }
}
