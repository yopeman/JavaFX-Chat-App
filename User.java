import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static String uname;
    private static String email;
    private static String pswd;
    
    public static boolean signup(String uname, String email, String pswd){
        try {
            Database db = new Database();
            String sql = "insert into user (username, email, pswd) values (?,?,?)";
            PreparedStatement ps = db.get_pStatement(sql);
            ps.setString(1, uname);
            ps.setString(2, email);
            ps.setString(3, pswd);

            if(db.db_exec(ps)){
                User.uname = uname;
                User.email = email;
                User.pswd = pswd;
                JOptionPane.showMessageDialog(null, "You are successfully registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
                save_user_info();
                return true;
            }
            else {
                if(db.error_msg.toLowerCase().contains("unique")){
                    JOptionPane.showMessageDialog(null, "This account alrady registered!", "Warming", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Something is wrong!", "Warming", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean login(String email, String pswd){
        try {
            Database db = new Database();
            String sql = "select * from user where email=? and pswd=?";
            PreparedStatement ps = db.get_pStatement(sql);
            ps.setString(1, email);
            ps.setString(2, pswd);
            ResultSet rs;

            if(db.db_row_num(ps) == 1 && (rs = db.db_query(ps)) != null && rs.next()){
                User.uname = rs.getString("username");
                User.email = rs.getString("email");
                User.pswd = rs.getString("pswd");
                JOptionPane.showMessageDialog(null, "You are successfully Login!", "Success", JOptionPane.INFORMATION_MESSAGE);
                save_user_info();
                return true;
            }
            else {
                JOptionPane.showMessageDialog(null, "You entered wrong email or password", "Warming", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
	public static boolean isLogin(){
        if(uname != null){
            if (!uname.isEmpty()) {
                return true;
            }
        } 
        
        try {
            File user_file = new File("login_user_info.dat");
            if (user_file.exists()) {
                ObjectInputStream obj_inp = new ObjectInputStream(new FileInputStream(user_file));
                ChatUser user = (ChatUser) obj_inp.readObject();
                
                if (user != null) {
                    User.uname = user.uname;
                    User.email = user.email;
                    User.pswd = user.user_id;
                    obj_inp.close();
                    return true;
                }
                user_file.delete();
                obj_inp.close();
            }
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

        return false;
    }

    private static void save_user_info(){
        try {
			ObjectOutputStream obj_out = new ObjectOutputStream(new FileOutputStream("login_user_info.dat"));
            obj_out.writeObject(new ChatUser(User.pswd, User.uname, User.email, "",""));
            obj_out.flush();
            obj_out.close();
            obj_out = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static boolean logout(){
        uname = email = pswd = null;
        File user_file = new File("login_user_info.dat");
        if (user_file.exists()) {
            if(user_file.delete()){
                return true;
            }
        }
        return true;
    }
    
    public static void set_uname(String uname){
        User.uname = uname;
    }
    
    public static void set_email(String email){
        User.email = email;
    }
    
    public static void set_pswd(String pswd){
        User.pswd = pswd;
    }
    
    public static String get_uname(){
        return uname;
    }
    
    public static String get_email(){
        return email;
    }
}
