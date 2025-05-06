import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileServer {
    public static boolean upload_file(Socket socket, ChatUser user){
        try {
            DataInputStream data_inp = new DataInputStream(socket.getInputStream());
            String file_name = data_inp.readUTF();

            ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read_byte;
            while ((read_byte = data_inp.read(buffer)) != -1) {
                byte_out.write(buffer, 0, read_byte);
            }
            byte[] file_data = byte_out.toByteArray();
            byte_out.close();

            Database db = new Database();
            String sql = "insert into file (sender_id, file_name, file_data) VALUES (?,?,?)";
            PreparedStatement ps = db.get_pStatement(sql);
            ps.setString(1, user.user_id);
            ps.setString(2, file_name);
            ps.setBytes(3, file_data);
            
            if(db.db_exec(ps)){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
			e.printStackTrace();
		}
        return false;
    }

    public static boolean download_file(Socket socket, String file_id){
        try {
            Database db = new Database();
            String sql = "select file_name, file_data from file where file_id=?";
            PreparedStatement ps = db.get_pStatement(sql);
            ps.setString(1, file_id);
            ResultSet rs = db.db_query(ps);
            
            if (rs.next()) {
                String file_name = rs.getString("file_name");
                byte[] file_data = rs.getBytes("file_data");
                DataOutputStream data_out = new DataOutputStream(socket.getOutputStream());

                data_out.writeUTF(file_name);
                data_out.writeInt(file_data.length);
                data_out.write(file_data);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {  
            e.printStackTrace();
        }
        return false;
    }
}
