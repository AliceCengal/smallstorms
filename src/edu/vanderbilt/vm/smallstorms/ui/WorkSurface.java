package edu.vanderbilt.vm.smallstorms.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;
import edu.vanderbilt.vm.smallstorms.R;

/**
 * Date: 6/27/13
 * Time: 10:09 PM
 */
public class WorkSurface extends SurfaceView implements SurfaceHolder.Callback {

public WorkSurface(Context context) {
    super(context);
    mCtx = context;
    init();
}

public WorkSurface(Context context, AttributeSet attrs) {
    super(context, attrs);
    mCtx = context;
    init();
}

private void init() {
    mHolder = getHolder();
    mHolder.addCallback(this);

    setFocusable(true);
    mThisHandler = defineHandler();
}

private static final String TAG = "WorkSurface";

private Context mCtx;
private SurfaceHolder mHolder;
private Handler mThisHandler;

private WorkThread mThread;
private Handler mThreadHandler;

private Handler defineHandler() {
    return new Handler() {
        @Override public void handleMessage(Message message) {
            switch (message.what) {

            }
        }};
}

private boolean mPointerDown;

@Override
public boolean onTouchEvent (MotionEvent event) {

    Message m = mThisHandler.obtainMessage();
    m.what = WorkThread.DELEGATE_TOUCH_EVENT;
    m.getData().putParcelable("motion_event", event);
    mThreadHandler.handleMessage(m);

    try { Thread.sleep(16); }
    catch (InterruptedException e) { e.printStackTrace(); }

    /*switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
        if (!mPointerDown) {
            mPointerDown = true;
            mThreadHandler.sendMessage(Memo.from(mThisHandler)
                    .what(WorkThread.POINTER_COORDINATE_START)
                    .arg1((int) event.getX())
                    .arg2((int) event.getY())
                    .done()); }
        break;

    case MotionEvent.ACTION_MOVE:
        mThreadHandler.sendMessage(Memo.from(mThisHandler)
                .what(WorkThread.POINTER_COORDINATE_END)
                .arg1((int) event.getX())
                .arg2((int) event.getY())
                .done());
        break;

    case MotionEvent.ACTION_UP:
        mPointerDown = false;
        mThreadHandler.sendMessage(Memo.from(mThisHandler)
                .what(WorkThread.POINTER_COORDINATE_START)
                .arg1(-1)
                .arg2(-1)
                .done());
        mThreadHandler.sendMessage(Memo.from(mThisHandler)
                .what(WorkThread.POINTER_COORDINATE_END)
                .arg1(-1)
                .arg2(-1)
                .done());
        break;
    }*/

    return true;
}

@Override
public void surfaceCreated(SurfaceHolder surfaceHolder) {

    if (mThread == null || mThread.getState() == Thread.State.TERMINATED) {
        mThread = new WorkThread(this.mHolder, getContext(), null, this); }

    mThreadHandler = mThread.getHandler();
    mThread.setRunning(true);
    mThread.start();
}

@Override
public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
}

@Override
public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    boolean retry = true;
    mThread.setRunning(false);

    while (retry) {
        try {
            mThread.join();
            retry = false; }

        catch (InterruptedException e) {}}
}

private static class WorkThread extends Thread {

    WorkThread(SurfaceHolder holder, final Context context, Handler handler, SurfaceView view) {
        mHolder = holder;
        //mSurfaceHandler = handler;
        //mView = view;


        mButton1 = new Button(context);
        mButton1.setText("Button1");
        mButton1.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mButton1.measure(0, 0);
        mButton1.layout(
                0,
                150,
                mButton1.getMeasuredWidth(),
                mButton1.getMeasuredHeight() + 150);
        mButtonRect = new Rect();
        mButton1.getDrawingRect(mButtonRect);
        mButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Button selected. Rect: " + mButtonRect.flattenToString(), Toast.LENGTH_SHORT).show();
            }
        });

        mIcon1 = context.getResources().getDrawable(R.drawable.ic_launcher);
        mIcon1.setBounds(0, 0, 100, 100);

        preparePointerDrawables(context);



        mBackground = context.getResources().getDrawable(R.color.light_orange);
        mBackground.setBounds(mHolder.getSurfaceFrame());
    }

    private boolean mIsRunning = false;

    private SurfaceHolder mHolder;
    //private Handler mSurfaceHandler;
    //private SurfaceView mView;

    private Button mButton1;
    private Rect mButtonRect;

    private Drawable mIcon1;
    private Drawable mBackground;

    private Paint mPointerPaint;
    private Drawable mStartSprite;
    private Drawable mEndSprite;
    private Drawable mArrowSprite;

    private Point mPStart = new Point(-1, -1);
    private Point mPEnd = new Point(-1, -1);

    static final int DELEGATE_TOUCH_EVENT = 3;

    public void setRunning(boolean running) {
        mIsRunning = running;
    }

    public Handler getHandler() {
        return defineHandler();
    }

    @Override
    public void run() {

        while (mIsRunning) {
            lockCanvasAndDraw();
        }

        Log.i("WorkSurface", "Workspace's drawing thread is terminated.");
    }

    private Handler defineHandler() {
        return new Handler() {
            @Override public void handleMessage(Message message) {
                switch (message.what) {
                case DELEGATE_TOUCH_EVENT:
                    handleTouchEvents((MotionEvent) message.getData().getParcelable("motion_event"));
                    break;
                }
            }};
    }

    private void handleTouchEvents(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_UP:


            if (mButtonRect.contains(mPStart.x, mPStart.y)) {
                mButton1.setSelected(false); }

            mPStart.set(-1, -1);
            mPEnd.set(-1, -1);
            break;

        case MotionEvent.ACTION_MOVE:
            mPEnd.set((int) event.getX(), (int) event.getY());
            break;

        case MotionEvent.ACTION_DOWN:
            mPStart.set((int) event.getX(), (int) event.getY());

            //mButton1.dispatchTouchEvent(event);
            mButton1.getDrawingRect(mButtonRect);
            if (mButtonRect.contains(mPStart.x, mPStart.y)) {
                mButton1.setSelected(true);
                mButton1.performClick();   }

            break;
        }
    }

    private void handleDropEvent(Point start, Point end) {

    }

    private void preparePointerDrawables(Context context) {
        mStartSprite = context.getResources().getDrawable(R.drawable.thumb_start);
        mStartSprite.setBounds(
                (int) convertDpToPixel(-25, context),
                (int) convertDpToPixel(-25, context),
                (int) convertDpToPixel(25, context),
                (int) convertDpToPixel(25, context));
        mStartSprite.setAlpha(200);

        mEndSprite = context.getResources().getDrawable(R.drawable.thumb_end);
        mEndSprite.setBounds(
                (int) convertDpToPixel(-25, context),
                (int) convertDpToPixel(-25, context),
                (int) convertDpToPixel(25, context),
                (int) convertDpToPixel(25, context));
        mStartSprite.setAlpha(200);

        mArrowSprite = context.getResources().getDrawable(R.drawable.thumb_arrow);
        mArrowSprite.setBounds(
                0, 0,
                (int) convertDpToPixel(50, context),
                (int) convertDpToPixel(50, context));
        mStartSprite.setAlpha(200);

        mPointerPaint = new Paint();
        mPointerPaint.setAlpha(200);
        mPointerPaint.setStrokeWidth(5.0f);
    }

    private void lockCanvasAndDraw() {
        Canvas c = mHolder.lockCanvas();
        if (c == null) {
            Log.e("WorkSurface", "lockCanvas returns null.");
            return; }

        // draw everything
        drawBackground(c);

        mIcon1.draw(c);

        c.translate(0.0f, 150.0f);
        mButton1.draw(c);

        drawPointers(c);

        mHolder.unlockCanvasAndPost(c);
    }


    private void drawBackground(Canvas c) {
        mBackground.draw(c);
    }

    private void drawPointers(Canvas c) {
        if (isStartPointerVisible()) {
            c.setMatrix(null);
            c.translate(mPStart.x, mPStart.y);
            mStartSprite.draw(c);

            if (isEndPointerVisible() &&
                    hypothenus(mPStart.x - mPEnd.x, mPStart.y - mPEnd.y) > 25.0) {
                c.translate(mPEnd.x - mPStart.x, mPEnd.y - mPStart.y);
                mEndSprite.draw(c);

                c.rotate((float) calculateArrowheadRotation(mPStart.x - mPEnd.x, mPStart.y - mPEnd.y));
                mArrowSprite.draw(c);

                c.setMatrix(null);
                c.drawLine(mPStart.x, mPStart.y, mPEnd.x, mPEnd.y, mPointerPaint);


            }
        }

    }

    private boolean isStartPointerVisible() {
        return mPStart.y != -1 && mPStart.x != -1;
    }

    private boolean isEndPointerVisible() {
        return mPEnd.x != -1 && mPEnd.y != -1;
    }

    private static double hypothenus(int dx, int dy) {
        return Math.sqrt(dx*dx + dy*dy);
    }

    private static double calculateArrowheadRotation(int dx, int dy) {
        return (dx == 0) ?
                    (dy > 0) ? 45 : 225 :
                (Math.atan((double)dy/dx)/Math.PI*180.0 - 45) +
                    ((dx > 0) ? 0 : 180);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

}



}
