// Reducer.java
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class Reducer {
<<<<<<< HEAD
    private static final int EXPECTED_RESULTS = 3; 
    private static final int REDUCER_PORT = 7000;
    private static final int MASTER_RESULT_PORT = 7001; 

    public static void main(String[] args) {
        System.out.println("[DEBUG] Entering method: void");
        Map<String, List<String>> resultBuffer = new HashMap<>();
        Map<String, Integer> resultCount = new HashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(REDUCER_PORT)) {
            System.out.println("Reducer started on port " + REDUCER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                new Thread(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        String reduceCommand = in.readLine();
                        int count = Integer.parseInt(in.readLine().trim());

                        List<String> parts = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            String line = in.readLine();
                            parts.add(line);
                        }

                        synchronized (resultBuffer) {
                            resultBuffer.putIfAbsent(reduceCommand, new ArrayList<>());
                            resultBuffer.get(reduceCommand).addAll(parts);

                            resultCount.put(reduceCommand, resultCount.getOrDefault(reduceCommand, 0) + count);

                            if (resultCount.get(reduceCommand) >= EXPECTED_RESULTS) {
                                
                                String output = reduce(reduceCommand, resultBuffer.get(reduceCommand));
                                sendResultToMaster(output);
                                resultBuffer.remove(reduceCommand);
                                resultCount.remove(reduceCommand);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String reduce(String command, List<String> parts) {
        System.out.println("[DEBUG] Entering method: String");
        if ("SEARCH".equals(command)) {
            JSONArray merged = new JSONArray();
            for (String part : parts) {
        System.out.println("[DEBUG] Entering method: for");
                if (part.isEmpty()) continue;
                int sep = part.indexOf(' ');
                String json = part.substring(sep + 1);
                JSONArray arr = new JSONArray(json);
                for (int j = 0; j < arr.length(); j++) {
                    merged.put(arr.get(j));
                }
            }
            return merged.toString();
        } else {
            JSONObject mergedObj = new JSONObject();
            double totalSum = 0;
            boolean includeTotal = command.equals("AGG_STORE") || command.equals("AGG_PROD");

            for (String part : parts) {
        System.out.println("[DEBUG] Entering method: for");
                if (part.isEmpty()) continue;
                int sep = part.indexOf(' ');
                String json = part.substring(sep + 1);
                JSONObject obj = new JSONObject(json);
                for (String key : obj.keySet()) {
        System.out.println("[DEBUG] Entering method: for");
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
            return mergedObj.toString();
        }
    }

    private static void sendResultToMaster(String output) {
        System.out.println("[DEBUG] Entering method: void");
        try (Socket socket = new Socket("127.0.0.1", MASTER_RESULT_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(output);
        } catch (IOException e) {
            e.printStackTrace();
=======
    static final int REDUCER_PORT = 7000;
    private static List<JSONArray>  searchMaps    = Collections.synchronizedList(new ArrayList<>());
    private static List<JSONObject> aggregateMaps = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws Exception {
        System.out.println("Reducer listening on port " + REDUCER_PORT);
        ServerSocket serverSocket = new ServerSocket(REDUCER_PORT);
        while (true) {
            Socket sock = serverSocket.accept();
            new Thread(() -> handle(sock)).start();
        }
    }

    private static void handle(Socket sock) {
        try (
            BufferedReader in  = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter    out = new PrintWriter(sock.getOutputStream(), true)
        ) {
            String line = in.readLine();
            if (line == null) return;

            String[] parts   = line.split(" ", 2);
            String   cmd     = parts[0];
            String   payload = parts.length > 1 ? parts[1] : "";

            switch (cmd) {
                case "RESET_SEARCH":
                    searchMaps.clear();
                    out.println("OK");
                    break;

                case "MAP_SEARCH":

                    searchMaps.add(new JSONArray(payload));
                    out.println("OK");
                    break;

                case "REDUCE_SEARCH": {
                    
                    JSONArray merged = new JSONArray();
                    Set<String> seen = new HashSet<>();
                    for (JSONArray arr : searchMaps) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject shop = arr.getJSONObject(i);
                            String name = shop.optString("StoreName");
                            if (!seen.contains(name)) {
                                seen.add(name);
                                merged.put(shop);
                            }
                        }
                    }
                    out.println(merged.toString());
                    break;
                }

                case "RESET_AGG":
                    aggregateMaps.clear();
                    out.println("OK");
                    break;

                case "MAP_AGG":
                    aggregateMaps.add(new JSONObject(payload));
                    out.println("OK");
                    break;

                case "REDUCE_AGG": {
                    JSONObject result = new JSONObject();
                    for (JSONObject obj : aggregateMaps) {
                        for (String key : obj.keySet()) {
                            result.put(key, result.optDouble(key, 0) + obj.getDouble(key));
                        }
                    }
                    out.println(result.toString());
                    break;
                }

                default:
                    out.println("ERROR Unknown reducer command");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { sock.close(); } catch (IOException ignored) {}
>>>>>>> 9a44961fdf75d9b87841c6c49eba762a657dd748
        }
    }
}
