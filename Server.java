import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.sql.ResultSet;

public class Server extends Thread {
    Socket client;
    private static HashMap<Socket,ChatUser> users = new HashMap<Socket,ChatUser>();

    public Server(Socket client){
        this.client = client;
    }

    public static void main(String[] args) {
        new Thread(()->{
            try {
                @SuppressWarnings("resource")
                ServerSocket server = new ServerSocket(64318);
                System.out.println("\n\tServer are started!\n");
                for(;;){
                    Socket client = server.accept();
                    System.out.println("Client start!");
                    new Server(client).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void run(){
        for(;;){
            System.out.println(0);
            try {
                InputStream input = client.getInputStream();
                OutputStream output = client.getOutputStream();

                DataInputStream data_inp = new DataInputStream(input);
                DataOutputStream data_out = new DataOutputStream(output);

                String action = data_inp.readUTF();
                System.out.println("\n\n\n \t Action => " + action + " \n\n\n");
                Database db = new Database();

                if(action.equals("register")){
                    String uname = data_inp.readUTF();
                    String email = data_inp.readUTF();
                    String pswd = data_inp.readUTF();
                    boolean result = User.signup(uname, email, pswd);
                    data_out.writeBoolean(result);
                    data_out.flush();
                    System.out.println("Register: " + result);
                }

                else if(action.equals("login")){
                    String email = data_inp.readUTF();
                    String pswd = data_inp.readUTF();
                    boolean result = User.login(email, pswd);
                    data_out.writeBoolean(result);
                    data_out.flush();
                    System.out.println("Login: " + result);
                }

                else if(action.equals("start-chat")){
                    try{
                        String email = data_inp.readUTF();
                        String sql = "select * from user where email=?";
                        PreparedStatement pStatement = db.get_pStatement(sql);
                        pStatement.setString(1, email);
                        ResultSet rs = db.db_query(pStatement);

                        if (rs.next()) {
                            String user_id = rs.getString("user_id");
                            String uname = rs.getString("username");
                            String createdAt = rs.getString("createdAt");
                            String updatedAt = rs.getString("updatedAt");

                            ChatUser chat_user = new ChatUser(user_id, uname, email, createdAt, updatedAt);
                            users.put(client, chat_user);

                            String user_data = "";
                            for(ChatUser user : users.values()){
                                user_data += user.toString();
                            }

                            for(Socket s : users.keySet()){
                                DataOutputStream temp_out = new DataOutputStream(s.getOutputStream());
                                temp_out.writeUTF("update-user");
                                temp_out.writeUTF(user_data);
                                temp_out.flush();
                            }

                            // data_out.writeUTF(user_data);
                            // data_out.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } 
            
            catch (EOFException e) {
                System.out.println("Client end!");

                try {
                    users.remove(client);
                    client.close();
                    
                    String user_data = "";
                    for(ChatUser user : users.values()){
                        user_data += user.toString();
                    }

                    for(Socket s : users.keySet()){
                        DataOutputStream temp_out = new DataOutputStream(s.getOutputStream());
                        temp_out.writeUTF("update-user");
                        temp_out.writeUTF(user_data);
                        temp_out.flush();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }       

                break;
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
