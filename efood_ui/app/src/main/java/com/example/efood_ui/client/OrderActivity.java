package com.example.efood_ui.client;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.efood_ui.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recycler_view;
    private TextView text_title;
    private ProgressBar progress_bar;
    private LinearLayout layout_cart_summary;

    private TextView text_cart;
    private Button btn_purchase;

    private List<JSONObject> allShops = new ArrayList<>();
    private Set<String> uniqueCategories = new HashSet<>();

    private Map<String, CartItem> cartMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recycler_view = findViewById(R.id.recycler_view);
        text_title = findViewById(R.id.text_title);
        progress_bar = findViewById(R.id.progress_bar);
        layout_cart_summary = findViewById(R.id.layout_cart_summary);

        text_cart = findViewById(R.id.text_cart_summary);
        btn_purchase = findViewById(R.id.btn_purchase);

        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        setupPurchaseButton();
        fetchShops();
    }

    private void setupPurchaseButton() {
        btn_purchase.setOnClickListener(v -> makePurchase());
        updateCartSummary();
    }

    private void updateCartSummary() {
        int totalCount = 0;
        double totalPrice = 0.0;
        for (CartItem item : cartMap.values()) {
            totalCount += item.quantity;
            totalPrice += item.quantity * item.price;
        }
        text_cart.setText("Items: " + totalCount + " | Total: €" + String.format("%.2f", totalPrice));
        btn_purchase.setEnabled(totalCount > 0);
    }

    private void makePurchase() {
        // Build BUY request payload
        try {
            String storeName = (String) recycler_view.getTag(); // storeName was saved as tag
            JSONArray itemsArray = new JSONArray();
            for (CartItem item : cartMap.values()) {
                JSONObject it = new JSONObject();
                it.put("ProductName", item.name);
                it.put("Quantity", item.quantity);
                itemsArray.put(it);
            }
            JSONObject buyReq = new JSONObject();
            buyReq.put("StoreName", storeName);
            buyReq.put("Items", itemsArray);

            TcpClient.sendRequest("BUY " + buyReq.toString(), response -> runOnUiThread(() -> {
                Toast.makeText(this, "Purchase response: " + response, Toast.LENGTH_LONG).show();
                // After purchase, clear cart & refresh menu to update availability
                cartMap.clear();
                updateCartSummary();
                fetchShops(); // re-fetch to update stock on next view
            }));
        } catch (Exception e) {
            Toast.makeText(this, "Purchase failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchShops() {
        progress_bar.setVisibility(View.VISIBLE);

        try {
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
                    allShops.clear();
                    uniqueCategories.clear();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject shop = array.getJSONObject(i);
                        allShops.add(shop);
                        uniqueCategories.add(shop.getString("FoodCategory"));
                    }

                    showCategories();
                } catch (Exception e) {
                    Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                } finally {
                    progress_bar.setVisibility(View.GONE);
                }
            }));

        } catch (Exception e) {
            Toast.makeText(this, "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progress_bar.setVisibility(View.GONE);
        }
    }

    private void showCategories() {
        text_title.setText("Select Food Category:");
        List<String> categories = new ArrayList<>(uniqueCategories);
        recycler_view.setAdapter(new CategoryAdapter(categories, selectedCategory -> showStores(selectedCategory)));
        layout_cart_summary.setVisibility(View.GONE);
    }

    private void showStores(String category) {
        text_title.setText("Select a Store:");
        List<JSONObject> filtered = new ArrayList<>();
        for (JSONObject shop : allShops) {
            if (shop.optString("FoodCategory").equals(category)) {
                filtered.add(shop);
            }
        }
        recycler_view.setAdapter(new StoreAdapter(filtered, this::showMenu));
        layout_cart_summary.setVisibility(View.GONE);
    }

    private void showMenu(JSONObject shopJson) {
        String storeName = shopJson.optString("StoreName");
        text_title.setText("Menu of " + storeName);

        // Save storeName as tag on RecyclerView for purchase later
        recycler_view.setTag(storeName);

        // Extract product list
        List<JSONObject> products = new ArrayList<>();
        JSONArray arr = shopJson.optJSONArray("Products");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                products.add(arr.optJSONObject(i));
            }
        }

        MenuAdapter.OnCartChangeListener cartListener = (item, newQty) -> {
            CartItem existing = cartMap.get(item.name);
            if (existing == null) {
                item.quantity = newQty;
                cartMap.put(item.name, item);
            } else {
                existing.quantity += newQty;
            }
            updateCartSummary();
        };

        recycler_view.setAdapter(new MenuAdapter(products, cartListener));
        layout_cart_summary.setVisibility(View.VISIBLE);
    }
}
