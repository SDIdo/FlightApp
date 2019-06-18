package com.example.flightapp;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/***
 * The joystick component.
 */
public class Joystick extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{

    float centerX;
    float centerY;
    float baseRadius;
    float hatRadius;
    private JoystickListener joystickCallback;
    boolean pressedInBase = false;

    /**
     * The joystick constructor
     * @param context - derives the activity
     */
    public Joystick(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener) {
            joystickCallback = (JoystickListener) context;
        }
    }

    /**
     * Initialize the dimension of the joystick
     */
    void setupDimensions(){
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / 3;
        hatRadius = Math.min(getWidth(), getHeight()) / 10;
    }

    /**
     * Draws the joystick base according to center values and joystick knob
     * according to given parameters which changes by the user interactively.
     * @param newX - the x axis of which joystick knob should be
     * @param newY - the y axis of which joystick knob should be
     */
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

    /**
     * This is called immediately after the surface is first created.
     * @param surfaceHolder - the painter.
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    /**
     * surface has changed - update dimensions.
     * This is called immediately after any structural changes (format or size)
     * have been made to the surface
     * @param surfaceHolder - the painter.
     * @param i - format.
     * @param i1 - width.
     * @param i2 - height.
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        setupDimensions();
        drawJoystick(centerX, centerY);
    }


    /**
     * This is called immediately before a surface is being destroyed.
     * @param surfaceHolder - the painter.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    /**
     * Called when a touch event is dispatched to a view
     * @param view - The view the touch event has been dispatched to.
     * @param motionEvent - The MotionEvent object containing full
     * information about the event.
     * @return - True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float displacement = (float) Math.sqrt(Math.pow(motionEvent.getX() - centerX, 2)
                + Math.pow(motionEvent.getY() - centerY, 2));
        float ratio = baseRadius / displacement;
        if(displacement < baseRadius) {
            pressedInBase = true;
            if (motionEvent.getAction() != motionEvent.ACTION_UP) {
                drawJoystick(motionEvent.getX(), motionEvent.getY());
                joystickCallback.onJoystickMoved((motionEvent.getX()
                        - centerX) / baseRadius, (motionEvent.getY()
                        - centerY) / baseRadius);
            }
            else{
                pressedInBase = false;
                drawJoystick(centerX, centerY);
                joystickCallback.onJoystickMoved(0, 0);
            }

                }
            else{   /// out of joystick base
                if (pressedInBase && motionEvent.getAction() != motionEvent.ACTION_UP) {

                float constrainedX = centerX + (motionEvent.getX() - centerX) * ratio;
                float constrainedY = centerY + (motionEvent.getY() - centerY) * ratio;
                drawJoystick(constrainedX, constrainedY);
                joystickCallback.onJoystickMoved((constrainedX - centerX) / baseRadius,
                    (constrainedY - centerY) / baseRadius);
                }
                if (pressedInBase && motionEvent.getAction() == motionEvent.ACTION_UP) {
                    pressedInBase = false;
                    drawJoystick(centerX, centerY);
                    joystickCallback.onJoystickMoved(0, 0);
                }
            }
        return true;
    }

    /**
     * class that implements has an option to listen to the joystick's
     * knob's x y location
     */
    public interface JoystickListener
    {
        /**
         * Joystick Observer
         * @param xPercent - the x location of the knob
         * @param yPercent - the y location of the knob
         */
        void onJoystickMoved(float xPercent, float yPercent);
    }
}
