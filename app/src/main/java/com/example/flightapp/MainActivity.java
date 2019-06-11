package com.example.flightapp;
import java.io.*;
import java.net.*;
import java.util.concurrent.Semaphore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    static Semaphore semaphore = new Semaphore(1);
    DataOutputStream outToServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connectBtn = findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view){

                EditText debug = findViewById(R.id.debugText);
                new Thread(new Runnable(){
                    public void run(){
                        try {
                            EditText ipText = findViewById(R.id.ipText);
                            EditText portText = findViewById(R.id.portText);
                            int parsedPort = Integer.parseInt(portText.getText().toString());
                            String ip = ipText.getText().toString();
                            semaphore.acquire();
                            Socket clientSocket = new Socket(ip, parsedPort);
                            outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.writeBytes("connection success\r\n");
                            semaphore.release();
                        }
                        catch (Exception e){
                        }
                    }
                }).start();
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                semaphore.acquire();
                                outToServer.writeBytes("hello\r\n");
                                sleep(3000);
                                semaphore.release();
                            } catch (Exception e) {

                            }
                        }
                    });
            }
        });
    }
}
