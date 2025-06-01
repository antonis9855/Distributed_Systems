import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class Worker {
    private final int port;
    private final Map<String,Shop> shopMap = Collections.synchronizedMap(new HashMap<>());
    private static final String REDUCER_HOST = "127.0.0.1";
    private static final int    REDUCER_PORT = 7000;

    public Worker(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Worker <port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        System.out.println("Worker listening on port " + port);
        new Worker(port).run();
    }

    public void run() throws IOException {
        System.out.println("[DEBUG] Handling run in Worker :" + port);

        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handle(clientSocket)).start();
        }
    }

    private void handle(Socket clientSocket) {
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(
                clientSocket.getOutputStream(), true)
        ) {
            String line = reader.readLine();
            if (line == null) return;

            String[] parts  = line.split(" ", 2);
            String command  = parts[0];
            String payload  = parts.length > 1 ? parts[1] : "";

            switch (command) {
                case "ADD_SHOP":
                    System.out.println("[DEBUG] Handling case ADD_SHOP in Worker" + port );

                    addShop(new JSONObject(payload));
                    writer.println("OK");
                    printState();
                    break;

                case "ADD_ITEM": {
<<<<<<< HEAD
                    System.out.println("[DEBUG] Handling case ADD_ITEM in Worker" + port );
                    String[] p = payload.split(" ", 2);
                    addItem(p[0], new JSONObject(p[1]));
=======
                    JSONObject wp = new JSONObject(payload);
                    addItem(
                        wp.getString("StoreName"),
                        wp.getJSONObject("Product")
                    );
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
                    writer.println("OK");
                    printState();
                    break;
                }

                case "REMOVE_ITEM": {
<<<<<<< HEAD
                    System.out.println("[DEBUG] Handling case REMOVE_ITEM in Worker" + port );
                    String[] p = payload.split(" ", 2);
                    removeItem(p[0], p[1]);
=======
                    JSONObject wp = new JSONObject(payload);
                    removeItem(
                        wp.getString("StoreName"),
                        wp.getString("ProductName")
                    );
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
                    writer.println("OK");
                    printState();
                    break;
                }

                case "RESTOCK": {
<<<<<<< HEAD
                    System.out.println("[DEBUG] Handling case RESTOCK in Worker" + port );
                    String[] p = payload.split(" ", 3);
                    restock(p[0], p[1], Integer.parseInt(p[2]));
                    writer.println("OK");
                    break;
                }
                case "SEARCH":
                    System.out.println("[DEBUG] Handling case SEARCH in Worker" + port );
                    writer.println(search(new JSONObject(payload)).toString());
                    break;
                case "BUY":
                    System.out.println("[DEBUG] Handling case BUY in Worker" + port );
                    writer.println(buy(new JSONObject(payload)).toString());
                    break;
                case "TOTAL_SALES_PER_PRODUCT":
                    System.out.println("[DEBUG] Handling case TOTAL_SALES_PER_PRODUCT in Worker" + port );
                    writer.println(totalSalesPerProduct().toString());
                    break;
                case "TOTAL_SALES_BY_STORE_TYPE": {
                    System.out.println("[DEBUG] Handling case TOTAL_SALES_BY_STORE_TYPE in Worker" + port );
                    String category = new JSONObject(payload).getString("FoodCategory");
                    writer.println(totalSalesByStoreType(category).toString());
=======
                    JSONObject wp = new JSONObject(payload);
                    restock(
                        wp.getString("StoreName"),
                        wp.getString("ProductName"),
                        wp.getInt("Delta")
                    );
                    writer.println("OK");
                    printState();
                    break;
                }

                case "SEARCH": {
                    JSONArray arr = search(new JSONObject(payload));
                    sendToReducer("MAP_SEARCH " + arr.toString());
                    writer.println("OK");
                    break;
                }

                case "TOTAL_SALES_PER_PRODUCT": {
                    JSONObject map = totalSalesPerProduct();
                    sendToReducer("MAP_AGG " + map.toString());
                    writer.println("OK");
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
                    break;
                }

                case "TOTAL_SALES_BY_STORE_TYPE": {
                    String cat = new JSONObject(payload).getString("FoodCategory");
                    JSONObject map = totalSalesByStoreType(cat);
                    sendToReducer("MAP_AGG " + map.toString());
                    writer.println("OK");
                    break;
                }

                case "TOTAL_SALES_BY_PRODUCT_CATEGORY": {
                    System.out.println("[DEBUG] Handling case TOTAL_SALES_BY_PRODUCT_CATEGORY in Worker" + port );
                    String type = new JSONObject(payload).getString("ProductType");
                    JSONObject map = totalSalesByProductCategory(type);
                    sendToReducer("MAP_AGG " + map.toString());
                    writer.println("OK");
                    break;
                }

                case "BUY": {
                    JSONObject result = buy(new JSONObject(payload));
                    writer.println(result.toString());
                    printState();
                    break;
                }

                case "RATE": {
                    JSONObject rp = new JSONObject(payload);
                    String store = rp.getString("StoreName");
                    int    stars  = rp.getInt("Stars");
                    Shop shop = shopMap.get(store);
                    if (shop != null) {
                        synchronized (shop) {
                            int totalVotes = shop.noOfVotes;
                            int sumStars   = shop.starRating * totalVotes + stars;
                            shop.noOfVotes = totalVotes + 1;
                            shop.starRating = (int)Math.round(
                                sumStars / (double)shop.noOfVotes
                            );
                        }
                        writer.println("OK");
                    } else {
                        writer.println("NO_STORE");
                    }
                    printState();
                    break;
                }

                case "DUMP": {
                    JSONArray dump = new JSONArray();
                    synchronized (shopMap) {
                        for (Shop s : shopMap.values()) {
                            dump.put(s.toJson());
                        }
                    }
                    writer.println(dump.toString());
                    break;
                }

                default:
                    writer.println("ERROR Unknown command");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }

    private void printState() {
        System.out.println("\n=== Worker@" + port + " State ===");
        synchronized (shopMap) {
            for (Shop s : shopMap.values()) {
                System.out.printf(
                    "Store: %s | Category: %s | Stars: %d | Votes: %d | TotalSales: %.2f%n",
                    s.storeName, s.foodCategory, s.starRating, s.noOfVotes, s.totalSales
                );
                for (Product p : s.productMap.values()) {
                    System.out.printf(
                        "  - Product: %s | Type: %s | Price: %.2f | Stock: %d | Revenue: %.2f%n",
                        p.name, p.type, p.price, p.stock, p.revenue
                    );
                }
            }
        }
        System.out.println("=== End State ===\n");
    }

    private void sendToReducer(String msg) {
        try (
            Socket sock = new Socket(REDUCER_HOST, REDUCER_PORT);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true)
        ) {
            out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addShop(JSONObject js) {
        String storeName     = js.getString("StoreName");
        double latitude      = js.getDouble("Latitude");
        double longitude     = js.getDouble("Longitude");
        String foodCategory  = js.getString("FoodCategory");
        int starRating       = js.getInt("Stars");
        int voteCount        = js.getInt("NoOfVotes");
        String storeLogo     = js.optString("StoreLogo", "");
        Shop shop = new Shop(
            storeName, latitude, longitude,
            foodCategory, starRating, voteCount, storeLogo
        );

        JSONArray products = js.optJSONArray("Products");
        if (products != null) {
            for (int i = 0; i < products.length(); i++) {
                JSONObject pj = products.getJSONObject(i);
                shop.productMap.put(
                    pj.getString("ProductName"),
                    new Product(
                        pj.getString("ProductName"),
                        pj.getString("ProductType"),
                        pj.getDouble("Price"),
                        pj.getInt("Available Amount")
                    )
                );
            }
        }
        shopMap.put(storeName, shop);
    }

    private void addItem(String storeName, JSONObject pj) {
        Shop shop = shopMap.get(storeName);
        if (shop != null) {
            shop.productMap.put(
                pj.getString("ProductName"),
                new Product(
                    pj.getString("ProductName"),
                    pj.getString("ProductType"),
                    pj.getDouble("Price"),
                    pj.getInt("Available Amount")
                )
            );
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
        double userLat      = filter.getDouble("Latitude");
        double userLon      = filter.getDouble("Longitude");
        String wantCategory = filter.optString("FoodCategory", "");
        int minStars        = filter.optInt("MinStars", 1);
        List<String> bands  = toList(filter.optJSONArray("PriceBands"));
        JSONArray result    = new JSONArray();

        synchronized (shopMap) {
            for (Shop s : shopMap.values()) {
                if (distanceKm(userLat, userLon, s.latitude, s.longitude) <= 5
                 && (wantCategory.isEmpty() || s.foodCategory.equals(wantCategory))
                 && s.starRating >= minStars
                 && (bands.isEmpty() || bands.contains(computePriceBand(s)))
                ) {
                    result.put(s.toJson());
                }
            }
        }
        return result;
    }

    private JSONObject buy(JSONObject req) {
        String storeName = req.getString("StoreName");
        JSONArray items  = req.optJSONArray("Items");
        JSONObject receipt = new JSONObject().put("status", "OK");

        Shop shop = shopMap.get(storeName);
        if (shop == null) return receipt.put("status","NO_STORE");
        if (items == null) return receipt.put("status","NO_ITEMS");

        for (int i = 0; i < items.length(); i++) {
            JSONObject it = items.getJSONObject(i);
            String pName  = it.getString("ProductName");
            int qty       = it.getInt("Quantity");
            Product p     = shop.productMap.get(pName);
            if (p == null) {
                receipt.put(pName,"NO_PRODUCT");
                continue;
            }
            synchronized (p) {
                while (p.stock < qty) {
                    try { p.wait(); } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                p.stock -= qty;
                double sale = qty * p.price;
                shop.totalSales += sale;
                p.revenue      += sale;
            }
            receipt.put(pName, qty);
        }
        receipt.put("totalSales", shop.totalSales);
        return receipt;
    }

    private JSONObject totalSalesPerProduct() {
        JSONObject res = new JSONObject();
        synchronized (shopMap) {
            for (Shop s : shopMap.values()) {
                for (Product p : s.productMap.values()) {
                    res.put(p.name, res.optDouble(p.name, 0) + p.revenue);
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

    private JSONObject totalSalesByProductCategory(String type) {
        JSONObject res = new JSONObject();
        double total = 0;
        synchronized (shopMap) {
            for (Shop s : shopMap.values()) {
                double sum = 0;
                for (Product p : s.productMap.values()) {
                    if (p.type.equals(type)) sum += p.revenue;
                }
                if (sum > 0) {
                    res.put(s.storeName, sum);
                    total += sum;
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
        double R    = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                 + Math.cos(Math.toRadians(lat1))
                 * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon/2) * Math.sin(dLon/2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

<<<<<<< HEAD


        private String computePriceBand(Shop shop) {

            if (shop.productMap.isEmpty()) {
                return "$";
            }

            double totalPrice = 0.0;
            for (Product p : shop.productMap.values()) {
                totalPrice += p.price;
            }

            double average = totalPrice / shop.productMap.size();


            if (average <= 5.0) {
                return "$";
            } else if (average <= 15.0) {
                return "$$";
            } else {
                return "$$$";
            }
        }



    
=======
    private String computePriceBand(Shop s) {
        if (s.productMap.isEmpty()) return "$";
        double sum = 0;
        for (Product p : s.productMap.values()) {
            sum += p.price;
        }
        double avg = sum / s.productMap.size();
        return avg <= 5   ? "$"
             : avg <= 15  ? "$$"
                          : "$$$";
    }
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748

    static class Shop {
        String storeName, foodCategory, storeLogo;
        double latitude, longitude;
        int starRating, noOfVotes;
        double totalSales = 0;
        Map<String,Product> productMap = new HashMap<>();

        Shop(String storeName, double latitude, double longitude,
             String foodCategory, int starRating, int noOfVotes, String storeLogo)
        {
            this.storeName    = storeName;
            this.latitude     = latitude;
            this.longitude    = longitude;
            this.foodCategory = foodCategory;
            this.starRating   = starRating;
            this.noOfVotes    = noOfVotes;
            this.storeLogo    = storeLogo;
        }

        JSONObject toJson() {
            JSONObject j = new JSONObject()
                .put("StoreName",    storeName)
                .put("Latitude",     latitude)
                .put("Longitude",    longitude)
                .put("FoodCategory", foodCategory)
                .put("Stars",        starRating)
                .put("NoOfVotes",    noOfVotes)
                .put("StoreLogo",    storeLogo)
                .put("TotalSales",   totalSales);
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
            this.name  = name;
            this.type  = type;
            this.price = price;
            this.stock = stock;
        }

        JSONObject toJson() {
            return new JSONObject()
                .put("ProductName",      name)
                .put("ProductType",      type)
                .put("Price",            price)
                .put("Available Amount", stock);
        }
    }
}
