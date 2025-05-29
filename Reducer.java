// Reducer.java
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class Reducer {
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
        }
    }
}
