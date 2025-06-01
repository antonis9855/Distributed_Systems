package com.example.efood_ui.client;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.efood_ui.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class BuyActivity extends AppCompatActivity {
    EditText edtStoreName, edtItemCount;
    LinearLayout itemInputsLayout;
    Button btnBuy;
    TextView txtBuyResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        edtStoreName = findViewById(R.id.edtStoreName);
        edtItemCount = findViewById(R.id.edtItemCount);
        itemInputsLayout = findViewById(R.id.itemInputsLayout);
        btnBuy = findViewById(R.id.btnBuy);
        txtBuyResult = findViewById(R.id.txtBuyResult);

        edtItemCount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) generateItemInputs();
        });

        btnBuy.setOnClickListener(v -> buyItems());
    }

    private void generateItemInputs() {
        itemInputsLayout.removeAllViews();
        try {
            int count = Integer.parseInt(edtItemCount.getText().toString().trim());
            for (int i = 0; i < count; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                EditText name = new EditText(this);
                name.setHint("Product name");
                name.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                EditText qty = new EditText(this);
                qty.setHint("Qty");
                qty.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                qty.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                row.addView(name);
                row.addView(qty);
                itemInputsLayout.addView(row);
            }
        } catch (NumberFormatException ignored) {}
    }

    private void buyItems() {
        try {
            String store = edtStoreName.getText().toString().trim();
            JSONArray items = new JSONArray();

            for (int i = 0; i < itemInputsLayout.getChildCount(); i++) {
                LinearLayout row = (LinearLayout) itemInputsLayout.getChildAt(i);
                EditText name = (EditText) row.getChildAt(0);
                EditText qty = (EditText) row.getChildAt(1);
                items.put(new JSONObject()
                        .put("ProductName", name.getText().toString().trim())
                        .put("Quantity", Integer.parseInt(qty.getText().toString().trim())));
            }

            JSONObject req = new JSONObject()
                    .put("StoreName", store)
                    .put("Items", items);

            TcpClient.sendRequest("BUY " + req.toString(), response -> txtBuyResult.setText("Server: " + response));
        } catch (Exception e) {
            txtBuyResult.setText("Invalid input.");
        }
    }
}
