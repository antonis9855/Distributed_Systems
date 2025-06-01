package com.example.efood_ui.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.efood_ui.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    public interface OnStoreClickListener {
        void onStoreClick(JSONObject storeJson);
    }

    private final List<JSONObject> stores;
    private final OnStoreClickListener listener;

    public StoreAdapter(List<JSONObject> stores, OnStoreClickListener listener) {
        this.stores = stores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_store, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.ViewHolder holder, int position) {
        JSONObject shop = stores.get(position);
        String name = shop.optString("StoreName", "Unknown");
        int stars = shop.optInt("Stars", 0);

        // Compute price tier based on average price of products
        String priceTier = "$";
        JSONArray products = shop.optJSONArray("Products");
        if (products != null && products.length() > 0) {
            double sum = 0;
            for (int i = 0; i < products.length(); i++) {
                JSONObject p = products.optJSONObject(i);
                if (p != null) {
                    sum += p.optDouble("Price", 0.0);
                }
            }
            double avg = sum / products.length();
            if (avg <= 5) priceTier = "$";
            else if (avg <= 15) priceTier = "$$";
            else priceTier = "$$$";
        }

        holder.text_store_name.setText(name);
        holder.text_price_tier.setText(priceTier);
        holder.text_stars.setText(stars + "★");

        holder.itemView.setOnClickListener(v -> listener.onStoreClick(shop));
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_store_name;
        TextView text_price_tier;
        TextView text_stars;

        ViewHolder(View itemView) {
            super(itemView);
            text_store_name = itemView.findViewById(R.id.text_store_name);
            text_price_tier = itemView.findViewById(R.id.text_price_tier);
            text_stars = itemView.findViewById(R.id.text_stars);
        }
    }
}
