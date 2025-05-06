import java.util.Scanner;

public class Test4 {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        for(;;){
            System.out.print("Enter input: ");
            String input = scan.nextLine();
            System.out.println(
                "is valid username: " + Validate.is_valid_uname(input) + "\n" + 
                "is valid email: " + Validate.is_valid_email(input) + "\n" + 
                "is valid password: " + Validate.is_valid_pswd(input) + "\n" + 
                "hash password: " + Validate.hash_pswd(input) + "\n\n\n" 
            );
            if (input.equals(".q")) {
                break;
            }
        }
        scan.close();
        for(int i=0; i<=1000; i++){
            System.out.println(i + " => " + Validate.hash_pswd(i + ""));
        }
    }
}
