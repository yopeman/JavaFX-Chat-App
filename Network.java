import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Network {
    private static String server_hostname = null;
    public static Socket get_server(){
		try {
            if(server_hostname == null){
                server_hostname = JOptionPane.showInputDialog("Enter server IP address or hostname: ", "localhost");
            }
            Socket server = new Socket(server_hostname, 64318);
            
            if(server == null){
                JOptionPane.showMessageDialog(null, "Server not found");
            }

            return server;
		} catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Server not found");
			e.printStackTrace();
		}
        return null;
    }
}
