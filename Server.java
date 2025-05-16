import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.PreparedStatement;
import java.util.Date;
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

                InetAddress ip = InetAddress.getLocalHost();
                String ip_address = ip.getHostAddress();

                System.out.println("\n[ Server are started on " + ip_address + ":64318 at " + new Date() + " ]\n");
                for(;;){
                    Socket client = server.accept();
                    System.out.println("[ Client connection started at " + new Date() + " ]");
                    new Server(client).start();
                }
            } catch (Exception e) {
                // e.printStackTrace();
                ErrorHandler.write_log_file(e);
            }
        }).start();
    }

    public void run(){
        for(;;){
            // System.out.println(0);
            try {
                InputStream input = client.getInputStream();
                OutputStream output = client.getOutputStream();

                DataInputStream data_inp = new DataInputStream(input);
                DataOutputStream data_out = new DataOutputStream(output);

                String action = data_inp.readUTF();
                // System.out.println("\n\n\n \t Action => " + action + " \n\n\n");
                System.out.println("[ Client request " + action + " at " + new Date() + " ]");
                Database db = new Database();

                if(action.equals("register")){
                    String uname = data_inp.readUTF();
                    String email = data_inp.readUTF();
                    String pswd = data_inp.readUTF();
                    boolean result = User.signup(uname, email, pswd);
                    data_out.writeBoolean(result);
                    data_out.flush();
                    // System.out.println("Register: " + result);
                }

                else if(action.equals("login")){
                    String email = data_inp.readUTF();
                    String pswd = data_inp.readUTF();
                    boolean result = User.login(email, pswd);
                    data_out.writeBoolean(result);
                    data_out.flush();
                    // System.out.println("Login: " + result);
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
                            }
                            data_out.writeUTF("load-chat");
                            data_out.writeUTF(chat_data);
                            data_out.flush();
                        }
                    } catch (Exception e) {
                        // e.printStackTrace();
                        ErrorHandler.write_log_file(e);
                    }
                }
            
                else if(action.equals("new-chat-msg")){
                    String chat_msg = data_inp.readUTF();
                    String sql = "insert into chat_message (sender_id, message) values (?, ?)";
                    PreparedStatement pStatement = db.get_pStatement(sql);
                    pStatement.setString(1, users.get(client).user_id);
                    pStatement.setString(2, chat_msg);
                    
                    if(!db.db_exec(pStatement)){
                        // System.out.println("Message are not inserted!");
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
            
                else if(action.equals("file-transfer")){
                    // System.out.println(0);
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

                            // String user_data = "";
                            // for(ChatUser user : users.values()){
                            //     user_data += user.toString();
                            // }

                            // for(Socket s : users.keySet()){
                            //     DataOutputStream temp_out = new DataOutputStream(s.getOutputStream());
                            //     temp_out.writeUTF("update-user");
                            //     temp_out.writeUTF(user_data);
                            //     temp_out.flush();
                            // }

                            sql = "select u.email, u.username, f.file_id, f.file_name, f.uploadAt from user u, file f where u.user_id = f.sender_id";
                            pStatement = db.get_pStatement(sql);
                            ResultSet rs2 = db.db_query(pStatement);

                            String file_list = "";
                            String split_text_1 = "!___This_Is_The_First_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                            String split_text_2 = "!___This_Is_The_Second_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                            while(rs2.next()){
                                email = rs2.getString("email");
                                String sender_uname = rs2.getString("username");
                                String file_id = rs2.getString("file_id");
                                String file_name = rs2.getString("file_name");
                                String upload_at = rs2.getString("uploadAt");

                                file_list += email + split_text_1 + sender_uname + split_text_1 + file_id + split_text_1 + file_name + split_text_1 + upload_at + split_text_2;
                                // System.out.println(file_list);
                            }
                            data_out.writeUTF("update-file-lists");
                            data_out.writeUTF(file_list);
                            data_out.flush();
                            // System.out.println(file_list);
                        }
                    } catch (Exception e) {
                        // e.printStackTrace();
                        ErrorHandler.write_log_file(e);
                    }
                }

                else if(action.equals("upload-file")){
                    if (FileServer.upload_file(client, users.get(client))) {
                        String sql = "select u.email, u.username, f.file_id, f.file_name, f.uploadAt from user u, file f where u.user_id = f.sender_id";
                        PreparedStatement pStatement = db.get_pStatement(sql);
                        ResultSet rs2 = db.db_query(pStatement);

                        String file_list = "";
                        String split_text_1 = "!___This_Is_The_First_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                        String split_text_2 = "!___This_Is_The_Second_Split_Text_For_This_Chat_App_Project_With_JavaFX_AND_JDBC___!";
                        while(rs2.next()){
                            String email = rs2.getString("email");
                            String sender_uname = rs2.getString("username");
                            String file_id = rs2.getString("file_id");
                            String file_name = rs2.getString("file_name");
                            String upload_at = rs2.getString("uploadAt");

                            file_list += email + split_text_1 + sender_uname + split_text_1 + file_id + split_text_1 + file_name + split_text_1 + upload_at + split_text_2;
                        }

                        for(Socket s : users.keySet()){
                            DataOutputStream temp_out = new DataOutputStream(s.getOutputStream());
                            temp_out.writeUTF("update-file-lists");
                            temp_out.writeUTF(file_list);
                            temp_out.flush();
                        }

                        // System.out.println("File uploaded!");
                    }
                }

                else if(action.equals("download-file")){
                    String file_id = data_inp.readUTF();
                    if (FileServer.download_file(client, file_id)) {
                        // System.out.println("File are downloaded!");
                    } else {
                        // System.out.println("File are not downloaded!");
                    }
                }
            
                else if(action.equals("delete-account")){
                    String email = data_inp.readUTF();
                    String sql = "delete from user where email=?";
                    PreparedStatement pStatement = db.get_pStatement(sql);
                    pStatement.setString(1, email);

                    data_out.writeBoolean(db.db_exec(pStatement));
                    data_out.flush();
                }

                else if(action.equals("update-uname")){
                    String email = data_inp.readUTF();
                    String uname = data_inp.readUTF();
                    
                    String sql = "update user set username=? where email=?";
                    PreparedStatement pStatement = db.get_pStatement(sql);
                    pStatement.setString(1, uname);
                    pStatement.setString(2, email);

                    data_out.writeBoolean(db.db_exec(pStatement));
                    data_out.flush();
                }
                
                else if(action.equals("update-email")){
                    String old_email = data_inp.readUTF();
                    String new_email = data_inp.readUTF();

                    String sql = "select user_id from user where email=?";
                    PreparedStatement pStatement = db.get_pStatement(sql);
                    pStatement.setString(1, old_email);
                    ResultSet rs = db.db_query(pStatement);

                    if (rs.next()) {
                        sql = "update user set email=? where user_id=?";
                        pStatement = db.get_pStatement(sql);
                        pStatement.setString(1, new_email);
                        pStatement.setString(2, rs.getString("user_id"));

                        data_out.writeBoolean(db.db_exec(pStatement));
                        data_out.flush();
                    }
                }
                
                else if(action.equals("update-pswd")){
                    String email = data_inp.readUTF();
                    String pswd = data_inp.readUTF();
                    
                    String sql = "update user set pswd=? where email=?";
                    PreparedStatement pStatement = db.get_pStatement(sql);
                    pStatement.setString(1, pswd);
                    pStatement.setString(2, email);

                    data_out.writeBoolean(db.db_exec(pStatement));
                    data_out.flush();
                }
            } 
            
            catch (EOFException | SocketException e) {
                try {
                    users.remove(client);
                    client.close();
                    client = null;
                    
                    for(Socket sc : users.keySet()){
                        if (sc == null) {
                            users.remove(sc);
                        }
                    }

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
                    // ex.printStackTrace();
                    ErrorHandler.write_log_file(ex);
                }

                System.out.println("[ Client connection terminated at " + new Date() + " ]");
                break;
            }

            catch (IOException e) {
                // e.printStackTrace();
                ErrorHandler.write_log_file(e);
            } 
            
            catch (SQLException e) {
				// e.printStackTrace();
                ErrorHandler.write_log_file(e);
			}
        }
    }
}
