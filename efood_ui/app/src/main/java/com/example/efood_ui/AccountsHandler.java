package com.example.efood_ui;

import android.content.Context;

import androidx.activity.SystemBarStyle;

import com.example.efood_ui.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class AccountsHandler{


    File file;
    Scanner scanner;
    PrintWriter printWriter;


    public AccountsHandler(Context context) {
        try {
            file=new File(context.getFilesDir(),"accounts.csv");
            InputStream inputStream = context.getResources().openRawResource(R.raw.accounts);
            scanner = new Scanner(inputStream);
            scanner.useDelimiter(",|\n");
        } catch (Exception e) {
            e.printStackTrace();
            scanner = null; // prevent future crashes
        }
    }




    public void addAccount(String username,String password) throws IOException {
        FileWriter fileWriter = new FileWriter(file,true);
        String data=username+"," + password;
        fileWriter.append(data +"\n");
        fileWriter.close();



    }

    public boolean checkEmail(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();


    }





    public boolean searchUsername(String username) throws IOException {
        while(scanner.hasNextLine()){
            String myline=scanner.nextLine();
            String[] string_array=myline.split(",");
            if(string_array[0].equals(username)){
                return true;
            }

        }

        return false;
    }
    public boolean searchAccount(String username,String password) throws FileNotFoundException {
        while(scanner.hasNextLine()){
            String myline=scanner.nextLine();
            String[] string_array=myline.split(",");
            if(string_array[0].equals(username) &&string_array[1].equals(password)){
                return true;
            }

        }

        return false;






    }





}
