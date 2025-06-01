package com.example.efood_ui.SignUp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.efood_ui.AccountsHandler;
import com.example.efood_ui.R;
import com.example.efood_ui.SignIn.SignInActivity;
import com.example.efood_ui.client.MainClientActivity;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SignUpActivity  extends AppCompatActivity {

    Button confirm;

    EditText username,password,email;

    AccountsHandler accountsHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_sign_up), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        confirm = findViewById(R.id.button);

        username =findViewById(R.id.username);

        password=findViewById(R.id.password);
        email=findViewById(R.id.email);

        confirm.setOnClickListener(v-> {
            try {
                onConfirm();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        accountsHandler =new AccountsHandler(this);


    }

    public void onError(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    public void launchMainMenu(){
        Intent intent = new Intent(SignUpActivity.this, MainClientActivity.class);
        startActivity(intent);
        finish();





    }

    public void onConfirm() throws IOException {

        String my_username = username.getText().toString();
        String my_email=email.getText().toString();
        String my_password=password.getText().toString();

       if(accountsHandler.checkEmail(my_email) && !accountsHandler.searchUsername(my_username)){
                accountsHandler.addAccount(my_username,my_password);
                launchMainMenu();



       }
       else{
           onError("Invalid username or email!");
       }

    }
}
