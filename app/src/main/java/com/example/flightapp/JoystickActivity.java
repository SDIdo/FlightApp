package com.example.flightapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

/***
 * Joystick activity contains the connection and disconnection functionality
 * additionally it uses a joystick component to send commands to a sever.
 */
public class JoystickActivity extends AppCompatActivity implements Joystick.JoystickListener {

    //Magic Numbers//

    private static final float UPPER_APROX = 0.999f;
    private static final float LOWER_APROX = -0.999f;
    private static final float NEG_Y_RATIO = -1;
    private static final float CENTER = 0;
    private static final String UPPER_BOUND_STR = "1";
    private static final String LOWER_BOUND_STR = "-1";
    private static final String SET_AILERON = "set /controls/flight/aileron";
    private static final String SET_ELEVATOR = "set /controls/flight/elevator";
    private static final String NEW_LINE = "\r\n";


    //Members//
    static Semaphore semaphore = new Semaphore(1);
    DataOutputStream outToServer;
    Socket clientSocket;
    Joystick joystickView;


    /***
     * Upon entering joystick screen connect to the server and start the joystick component
     * @param savedInstanceState - sends saved state to super.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        joystickView = new Joystick(this);
        setContentView(joystickView);
        new Thread(new Runnable() {
            public void run() {
                try {
                    semaphore.acquire();
                    String ip = (String) getIntent().getSerializableExtra("ip");
                    int parsedPort = (int) getIntent().getSerializableExtra("port");
                    clientSocket = new Socket(ip, parsedPort);
                    outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    semaphore.release();
                } catch (Exception e) {
                    onDestroy();
                }
            }
        }).start();
    }

    /***
     * Observes joystick's movements that are normalized between -1 to 1
     * @param xPercent - value of joystick's placement in the x axis
     * @param yPercent - value of joystick's placement in the y axis
     */
    @Override
    public void onJoystickMoved(final float xPercent, final float yPercent) {
        /* Sending to server */
        new Thread(new Runnable() {
            public void run() {
                try {   /* set commands */
                    String xStr = Float.toString(xPercent);
                    String yStr = Float.toString(yPercent * NEG_Y_RATIO);
                    semaphore.acquire();
                    if (xPercent > UPPER_APROX) {
                        xStr = UPPER_BOUND_STR;
                    } else if (xPercent < LOWER_APROX) {
                        xStr = LOWER_BOUND_STR;
                    }
                    if (yPercent > UPPER_APROX) {
                        yStr = LOWER_BOUND_STR;
                    } else if (yPercent < LOWER_APROX) {
                        yStr = UPPER_BOUND_STR;
                    }
                    if (yPercent == CENTER) {
                        yStr = Float.toString(yPercent);
                    }
                    outToServer.writeBytes(SET_AILERON + xStr + NEW_LINE);
                    outToServer.writeBytes(SET_ELEVATOR + yStr + NEW_LINE);
                    semaphore.release();
                } catch (Exception e) {
                }
            }
        }).start();
    }

    /***
     * Upon exiting app and/or pressing the back button disconnect from server.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            public void run() {
                try {
                    clientSocket.close();
                }
                catch (Exception e){

                }
            }
        }).start();
    }
}