import java.util.Scanner;
public class CosnoleAppManager {
    public static  void main(String[] main){
        Scanner scanner = new Scanner(System.in);

        while(true){
                System.out.println("=== MANAGER MENU === \n");
                System.err.println("Please select an option to continue or press 5 to exit" );
                System.out.println("1. Add store");
                System.out.println("2. Add product");
                System.out.println("3. Remove product");
                System.out.println("4. Total sales per product");
                System.out.println("5. Exit");

                String input=scanner.nextLine();

            
                switch (input){
                        case "1":

                            System.out.println();

                        case "2":

                        case "3":

                        case "4":

                        case "5":
                            break;

                    

                }







        }

    }

}
