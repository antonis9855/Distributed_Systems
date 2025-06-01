package com.example.efood_ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.efood_ui.R;

public class MainClientActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client);

        Button btnOrder = findViewById(R.id.btn_order);
        Button btnRate = findViewById(R.id.btn_rate);

        btnOrder.setOnClickListener(v ->
                startActivity(new Intent(MainClientActivity.this, OrderActivity.class))
        );

        btnRate.setOnClickListener(v ->
                startActivity(new Intent(MainClientActivity.this, RateActivity.class))
        );
    }
}
