package com.example.efood_ui.SignIn;

import android.content.Context; // Import Context for getSharedPreferences
import android.content.Intent;
import android.graphics.Insets;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences; // Import SharedPreferences

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.efood_ui.AccountsHandler;

import com.example.efood_ui.Menu.MainMenuActivity;
import com.example.efood_ui.R;
import com.example.efood_ui.client.MainClientActivity;

import java.io.FileNotFoundException;

public class SignInActivity extends AppCompatActivity {

    EditText usernameSignIn,passwordSignIn;

    AccountsHandler accountsHandler;

    Button login;




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_sign_in),(v,insets)-> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()).toPlatformInsets();
            v.setPadding(systemBars.left,systemBars.top,systemBars.right,systemBars.bottom);
            return insets;
        });
        usernameSignIn = findViewById(R.id.UsernameText);
        passwordSignIn = findViewById(R.id.PasswordText);

        accountsHandler=new AccountsHandler(this);

        login =findViewById(R.id.LoginButton);

        login.setOnClickListener(v -> {
            try {
                onLogin();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });




    }

    public void launchMainMenu(){
        Intent intent = new Intent(SignInActivity.this, MainClientActivity.class);
        startActivity(intent);
        finish();





    }

    public void onError(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    public void onLogin() throws FileNotFoundException {
        String username = usernameSignIn.getText().toString();
        String password = passwordSignIn.getText().toString();

        if(accountsHandler.searchAccount(username,password)){
            launchMainMenu();
        }

        else{

            onError("Wrong username or password!");
        }


    }




}
