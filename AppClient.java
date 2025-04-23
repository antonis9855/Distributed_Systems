import java.util.Scanner;
import java.io.*;
import java.net.*;
import org.json.*;

public class AppClient {
    private static final String MASTER_IP       = "127.0.0.1";
    private static final int    MASTER_PORT     = 5000;
    private static final double CLIENT_LATITUDE  = 37.99;
    private static final double CLIENT_LONGITUDE = 23.73;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Client Menu ===");
            System.out.println("1) Search shops");
            System.out.println("2) Buy products");
            System.out.println("3) Rate a shop");
            System.out.println("0) Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                searchShops(scanner);
            } else if (choice.equals("2")) {
                buyProducts(scanner);
            } else if (choice.equals("3")) {
                rateShop(scanner);
            } else if (choice.equals("0")) {
                break;
            } else {
                System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    private static void searchShops(Scanner scanner) {
        try {
            JSONObject filter = new JSONObject()
                .put("Latitude",  CLIENT_LATITUDE)
                .put("Longitude", CLIENT_LONGITUDE);
            System.out.print("Food category (or leave blank): ");
            filter.put("FoodCategory", scanner.nextLine().trim());
            System.out.print("Minimum stars (1–5): ");
            filter.put("MinStars", Integer.parseInt(scanner.nextLine().trim()));
            System.out.print("Number of price bands (0–3): ");
            int bandsCount = Integer.parseInt(scanner.nextLine().trim());
            JSONArray priceBands = new JSONArray();
            for (int i = 0; i < bandsCount; i++) {
                System.out.print("  Band " + (i+1) + " ($, $$, $$$): ");
                priceBands.put(scanner.nextLine().trim());
            }
            filter.put("PriceBands", priceBands);

            String request  = "SEARCH " + filter.toString();
            String response = sendRequest(request);
            JSONArray shops = new JSONArray(response);

            System.out.println("\nFound " + shops.length() + " shops:");
            for (int i = 0; i < shops.length(); i++) {
                JSONObject s = shops.getJSONObject(i);
                System.out.printf("  %s (%s) at [%.4f, %.4f], %d stars%n",
                    s.getString("StoreName"),
                    s.getString("FoodCategory"),
                    s.getDouble("Latitude"),
                    s.getDouble("Longitude"),
                    s.getInt("Stars"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void buyProducts(Scanner scanner) {
        try {
            System.out.print("StoreName to buy from: ");
            String storeName = scanner.nextLine().trim();
            System.out.print("Number of items: ");
            int itemCount = Integer.parseInt(scanner.nextLine().trim());
            JSONArray items = new JSONArray();
            for (int i = 0; i < itemCount; i++) {
                System.out.print("  ProductName: ");
                String productName = scanner.nextLine().trim();
                System.out.print("  Quantity: ");
                int qty = Integer.parseInt(scanner.nextLine().trim());
                items.put(new JSONObject()
                    .put("ProductName", productName)
                    .put("Quantity",    qty));
            }
            JSONObject buyReq = new JSONObject()
                .put("StoreName", storeName)
                .put("Items",     items);
            String response = sendRequest("BUY " + buyReq.toString());
            JSONObject result = new JSONObject(response);

            System.out.println("\nPurchase status: " + result.getString("status"));
            for (String key : result.keySet()) {
                if (!key.equals("status")) {
                    System.out.printf("  %s: %s%n", key, result.get(key).toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void rateShop(Scanner scanner) {
        try {
            System.out.print("StoreName to rate: ");
            String storeName = scanner.nextLine().trim();
            System.out.print("Your rating (1–5): ");
            int stars = Integer.parseInt(scanner.nextLine().trim());
            if (stars < 1 || stars > 5) {
                System.out.println("Rating must be between 1 and 5");
                return;
            }
            JSONObject rateReq = new JSONObject()
                .put("StoreName", storeName)
                .put("Stars",     stars);
            String response = sendRequest("RATE " + rateReq.toString());
            System.out.println("Rating response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
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
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
