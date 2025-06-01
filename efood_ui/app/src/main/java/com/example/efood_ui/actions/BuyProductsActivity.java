package com.example.efood_ui.actions;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.efood_ui.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BuyProductsActivity extends AppCompatActivity {




    private static final String MASTER_IP = "127.0.0.1";
    private static final int MASTER_PORT = 5000;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
       // setContentView(R.layout.activity_buy_products);



    }

    /*private static void buy(Scanner scanner) {
        System.out.println("[DEBUG] Entering method: void");
        try {
            System.out.print("Store name: ");
            //String storeName = scanner.nextLine().trim();

            System.out.print("Number of items: ");
            int itemCount = Integer.parseInt(scanner.nextLine().trim());
            JSONArray items = new JSONArray();

            for (int i = 0; i < itemCount; i++) {
                System.out.print("  Product name: ");
                String productName = scanner.nextLine().trim();

                System.out.print("  Quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine().trim());

                items.put(new JSONObject()
                        .put("ProductName", productName)
                        .put("Quantity",    quantity));
            }

            JSONObject requestJson = new JSONObject()
                    .put("StoreName", storeName)
                    .put("Items",     items);

            String response = sendRequest("BUY " + requestJson);


            if (!response.startsWith("{")) {
                System.out.println("Buy response (not JSON): " + response);
                return;
            }

            JSONObject result = new JSONObject(response);
            System.out.println("Purchase status: " + result.getString("status"));
            for (String key : result.keySet()) {
                System.out.println("[DEBUG] Entering method: for");
                if (!"status".equals(key)) {
                    System.out.printf("  %s: %s%n", key, result.get(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private static String sendRequest(String request) {
        System.out.println("[DEBUG] Entering method: String");
        try (Socket socket = new Socket(MASTER_IP, MASTER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request);
            return in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }



}
