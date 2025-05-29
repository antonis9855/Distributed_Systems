package com.example.client;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient {
    public interface TcpCallback {
        void onResponse(String response);
    }

    public static void sendRequest(String request, TcpCallback callback) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("10.0.2.2", 5000); // Replace with your Master IP
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(request);
                String response = in.readLine();
                socket.close();

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onResponse(response != null ? response.trim() : "")
                );
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onResponse("ERROR: " + e.getMessage())
                );
            }
        }).start();
    }
}
