
import java.io.*;
import java.net.*;
import org.json.*;

public class Worker {
    private static final int PORT = 6000;
    
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Worker started on port " + PORT);
            while (true) {
                Socket socket = server.accept();
                new WorkerHandler(socket).start();
            }
        } catch (IOException e) {
            System.out.println("Worker error: " + e.getMessage());
        }
    }
}

class WorkerHandler extends Thread {
    private Socket socket;
    public WorkerHandler(Socket socket) { this.socket = socket; }
    
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String cmdLine = in.readLine();
            System.out.println("Worker received: " + cmdLine);
            String response = processCommand(cmdLine);
            out.println(response);
        } catch (IOException e) {
            System.out.println("WorkerHandler error: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ex) { }
        }
    }
    
    // Process commands: ADD_STORE, ADD_PRODUCT, REMOVE_PRODUCT, DISPLAY_DATA
    private String processCommand(String cmdLine) {
        if (cmdLine == null || cmdLine.isEmpty()) return "Empty command";
        String[] parts = cmdLine.split(" ", 2);
        String command = parts[0];
        String params = parts.length > 1 ? parts[1] : "";
        try {
            if ("ADD_STORE".equals(command)) {
                return addStore(params);
            } else if ("ADD_PRODUCT".equals(command)) {
                return addProduct(params);
            } else if ("REMOVE_PRODUCT".equals(command)) {
                return removeProduct(params);
            } else if ("DISPLAY_DATA".equals(command)) {
                return displayData(params);
            } else {
                return "Unknown command";
            }
        } catch (Exception e) {
            return "Error processing command: " + e.getMessage();
        }
    }
    
    private String getStoreFileName(String storeName) {
        return "store_" + storeName + ".json";
    }
    
    // Expects a JSON string with a "StoreName" field.
    private String addStore(String jsonData) throws Exception {
        JSONObject store = new JSONObject(jsonData);
        String storeName = store.getString("StoreName");
        try (FileWriter fw = new FileWriter(getStoreFileName(storeName))) {
            fw.write(store.toString());
        }
        return "Store " + storeName + " added.";
    }
    
    // Expects: storeName productName price availableAmount
    private String addProduct(String params) throws Exception {
        String[] tokens = params.split(" ");
        if (tokens.length < 4) return "Invalid parameters for ADD_PRODUCT";
        String storeName = tokens[0];
        String productName = tokens[1];
        double price = Double.parseDouble(tokens[2]);
        int available = Integer.parseInt(tokens[3]);
        String fileName = getStoreFileName(storeName);
        File file = new File(fileName);
        if (!file.exists()) return "Store not found: " + storeName;
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        JSONObject store = new JSONObject(content);
        JSONArray products = store.has("Products") ? store.getJSONArray("Products") : new JSONArray();
        JSONObject product = new JSONObject();
        product.put("ProductName", productName);
        product.put("Price", price);
        product.put("AvailableAmount", available);
        products.put(product);
        store.put("Products", products);
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(store.toString());
        }
        return "Product " + productName + " added to " + storeName;
    }
    
    // Expects: storeName productName
    private String removeProduct(String params) throws Exception {
        String[] tokens = params.split(" ");
        if (tokens.length < 2) return "Invalid parameters for REMOVE_PRODUCT";
        String storeName = tokens[0];
        String productName = tokens[1];
        String fileName = getStoreFileName(storeName);
        File file = new File(fileName);
        if (!file.exists()) return "Store not found: " + storeName;
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        JSONObject store = new JSONObject(content);
        if (!store.has("Products")) return "No products in " + storeName;
        JSONArray products = store.getJSONArray("Products");
        JSONArray newProducts = new JSONArray();
        boolean removed = false;
        for (int i = 0; i < products.length(); i++) {
            JSONObject prod = products.getJSONObject(i);
            if (prod.getString("ProductName").equals(productName)) {
                removed = true;
            } else {
                newProducts.put(prod);
            }
        }
        if (!removed) return "Product " + productName + " not found in " + storeName;
        store.put("Products", newProducts);
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(store.toString());
        }
        return "Product " + productName + " removed from " + storeName;
    }
    
    // Expects: storeName
    private String displayData(String storeName) throws Exception {
        String fileName = getStoreFileName(storeName.trim());
        File file = new File(fileName);
        if (!file.exists()) return "Store not found: " + storeName;
        return new String(java.nio.file.Files.readAllBytes(file.toPath()));
    }
}