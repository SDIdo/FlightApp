package com.example.flightapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Joystick extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{

    float centerX;

    float centerY;

    float baseRadius;

    float hatRadius;
    private JoystickListener joystickCallback;
    boolean shouldStop = false;
    boolean wasIn = false;

    public Joystick(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener) {
            joystickCallback = (JoystickListener) context;
        }
    }
    public Joystick(Context context, AttributeSet attributes, int style){
        super(context, attributes, style);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener) {
            joystickCallback = (JoystickListener) context;
        }
    }
    public Joystick(Context context, AttributeSet attributes){
        super(context, attributes);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener) {
            joystickCallback = (JoystickListener) context;
        }
    }

    void setupDimensions(){
        centerX = getWidth() / 2;

        centerY = getHeight() / 2;

        baseRadius = Math.min(getWidth(), getHeight()) / 3;

        hatRadius = Math.min(getWidth(), getHeight()) / 10;
    }
    private void drawJoystick(float newX, float newY){
        if(getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint color = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            color.setARGB(255, 50, 50, 50);
            myCanvas.drawCircle(centerX, centerY, baseRadius, color);
            color.setARGB(255, 0, 0, 255);
            myCanvas.drawCircle(newX, newY, hatRadius, color);
            getHolder().unlockCanvasAndPost(myCanvas);
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d("JOYYYY", "CHANGEDDD");
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d("JOYYYY", "DEstroyeddd");
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        shouldStop = true;
        float displacement = (float) Math.sqrt(Math.pow(motionEvent.getX() - centerX, 2)
                + Math.pow(motionEvent.getY() - centerY, 2));
        float ratio = baseRadius / displacement;
        if(displacement < baseRadius) {
            wasIn = true;
            if (motionEvent.getAction() != motionEvent.ACTION_UP) {
//        if(view.equals(this)){
//        if (motionEvent.getX() < Math.min(getWidth(), getHeight()) / 3 && motionEvent.getX() < 600){


//                if(displacement < baseRadius) {
                drawJoystick(motionEvent.getX(), motionEvent.getY());
                joystickCallback.onJoystickMoved((motionEvent.getX()
                        - centerX) / baseRadius, (motionEvent.getY()
                        - centerY) / baseRadius, getId());
            }
            else{
                wasIn = false;
                drawJoystick(centerX, centerY);
                joystickCallback.onJoystickMoved(0, 0, getId());
            }

                }
//            }
            else{   /// out of graybase
                if (wasIn && motionEvent.getAction() != motionEvent.ACTION_UP) {

                float constrainedX = centerX + (motionEvent.getX() - centerX) * ratio;
                float constrainedY = centerY + (motionEvent.getY() - centerY) * ratio;
                drawJoystick(constrainedX, constrainedY);
                joystickCallback.onJoystickMoved((constrainedX - centerX) / baseRadius,
                    (constrainedY - centerY) / baseRadius, getId());
                }
                if (wasIn && motionEvent.getAction() == motionEvent.ACTION_UP) {
                    wasIn = false;
                    drawJoystick(centerX, centerY);
                    joystickCallback.onJoystickMoved(0, 0, getId());
                }
            }
//        }
        return true;
    }
    public interface JoystickListener

    {
        void onJoystickMoved(float xPercent, float yPercent, int source);
    }
}
