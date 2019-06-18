package com.example.flightapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class JoystickActivity extends AppCompatActivity implements Joystick.JoystickListener {

    static Semaphore semaphore = new Semaphore(1);
    DataOutputStream outToServer;
    Socket clientSocket;
    Joystick jsv;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jsv = new Joystick(this);
        setContentView(jsv);
        new Thread(new Runnable() {
            public void run() {
                try {
                    semaphore.acquire();
                    String ip = (String) getIntent().getSerializableExtra("ip");
                    int parsedPort = (int) getIntent().getSerializableExtra("port");
                    clientSocket = new Socket(ip, parsedPort);
                    outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes("Connection Success from Joystick\r\n");
                    semaphore.release();
                } catch (Exception e) {

                }
            }
        }).start();
    }

    @Override
    public void onJoystickMoved(final float xPercent, final float yPercent, int source) {
        /* Sending to server */
        new Thread(new Runnable() {
            public void run() {
                try {   /* set commands */
                    semaphore.acquire();
                    String xStr = Float.toString(xPercent);
                    String yStr = Float.toString(yPercent * -1);
                    if (yPercent == 0){
                        yStr = Float.toString(yPercent);
                    }

                    outToServer.writeBytes("set /controls/flight/aileron " + xStr + "\r\n");
                    outToServer.writeBytes("set /controls/flight/elevator " + yStr + "\r\n");
                    semaphore.release();
                } catch (Exception e) {

                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            new Thread(new Runnable() {
                public void run() { //TODO maybe semaphore needed.
                    try {
                        outToServer.writeBytes("about to close app");
                        clientSocket.close();
                    } catch (Exception e) {
                    }
                }

            }).start();
    }
}
