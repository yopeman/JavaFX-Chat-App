import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class Validate {
    public static boolean is_valid_uname(String uname){
        return uname != null && uname.length() >= 3 && uname.length() <= 32 && 
               Pattern.matches("^[a-zA-Z0-9_.-]*$", uname);
    }

    public static boolean is_valid_email(String email){
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && Pattern.matches(emailRegex, email);
    }

    public static boolean is_valid_pswd(String pswd){
        return pswd != null && pswd.length() >= 8 && 
               Pattern.compile("[0-9]").matcher(pswd).find() && 
               Pattern.compile("[a-zA-Z]").matcher(pswd).find();
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
            e.printStackTrace();
        }
        return pswd;
    }
}
