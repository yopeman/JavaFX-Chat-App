import java.io.Serializable;

public class ChatUser implements Serializable {
    private static final long serialVersionUID = 1L;
    String user_id;
    String uname;
    String email;
    String createdAt;
    String updatedAt;
    
    public ChatUser(String user_id, String uname, String email, String createdAt, String updatedAt){
        this.user_id = user_id;
        this.uname = uname;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String user_info(){
        return "User ID: " + user_id + "\n" +
               "Username: " + uname + "\n" +
               "Email: " + email + "\n" +
               "Created At: " + createdAt + "\n" +
               "Updated At: " + updatedAt + "\n";
    }

    public String toString(){
        return user_id + "," + uname + "," + email + "," + createdAt + "," + updatedAt + ";";
    }
}
