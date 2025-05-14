import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class Validate {
    public static boolean is_valid_uname(String uname){
        boolean temp = uname != null && uname.length() >= 3 && uname.length() <= 32 && 
               Pattern.matches("^[a-zA-Z0-9_.-]*$", uname);
        
        if (!temp) {
            JOptionPane.showMessageDialog(null, "You entered invalid username! \n Checks if the username is between 3 and 32 characters and contains only allowed characters (letters, numbers, underscores, hyphens, and periods but not space).", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        return temp;
    }

    public static boolean is_valid_email(String email){
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        boolean temp = email != null && Pattern.matches(emailRegex, email);
        
        if (!temp) {
            JOptionPane.showMessageDialog(null, "You entered invalid email address! \n Validates the email format.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        return temp;
    }

    public static boolean is_valid_pswd(String pswd){
        boolean temp = pswd != null && pswd.length() >= 8 && 
               Pattern.compile("[0-9]").matcher(pswd).find() && 
               Pattern.compile("[a-zA-Z]").matcher(pswd).find();

        if (!temp) {
            JOptionPane.showMessageDialog(null, "You entered invalid password! \n Checks if the password is at least 8 characters long and contains both letters and numbers.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        return temp;
    }

    public static String hash_pswd(String pswd){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pswd.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // e.printStackTrace();
            ErrorHandler.write_log_file(e);
        }
        return pswd;
    }
}
