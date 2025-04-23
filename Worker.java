import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class Worker {
    private final int port;
    private final Map<String,Shop> shops = Collections.synchronizedMap(new HashMap<>());

    public Worker(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Worker <port>");
            System.exit(1);
        }
        new Worker(Integer.parseInt(args[0])).run();
    }

    public void run() throws Exception {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Worker listening on port " + port);
        while (true) {
            Socket sock = server.accept();
            new Thread(() -> handle(sock)).start();
        }
    }

    private void handle(Socket sock) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true)
        ) {
            String line = in.readLine();
            if (line == null) return;

            String[] parts   = line.split(" ", 2);
            String   command = parts[0];
            String   payload = parts.length > 1 ? parts[1] : "";

            switch (command) {
              case "ADD_SHOP":
                addShop(new JSONObject(payload));
                out.println("OK");
                break;

              case "ADD_ITEM": {
                String[] p = payload.split(" ", 2);
                addItem(p[0], new JSONObject(p[1]));
                out.println("OK");
                break;
              }

              case "REMOVE_ITEM": {
                String[] p = payload.split(" ", 2);
                removeItem(p[0], p[1]);
                out.println("OK");
                break;
              }

              case "RESTOCK": {
                String[] p = payload.split(" ", 3);
                restock(p[0], p[1], Integer.parseInt(p[2]));
                out.println("OK");
                break;
              }

              case "SEARCH":
                out.println(search(new JSONObject(payload)).toString());
                break;

              case "BUY":
                out.println(buy(new JSONObject(payload)).toString());
                break;

              default:
                out.println("ERROR Unknown command");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}

class Shop{
    String name;
    Map<String,Product> items = new HashMap<>();
    double latitude;
    double longitude;
    String foodCategory;
    int stars;
    int noOfVotes;
    String storeLogo;
    double totalSales = 0;

    public Shop(String name, Map<String, Product> items, double latitude, double longitude, String foodCategory, int stars, int noOfVotes, String storeLogo) {
        this.name = name;
        this.items = items;
        this.latitude = latitude;
        this.longitude = longitude;
        this.foodCategory = foodCategory;
        this.stars = stars;
        this.noOfVotes = noOfVotes;
        this.storeLogo = storeLogo;
    }

    JSONObject.toJson(){
        JSONObject j = new JSONObject();
    }

    j.put("StoreName: " , this.name);
    j.put("Latitude: " , this.latitude);
    j.put("Longitude: " , this.longitude);
    j.put("FoodCategory: " , this.foodCategory);
    j.put("Stars: " , this.stars);
    j.put("NoOfVotes: " , this.noOfVotes);
    j.put("StoreLogo: " , this.storeLogo);













}







class Product{
    String name;
    String type;
    int amount;
    double price;



}

