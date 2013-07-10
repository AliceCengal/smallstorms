package edu.vanderbilt.vm.smallstorms.framework.impl;

import android.app.Activity;
//import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
//import android.os.PowerManager;
//import android.os.PowerManager.WakeLock;
import android.view.ViewTreeObserver;
import android.view.Window;
//import android.view.WindowManager;

import android.view.WindowManager;
import android.widget.Toast;
import edu.vanderbilt.vm.smallstorms.framework.Audio;
import edu.vanderbilt.vm.smallstorms.framework.FileIO;
import edu.vanderbilt.vm.smallstorms.framework.Game;
import edu.vanderbilt.vm.smallstorms.framework.Graphics;
import edu.vanderbilt.vm.smallstorms.framework.Input;
import edu.vanderbilt.vm.smallstorms.framework.Screen;

public abstract class AndroidGame extends Activity implements Game, ViewTreeObserver.OnGlobalLayoutListener {
    AndroidFastRenderView renderView;
    Graphics graphics;
    //Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    //WakeLock wakeLock;

    /* Some typical sizes for the SurfaceView : MAJOR_AXIS, MINOR_AXIS

       HTC EVO (landscape, with Actionbar) : 960, 442
       Nexus 7 (lansdcape, with Actionbar) : 1280, 696
       From the book, fullscreen : 480, 320
     */

    public static final int MAJOR_AXIS = 1280;
    public static final int MINOR_AXIS = 696;

    private int frameBufferWidth;
    private int frameBufferHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        frameBufferWidth = isLandscape ? MAJOR_AXIS : MINOR_AXIS;
        frameBufferHeight = isLandscape ? MINOR_AXIS : MAJOR_AXIS;

        Bitmap frameBuffer = Bitmap.createBitmap(
                frameBufferWidth,
                frameBufferHeight,
                Config.RGB_565);

        renderView = new AndroidFastRenderView(this, frameBuffer);
        setContentView(renderView);

        renderView.getViewTreeObserver()
                .addOnGlobalLayoutListener(this);

        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(this);
        //audio = new AndroidAudio(this);
        screen = getStartScreen();

    }

    @Override
    public void onResume() {
        super.onResume();
        //wakeLock.acquire();
        screen.resume();
        renderView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //wakeLock.release();
        renderView.pause();
        screen.pause();

        if (isFinishing())
            screen.dispose();
    }

    public Input getInput() {
        return input;
    }

    public FileIO getFileIO() {
        return fileIO;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public Audio getAudio() {
        return null; //audio;
    }

    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");

        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }

    public Screen getCurrentScreen() {
        return screen;
    }

    @Override
    public void onGlobalLayout() {
        /*Toast.makeText(
                this,
                "RenderView width : " + renderView.getWidth() + ", height : " + renderView.getHeight(),
                Toast.LENGTH_SHORT)
                .show();*/

        float scaleX = (float) frameBufferWidth
                / renderView.getWidth();
        float scaleY = (float) frameBufferHeight
                / renderView.getHeight();

        input = new AndroidInput(this, renderView, scaleX, scaleY);

    }

}
