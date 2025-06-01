import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import org.json.*;

public class AppManager {
    private static final String MASTER_HOST = "127.0.0.1";
    private static final int MASTER_PORT = 5000;

    public static void main(String[] args) {
        System.out.println("[DEBUG] Entering method: void");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("=== MANAGER MENU ===");
            System.out.println("1) Add store");
            System.out.println("2) Add product to store");
            System.out.println("3) Remove product from store");
            System.out.println("4) Restock product");
            System.out.println("5) Total sales per product");
            System.out.println("6) Total sales by store category");
            System.out.println("7) Total sales by product category");
            System.out.println("0) Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            if ("0".equals(choice)) {
                System.out.println("Exiting Manager");
                scanner.close();
                break;
            }
            switch (choice) {
        
        
                case "1":
                    System.out.println("[DEBUG] Handling case 1:");
                    System.out.print("Path to store JSON file: ");
                    String storePath = scanner.nextLine().trim();
                    String storeJson = readFile(storePath);
                    if (storeJson != null) {
                        System.out.println(sendRequest("ADD_SHOP " + storeJson));
                    }
                    break;
        
                case "2":
                    System.out.println("[DEBUG] Handling case 2:");
                    System.out.print("Store name: ");
                    String addStore = scanner.nextLine().trim();
                    System.out.print("Path to product JSON file: ");
                    String prodPath = scanner.nextLine().trim();
                    String prodJson = readFile(prodPath);
                    if (prodJson != null) {
                        System.out.println(sendRequest("ADD_ITEM " + addStore + " " + prodJson));
                    }
                    break;
        
                case "3":
                    System.out.println("[DEBUG] Handling case 3:");
                    System.out.print("Store name: ");
                    String remStore = scanner.nextLine().trim();
                    System.out.print("Product name: ");
                    String remProduct = scanner.nextLine().trim();
                    System.out.println(sendRequest("REMOVE_ITEM " + remStore + " " + remProduct));
                    break;
        
                case "4":
                    System.out.println("[DEBUG] Handling case 4:");
                    System.out.print("Store name: ");
                    String resStore = scanner.nextLine().trim();
                    System.out.print("Product name: ");
                    String resProduct = scanner.nextLine().trim();
                    System.out.print("Quantity to add: ");
                    String delta = scanner.nextLine().trim();
                    System.out.println(sendRequest("RESTOCK " + resStore + " " + resProduct + " " + delta));
                    break;
        
                case "5":
                    System.out.println("[DEBUG] Handling case 5:");
                    String s5 = sendRequest("TOTAL_SALES_PER_PRODUCT");
                    JSONObject j5 = new JSONObject(s5);
                    System.out.println("Sales per product:");
                    for (String prod : j5.keySet()) {
                    System.out.println("[DEBUG] Entering method: for");
                        System.out.printf("  %s: %.2f%n", prod, j5.getDouble(prod));
                    }
                    break;
        
                case "6":
                    System.out.println("[DEBUG] Handling case  6:");
                    System.out.print("Food category: ");
                    String cat = scanner.nextLine().trim();
                    JSONObject catFilter = new JSONObject().put("FoodCategory", cat);
                    String s6 = sendRequest("TOTAL_SALES_BY_STORE_TYPE " + catFilter);
                    JSONObject j6 = new JSONObject(s6);
                    System.out.println("Sales by store category:");
                    for (String store : j6.keySet()) {
                    System.out.println("[DEBUG] Entering method: for");
                        System.out.printf("  %s: %.2f%n", store, j6.getDouble(store));
                    }
                    break;
        
                case "7":
                    System.out.println("[DEBUG] Handling case  7 :");
                    System.out.print("Product type: ");
                    String pt = scanner.nextLine().trim();
                    JSONObject prodFilter = new JSONObject().put("ProductType", pt);
                    String s7 = sendRequest("TOTAL_SALES_BY_PRODUCT_CATEGORY " + prodFilter);
                    JSONObject j7 = new JSONObject(s7);
                    System.out.println("Sales by product category:");
                    for (String store : j7.keySet()) {
                    System.out.println("[DEBUG] Entering method: for");
                        System.out.printf("  %s: %.2f%n", store, j7.getDouble(store));
                    }
                    break;
                    
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static String readFile(String path) {
        System.out.println("[DEBUG] Entering method: readFile");
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            String content = new String(bytes, StandardCharsets.UTF_8);
            if (content.startsWith("\uFEFF")) {
                content = content.substring(1);
            }
            content = content.replaceAll("\\R", "").trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1).trim();
            }
            return content;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }
    }

    private static String sendRequest(String request) {
        System.out.println("[DEBUG] Entering method: sendRequest");
        try (Socket sock = new Socket(MASTER_HOST, MASTER_PORT);
             PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()))) {
            out.println(request);
            String response = in.readLine();
            return response == null ? "" : response.trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}