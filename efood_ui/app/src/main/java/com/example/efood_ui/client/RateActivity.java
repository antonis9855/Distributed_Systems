package com.example.efood_ui.client;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.efood_ui.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RateActivity extends AppCompatActivity {
    private Spinner spinner_shops;
    private EditText edt_stars;
    private Button btn_submit;
    private ProgressBar progress_bar;

    private List<String> shopNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        spinner_shops = findViewById(R.id.spinner_shops);
        edt_stars = findViewById(R.id.edt_stars);
        btn_submit = findViewById(R.id.btn_submit);
        progress_bar = findViewById(R.id.progress_bar);

        setupSubmitButton();
        fetchShops(); // populate spinner
    }

    private void setupSubmitButton() {
        btn_submit.setOnClickListener(v -> sendRating());
        btn_submit.setEnabled(false);
    }

    private void fetchShops() {
        progress_bar.setVisibility(View.VISIBLE);

        try {
            // Send SEARCH with no filters to get all shops
            JSONObject filter = new JSONObject();
            filter.put("Latitude", 37.99);
            filter.put("Longitude", 23.73);
            filter.put("FoodCategory", "");
            filter.put("MinStars", 1);
            filter.put("PriceBands", new JSONArray());

            String request = "SEARCH " + filter.toString();
            TcpClient.sendRequest(request, response -> runOnUiThread(() -> {
                try {
                    JSONArray array = new JSONArray(response);
                    shopNames.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject shop = array.getJSONObject(i);
                        shopNames.add(shop.getString("StoreName"));
                    }
                    setupSpinner();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load shops", Toast.LENGTH_SHORT).show();
                } finally {
                    progress_bar.setVisibility(View.GONE);
                }
            }));
        } catch (Exception e) {
            Toast.makeText(this, "Search error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progress_bar.setVisibility(View.GONE);
        }
    }

    private void setupSpinner() {
        if (shopNames.isEmpty()) {
            Toast.makeText(this, "No shops available", Toast.LENGTH_SHORT).show();
            btn_submit.setEnabled(false);
            return;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                shopNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_shops.setAdapter(adapter);
        btn_submit.setEnabled(true);
    }

    private void sendRating() {
        String storeName = (String) spinner_shops.getSelectedItem();
        String starsText = edt_stars.getText().toString().trim();

        if (storeName == null || storeName.isEmpty()) {
            Toast.makeText(this, "No store selected", Toast.LENGTH_SHORT).show();
            return;
        }
        int stars;
        try {
            stars = Integer.parseInt(starsText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Stars must be a number 1–5", Toast.LENGTH_SHORT).show();
            return;
        }
        if (stars < 1 || stars > 5) {
            Toast.makeText(this, "Stars must be between 1 and 5", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject rateReq = new JSONObject();
            rateReq.put("StoreName", storeName);
            rateReq.put("Stars", stars);

            btn_submit.setEnabled(false);
            progress_bar.setVisibility(View.VISIBLE);

            TcpClient.sendRequest(
                    "RATE " + rateReq.toString(),
                    response -> runOnUiThread(() -> {
                        progress_bar.setVisibility(View.GONE);
                        btn_submit.setEnabled(true);
                        Toast.makeText(
                                RateActivity.this,
                                "Rating response: " + response,
                                Toast.LENGTH_LONG
                        ).show();
                    })
            );
        } catch (Exception e) {
            Toast.makeText(this, "Failed to build request", Toast.LENGTH_SHORT).show();
        }
    }
}

