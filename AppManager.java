// AppManager.java
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.*;

public class AppManager {
    private static final String MASTER_IP   = "127.0.0.1";
    private static final int    MASTER_PORT = 5000;
    private static int managerId = 0;

    public static void main(String[] args) throws Exception {
        if (args.length >= 1) {
            managerId = Integer.parseInt(args[0]);
        }
        System.out.println("AppManager id=" + managerId + " started");

        Scanner sc = new Scanner(System.in);
        List<String> storeNames = new ArrayList<>();

<<<<<<< HEAD
    public static void main(String[] args) {
        System.out.println("[DEBUG] Entering method: void");
        Scanner scanner = new Scanner(System.in);
=======
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
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
<<<<<<< HEAD
                    System.out.println("[DEBUG] Handling case 1:");
=======
                    // ADD_SHOP
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
                    System.out.print("Path to store JSON file: ");
                    String shopPath = sc.nextLine().trim();
                    String shopJson = readFile(shopPath);
                    if (shopJson == null) break;
                    response = sendRequest("ADD_SHOP " + shopJson);
                    System.out.println("-> " + response);
                    if ("OK".equals(response)) {
                        String newStore = new JSONObject(shopJson).getString("StoreName");
                        storeNames.add(newStore);
                    }
                    break;
<<<<<<< HEAD
        
                case "2":
                    System.out.println("[DEBUG] Handling case 2:");
                    System.out.print("Store name: ");
                    String addStore = scanner.nextLine().trim();
                    System.out.print("Path to product JSON file: ");
                    String prodPath = scanner.nextLine().trim();
                    String prodJson = readFile(prodPath);
                    if (prodJson != null) {
                        System.out.println(sendRequest("ADD_ITEM " + addStore + " " + prodJson));
=======

                case "2":
                    if (storeNames.isEmpty()) {
                        System.out.println("No stores available. Add one first.");
                        break;
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
                    }
                    System.out.println("Select store to add product:");
                    for (int i = 0; i < storeNames.size(); i++) {
                        System.out.printf("  %d) %s%n", i+1, storeNames.get(i));
                    }
                    System.out.print("Choice: ");
                    int idxAdd = Integer.parseInt(sc.nextLine().trim()) - 1;
                    if (idxAdd < 0 || idxAdd >= storeNames.size()) {
                        System.out.println("Invalid selection.");
                        break;
                    }
                    String storeForAdd = storeNames.get(idxAdd);
                    System.out.print("ProductName: ");
                    String pname = sc.nextLine().trim();
                    System.out.print("ProductType: ");
                    String ptype = sc.nextLine().trim();
                    System.out.print("Available Amount: ");
                    int pstock = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Price: ");
                    double pprice = Double.parseDouble(sc.nextLine().trim());
                    JSONObject prodObj = new JSONObject()
                        .put("ProductName", pname)
                        .put("ProductType", ptype)
                        .put("Available Amount", pstock)
                        .put("Price", pprice);
                    JSONObject wrapAdd = new JSONObject()
                        .put("StoreName", storeForAdd)
                        .put("Product", prodObj);
                    response = sendRequest("ADD_ITEM " + wrapAdd.toString());
                    System.out.println("-> " + response);
                    break;
<<<<<<< HEAD
        
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
                    
=======

                case "3":
                    if (storeNames.isEmpty()) {
                        System.out.println("No stores available.");
                        break;
                    }
                    System.out.println("Select store to remove product:");
                    for (int i = 0; i < storeNames.size(); i++) {
                        System.out.printf("  %d) %s%n", i+1, storeNames.get(i));
                    }
                    System.out.print("Choice: ");
                    int idxRem = Integer.parseInt(sc.nextLine().trim()) - 1;
                    if (idxRem < 0 || idxRem >= storeNames.size()) {
                        System.out.println("Invalid selection.");
                        break;
                    }
                    String storeForRemove = storeNames.get(idxRem);
                    System.out.print("ProductName: ");
                    String prodToRemove = sc.nextLine().trim();
                    JSONObject wrapRem = new JSONObject()
                        .put("StoreName", storeForRemove)
                        .put("ProductName", prodToRemove);
                    response = sendRequest("REMOVE_ITEM " + wrapRem.toString());
                    System.out.println("-> " + response);
                    break;

                case "4":
                    if (storeNames.isEmpty()) {
                        System.out.println("No stores available.");
                        break;
                    }
                    System.out.println("Select store to restock:");
                    for (int i = 0; i < storeNames.size(); i++) {
                        System.out.printf("  %d) %s%n", i+1, storeNames.get(i));
                    }
                    System.out.print("Choice: ");
                    int idxRes = Integer.parseInt(sc.nextLine().trim()) - 1;
                    if (idxRes < 0 || idxRes >= storeNames.size()) {
                        System.out.println("Invalid selection.");
                        break;
                    }
                    String storeForRestock = storeNames.get(idxRes);
                    System.out.print("ProductName: ");
                    String prodToRestock = sc.nextLine().trim();
                    System.out.print("Delta amount: ");
                    int delta = Integer.parseInt(sc.nextLine().trim());
                    JSONObject wrapRestock = new JSONObject()
                        .put("StoreName", storeForRestock)
                        .put("ProductName", prodToRestock)
                        .put("Delta", delta);
                    response = sendRequest("RESTOCK " + wrapRestock.toString());
                    System.out.println("-> " + response);
                    break;

                case "5":
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
                    System.out.print("FoodCategory: ");
                    String cat = sc.nextLine().trim();
                    JSONObject catPayload = new JSONObject()
                        .put("FoodCategory", cat);
                    response = sendRequest("TOTAL_SALES_BY_STORE_TYPE " + catPayload.toString());
                    JSONObject salesByStore = new JSONObject(response);
                    System.out.println("Sales by store for category '" + cat + "':");
                    for (String store : salesByStore.keySet()) {
                        System.out.printf("  %s: %.2f%n",
                            store,
                            salesByStore.getDouble(store));
                    }
                    break;

                case "7":
                    System.out.print("ProductType: ");
                    String pcat = sc.nextLine().trim();
                    JSONObject prodPayload = new JSONObject()
                        .put("ProductType", pcat);
                    response = sendRequest("TOTAL_SALES_BY_PRODUCT_CATEGORY " + prodPayload.toString());
                    JSONObject salesByProdCat = new JSONObject(response);
                    System.out.println("Sales by store for product type '" + pcat + "':");
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

>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static String readFile(String path) {
        System.out.println("[DEBUG] Entering method: readFile");
        try {
            byte[] raw = Files.readAllBytes(Paths.get(path));
            String content = new String(raw, StandardCharsets.UTF_8);
            if (content.startsWith("\uFEFF")) content = content.substring(1);
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
<<<<<<< HEAD
        System.out.println("[DEBUG] Entering method: sendRequest");
        try (Socket sock = new Socket(MASTER_HOST, MASTER_PORT);
             PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()))) {
=======
        try (
            Socket sock = new Socket(MASTER_IP, MASTER_PORT);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(sock.getInputStream()))
        ) {
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
            out.println(request);
            String line = in.readLine();
            return line == null ? "" : line.trim();
        } catch (IOException e) {
            System.out.println("Error connecting to Master: " + e.getMessage());
            return "";
        }
    }
}