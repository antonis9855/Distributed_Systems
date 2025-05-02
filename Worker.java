import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class Worker {
    private final int port;
    private final Map<String, Shop> shopMap = Collections.synchronizedMap(new HashMap<>());

    public Worker(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java Worker <port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        new Worker(port).run();
    }

    public void run() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Worker started on port " + port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handle(clientSocket)).start();
        }
    }

    private void handle(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String line = reader.readLine();
            if (line == null) return;
            String[] parts = line.split(" ", 2);
            String command = parts[0];
            String payload = parts.length > 1 ? parts[1] : "";
            switch (command) {
                case "ADD_SHOP":
                    addShop(new JSONObject(payload));
                    writer.println("OK");
                    break;
                case "ADD_ITEM": {
                    String[] p = payload.split(" ", 2);
                    addItem(p[0], new JSONObject(p[1]));
                    writer.println("OK");
                    break;
                }
                case "REMOVE_ITEM": {
                    String[] p = payload.split(" ", 2);
                    removeItem(p[0], p[1]);
                    writer.println("OK");
                    break;
                }
                case "RESTOCK": {
                    String[] p = payload.split(" ", 3);
                    restock(p[0], p[1], Integer.parseInt(p[2]));
                    writer.println("OK");
                    break;
                }
                case "SEARCH":
                    writer.println(search(new JSONObject(payload)).toString());
                    break;
                case "BUY":
                    writer.println(buy(new JSONObject(payload)).toString());
                    break;
                case "TOTAL_SALES_PER_PRODUCT":
                    writer.println(totalSalesPerProduct().toString());
                    break;
                case "TOTAL_SALES_BY_STORE_TYPE": {
                    String category = new JSONObject(payload).getString("FoodCategory");
                    writer.println(totalSalesByStoreType(category).toString());
                    break;
                }
                case "TOTAL_SALES_BY_PRODUCT_CATEGORY": {
                    String type = new JSONObject(payload).getString("ProductType");
                    writer.println(totalSalesByProductCategory(type).toString());
                    break;
                }
                default:
                    writer.println("ERROR Unknown command");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { clientSocket.close(); }
            catch (IOException ignored) {}
        }
    }

    private void addShop(JSONObject js) {
        String storeName = js.getString("StoreName");
        double latitude = js.getDouble("Latitude");
        double longitude = js.getDouble("Longitude");
        String foodCategory = js.getString("FoodCategory");
        int starRating = js.getInt("Stars");
        int voteCount = js.getInt("NoOfVotes");
        String storeLogo = js.optString("StoreLogo", "");
        Shop shop = new Shop(storeName, latitude, longitude, foodCategory, starRating, voteCount, storeLogo);

        JSONArray products = js.optJSONArray("Products");
        if (products != null) {
            for (int i = 0; i < products.length(); i++) {
                JSONObject pj = products.getJSONObject(i);
                String productName = pj.getString("ProductName");
                String productType = pj.getString("ProductType");
                double price = pj.getDouble("Price");
                int availableAmount = pj.getInt("Available Amount");
                shop.productMap.put(productName, new Product(productName, productType, price, availableAmount));
            }
        }
        shopMap.put(storeName, shop);
    }

    private void addItem(String storeName, JSONObject pj) {
        Shop shop = shopMap.get(storeName);
        if (shop != null) {
            String productName = pj.getString("ProductName");
            String productType = pj.getString("ProductType");
            double price = pj.getDouble("Price");
            int availableAmount = pj.getInt("Available Amount");
            shop.productMap.put(productName, new Product(productName, productType, price, availableAmount));
        }
    }

    private void removeItem(String storeName, String productName) {
        Shop shop = shopMap.get(storeName);
        if (shop != null) {
            shop.productMap.remove(productName);
        }
    }

    private void restock(String storeName, String productName, int delta) {
        Shop shop = shopMap.get(storeName);
        if (shop != null) {
            Product p = shop.productMap.get(productName);
            if (p != null) {
                synchronized (p) {
                    p.stock += delta;
                    p.notifyAll();
                }
            }
        }
    }

    private JSONArray search(JSONObject filter) {
        double userLat = filter.getDouble("Latitude");
        double userLon = filter.getDouble("Longitude");
        String wantCategory = filter.optString("FoodCategory", "");
        int minStars = filter.optInt("MinStars", 1);
        List<String> priceBands = toList(filter.optJSONArray("PriceBands"));

        JSONArray result = new JSONArray();
        synchronized (shopMap) {
            for (Shop s : shopMap.values()) {
                if (distanceKm(userLat, userLon, s.latitude, s.longitude) <= 5
                   && (wantCategory.isEmpty() || s.foodCategory.equals(wantCategory))
                   && s.starRating >= minStars
                   && (priceBands.isEmpty() || priceBands.contains(computePriceBand(s))))
                {
                    result.put(s.toJson());
                }
            }
        }
        return result;
    }

    private JSONObject buy(JSONObject req) {
        String storeName = req.getString("StoreName");
        JSONArray items = req.optJSONArray("Items");
        JSONObject receipt = new JSONObject().put("status", "OK");

        Shop shop = shopMap.get(storeName);
        if (shop == null) return receipt.put("status", "NO_STORE");
        if (items == null)  return receipt.put("status", "NO_ITEMS");

        for (int i = 0; i < items.length(); i++) {
            JSONObject it = items.getJSONObject(i);
            String productName = it.getString("ProductName");
            int quantity = it.getInt("Quantity");

            Product p = shop.productMap.get(productName);
            if (p == null) {
                receipt.put(productName, "NO_PRODUCT");
                continue;
            }

            synchronized(p) {
                while (p.stock < quantity) {
                    try { p.wait(); }
                    catch (InterruptedException ex) { Thread.currentThread().interrupt(); break; }
                }
                p.stock -= quantity;
                double sale = quantity * p.price;
                shop.totalSales += sale;
                p.revenue += sale;
            }
            receipt.put(productName, quantity);
        }

        receipt.put("totalSales", shop.totalSales);
        return receipt;
    }

    private JSONObject totalSalesPerProduct() {
        JSONObject res = new JSONObject();
        synchronized (shopMap) {
            for (Shop s : shopMap.values()) {
                for (Product p : s.productMap.values()) {
                    res.put(p.name, res.optDouble(p.name, 0.0) + p.revenue);
                }
            }
        }
        return res;
    }

    private JSONObject totalSalesByStoreType(String category) {
        JSONObject res = new JSONObject();
        double total = 0;
        synchronized (shopMap) {
            for (Shop s : shopMap.values()) {
                if (s.foodCategory.equals(category)) {
                    res.put(s.storeName, s.totalSales);
                    total += s.totalSales;
                }
            }
        }
        res.put("total", total);
        return res;
    }

    private JSONObject totalSalesByProductCategory(String productType) {
        JSONObject res = new JSONObject();
        double total = 0;
        synchronized (shopMap) {
            for (Shop s : shopMap.values()) {
                double storeSum = 0;
                for (Product p : s.productMap.values()) {
                    if (p.type.equals(productType)) storeSum += p.revenue;
                }
                if (storeSum > 0) {
                    res.put(s.storeName, storeSum);
                    total += storeSum;
                }
            }
        }
        res.put("total", total);
        return res;
    }

    private List<String> toList(JSONArray arr) {
        if (arr == null) return Collections.emptyList();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            list.add(arr.getString(i));
        }
        return list;
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                 + Math.cos(Math.toRadians(lat1))
                 * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon/2)*Math.sin(dLon/2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    private String computePriceBand(Shop s) {
        if (s.productMap.isEmpty()) return "$";
        double sum = 0;
        for (Product p : s.productMap.values()) sum += p.price;
        double avg = sum / s.productMap.size();
        return avg <= 5 ? "$" : avg <= 15 ? "$$" : "$$$";
    }

    static class Shop {
        String storeName, foodCategory, storeLogo;
        double latitude, longitude;
        int starRating, noOfVotes;
        double totalSales = 0;
        Map<String,Product> productMap = new HashMap<>();

        Shop(String storeName, double latitude, double longitude,
             String foodCategory, int starRating, int noOfVotes,
             String storeLogo) {
            this.storeName = storeName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.foodCategory = foodCategory;
            this.starRating = starRating;
            this.noOfVotes = noOfVotes;
            this.storeLogo = storeLogo;
        }

        JSONObject toJson() {
            JSONObject j = new JSONObject();
            j.put("StoreName", storeName)
             .put("Latitude", latitude)
             .put("Longitude", longitude)
             .put("FoodCategory", foodCategory)
             .put("Stars", starRating)
             .put("NoOfVotes", noOfVotes)
             .put("StoreLogo", storeLogo)
             .put("TotalSales", totalSales);
            JSONArray arr = new JSONArray();
            for (Product p : productMap.values()) {
                arr.put(p.toJson());
            }
            j.put("Products", arr);
            return j;
        }
    }

    static class Product {
        String name, type;
        double price;
        int stock;
        double revenue = 0;

        Product(String name, String type, double price, int stock) {
            this.name = name;
            this.type = type;
            this.price = price;
            this.stock = stock;
        }

        JSONObject toJson() {
            return new JSONObject()
              .put("ProductName", name)
              .put("ProductType", type)
              .put("Price", price)
              .put("Available Amount", stock);
        }
    }
}
