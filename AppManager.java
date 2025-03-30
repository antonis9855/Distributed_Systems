import java.util.Scanner;

public class AppManager {
    public static  void main(String[] main){
        Scanner scanner = new Scanner(System.in);

        while(true){

            System.out.println("=== MANAGER MENU === \n");
            System.err.println("Please select an option to continue or press 0 to exit" );
            System.out.println("1. Add store");
            System.out.println("2. Add product");
            System.out.println("3. Remove product");
            System.out.println("4. Total sales per product");
            System.out.println("0. Exit");

            String input=scanner.nextLine();

            String Command = "";
            switch (input){
                case "1":

                    System.out.println("Enter store data: ");
                    String storeData = scanner.nextLine();
                    // command = "Add_Store " + storeData;
                     break;

                 case "2":

                    System.out.println("Enter product name: ");
                    String productData = scanner.nextLine();
                    // command = "ADD_PRODUCT " + productData;
                    break;

                case "3":

                    System.out.println("Enter product name to remove: ");
                    String removeProductData = scanner.nextLine();
                    //command = "REMOVE_PRODUCT" + removeProductData;

                case "4":

                    System.out.println("Enter store name for total sales");
                    String storeName = scanner.nextLine();
                    //command = "DISPLAY_DATA" + storeName 


                case "0":

                    System.out.print("Exiting");
                    for (int i = 0; i < 5; i++) {
                        try {
                            
                            Thread.sleep(500); 
                        } catch (InterruptedException e) {}
                            System.out.print(".");}

                        System.out.println("\nSystem off");
                        scanner.close();
                        System.exit(0);
                        break;
                        
                default:
                    System.out.println("Invalid Menu option: " + input);
                    continue;

                /* sendCommandtoMaster(Command); */

                    

            }

        /*  private static void sendCommandtoMaster(String command){
         * 
        } */







        }

    }

}


