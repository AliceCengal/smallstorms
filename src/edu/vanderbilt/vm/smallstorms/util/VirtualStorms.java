package edu.vanderbilt.vm.smallstorms.util;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

/**
 * <p>
 *     A facade that provides a high level abstraction over the BT communication with the MINDSTORMS robot. Low level
 *     operations can still be done by calling <code>getBTCommunicator()</code> and using the BTCommunicator object
 *     to send commands directly to the robot.
 * </p>
 * <p>
 *     When this object is created, a Bluetooth connection is established using a MACAdress passed in from the
 *     constructor. The process of discovery and Bluetooth activation should be done before initializing this object.
 *     The connection is maintained until <code>destroy()</code> is called or a connection error is encountered. An
 *     error message will be sent out, and the client should release all reference and create a new instance if needed.
 * </p>
 * <p>
 *     The Handler object returned by <code>getRobotHandler()</code> should be used to send messages to this bot. This
 *     class defines a set of valid messages that can be passed to the Handler. This bot also acts as a proxy for
 *     sensor values. In order to get a sensor reading, send a message declaring the intent, and it will respond with
 *     another message carrying the value.
 * </p>
 * <p>
 *     This class defines message values from 2000 to 3000.
 * </p>
 * Date: 6/19/13
 * Time: 4:24 AM
 */
public class VirtualStorms implements BTConnectable {

/**
 * Constructor. A new BT connection will be established, and messages will be sent to the uiHandler as to whether the
 * connection is successful.
 *
 * @param uiHandler
 * @param macAddress
 */
public VirtualStorms(Handler uiHandler, String macAddress) {
    mUIHandler = uiHandler;
    mHandler = defineHandler();

    startBTConnection(macAddress);
}

/**
 * @return the Handler used to send messages to the virtual bot
 */
public Handler getRobotHandler() {
    return mHandler;
}

/**
 * @return the BTCommunicator used to send messages directly to the real bot
 */
public BTCommunicator getBTCommunicator() {
    return mBTCommunicator;
}

/**
 * Terminate BT connection with the real bot and cleanup any other resources.
 */
public void destroy() {
    try {
        mBTCommunicator.destroyNXTconnection(); }

    catch (IOException e) {
        e.printStackTrace(); }

    mBTCommunicator = null;
    mUIHandler = null;
    mBTHandler = null;
}

/**
 * Set the Bot's motor power, where the power ranges from -100 to 100 and 0 indicate stationary.
 *
 * value1 => left motor, value2 => right motor
 */
public static final int SET_MOTOR_POWER = 2001;

/**
 *
 */
public static final int REQUEST_SENSOR_VALUE = 2002;
public static final int RETURN_SENSOR_VALUE = 2003;

private Handler mUIHandler;
private Handler mHandler;
private BTCommunicator mBTCommunicator;
private Handler mBTHandler;

private static final int MOTOR_LEFT = BTCommunicator.MOTOR_C;
private static final int MOTOR_RIGHT = BTCommunicator.MOTOR_B;

private Handler defineHandler() {
    return new Handler() {
        @Override public void handleMessage(Message message) {
            Message forward = mHandler.obtainMessage();
            forward.copyFrom(message);

            switch (message.what) {

            // From RealBot
            case BTCommunicator.STATE_CONNECTED:
                mUIHandler.sendMessage(forward);
                break;

            case BTCommunicator.STATE_CONNECTERROR:
                mUIHandler.sendMessage(forward);
                break;

            // From UI
            case SET_MOTOR_POWER:
                Bundle data = message.getData();
                sendBtMessage(BTCommunicator.NO_DELAY, MOTOR_LEFT, data.getInt("value1"), 0);
                sendBtMessage(BTCommunicator.NO_DELAY, MOTOR_RIGHT, data.getInt("value2"), 0);
                break;

            default: break; }}};
}

private void startBTConnection(String address) {
    if (mBTCommunicator != null) {
        try {
            mBTCommunicator.destroyNXTconnection(); }

        catch (IOException e) {
            e.printStackTrace(); }}

    mBTCommunicator = new BTCommunicator(
            this,
            mHandler,
            BluetoothAdapter.getDefaultAdapter());
    mBTHandler = mBTCommunicator.getHandler();

    mBTCommunicator.setMACAddress(address);
    mBTCommunicator.start();
}

/**
 * @return true, when currently pairing
 */
@Override
public boolean isPairing() {
    return true;
}

/**
 * Sends the message via the BTCommuncator to the robot.
 * @param delay time to wait before sending the message.
 * @param message the message type (as defined in BTCommucator
 * @param value1 first data value
 * @param value2 second data value
 */
private void sendBtMessage(int delay, int message, int value1, int value2) {
    Bundle bundle = new Bundle();
    bundle.putInt("value1", value1);
    bundle.putInt("value2", value2);

    Message outMsg = mHandler.obtainMessage();
    outMsg.what = message;
    outMsg.setData(bundle);

    if (delay == 0) {
        mBTHandler.sendMessage(outMsg); }

    else {
        mBTHandler.sendMessageDelayed(outMsg, delay); }

}

/**
 * Sends the message via the BTCommuncator to the robot.
 * @param delay time to wait before sending the message.
 * @param message the message type (as defined in BTCommucator)
 * @param name a String parameter
 */
void sendBTCmessage(int delay, int message, String name) {
    Bundle bundle = new Bundle();
    bundle.putInt("message", message);
    bundle.putString("name", name);

    Message outMsg = mHandler.obtainMessage();
    outMsg.what = message;
    outMsg.setData(bundle);

    if (delay == 0) {
        mBTHandler.sendMessage(outMsg); }

    else {
        mBTHandler.sendMessageDelayed(outMsg, delay); }
}

}
