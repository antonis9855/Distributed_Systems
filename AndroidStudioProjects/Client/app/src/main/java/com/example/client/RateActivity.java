package com.example.client;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class RateActivity extends AppCompatActivity {
    EditText edtStoreName, edtStars;
    Button btnRate;
    TextView txtRateResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        edtStoreName = findViewById(R.id.edtStoreName);
        edtStars = findViewById(R.id.edtStars);
        btnRate = findViewById(R.id.btnRate);
        txtRateResult = findViewById(R.id.txtRateResult);

        btnRate.setOnClickListener(v -> sendRating());
    }

    private void sendRating() {
        try {
            JSONObject req = new JSONObject()
                    .put("StoreName", edtStoreName.getText().toString().trim())
                    .put("Stars", Integer.parseInt(edtStars.getText().toString().trim()));

            TcpClient.sendRequest("RATE " + req.toString(), response ->
                    txtRateResult.setText("Server: " + response));
        } catch (Exception e) {
            txtRateResult.setText("Invalid input.");
        }
    }
}
