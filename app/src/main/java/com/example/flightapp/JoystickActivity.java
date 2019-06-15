package com.example.flightapp;

import android.app.Activity;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.Console;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import static com.example.flightapp.MainActivity.semaphore;
import static java.lang.Thread.sleep;

public class JoystickActivity extends Activity
        implements View.OnTouchListener, GestureDetector.OnGestureListener, View.OnDragListener {
    static Semaphore semaphore = new Semaphore(1);

    private float xDelta;
    private float yDelta;
    private ViewGroup joystickLayout;
    private ImageView image;

    DataOutputStream outToServer;
    Socket clientSocket;
    private GestureDetector mGestureDetector;
    private static final String TAG = "JoyStick"; //TODO get rid of.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetector = new GestureDetector(this, this);

        setContentView(R.layout.activity_joystick);

        joystickLayout = (RelativeLayout) findViewById(R.id.joystickActivity);
        image = (ImageView) findViewById(R.id.joyKbob);

        image.setOnTouchListener(this);

//        new Thread(new Runnable() {
//            public void run() {
//
//                try {
//                    semaphore.acquire();
//                    String ip = (String) getIntent().getSerializableExtra("ip");
//                    int parsedPort = (int) getIntent().getSerializableExtra("port");
//                    clientSocket = new Socket(ip, parsedPort);
//                    outToServer = new DataOutputStream(clientSocket.getOutputStream());
//                    outToServer.writeBytes("Connection Success from Joystick\r\n");
//                    semaphore.release();
//                } catch (Exception e) {
//
//                }
//            }
//        }).start();

//
//        ImageView joyKnob = findViewById(R.id.joyKbob);
//        joyKnob.setOnTouchListener(this);

//        connectBtn2.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                new Thread(new Runnable() {
//                    public void run() {
//                        try {
//                            semaphore.acquire();
//                            outToServer.writeBytes("Command From Joystick\r\n");
//                            semaphore.release();
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }).start();
//            }
//        });
    }

//    protected void onDestroy() {
//        super.onDestroy();
//        new Thread(new Runnable() {
//            public void run() { //TODO maybe semaphore needed.
//                try {
//                    outToServer.writeBytes("about to close app");
//                    clientSocket.close();
//                } catch (Exception e) {
//                }
//            }
//
//        }).start();
//    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param motionEvent The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {

//        if (v.getId() == R.id.joyKbob) {
//            mGestureDetector.onTouchEvent(motionEvent);
//            return true;
//        }

        int action = motionEvent.getAction();


        final float x = motionEvent.getX();
        final float y = motionEvent.getY();
        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(TAG,"Action was DOWN");

                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)v.getLayoutParams();

                xDelta = x - lParams.leftMargin;
                yDelta = y - lParams.topMargin;
                break;
            case (MotionEvent.ACTION_MOVE) :
                Log.d(TAG,"Action was MOVE: " + motionEvent.getX() + " , " + motionEvent.getY());
                RelativeLayout.LayoutParams lParams2 = (RelativeLayout.LayoutParams) v.getLayoutParams();
                lParams2.leftMargin = (int)(x - xDelta);
                lParams2.topMargin = (int)(y - yDelta);
                lParams2.rightMargin = 0;
                lParams2.bottomMargin = 0;
                v.setLayoutParams(lParams2);
                break;
            case (MotionEvent.ACTION_UP) :
                Log.d(TAG,"Action was UP");


//                layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
//                layoutParams.leftMargin = (int)(x);
//                layoutParams.topMargin = (int)(y);
//                layoutParams.rightMargin = 0;
//                layoutParams.bottomMargin = 0;
//                v.setLayoutParams(layoutParams);

                break;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(TAG,"Action was CANCEL");
                break;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(TAG,"Movement occurred outside bounds " +
                        "of current screen element");
                break;
            default :
                return super.onTouchEvent(motionEvent);
        }
        joystickLayout.invalidate();
        return true;
    }

    /**
     * Notified when a tap occurs with the down {@link MotionEvent}
     * that triggered it. This will be triggered immediately for
     * every down event. All other events should be preceded by this.
     *
     * @param e The down motion event.
     */
    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "onDown");
        return false;
    }

    /**
     * The user has performed a down {@link MotionEvent} and not performed
     * a move or up yet. This event is commonly used to provide visual
     * feedback to the user to let them know that their action has been
     * recognized i.e. highlight an element.
     *
     * @param e The down motion event
     */
    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "onShow");
    }

    /**
     * Notified when a tap occurs with the up {@link MotionEvent}
     * that triggered it.
     *
     * @param e The up motion event that completed the first tap
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "singleTap");
        return false;
    }

    /**
     * Notified when a scroll occurs with the initial on down {@link MotionEvent} and the
     * current move {@link MotionEvent}. The distance in x and y is also supplied for
     * convenience.
     *
     * @param e1        The first down motion event that started the scrolling.
     * @param e2        The move motion event that triggered the current onScroll.
     * @param distanceX The distance along the X axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @param distanceY The distance along the Y axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    /**
     * Notified when a long press occurs with the initial on down {@link MotionEvent}
     * that trigged it.
     *
     * @param e The initial on down motion event that started the longpress.
     */
    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "Long");
    }

    /**
     * Notified of a fling event when it occurs with the initial on down {@link MotionEvent}
     * and the matching up {@link MotionEvent}. The calculated velocity is supplied along
     * the x and y axis in pixels per second.
     *
     * @param e1        The first down motion event that started the fling.
     * @param e2        The move motion event that triggered the current onFling.
     * @param velocityX The velocity of this fling measured in pixels per second
     *                  along the x axis.
     * @param velocityY The velocity of this fling measured in pixels per second
     *                  along the y axis.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling");
        return false;
    }

    /**
     * Called when a drag event is dispatched to a view. This allows listeners
     * to get a chance to override base View behavior.
     *
     * @param v     The View that received the drag event.
     * @param event The {@link DragEvent} object for the drag event.
     * @return {@code true} if the drag event was handled successfully, or {@code false}
     * if the drag event was not handled. Note that {@code false} will trigger the View
     * to call its {@link #onDragEvent(DragEvent) onDragEvent()} handler.
     */
    @Override
    public boolean onDrag(View v, DragEvent event) {
        return false;
    }
}
