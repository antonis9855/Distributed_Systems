package com.example.efood_ui.client;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.efood_ui.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity {
    TextView txtResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        txtResults = findViewById(R.id.txtResults);
        fetchAllShops();
    }

    private void fetchAllShops() {
        try {
            JSONObject filter = new JSONObject();
            filter.put("Latitude", 37.99);
            filter.put("Longitude", 23.73);
            filter.put("FoodCategory", "");
            filter.put("MinStars", 1);
            filter.put("PriceBands", new JSONArray()); // empty array = no filtering

            String request = "SEARCH " + filter.toString();

            TcpClient.sendRequest(request, response -> {
                try {
                    JSONArray shops = new JSONArray(response);
                    StringBuilder result = new StringBuilder("Shops:\n");
                    for (int i = 0; i < shops.length(); i++) {
                        JSONObject shop = shops.getJSONObject(i);
                        result.append(String.format("• %s (%s) - %d★\n",
                                shop.getString("StoreName"),
                                shop.getString("FoodCategory"),
                                shop.getInt("Stars")));
                    }
                    txtResults.setText(result.toString());
                } catch (Exception e) {
                    txtResults.setText("Error: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            txtResults.setText("Failed to build request.");
        }
    }
}
