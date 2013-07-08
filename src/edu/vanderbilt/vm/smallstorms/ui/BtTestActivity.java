package edu.vanderbilt.vm.smallstorms.ui;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import edu.vanderbilt.vm.smallstorms.R;
import edu.vanderbilt.vm.smallstorms.util.BTCommunicator;
import edu.vanderbilt.vm.smallstorms.util.Memo;
import edu.vanderbilt.vm.smallstorms.util.VirtualStorms;

/**
 * Date: 6/15/13
 * Time: 3:10 AM
 */
public class BtTestActivity extends Activity {

private Button mLeft;
private Button mForward;
private Button mReverse;
private Button mRight;
private MenuItem mBtMenuItem;
private Toast mToast;

private BluetoothDevice mDevice;
private VirtualStorms mVirtualBot;
private Handler mBotHandler;
private Handler mUiHandler;

private LinearMotionState mLinearState;
private RotationalMotionState mRotationalState;
private boolean mIsRunning = false;


private static final int UPDATE_TIME = 200;

public static void open(Context ctx) {
    ctx.startActivity(new Intent(ctx, BtTestActivity.class));
}

/**
 * Called when the activity is first created.
 */
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fourway_button);

    mLeft = (Button) findViewById(R.id.fourway_left);
    mForward = (Button) findViewById(R.id.fourway_forward);
    mReverse = (Button) findViewById(R.id.fourway_reverse);
    mRight = (Button) findViewById(R.id.fourway_right);

    mLinearState = new LinearMotionState();
    mRotationalState = new RotationalMotionState();
    mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    mUiHandler = defineHandler();

}


/*
@Override
public boolean onTouch(View view, MotionEvent motionEvent) {

    Log.i("SmallStorms_onTouch",
            (   (view == mLeft) ? "Left" :
                (view == mForward)? "Forward" :
                (view == mReverse) ? "Reverse" :
                (view == mRight) ? "Right" :
                "Other") +
            ", " +
            (motionEvent.toString()));


    return false;
} */


@Override
public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.remote_control, menu);
    return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_bluetooth_connection) {
        item.setIcon(R.drawable.device_access_bluetooth_searching);
        mBtMenuItem = item;

        new DeviceListDialog(
                new DeviceListDialog.BtDeviceReceiver() {
                    @Override public void receive(BluetoothDevice device) {
                        mDevice = device;

                        mLeft.setOnTouchListener(mRotationalState);
                        mForward.setOnTouchListener(mLinearState);
                        mReverse.setOnTouchListener(mLinearState);
                        mRight.setOnTouchListener(mRotationalState);

                        mVirtualBot = new VirtualStorms(mUiHandler, mDevice.getAddress());
                        mBotHandler = mVirtualBot.getRobotHandler(); }}

        ).show(getFragmentManager(), "device_list_dialog");

        return true; }

    else {
        return false; }
}

@Override
public void onStop() {
    super.onStop();
    mIsRunning = false;

    if (mVirtualBot != null) {
        mVirtualBot.destroy();
        mVirtualBot = null; }
}

private Handler defineHandler() {
    return new Handler() {
        @Override public void handleMessage(Message message) {
            switch (message.what) {
            case BTCommunicator.STATE_CONNECTED:
                mBtMenuItem.setIcon(R.drawable.device_access_bluetooth_connected);
                mToast.setText("Bluetooth connection successful.");
                mToast.show();

                getActionBar().setTitle(mDevice.getName());
                mIsRunning = true;
                mUiHandler.post(mGameLoop);
                break;

            case BTCommunicator.STATE_CONNECTERROR:
                mIsRunning = false;
                mToast.setText("Bluetooth connection failed.");
                mToast.show();
                break;

            default: break; }}};
}

private static class LinearMotionState implements View.OnTouchListener {

    public static final int FORWARD = 1;
    public static final int STATIONARY = 0;
    public static final int REVERSE = -1;

    private int mId;
    private int mAction;

    public LinearMotionState() {
        mMotionState = STATIONARY;
    }

    private int mMotionState;
    public int getMotionState() {
        return mMotionState;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mId = view.getId();
        mAction = motionEvent.getAction();

        switch (mMotionState) {
        case FORWARD:
            if (mAction == MotionEvent.ACTION_UP) {
                mMotionState = STATIONARY; }

            else if (mAction == MotionEvent.ACTION_DOWN &&
                    mId == R.id.fourway_reverse) {
                mMotionState = STATIONARY; }

            break;

        case REVERSE:
            if (mAction == MotionEvent.ACTION_UP) {
                mMotionState = STATIONARY; }

            else if (mAction == MotionEvent.ACTION_DOWN &&
                    mId == R.id.fourway_forward) {
                mMotionState = STATIONARY; }

            break;

        case STATIONARY:
            if (mAction == MotionEvent.ACTION_DOWN) {
                if (mId == R.id.fourway_forward) {
                    mMotionState = FORWARD; }

                else if (mId == R.id.fourway_reverse) {
                    mMotionState = REVERSE; }}

            break;
        }

        return false;
    }
}

private static class RotationalMotionState implements View.OnTouchListener {

    public static final int LEFT = 1;
    public static final int STATIONARY = 0;
    public static final int RIGHT = -1;

    private int mId;
    private int mAction;

    public RotationalMotionState() {
        mRotationalState = STATIONARY;
    }

    private int mRotationalState;
    public int getMotionState() {
        return mRotationalState;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mId = view.getId();
        mAction = motionEvent.getAction();

        switch (mRotationalState) {
        case LEFT:
            if (mAction == MotionEvent.ACTION_UP) {
                mRotationalState = STATIONARY; }

            else if (mAction == MotionEvent.ACTION_DOWN &&
                    mId == R.id.fourway_right) {
                mRotationalState = STATIONARY; }

            break;

        case RIGHT:
            if (mAction == MotionEvent.ACTION_UP) {
                mRotationalState = STATIONARY; }

            else if (mAction == MotionEvent.ACTION_DOWN &&
                    mId == R.id.fourway_left) {
                mRotationalState = STATIONARY; }

            break;

        case STATIONARY:
            if (mAction == MotionEvent.ACTION_DOWN) {
                if (mId == R.id.fourway_left) {
                    mRotationalState = LEFT; }

                else if (mId == R.id.fourway_right) {
                    mRotationalState = RIGHT; }}

            break;
        }

        return false;
    }
}



private Runnable mGameLoop = new Runnable() {
    @Override
    public void run() {

        //Log.i("SmallStorms_GameLoop", "GameLoop started");
        int A = mLinearState.getMotionState();
        int B = mRotationalState.getMotionState();

        int left = 0;
        int right = 0;

        if (A == 1) {
            if (B == 1) { left = 1; right = 3; } else
            if (B == 0) { left = 2; right = 2; } else
            if (B == -1) { left = 3; right = 1; }}

        else if (A == 0) {
            if (B == 1) { left = -2; right = 2; } else
            if (B == -1) { left = 2; right = -2; }}

        else if (A == -1) {
            if (B == 1) { left = -1; right = -3; } else
            if (B == 0) { left = -2; right = -2; } else
            if (B == -1) {left = -3; right = -1; }}

        mBotHandler.sendMessage(Memo.from(mUiHandler)
                .what(VirtualStorms.SET_MOTOR_POWER)
                .value1(left * 25)
                .value2(right * 25)
                .done());

        if (mIsRunning) {
            mUiHandler.postDelayed(mGameLoop, UPDATE_TIME); }

    }
};

/*
private void sendBotMessage(int delay, int message, int value1, int value2) {
    Bundle bundle = new Bundle();
    bundle.putInt("value1", value1);
    bundle.putInt("value2", value2);

    Message outMsg = mUiHandler.obtainMessage();
    outMsg.what = message;
    outMsg.setData(bundle);

    Memo outMemo = Memo.from(mUiHandler)
            .value1(value1)
            .value2(value2)
            .what(message);

    if (delay == 0) {
        mBotHandler.sendMessage(outMsg); }

    else {
        mBotHandler.sendMessageDelayed(outMsg, delay); }
}

private void sendBotMessage(int delay, int message, String name) {
    Bundle bundle = new Bundle();
    bundle.putInt("message", message);
    bundle.putString("name", name);

    Message outMsg = mUiHandler.obtainMessage();
    outMsg.what = message;
    outMsg.setData(bundle);

    if (delay == 0) {
        mBotHandler.sendMessage(outMsg); }

    else {
        mBotHandler.sendMessageDelayed(outMsg, delay); }
}
*/
}
