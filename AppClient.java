import java.util.Scanner;
import java.io.*;
import java.net.*;
import org.json.*;

public class AppClient {
    private static final String MASTER_IP       = "127.0.0.1";
    private static final int    MASTER_PORT     = 5000;
    private static int clientId = 0;
    private static final double CLIENT_LATITUDE  = 37.99;
    private static final double CLIENT_LONGITUDE = 23.73;

    public static void main(String[] args) {
        if (args.length >= 1) {
            clientId = Integer.parseInt(args[0]);
        }
        System.out.println("AppClient id=" + clientId + " started");

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
                    searchShops();
                    break;
                case "2":
                    buyProducts(scanner);
                    break;
                case "3":
                    rateShop(scanner);
                    break;
                case "0":
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private static void searchShops() {
        try {
            JSONObject filter = new JSONObject()
                .put("Latitude",  CLIENT_LATITUDE)
                .put("Longitude", CLIENT_LONGITUDE);

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
            for (String key : result.keySet()) {
                if (!"status".equals(key)) {
                    System.out.printf("  %s: %s%n", key, result.get(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private static String sendRequest(String request) throws IOException {
        try (
            Socket sock = new Socket(MASTER_IP, MASTER_PORT);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(sock.getInputStream()))
        ) {
            out.println(request);
            return in.readLine();
        }
    }
}
