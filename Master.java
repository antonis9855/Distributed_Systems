// Master.java
import java.io.*;
import java.net.*;
import org.json.*;

public class Master {
    static final int masterPort       = 5000;
    static final String[] workerHosts = {
        "127.0.0.1",
        "127.0.0.1",
        "127.0.0.1"
    };
    static final int workerBasePort   = 6000;
    static final String reducerHost   = "127.0.0.1";
    static final int reducerPort      = 7000;

    public static void main(String[] args) {
        System.out.println("Master started on port " + masterPort);
        try (ServerSocket serverSocket = new ServerSocket(masterPort)) {
            while (true) {
                Socket managerSocket = serverSocket.accept();
                new Thread(new ManagerHandler(managerSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ManagerHandler implements Runnable {
        private final Socket managerSocket;

        ManagerHandler(Socket socket) {
            this.managerSocket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(managerSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(
                    managerSocket.getOutputStream(), true)
            ) {
                String request = reader.readLine();
                if (request == null) return;

                String[] parts   = request.split(" ", 2);
                String   command = parts[0];
                String   payload = parts.length > 1 ? parts[1].trim() : "";

                if ("SEARCH".equals(command)) {
                    String merged = handleSearch(request);
                    writer.println(merged != null ? merged : "[]");

                } else if (
                    "TOTAL_SALES_PER_PRODUCT".equals(command) ||
                    "TOTAL_SALES_BY_STORE_TYPE".equals(command) ||
                    "TOTAL_SALES_BY_PRODUCT_CATEGORY".equals(command)
                ) {
                    String merged = handleAggregate(command, payload);
                    writer.println(merged != null ? merged : "{}");

                } else if (
                    "ADD_SHOP".equals(command)    ||
                    "ADD_ITEM".equals(command)    ||
                    "REMOVE_ITEM".equals(command) ||
                    "RESTOCK".equals(command)     ||
                    "BUY".equals(command)         ||
                    "RATE".equals(command)
                ) {
                   
                    String storeName = new JSONObject(payload).getString("StoreName");
                    int idx = Math.abs(storeName.hashCode()) % Master.workerHosts.length;
                    String host = Master.workerHosts[idx];
                    int    port = Master.workerBasePort + idx;
                    String resp = talk(host, port, request);
                    writer.println(resp != null ? resp : "ERROR");

                } else {
                    writer.println("ERROR Unknown command");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { managerSocket.close(); } catch (IOException ignored) {}
            }
        }

        private String handleSearch(String request) {
            // clear previous
            talkReducer("RESET_SEARCH", "");
            // map phase on all workers
            for (int i = 0; i < Master.workerHosts.length; i++) {
                talk(Master.workerHosts[i], Master.workerBasePort + i, request);
            }
            // reduce phase
            return talkReducer("REDUCE_SEARCH", "");
        }

        private String handleAggregate(String command, String payload) {
            talkReducer("RESET_AGG", "");
            String msg = command + (payload.isEmpty() ? "" : " " + payload);
            for (int i = 0; i < Master.workerHosts.length; i++) {
                talk(Master.workerHosts[i], Master.workerBasePort + i, msg);
            }
            return talkReducer("REDUCE_AGG", "");
        }

        private String talk(String host, int port, String msg) {
            try (
                Socket sock = new Socket(host, port);
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()))
            ) {
                out.println(msg);
                return in.readLine();
            } catch (IOException e) {
                return null;
            }
        }

        private String talkReducer(String cmd, String payload) {
            String full = payload.isEmpty() ? cmd : cmd + " " + payload;
            try (
                Socket sock = new Socket(Master.reducerHost, Master.reducerPort);
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()))
            ) {
                out.println(full);
                return in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
