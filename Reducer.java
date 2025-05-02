import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class Reducer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Reducer <port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Reducer started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handle(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handle(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String command = in.readLine();
            int count     = Integer.parseInt(in.readLine().trim());
            Map<Integer,String> mapResults = new TreeMap<>();
            for (int i = 0; i < count; i++) {
                String line = in.readLine();
                int sep = line.indexOf(' ');
                int mapId = Integer.parseInt(line.substring(0, sep));
                String json = line.substring(sep + 1);
                mapResults.put(mapId, json);
            }
            List<String> ordered = new ArrayList<>(mapResults.values());
            String output;
            if ("SEARCH".equals(command)) {
                JSONArray merged = new JSONArray();
                for (String part : ordered) {
                    if (part.isEmpty()) continue;
                    JSONArray arr = new JSONArray(part);
                    for (int j = 0; j < arr.length(); j++) {
                        merged.put(arr.get(j));
                    }
                }
                output = merged.toString();
            } else {
                boolean includeTotal = "AGG_STORE".equals(command) || "AGG_PROD".equals(command);
                JSONObject mergedObj = new JSONObject();
                double totalSum = 0;
                for (String part : ordered) {
                    if (part.isEmpty()) continue;
                    JSONObject obj = new JSONObject(part);
                    for (String key : obj.keySet()) {
                        double val = obj.getDouble(key);
                        if ("total".equals(key)) {
                            totalSum += val;
                        } else {
                            mergedObj.put(key, mergedObj.optDouble(key, 0) + val);
                        }
                    }
                }
                if (includeTotal) {
                    mergedObj.put("total", totalSum);
                }
                output = mergedObj.toString();
            }
            out.println(output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); }
            catch (IOException ignored) {}
        }
    }
}
