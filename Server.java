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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

                            sql = "select u.email, u.username, cm.message, cm.sendAt from user u, chat_message cm where u.user_id = cm.sender_id";
                            pStatement = db.get_pStatement(sql);
                            ResultSet rs2 = db.db_query(pStatement);

                            String chat_data = "";
                            String split_text_1 = "!___This_Is_The_First_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                            String split_text_2 = "!___This_Is_The_Second_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                            while(rs2.next()){
                                email = rs2.getString("email");
                                String sender_uname = rs2.getString("username");
                                String message = rs2.getString("message");
                                String sendAt = rs2.getString("sendAt");

                                chat_data += email + split_text_1 + sender_uname + split_text_1 + message + split_text_1 + sendAt + split_text_2;
                                System.out.println(chat_data);
                            }
                            data_out.writeUTF("load-chat");
                            data_out.writeUTF(chat_data);
                            data_out.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            
                else if(action.equals("new-chat-msg")){
                    String chat_msg = data_inp.readUTF();
                    String sql = "insert into chat_message (sender_id, message) values (?, ?)";
                    PreparedStatement pStatement = db.get_pStatement(sql);
                    pStatement.setString(1, users.get(client).user_id);
                    pStatement.setString(2, chat_msg);
                    
                    if(!db.db_exec(pStatement)){
                        System.out.println("Message are not inserted!");
                    }

                    String split_text_1 = "!___This_Is_The_First_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                    String chat_data = users.get(client).email + split_text_1 + users.get(client).uname + split_text_1 + chat_msg + split_text_1 + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    for(Socket s : users.keySet()){
                        DataOutputStream temp_out = new DataOutputStream(s.getOutputStream());
                        temp_out.writeUTF("add-new-chat-msg");
                        temp_out.writeUTF(chat_data);
                        temp_out.flush();
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
            
            catch (SQLException e) {
				e.printStackTrace();
			}
        }
    }
}
