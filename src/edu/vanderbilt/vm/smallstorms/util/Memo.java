package edu.vanderbilt.vm.smallstorms.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * A convenience class that provides a cleaner interface to the Messaging system. A typical
 * usage should look like this:
 * <code>
 *     mOutHandler.sendMessage(Memo.from(mMyHandler)
 *         .what(4321)
 *         .value1(1234)
 *         .value2(5678)
 *         .name("something")
 *         .done());
 * </code>
 *
 * Date: 6/27/13
 * Time: 9:36 PM
 */
public class Memo {

final private Message mMessage;
final private Bundle mBundle;
private boolean isSent;

private Memo(Handler h) {
    mMessage = h.obtainMessage();
    mBundle = new Bundle();
    isSent = false;
}

public static Memo from(Handler h) {
    return new Memo(h);
}

public Memo what(int what) {
    checkValidity();
    mMessage.what = what;
    return this;
}

public Memo arg1(int arg) {
    checkValidity();
    mMessage.arg1 = arg;
    return this;
}

public Memo arg2(int arg) {
    checkValidity();
    mMessage.arg2 = arg;
    return this;
}

public Memo value1(int val) {
    checkValidity();
    mBundle.putInt("value1", val);
    return this;
}

public Memo value2(int val) {
    checkValidity();
    mBundle.putInt("value2", val);
    return this;
}

public Memo name(String name) {
    checkValidity();
    mBundle.putString("name", name);
    return this;
}

public Memo put(String key, String val) {
    checkValidity();
    mBundle.putString(key, val);
    return this;
}

public Memo put(String key, int val) {
    checkValidity();
    mBundle.putInt(key, val);
    return this;
}

public Memo put(String key, float val) {
    checkValidity();
    mBundle.putFloat(key, val);
    return this;
}

public Memo put(String key, double val) {
    checkValidity();
    mBundle.putDouble(key, val);
    return this;
}

public Memo put(String key, boolean val) {
    checkValidity();
    mBundle.putBoolean(key, val);
    return this;
}

public Bundle bundle() {
    return mBundle;
}

public Message done() {
    isSent = true;
    mMessage.setData(mBundle);
    return mMessage;
}

private void checkValidity() {
    if (isSent)
        throw new IllegalStateException(
                "A Memo should only be used once. Create a new one for a different message.");
}

}
