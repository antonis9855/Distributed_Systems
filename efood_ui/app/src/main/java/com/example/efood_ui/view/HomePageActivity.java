package com.example.efood_ui.view;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.efood_ui.R;
import com.example.efood_ui.SignIn.SignInActivity;
import com.example.efood_ui.SignUp.SignUpActivity;

public class HomePageActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v,insets)->{
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left,systemBars.top,systemBars.right,systemBars.bottom);
            return insets;
        });

        Button signInButton= findViewById(R.id.SignInButton);
        Button signUpButton = findViewById(R.id.SignUpButton);
        ImageView imageView = findViewById(R.id.FoodNowLogo);

        signInButton.setOnClickListener(v->signInActivation());
        signUpButton.setOnClickListener(v->signUpActivation());
        imageView.setOnClickListener(v->imageRotation(imageView));

    }


    public void signInActivation(){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    public void signUpActivation(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void imageRotation(View imageView){
        ObjectAnimator rotate = ObjectAnimator.ofFloat(imageView,"rotationY",0f,360f);
        rotate.setDuration(100);
        rotate.start();
    }

}
