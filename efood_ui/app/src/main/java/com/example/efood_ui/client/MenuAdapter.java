package com.example.efood_ui.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.efood_ui.R;

import org.json.JSONObject;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    public interface OnCartChangeListener {
        void onCartChange(CartItem item, int newQuantity);
    }

    private final List<JSONObject> products;
    private final OnCartChangeListener listener;

    public MenuAdapter(List<JSONObject> products, OnCartChangeListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position) {
        JSONObject prod = products.get(position);
        String name = prod.optString("ProductName", "Unknown");
        double price = prod.optDouble("Price", 0.0);
        int available = prod.optInt("Available Amount", 0);

        holder.text_product_name.setText(name);
        holder.text_price.setText(String.format("€%.2f", price));
        holder.text_available.setText("In stock: " + available);

        holder.btn_add.setEnabled(available > 0);
        holder.btn_add.setOnClickListener(v -> {
            CartItem item = new CartItem(name, price, available);
            listener.onCartChange(item, 1);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_product_name, text_price, text_available;
        Button btn_add;

        ViewHolder(View itemView) {
            super(itemView);
            text_product_name = itemView.findViewById(R.id.text_product_name);
            text_price = itemView.findViewById(R.id.text_price);
            text_available = itemView.findViewById(R.id.text_available);
            btn_add = itemView.findViewById(R.id.btn_add);
        }
    }
}
