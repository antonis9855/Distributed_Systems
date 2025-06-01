import java.util.Scanner;
import java.io.*;
import java.net.*;
import org.json.*;

public class AppClient {
<<<<<<< HEAD
    private static final String MASTER_HOST = "127.0.0.1";
    private static final int MASTER_PORT = 5000;

=======
    private static final String MASTER_IP       = "127.0.0.1";
    private static final int    MASTER_PORT     = 5000;
    private static int clientId = 0;
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
    private static final double CLIENT_LATITUDE  = 37.99;
    private static final double CLIENT_LONGITUDE = 23.73;

    public static void main(String[] args) {
<<<<<<< HEAD
        System.out.println("[DEBUG] Entering method: void");
=======
        if (args.length >= 1) {
            clientId = Integer.parseInt(args[0]);
        }
        System.out.println("AppClient id=" + clientId + " started");

>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Client Menu ===");
            System.out.println("1) Search shops");
            System.out.println("2) Buy products");
            System.out.println("3) Rate a shop");
            System.out.println("0) Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                
                case "1":
<<<<<<< HEAD
                System.out.println("[DEBUG] Handling case 1:");
                    search(scanner);
                    break;
                case "2":
                System.out.println("[DEBUG] Handling case 2:");
                    buy(scanner);
                    break;
                case "3":
                System.out.println("[DEBUG] Handling case 3:");
                    rate(scanner);
=======
                    searchShops();
                    break;
                case "2":
                    buyProducts(scanner);
                    break;
                case "3":
                    rateShop(scanner);
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
                    break;
                case "0":
                    scanner.close();
                    return;
                default:
<<<<<<< HEAD
                System.out.println("[DEBUG] Handling default:");
                    System.out.println("Invalid choice");
=======
                    System.out.println("Invalid option");
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
            }
        }
    }

<<<<<<< HEAD
    private static void search(Scanner scanner) {
        System.out.println("[DEBUG] Entering method: void");
=======
    private static void searchShops() {
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
        try {
            JSONObject filter = new JSONObject()
                .put("Latitude",  CLIENT_LATITUDE)
                .put("Longitude", CLIENT_LONGITUDE);
<<<<<<< HEAD

            System.out.print("Food category (or empty): ");
            filter.put("FoodCategory", scanner.nextLine().trim());

            System.out.print("Minimum stars (1-5): ");
            filter.put("MinStars", Integer.parseInt(scanner.nextLine().trim()));

            System.out.print("Number of price bands (0-3): ");
            int bandCount = Integer.parseInt(scanner.nextLine().trim());
            JSONArray priceBands = new JSONArray();
            for (int i = 0; i < bandCount; i++) {
                System.out.print("  Band " + (i + 1) + " ($, $$, $$$): ");
                priceBands.put(scanner.nextLine().trim());
            }
            filter.put("PriceBands", priceBands);
=======
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748

            String response = sendRequest("SEARCH " + filter.toString());
            JSONArray shops = new JSONArray(response);

            System.out.println("\nAvailable shops:");
            for (int i = 0; i < shops.length(); i++) {
                JSONObject s = shops.getJSONObject(i);
                String name     = s.getString("StoreName");
                String category = s.getString("FoodCategory");
                int stars       = s.getInt("Stars");

                JSONArray products = s.getJSONArray("Products");
                double sum = 0;
                for (int j = 0; j < products.length(); j++) {
                    sum += products.getJSONObject(j).getDouble("Price");
                }
                double avg = products.length() > 0 ? sum / products.length() : 0;
                String band = avg <= 5  ? "$"
                            : avg <= 15 ? "$$"
                                        : "$$$";

                System.out.printf("  %d) %s - %s - %d★ - %s%n",
                    i + 1, name, category, stars, band);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

<<<<<<< HEAD
    private static void buy(Scanner scanner) {
        System.out.println("[DEBUG] Entering method: void");
        try {
            System.out.print("Store name: ");
            String storeName = scanner.nextLine().trim();

            System.out.print("Number of items: ");
            int itemCount = Integer.parseInt(scanner.nextLine().trim());
            JSONArray items = new JSONArray();

            for (int i = 0; i < itemCount; i++) {
                System.out.print("  Product name: ");
                String productName = scanner.nextLine().trim();

                System.out.print("  Quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine().trim());

                items.put(new JSONObject()
                    .put("ProductName", productName)
                    .put("Quantity",    quantity));
            }

            JSONObject requestJson = new JSONObject()
                .put("StoreName", storeName)
                .put("Items",     items);

            String response = sendRequest("BUY " + requestJson);

            
            if (!response.startsWith("{")) {
                System.out.println("Buy response (not JSON): " + response);
                return;
            }

            JSONObject result = new JSONObject(response);
            System.out.println("Purchase status: " + result.getString("status"));
=======
    private static void buyProducts(Scanner scanner) {
        try {
            JSONObject filter = new JSONObject()
                .put("Latitude",  CLIENT_LATITUDE)
                .put("Longitude", CLIENT_LONGITUDE);
            JSONArray shops = new JSONArray(sendRequest("SEARCH " + filter.toString()));

            if (shops.isEmpty()) {
                System.out.println("No shops available.");
                return;
            }

            System.out.println("\nSelect a shop to buy from");
            for (int i = 0; i < shops.length(); i++) {
                System.out.printf("  %d) %s%n",
                    i + 1,
                    shops.getJSONObject(i).getString("StoreName"));
            }
            System.out.print("Enter shop number: ");
            int shopIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (shopIdx < 0 || shopIdx >= shops.length()) {
                System.out.println("Invalid selection");
                return;
            }
            JSONObject chosenShop = shops.getJSONObject(shopIdx);
            String storeName      = chosenShop.getString("StoreName");

            JSONArray products = chosenShop.getJSONArray("Products");
            if (products.isEmpty()) {
                System.out.println("No products available at " + storeName);
                return;
            }
            System.out.println("\nProducts at " + storeName);
            for (int i = 0; i < products.length(); i++) {
                JSONObject p = products.getJSONObject(i);
                System.out.printf("  %d) %s - €%.2f (%d in stock)%n",
                    i + 1,
                    p.getString("ProductName"),
                    p.getDouble("Price"),
                    p.getInt("Available Amount"));
            }

            JSONArray items = new JSONArray();
            while (true) {
                System.out.print("\nEnter product number to buy: ");
                int prodIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (prodIdx < 0 || prodIdx >= products.length()) {
                    System.out.println("Invalid product number");
                    continue;
                }
                String prodName = products.getJSONObject(prodIdx).getString("ProductName");
                System.out.print("Enter quantity: ");
                int qty = Integer.parseInt(scanner.nextLine().trim());
                items.put(new JSONObject()
                    .put("ProductName", prodName)
                    .put("Quantity",    qty));

                System.out.print("Add another product (y/n): ");
                String more = scanner.nextLine().trim().toLowerCase();
                if (!more.equals("y")) break;
            }

            JSONObject buyReq = new JSONObject()
                .put("StoreName", storeName)
                .put("Items",     items);
            JSONObject result = new JSONObject(sendRequest("BUY " + buyReq.toString()));

            System.out.println("\nPurchase status: " + result.getString("status"));
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
            for (String key : result.keySet()) {
        System.out.println("[DEBUG] Entering method: for");
                if (!"status".equals(key)) {
                    System.out.printf("  %s: %s%n", key, result.get(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

<<<<<<< HEAD
    private static void rate(Scanner scanner) {
        System.out.println("[DEBUG] Entering method: void");
        try {
            System.out.print("Store name: ");
            String storeName = scanner.nextLine().trim();

            System.out.print("Rating (1-5): ");
=======
    private static void rateShop(Scanner scanner) {
        try {
            JSONObject filter = new JSONObject()
                .put("Latitude",  CLIENT_LATITUDE)
                .put("Longitude", CLIENT_LONGITUDE);
            JSONArray shops = new JSONArray(sendRequest("SEARCH " + filter.toString()));

            if (shops.isEmpty()) {
                System.out.println("No shops available.");
                return;
            }

            System.out.println("\nSelect a shop to rate");
            for (int i = 0; i < shops.length(); i++) {
                System.out.printf("  %d) %s%n",
                    i + 1,
                    shops.getJSONObject(i).getString("StoreName"));
            }
            System.out.print("Enter shop number: ");
            int shopIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (shopIdx < 0 || shopIdx >= shops.length()) {
                System.out.println("Invalid selection");
                return;
            }
            String storeName = shops.getJSONObject(shopIdx).getString("StoreName");

            System.out.print("Enter rating 1-5: ");
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
            int stars = Integer.parseInt(scanner.nextLine().trim());
            if (stars < 1 || stars > 5) {
                System.out.println("Rating must be between 1 and 5");
                return;
            }

<<<<<<< HEAD
            JSONObject requestJson = new JSONObject()
                .put("StoreName", storeName)
                .put("Stars",     stars);

            String response = sendRequest("RATE " + requestJson);
            System.out.println("Rate response: " + response);
=======
            JSONObject rateReq = new JSONObject()
                .put("StoreName", storeName)
                .put("Stars",     stars);
            String response = sendRequest("RATE " + rateReq.toString());
            System.out.println("Rating response: " + response);
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

<<<<<<< HEAD
    private static String sendRequest(String request) {
        System.out.println("[DEBUG] Entering method: String");
        try (Socket socket = new Socket(MASTER_HOST, MASTER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request);
            return in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
            return "";
=======
    private static String sendRequest(String request) throws IOException {
        try (
            Socket sock = new Socket(MASTER_IP, MASTER_PORT);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(sock.getInputStream()))
        ) {
            out.println(request);
            return in.readLine();
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
        }
    }
}