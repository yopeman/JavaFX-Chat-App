import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database {
    String error_msg = "";
    Connection conn;
    
    public Database(){
        try {
            String sql;
            PreparedStatement ps;
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "");

            sql = "create database if not exists ChatDatabase";
            ps = get_pStatement(sql);
            
            if(!db_exec(ps) &&  !error_msg.isEmpty()){
                System.err.println("\n\n\n Error in creating ChatDatabase database! \n\n\n");
            }

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ChatDatabase", "root", "");

            sql = "create table if not exists user ("+
                    "user_id int auto_increment primary key,"+
                    "username varchar(255) not null,"+
                    "email varchar(255) not null unique,"+
                    "pswd varchar(255) not null,"+
                    "createdAt timestamp default current_timestamp,"+
                    "updatedAt timestamp default current_timestamp on update current_timestamp"+
                ")";
            ps = get_pStatement(sql);
            
            if(!db_exec(ps) &&  !error_msg.isEmpty()){
                System.err.println("\n\n\n Error in creating user database table! \n\n\n");
            }

            sql = "create table if not exists chat_message ("+
                    "chat_msg_id int auto_increment primary key,"+
                    "sender_id int not null,"+
                    "message varchar(255) not null,"+
                    "sendAt timestamp default current_timestamp,"+
                    "foreign key (sender_id) references user (user_id) on delete cascade"+
                ")";
            ps = get_pStatement(sql);
            
            if(!db_exec(ps) &&  !error_msg.isEmpty()){
                System.err.println("\n\n\n Error in creating chat_message database table! \n\n\n");
            }
            
            sql = "create table if not exists file ("+
                    "file_id int auto_increment primary key,"+
                    "sender_id int not null,"+
                    "file_name varchar(255) not null,"+
                    "file_data longblob not null,"+
                    "uploadAt timestamp default current_timestamp,"+
                    "foreign key (sender_id) references user (user_id) on delete cascade"+
                ")";
            ps = get_pStatement(sql);
            
            if(!db_exec(ps) &&  !error_msg.isEmpty()){
                System.err.println("\n\n\n Error in creating file database table! \n\n\n");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            error_msg = e.getMessage();
        }
    }

    public PreparedStatement get_pStatement(String sql){
        try {
            return conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            error_msg = e.getMessage();
        }
        return null;
    }

    public boolean db_exec(PreparedStatement ps){
        try {
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            error_msg = e.getMessage();
        }
        return false;
    }

    public ResultSet db_query(PreparedStatement ps){
        try {
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            error_msg = e.getMessage();
        }
        return null;
    }

    public ArrayList<String> db_column(PreparedStatement ps){
        try {
            ResultSet rs = db_query(ps);
            ResultSetMetaData rs_meta_data = rs.getMetaData();
            ArrayList<String> columns = new ArrayList<>();

            for(int i=1; i<= rs_meta_data.getColumnCount(); i++){
                columns.add(rs_meta_data.getColumnName(i));
            }
            return columns;
        } catch (SQLException e) {
            e.printStackTrace();
            error_msg = e.getMessage();
        }
        return null;
    }

    public int db_row_num(PreparedStatement ps){
        try {
            int row_num = 0;
            ResultSet rs = db_query(ps);
            while (rs.next()) { row_num++; }
            return row_num;
        } catch (SQLException e) {
            e.printStackTrace();
            error_msg = e.getMessage();
        }

        return 0;
    }

    public void db_close(){
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            error_msg = e.getMessage();
        }
    }
}
