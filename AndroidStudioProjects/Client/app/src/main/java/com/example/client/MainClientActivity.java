package com.example.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainClientActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client);

        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnBuy = findViewById(R.id.btnBuy);
        Button btnRate = findViewById(R.id.btnRate);

        btnSearch.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));

        btnBuy.setOnClickListener(v ->
                startActivity(new Intent(this, BuyActivity.class)));

        btnRate.setOnClickListener(v ->
                startActivity(new Intent(this, RateActivity.class)));
    }
}
