package com.example.efood_ui.actions;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.efood_ui.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class SearchShopsActivity extends AppCompatActivity {



    private static final double CLIENT_LATITUDE = 37.99;
    private static final double CLIENT_LONGITUDE = 23.73;





    private static final String MASTER_IP = "10.0.2.2";
    private static final int MASTER_PORT = 5000;







    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_shops);



    }



    /*private void searchShops() {
        try {
            JSONObject filter = new JSONObject()
                    .put("Latitude", CLIENT_LATITUDE)
                    .put("Longitude", CLIENT_LONGITUDE);

            String response = sendRequest("SEARCH " + filter.toString());
            JSONArray shops = new JSONArray(response);

            // Get the container from the layout
            LinearLayout container = findViewById(R.id.shopListContainer);
            container.removeAllViews(); // clear any previous content

            for (int i = 0; i < shops.length(); i++) {
                JSONObject s = shops.getJSONObject(i);
                String name     = s.getString("StoreName");
                String category = s.getString("FoodCategory");
                int stars       = s.getInt("Stars");

                JSONArray products = s.getJSONArray("Products");
                double sum = 0;
                for (int j = 0; j < products.length(); j++) {
                    sum += products.getJSONObject(j).getDouble("Price");
                }
                double avg = products.length() > 0 ? sum / products.length() : 0;
                String band = avg <= 5  ? "$" : avg <= 15 ? "$$" : "$$$";

                // Create and style the TextView
                TextView shopView = new TextView(this);
                shopView.setText(String.format("%d) %s - %s - %d★ - %s",
                        i + 1, name, category, stars, band));
                shopView.setTextSize(18);
                shopView.setPadding(0, 16, 0, 16);

                // Optional: Set ID or tag for later use
                shopView.setId(View.generateViewId());

                // Add to the container
                container.addView(shopView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String sendRequest(String request) throws IOException {
        try (
                Socket sock = new Socket(MASTER_IP, MASTER_PORT);
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(sock.getInputStream()))
        ) {
            out.println(request);
            return in.readLine();
        }
    }*/









}


