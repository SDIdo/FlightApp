package com.example.flightapp;
import java.util.concurrent.Semaphore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    static Semaphore semaphore = new Semaphore(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connectBtn = findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view){
                new Thread(new Runnable(){
                    public void run(){
                        try {
                            EditText ipText = findViewById(R.id.ipText);
                            EditText portText = findViewById(R.id.portText);
                            int parsedPort = Integer.parseInt(portText.getText().toString());
                            String ip = ipText.getText().toString();
                            Intent intent = new Intent(getApplicationContext(), JoystickActivity.class);
                            intent.putExtra("ip", ip);
                            intent.putExtra("port", parsedPort);
                            startActivity(intent);
                        }
                        catch (Exception e){
                        }
                    }
                }).start();
            }
        });
    }
}
