import java.io.*;
import java.net.*;
import java.util.Scanner;
import org.json.*;

public class AppClient {
    private static final String MASTER_HOST = "127.0.0.1";
    private static final int MASTER_PORT = 5000;

    private static final double CLIENT_LATITUDE  = 37.99;
    private static final double CLIENT_LONGITUDE = 23.73;

    public static void main(String[] args) {
        System.out.println("[DEBUG] Entering method: void");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("=== CLIENT MENU ===");
            System.out.println("1) Search shops");
            System.out.println("2) Buy products");
            System.out.println("3) Rate shop");
            System.out.println("0) Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            if ("0".equals(choice)) {
                break;
            }
            switch (choice) {
                
                case "1":
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
                    break;
                default:
                System.out.println("[DEBUG] Handling default:");
                    System.out.println("Invalid choice");
            }
        }
        scanner.close();
    }

    private static void search(Scanner scanner) {
        System.out.println("[DEBUG] Entering method: void");
        try {
            JSONObject filter = new JSONObject()
                .put("Latitude",  CLIENT_LATITUDE)
                .put("Longitude", CLIENT_LONGITUDE);

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

            String request  = "SEARCH " + filter.toString();
            String response = sendRequest(request);
            JSONArray shops = new JSONArray(response);

            System.out.println("Found " + shops.length() + " shops:");
            for (int i = 0; i < shops.length(); i++) {
                JSONObject s = shops.getJSONObject(i);
                System.out.printf(
                    "  %s (%s) [%.4f, %.4f] %d stars%n",
                    s.getString("StoreName"),
                    s.getString("FoodCategory"),
                    s.getDouble("Latitude"),
                    s.getDouble("Longitude"),
                    s.getInt("Stars")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private static void rate(Scanner scanner) {
        System.out.println("[DEBUG] Entering method: void");
        try {
            System.out.print("Store name: ");
            String storeName = scanner.nextLine().trim();

            System.out.print("Rating (1-5): ");
            int stars = Integer.parseInt(scanner.nextLine().trim());
            if (stars < 1 || stars > 5) {
                System.out.println("Rating must be between 1 and 5");
                return;
            }

            JSONObject requestJson = new JSONObject()
                .put("StoreName", storeName)
                .put("Stars",     stars);

            String response = sendRequest("RATE " + requestJson);
            System.out.println("Rate response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        }
    }
}