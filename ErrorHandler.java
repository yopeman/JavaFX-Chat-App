import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.Date;

public class ErrorHandler {
    public static void write_log_file(Exception e){
        Date date1 = new Date();
        LocalDateTime date2 = LocalDateTime.now();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("error_log_history.log", true))) {
            writer.write("["+ date1 + " | "+ date2 + "]" + " :\t " + e+"\n\n");
        } catch (Exception ex) {
            System.out.println("Something is wrong: "+ex);
        }
    }
}
