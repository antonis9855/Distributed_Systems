import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.*;

public class AppManager {
    private static final String MASTER_IP   = "127.0.0.1";
    private static final int    MASTER_PORT = 5000;

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== MANAGER MENU ===");
            System.out.println("1) Add store");
            System.out.println("2) Add product to store");
            System.out.println("3) Remove product from store");
            System.out.println("4) Restock product");
            System.out.println("5) Total sales per product");
            System.out.println("6) Total sales by store category");
            System.out.println("7) Total sales by product category");
            System.out.println("0) Exit");
            System.out.print("Choice: ");

            String choice = sc.nextLine().trim();
            String response;

            switch (choice) {
                case "1":
                    // ADD_SHOP <json>
                    System.out.print("Path to store JSON file: ");
                    String shopPath = sc.nextLine().trim();
                    String shopJson = readFile(shopPath);
                    if (shopJson == null) break;
                    response = sendRequest("ADD_SHOP " + shopJson);
                    System.out.println("-> " + response);
                    break;

                case "2":
                    // ADD_ITEM <StoreName> <json>
                    System.out.print("StoreName: ");
                    String storeNameForAdd = sc.nextLine().trim();
                    System.out.print("Path to product JSON file: ");
                    String prodPath = sc.nextLine().trim();
                    String prodJson = readFile(prodPath);
                    if (prodJson == null) break;
                    response = sendRequest("ADD_ITEM " 
                                            + storeNameForAdd 
                                            + " " + prodJson);
                    System.out.println("-> " + response);
                    break;

                case "3":
                    // REMOVE_ITEM <StoreName>|<ProductName>
                    System.out.print("StoreName: ");
                    String storeNameForRemove = sc.nextLine().trim();
                    System.out.print("ProductName: ");
                    String prodToRemove = sc.nextLine().trim();
                    // use '|' so Worker splits correctly
                    response = sendRequest("REMOVE_ITEM " 
                                            + storeNameForRemove 
                                            + "|" + prodToRemove);
                    System.out.println("-> " + response);
                    break;

                case "4":
                    // RESTOCK <StoreName> <ProductName> <delta>
                    System.out.print("StoreName: ");
                    String storeNameForRestock = sc.nextLine().trim();
                    System.out.print("ProductName: ");
                    String prodToRestock = sc.nextLine().trim();
                    System.out.print("Delta amount: ");
                    String delta = sc.nextLine().trim();
                    response = sendRequest("RESTOCK "
                                            + storeNameForRestock 
                                            + " " + prodToRestock
                                            + " " + delta);
                    System.out.println("-> " + response);
                    break;

                case "5":
                    // TOTAL_SALES_PER_PRODUCT
                    response = sendRequest("TOTAL_SALES_PER_PRODUCT");
                    JSONObject salesByProduct = new JSONObject(response);
                    System.out.println("Sales per product:");
                    for (String prod : salesByProduct.keySet()) {
                        System.out.printf("  %s: %.2f%n",
                            prod,
                            salesByProduct.getDouble(prod));
                    }
                    break;

                case "6":
                    // TOTAL_SALES_BY_STORE_TYPE {"FoodCategory":...}
                    System.out.print("FoodCategory: ");
                    String cat = sc.nextLine().trim();
                    JSONObject catPayload = new JSONObject()
                                                .put("FoodCategory", cat);
                    response = sendRequest("TOTAL_SALES_BY_STORE_TYPE " 
                                            + catPayload.toString());
                    JSONObject salesByStore = new JSONObject(response);
                    System.out.println("Sales by store for category '" + cat + "':");
                    for (String store : salesByStore.keySet()) {
                        System.out.printf("  %s: %.2f%n",
                            store,
                            salesByStore.getDouble(store));
                    }
                    break;

                case "7":
                    // TOTAL_SALES_BY_PRODUCT_CATEGORY {"ProductType":...}
                    System.out.print("ProductType: ");
                    String ptype = sc.nextLine().trim();
                    JSONObject prodPayload = new JSONObject()
                                                .put("ProductType", ptype);
                    response = sendRequest("TOTAL_SALES_BY_PRODUCT_CATEGORY " 
                                            + prodPayload.toString());
                    JSONObject salesByProdCat = new JSONObject(response);
                    System.out.println("Sales by store for product type '" + ptype + "':");
                    for (String store : salesByProdCat.keySet()) {
                        System.out.printf("  %s: %.2f%n",
                            store,
                            salesByProdCat.getDouble(store));
                    }
                    break;

                case "0":
                    System.out.println("Exiting Manager.");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static String readFile(String path) {
        try {
            byte[] raw = Files.readAllBytes(Paths.get(path));
            String content = new String(raw, StandardCharsets.UTF_8);
        
            if (content.startsWith("\uFEFF")) {
                content = content.substring(1);
            }
            
            content = content.replaceAll("\\R", "").trim();
            
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length()-1).trim();
            }
            return content;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }
    }

    private static String sendRequest(String request) {
        try (
            Socket sock = new Socket(MASTER_IP, MASTER_PORT);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(sock.getInputStream()))
        ) {
            out.println(request);
            String line = in.readLine();
            return line == null ? "" : line.trim();
        } catch (IOException e) {
            System.out.println("Error connecting to Master: " + e.getMessage());
            return "";
        }
    }
}