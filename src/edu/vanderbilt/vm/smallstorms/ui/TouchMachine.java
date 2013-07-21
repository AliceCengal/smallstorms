package edu.vanderbilt.vm.smallstorms.ui;

import android.graphics.Point;
import android.graphics.Rect;
import edu.vanderbilt.vm.smallstorms.framework.Input;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: 7/19/13
 * Time: 1:09 PM
 */
public class TouchMachine {

public interface TouchListener {

    void setPosition(Point position);
    Rect getTouchBox();
    int getX();
    int getY();

    void touch();
    void click();

    DragUpdater startDrag();
    void endDrag();

    void enterHover(TouchListener hoverer);
    void leaveHover();
    boolean drop(TouchListener dropper);

}

public interface DragUpdater {
    void update(Point position);
}

public TouchMachine() {
    initTouchMachines();
}

public void handleTouchInput(List<Input.TouchEvent> events) {
    mTouchState.handleTouchInput(events);
}

public TouchMachine addTouchListener(TouchListener listener) {
    mListeners.add(new WeakReference<TouchListener>(listener));
    return this;
}

public void setSleep(boolean sleeping) {
    if (sleeping) {
        mTouchState = mTouchMachines[SLEEP]; }

    else {
        mTouchState = mTouchMachines[VANGUARD]; }
}

//---------- PRIVATE ----------//

private MachineState mTouchState;

private List<WeakReference<TouchListener>> mListeners = new ArrayList<WeakReference<TouchListener>>();

private MachineState[] mTouchMachines;

private Point mReusablePoint = new Point();

private TouchListener getTouchingListener(Point loc) {

    for (int i = 0; i < mListeners.size(); i++) {
        TouchListener listener = mListeners.get(i).get();

        if (listener != null && listener.getTouchBox().contains(loc.x, loc.y)) {
            return listener; }
    }

    return null;
}

/** Common Interface for a bunch of state machines which will interpret touch input */
private interface MachineState {
    void handleTouchInput(List<Input.TouchEvent> events);
}

private static final int VANGUARD = 0;
private static final int VOID = 1;
private static final int CLICK = 2;
private static final int DRAG = 3;
private static final int SLEEP = 4;
private static final int HOVER = 5;

/**
 * Set up a pool of machines that can be recycled. This hopefully avoids GC
 * during vigorous touching sessions.
 */
private void initTouchMachines() {
    mTouchMachines = new MachineState[6];
    mTouchMachines[VANGUARD] = new VanguardMachine();
    mTouchMachines[VOID] = new VoidMachine();
    mTouchMachines[CLICK] = new ClickMachine();
    mTouchMachines[DRAG] = new DragMachine();
    mTouchMachines[SLEEP] = new SleepMachine();
    mTouchMachines[HOVER] = new HoverMachine();
    mTouchState = mTouchMachines[VANGUARD];
}

/**
 * The frontliner who will catch a DOWN event and determine if it falls on a valid touch recipient.
 * If yes, pass the touch input stream to ClickMachine, else to VoidMachine.
 */
private class VanguardMachine implements MachineState {

    @Override
    public void handleTouchInput(List<Input.TouchEvent> events) {
        int len = events.size();
        Input.TouchEvent event;

        for (int i = 0; i < len; i++) {
            event = events.get(i);

            if (event.type == Input.TouchEvent.TOUCH_DOWN) {

                mReusablePoint.set(event.x, event.y);
                TouchListener l = getTouchingListener(mReusablePoint);
                if (l != null) {
                    l.touch();
                    mTouchState = ((ClickMachine) mTouchMachines[CLICK])
                            .setTarget(l);
                    mTouchState.handleTouchInput(events.subList(i + 1, len)); }

                else {
                    mTouchState = mTouchMachines[VOID];
                    mTouchState.handleTouchInput(events.subList(i + 1, len));
                    return; }
            }
        }
    }
}

/**
 * Indicates invalid touch. Will consume MOVE events until it receives an UP event, at which point it will
 * return control to VanguardMachine.
 */
private class VoidMachine implements MachineState {

    @Override
    public void handleTouchInput(List<Input.TouchEvent> events) {
        int len = events.size();
        for (int i = 0; i < len; i++) {
            if (events.get(i).type == Input.TouchEvent.TOUCH_UP) {
                mTouchState = mTouchMachines[VANGUARD];
                mTouchState.handleTouchInput(events.subList(i + 1, len));
                return; }
        }
    }
}

/**
 * Indicates that a valid sprite is targeted by the DOWN event. Will check for the MOVE stream. If the MOVE
 * stream stays inside the bounds of the Sprite, the Sprite will receive a click event upon an UP event
 * and control will return to VanguardMachine. If the MOVE stream goes outside the Sprite, controls is passed
 * to DragMachine.
 */
private class ClickMachine implements MachineState {

    TouchListener mTarget;

    MachineState setTarget(TouchListener target) {
        mTarget = target;
        return this;
    }

    @Override
    public void handleTouchInput(List<Input.TouchEvent> events) {
        int len = events.size();
        Input.TouchEvent event;

        for (int i = 0; i < len; i++) {
            event = events.get(i);
            if (event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                if (!mTarget.getTouchBox().contains(event.x, event.y)) {
                    mTouchState = ((DragMachine) mTouchMachines[DRAG]).setOrigin(mTarget);
                    mTouchState.handleTouchInput(events.subList(i + 1, len));
                    return; }}

            else if (event.type == Input.TouchEvent.TOUCH_UP) {
                // Do click event
                mTarget.click();

                mTouchState = mTouchMachines[VANGUARD];
                mTouchState.handleTouchInput(events.subList(i + 1, len));
                return; }
        }
    }
}

/**
 * Handles a drag sequence. Will return control to Vanguard as soon as the drag sequence finishes.
 */
private class DragMachine implements MachineState {

    TouchListener mOrigin;
    DragUpdater mUpdater;
    Point mInitial = new Point();

    MachineState setOrigin(TouchListener origin) {
        mOrigin = origin;
        mInitial.set(mOrigin.getX(), mOrigin.getY());
        mUpdater = mOrigin.startDrag();
        return this;
    }

    @Override
    public void handleTouchInput(List<Input.TouchEvent> events) {
        int len = events.size();
        Input.TouchEvent event;

        for (int i = 0; i < len; i++) {
            event = events.get(i);
            if (event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                mReusablePoint.set(event.x, event.y);
                mUpdater.update(mReusablePoint);

                TouchListener l = getTouchingListener(mReusablePoint);
                if (l != null && l != mOrigin) {
                    mTouchState = ((HoverMachine) mTouchMachines[HOVER]).setPoints(mOrigin, mInitial, mUpdater, l);
                    mTouchState.handleTouchInput(events.subList(i + 1, len));
                }

            }

            else if (event.type == Input.TouchEvent.TOUCH_UP) {
                mOrigin.endDrag();

                mTouchState = mTouchMachines[VANGUARD];
                mTouchState.handleTouchInput(events.subList(i + 1, len));
                return; }
        }
    }
}


private class HoverMachine implements MachineState {

    TouchListener mOrigin;
    Point mInitial = new Point();
    DragUpdater mUpdater;

    TouchListener mDestination;


    MachineState setPoints(TouchListener origin, Point initial, DragUpdater updater, TouchListener destination) {
        mOrigin = origin;
        mDestination = destination;
        mInitial = initial;
        mUpdater = updater;

        mDestination.enterHover(mOrigin);

        return this;
    }

    @Override
    public void handleTouchInput(List<Input.TouchEvent> events) {
        int len = events.size();
        Input.TouchEvent event;

        for (int i = 0; i < len; i++) {
            event = events.get(i);
            if (event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                mReusablePoint.set(event.x, event.y);
                mUpdater.update(mReusablePoint);

                if (!mDestination.getTouchBox().contains(event.x, event.y)) {
                    mDestination.leaveHover();

                    mTouchState = ((DragMachine) mTouchMachines[DRAG]).setOrigin(mOrigin);
                    mTouchState.handleTouchInput(events.subList(i + 1, len));
                    return; }}

            if (event.type == Input.TouchEvent.TOUCH_UP) {
                mDestination.leaveHover();

                if (!mDestination.drop(mOrigin)) {
                    mUpdater.update(mInitial);
                }

                mOrigin.endDrag();

                mTouchState = mTouchMachines[VANGUARD];
                mTouchState.handleTouchInput(events.subList(i + 1, len));
                return; }
        }
    }
}

/**
 * Indicate that the workscreen is disabled. No response to any touch events.
 */
private class SleepMachine implements MachineState {

    @Override
    public void handleTouchInput(List<Input.TouchEvent> events) {
    }
}

}
