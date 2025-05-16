public class Main {
    public static void main(String[] args) {
        Network.get_server();
        new Database();
        
        if(User.isLogin()){
            ChatPage.main(args);
        } else {
            Authentication.main(args);
        }
    }
}
