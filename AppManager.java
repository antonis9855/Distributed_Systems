import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AppManager {

    private static final String Master_IP = "127.0.0.1";
    private static final int Master_PORT = 5000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("=== MANAGER MENU ===\n");
            System.err.println("Please select an option to continue or press 0 to exit");
            System.out.println("1. Add store");
            System.out.println("2. Add product");
            System.out.println("3. Remove product");
            System.out.println("4. Total sales per product");
            System.out.println("5. Total sales per product category");
            System.out.println("0. Exit");

            String input = scanner.nextLine();
            String command = "";

            switch (input) {
                case "1":
                    System.out.println("Enter the file path for the JSON store data:");
                    String filePath = scanner.nextLine();
                    String storeData = readFileContent(filePath);
                    if (storeData != null) {
                        command = "ADD_STORE " + storeData;
                    } else {
                        System.out.println("Could not read the file. Please try again.");
                        continue;
                    }
                    break;

                case "2":

                    String StoreList2 = getStoresList();
                    if (StoreList2 == null){

                        System.out.println("Store list is empty. Please add a store first");
                    }   
                    
                    System.out.println("Please select a store to add product " + StoreList2);
                    String StoreSelected2 = scanner.nextLine();
                    System.out.println("Enter product details as a JSON path: ");
                    String filePath2 = scanner.nextLine();
                    String productData2 = readFileContent(filePath2);
                    if (productData2 != null){

                        command = "ADD_PRODUCT " + StoreSelected2 + " " + productData2;
                    } else {
                        System.out.println("Could not read the file. Please try again.");
                        continue;
                    }
                    break;


                case "3":

                    String StoreList3 = getStoresList();
                    if(StoreList3 == null){

                        System.out.println("Store list is empty. Please add a store first");
                    }

                    String ProductList3 = getProductList();
                    if(ProductList3 == null){

                        System.out.println("Product list is empty. Please add a product first");

                    }            

                    System.out.println("Please select a store to remove product " + StoreList3);
                    String StoreSelected3 = scanner.nextLine();
                    System.out.println("Please select a product to remove " + ProductList3);
                    String ProductSelected3 = scanner.nextLine();
                    command = "REMOVE_PRODUCT " + StoreSelected3 + " " + ProductSelected3;
                    
                    break;


                case "4":

                    String StoreList4 = getStoresList();
                    if(StoreList4 == null){

                        System.out.println("Store list is empty. Please add a store first");
                    }

                    System.out.println("Enter store name for total sales " + StoreList4);
                    String storeName = scanner.nextLine();
                    command = "DISPLAY_DATA " + storeName;
                    break;


                case "5":

                    String foodCategoryList = getFoodCategoriesList();
                    if(foodCategoryList == null){
                        System.out.println("Category list is empty. Please add a food category first");
                    }

                    System.out.println("Food Category: " + foodCategoryList);
                    String foodCategoryName = scanner.nextLine();
                    command = "DISPLAY_FOOD_CATEGORY " + categoryName;
                    break;


                case "6":
                    String productCategoryList = getProductCategoryList();

                    if(productCategoryList == null){
                        System.out.println("Product list is empty. Please add a product category fist ");

                    }

                    System.out.println("Product Category:" + productCategoryList);
                    String productCategoryName = scanner.nextLine();
                    command = "DISPLAY_PRODUCT_CATEGORY" + productCategoryName;
                    break;







                    
                case "0":
                    System.out.print("Exiting");
                    for (int i = 0; i < 5; i++) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                        System.out.print(".");
                    }
                    System.out.println("\nSystem off");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid Menu option: " + input);
                    continue;
            }

            sendCommandtoMaster(command);
        }
    }



    private static String readFileContent(String filePath) {
        try {
            
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }
    }



    private static String getStoresList(){

        String command = "Get_Stores_List";
        try(Socket socket = new Socket(Master_IP, Master_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {


            out.println(command);
            String response = in.readLine();
            System.out.println("Stores to choose: " + response);
            return response;
        }   
            catch (IOException e){

                System.out.println("Error connecting to Master: " + e.getMessage());
                return null;
            }

    }


    private static String getProductList(){

        String command = "Get_Product_List";
        try(Socket socket = new Socket(Master_IP, Master_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);
            String response = in.readLine();
            System.out.println("Products to choose: " + response);
            return response;
        
        }

            catch (IOException e){

                System.out.println("Error connecting to Master: " + e.getMessage());
                return null;
            }
    }


    private static String getFoodCategoriesList(){
        String command = "Get_Food_Category_List";
        try(Socket socket = new Socket(Master_IP,Master_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){

            out.println(command);
            String response = in.readLine();
            System.out.println("Food Categories to choose: " + response);
            return response;

        }
         catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private static String getProductCategoryList(){
        String command = "Get_Product_Category_List";
        try(Socket socket = new Socket(Master_IP,Master_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){

            out.println(command);
            String response = in.readLine();
            System.out.println("Product Categories to choose: " + response);
            return  response;

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    



    private static void sendCommandtoMaster(String command) {
        try (Socket socket = new Socket(Master_IP, Master_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);
            String response = in.readLine();
            System.out.println("Response from Master: " + response);

        } catch (IOException error) {
            System.out.println("Error connecting to Master: " + error.getMessage());
        }
    }
}
