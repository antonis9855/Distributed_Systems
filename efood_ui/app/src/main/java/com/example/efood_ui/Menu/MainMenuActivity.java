package com.example.efood_ui.Menu;

import android.annotation.SuppressLint;
import com.example.efood_ui.actions.SearchShopsActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.efood_ui.R;
import com.example.efood_ui.actions.BuyProductsActivity;
import com.example.efood_ui.actions.RateShopActivity;
import com.example.efood_ui.actions.SearchShopsActivity;
import com.example.efood_ui.view.HomePageActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MainMenuActivity extends AppCompatActivity {

    private static final String MASTER_IP       = "127.0.0.1";
    private static final int    MASTER_PORT     = 5000;
    private static int clientId = 0;
    private static final double CLIENT_LATITUDE  = 37.99;
    private static final double CLIENT_LONGITUDE = 23.73;
    ImageView welcome ;

    Button search_button;
    Button buy_button;

    Button rate_button;

    Button exit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Toast.makeText(this,"MainMenuActivity started",Toast.LENGTH_LONG).show();
        Log.d("DEBUG","MainMenuActivity started");

        EdgeToEdge.enable(this);
        try{
            setContentView(R.layout.mainmenu_activity);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainmenu), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        catch (Exception e){
            Log.e("MainMenuActivity", "Crash in onCreate", e);
            Toast.makeText(this, "Crash: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }





        welcome=findViewById(R.id.imageView);

        search_button=findViewById(R.id.button_search_shops);
        buy_button=findViewById(R.id.button_buy_products);
        rate_button=findViewById(R.id.button_rate_shop);
        exit_button=findViewById(R.id.button_exit);

        search_button.setOnClickListener(v -> launchSearchShops());
        buy_button.setOnClickListener(v-> launchBuyProducts());
        rate_button.setOnClickListener(v-> launchRateShops());
        exit_button.setOnClickListener(v -> exit());




    }


    private void launchSearchShops() {
            Intent intent =new Intent(this, SearchShopsActivity.class);
            startActivity(intent);

    };


    private void launchBuyProducts(){
        Intent intent =new Intent(this, BuyProductsActivity.class);
        startActivity(intent);


    }

    private void launchRateShops(){
        Intent intent =new Intent(this, RateShopActivity.class);
        startActivity(intent);
    }








    public void exit(){
        Intent intent =new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }

}
