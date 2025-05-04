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
            return server;
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }
}
