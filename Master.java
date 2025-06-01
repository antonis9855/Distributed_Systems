import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class Master {
    public static final int MASTER_PORT       = 5000;
    public static final String[] WORKER_HOSTS = {
        "127.0.0.1", "127.0.0.1", "127.0.0.1"
    };
    public static final int WORKER_BASE_PORT  = 6000;
    public static final String REDUCER_HOST   = "127.0.0.1";
    public static final int REDUCER_PORT      = 7000;
    public static final int REPLICA_COUNT     = 2;

    public static void main(String[] args) {
        System.out.println("[DEBUG] Entering method: void main");
        try (ServerSocket serverSocket = new ServerSocket(MASTER_PORT)) {
            System.out.println("Master started on port " + MASTER_PORT);
            while (true) {
                Socket managerSocket = serverSocket.accept();
                new Thread(new ManagerHandler(managerSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ManagerHandler implements Runnable {
        private final Socket socket;

        ManagerHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
        System.out.println("[DEBUG] Entering method: run void");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter  writer = new PrintWriter(socket.getOutputStream(), true))
            {
                String requestLine = reader.readLine();
                if (requestLine == null) return;

                String[] parts   = requestLine.split(" ", 2);
                String   command = parts[0];
                String   payload = parts.length > 1 ? parts[1].trim() : "";

                switch (command) {
        
                    case "SEARCH":
                        System.out.println("[DEBUG] Handling case SEARCH:");
                        dispatchToWorkers("SEARCH", payload, "SEARCH", writer);
                        break;
        
                    case "TOTAL_SALES_PER_PRODUCT":
                        System.out.println("[DEBUG] Handling case TOTAL_SALES_PER_PRODUCT:");
                        dispatchToWorkers("TOTAL_SALES_PER_PRODUCT", "", "AGG_PRODUCT", writer);
                        break;
        
                    case "TOTAL_SALES_BY_STORE_TYPE":
                        System.out.println("[DEBUG] Handling case TOTAL_SALES_BY_STORE_TYPE:");
                        dispatchToWorkers("TOTAL_SALES_BY_STORE_TYPE", payload, "AGG_STORE", writer);
                        break;
        
                    case "TOTAL_SALES_BY_PRODUCT_CATEGORY":
                        System.out.println("[DEBUG] Handling case TOTAL_SALES_BY_PRODUCT_CATEGORY:");
                        dispatchToWorkers("TOTAL_SALES_BY_PRODUCT_CATEGORY", payload, "AGG_PROD", writer);
                        break;
        
                    case "ADD_SHOP":
                        System.out.println("[DEBUG] Handling case ADD_SHOP:");
        
                    case "ADD_ITEM":
                        System.out.println("[DEBUG] Handling case ADD_ITEM:");
        
                    case "REMOVE_ITEM":
                         System.out.println("[DEBUG] Handling case REMOVE_ITEM:");
        
                    case "RESTOCK":
                        System.out.println("[DEBUG] Handling case RESTOCK:");
        
                    case "BUY":
                        System.out.println("[DEBUG] Handling case BUY:");
                        handleWrite(command, payload, writer);
                        break;
        
                    default:
                        System.out.println("[DEBUG] Handling default:");
                        writer.println("ERROR Unknown command");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { socket.close(); }
                catch (IOException ignored) {}
            }
        }

        private void dispatchToWorkers(String command, String payload, String reduceCommand, PrintWriter writer) {
        System.out.println("[DEBUG] Entering method: void");
            for (int i = 0; i < WORKER_HOSTS.length; i++) {
                String fullPayload = i + " " + payload; // mapId + payload
                sendToWorker(i, command + " " + fullPayload);
            }
            
            String result = receiveFromReducer();
            writer.println(result);
        }

        private String receiveFromReducer() {
        System.out.println("[DEBUG] Entering method: String");
            try (ServerSocket serverSocket = new ServerSocket(REDUCER_PORT + 1); 
                 Socket socket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                return in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return "{}";
            }
        }


        private void handleWrite(String command, String payload, PrintWriter writer) {
            System.out.println("[DEBUG] Entering method: void handleWrite");
            String storeName;
            String fullMessage;
    
            switch (command) {
        
                case "ADD_SHOP":
                    System.out.println("[DEBUG] Handling case ADD_SHOP:");
                case "BUY":
                   System.out.println("[DEBUG] Handling case BUY:");
                    try {
                        JSONObject j = new JSONObject(payload);
                        storeName = j.getString("StoreName");
                    } catch (JSONException e) {
                        writer.println("ERROR Invalid JSON");
                        return;
                    }
                    fullMessage = command + " " + payload;
                    break;
    
        
                case "ADD_ITEM":
                    System.out.println("[DEBUG] Handling case ADD_ITEM:");
                    int brace = payload.indexOf('{');
                    if (brace < 0) {
                        writer.println("ERROR Invalid payload");
                        return;
                    }
                    storeName   = payload.substring(0, brace).trim();
                    String json = payload.substring(brace).trim();
                    fullMessage = "ADD_ITEM " + storeName + " " + json;
                    break;
    
        
                case "REMOVE_ITEM":
                    System.out.println("[DEBUG] Handling case REMOVE_ITEM:");
                    String[] parts = payload.split(" ", 2);
                    if (parts.length < 2) {
                        writer.println("ERROR Invalid payload");
                        return;
                    }
                    storeName   = parts[0];
                    String productName = parts[1].trim();
                    fullMessage = "REMOVE_ITEM " + storeName + " " + productName;
                    break;
    
        
                case "RESTOCK":
                    System.out.println("[DEBUG] Handling case RESTOCK:");
                    int lastSpace = payload.lastIndexOf(' ');
                    if (lastSpace < 0) {
                        writer.println("ERROR Invalid payload");
                        return;
                    }
                    String delta = payload.substring(lastSpace + 1).trim();
                    String left  = payload.substring(0, lastSpace).trim();
                   
                    parts = left.split(" ", 2);
                    if (parts.length < 2) {
                        writer.println("ERROR Invalid payload");
                        return;
                    }
                    storeName     = parts[0];
                    productName   = parts[1].trim();
                    fullMessage   = "RESTOCK " + storeName + " " + productName + " " + delta;
                    break;
    
        
                default:
                    System.out.println("[DEBUG] Handling default:");
                    writer.println("ERROR Unknown write command");
                    return;
            }
    
            List<Integer> replicas = getReplicaIndices(storeName);
            boolean anyOk = false;
            for (int idx : replicas) {
                String resp = sendToWorker(idx, fullMessage);
                if (resp != null && resp.startsWith("{") && resp.contains("\"status\":\"OK\"")) {
                    anyOk = true;
                }
            }
            writer.println(anyOk ? "{\"status\":\"OK\"}" : "{\"status\":\"ERROR\"}");
        }
    

        private List<Integer> getReplicaIndices(String storeName) {
            System.out.println("[DEBUG] Entering method: List<Integer>");
            int n       = WORKER_HOSTS.length;
            int primary = Math.abs(storeName.hashCode()) % n;
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < REPLICA_COUNT; i++) {
                list.add((primary + i) % n);
            }
            return list;
        }

        private String sendToWorker(int index, String message) {
        System.out.println("[DEBUG] Entering method: String");
            String host = WORKER_HOSTS[index];
            int    port = WORKER_BASE_PORT + index;
            try (Socket sock = new Socket(host, port);
                 PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream())))
            {
                out.println(message);
                return in.readLine();
            } catch (IOException e) {
                return "";
            }
        }

    }
}