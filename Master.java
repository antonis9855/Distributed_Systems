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
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                String requestLine = reader.readLine();
                if (requestLine == null) return;

                String[] parts   = requestLine.split(" ", 2);
                String  command = parts[0];
                String  payload = parts.length > 1 ? parts[1].trim() : "";

                switch (command) {
                    case "SEARCH":
                        handleSearch(requestLine, writer);
                        break;

                    case "TOTAL_SALES_PER_PRODUCT":
                    case "TOTAL_SALES_BY_STORE_TYPE":
                    case "TOTAL_SALES_BY_PRODUCT_CATEGORY":
                        handleAggregate(command, payload, writer);
                        break;

                    case "ADD_SHOP":
                    case "ADD_ITEM":
                    case "REMOVE_ITEM":
                    case "RESTOCK":
                    case "BUY":
                        handleWrite(command, payload, writer);
                        break;

                    default:
                        writer.println("ERROR Unknown command");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { socket.close(); }
                catch (IOException ignored) {}
            }
        }

        private void handleSearch(String request, PrintWriter writer) {
            List<String> mapResults = new ArrayList<>();
            for (int i = 0; i < WORKER_HOSTS.length; i++) {
                mapResults.add(sendToWorker(i, request));
            }
            String reduced = sendToReducer("SEARCH", mapResults);
            writer.println(reduced);
        }

        private void handleAggregate(String command, String payload, PrintWriter writer) {
            List<String> mapResults = new ArrayList<>();
            for (int i = 0; i < WORKER_HOSTS.length; i++) {
                String workerRequest = command + (payload.isEmpty() ? "" : " " + payload);
                mapResults.add(sendToWorker(i, workerRequest));
            }
            String reduceCommand;
            if ("TOTAL_SALES_PER_PRODUCT".equals(command)) {
                reduceCommand = "AGG_PRODUCT";
            } else if ("TOTAL_SALES_BY_STORE_TYPE".equals(command)) {
                reduceCommand = "AGG_STORE";
            } else {
                reduceCommand = "AGG_PROD";
            }
            String reduced = sendToReducer(reduceCommand, mapResults);
            writer.println(reduced);
        }

        private void handleWrite(String command, String payload, PrintWriter writer) {
            try {
                JSONObject json = new JSONObject(payload);
                String storeName = json.getString("StoreName");
                List<Integer> replicas = getReplicaIndices(storeName);
                boolean ok = false;
                for (int idx : replicas) {
                    String response = sendToWorker(idx, command + " " + payload);
                    if ("OK".equals(response)) {
                        ok = true;
                    }
                }
                writer.println(ok ? "OK" : "ERROR Replication failed");
            } catch (JSONException e) {
                writer.println("ERROR Invalid JSON");
            }
        }

        private List<Integer> getReplicaIndices(String storeName) {
            int n = WORKER_HOSTS.length;
            int primary = Math.abs(storeName.hashCode()) % n;
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < REPLICA_COUNT; i++) {
                list.add((primary + i) % n);
            }
            return list;
        }

        private String sendToWorker(int index, String message) {
            String host = WORKER_HOSTS[index];
            int port     = WORKER_BASE_PORT + index;
            try (Socket sock = new Socket(host, port);
                 PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                 BufferedReader in  = new BufferedReader(new InputStreamReader(sock.getInputStream()))) {
                out.println(message);
                return in.readLine();
            } catch (IOException e) {
                return "";
            }
        }

        private String sendToReducer(String reduceCommand, List<String> parts) {
            try (Socket sock = new Socket(REDUCER_HOST, REDUCER_PORT);
                 PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                 BufferedReader in  = new BufferedReader(new InputStreamReader(sock.getInputStream()))) {
                out.println(reduceCommand);
                out.println(parts.size());
                for (int i = 0; i < parts.size(); i++) {
                    String part = parts.get(i);
                    out.println(i + " " + (part == null ? "" : part));
                }
                return in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return reduceCommand.equals("SEARCH") ? "[]" : "{}";
            }
        }
    }
}
