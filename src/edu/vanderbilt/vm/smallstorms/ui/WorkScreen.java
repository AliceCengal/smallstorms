package edu.vanderbilt.vm.smallstorms.ui;

import android.graphics.Point;
import android.util.Log;
import edu.vanderbilt.vm.smallstorms.framework.*;
import edu.vanderbilt.vm.smallstorms.framework.impl.AndroidGame;
import edu.vanderbilt.vm.smallstorms.model.Workspace;

import java.util.List;
import java.util.Random;

/**
 * Date: 7/6/13
 * Time: 6:04 AM
 */
public class WorkScreen extends Screen {

Random mRandom;
FPSCounter mFPS;

private TouchMachine mTouchState;
private Workspace mWorkspace;

public WorkScreen(Game game) {
    super(game);
    mFPS = new FPSCounter();
    initTouchMachines();

    mWorkspace = new Workspace();

    mWorkspace
            .addSprite(new DragSprite(game, mWorkspace, Assets.mPointerArrow)
                    .setPosition(50, 50))

            .addSprite(new DragSprite(game, mWorkspace, Assets.mPointerStart)
                    .setPosition(150, 50))

            .addSprite(new DragSprite(game, mWorkspace, Assets.mAppIcon)
                    .setPosition(300, 300))

            .addSprite(new LabelSprite(game, mWorkspace, Assets.mPointerEnd)
                    .setText("Label")
                    .setPosition(500, 300))

            .addSprite(new RandomSpawningSprite(mGame, mWorkspace, Assets.mAppIcon)
                    .setPosition(200, 200));

}

@Override
public void update(float deltaTime) {
    mTouchState.handleTouchInput(mGame.getInput().getTouchEvents());

    //mFPS.logFrame();
}

@Override
public void present(float deltaTime) {

    Graphics g = mGame.getGraphics();
    g.clear(0xffee7d);

    for (int i = 0; i < mWorkspace.getSprites().size(); i++) {
        mWorkspace.getSprites().get(i).present(deltaTime);
    }

}

@Override
public void pause() {
}

@Override
public void resume() {
}

@Override
public void dispose() {
}

/** Common Interface for a bunch of state machines which will interpret touch input */
private interface TouchMachine {
    void handleTouchInput(List<Input.TouchEvent> events);
}

private TouchMachine[] mTouchMachines;

private static final int VANGUARD = 0;
private static final int VOID = 1;
private static final int CLICK = 2;
private static final int DRAG = 3;
private static final int SLEEP = 4;

/**
 * Set up a pool of machines that can be recycled. This hopefully avoids GC
 * during vigorous touching sessions.
 */
private void initTouchMachines() {
    mTouchMachines = new TouchMachine[5];
    mTouchMachines[VANGUARD] = new VanguardMachine();
    mTouchMachines[VOID] = new VoidMachine();
    mTouchMachines[CLICK] = new ClickMachine();
    mTouchMachines[DRAG] = new DragMachine();
    mTouchMachines[SLEEP] = new SleepMachine();
    mTouchState = mTouchMachines[VANGUARD];
}

/**
 * The frontliner who will catch a DOWN event and determine if it falls on a valid touch recipient.
 * If yes, pass the touch input stream to ClickMachine, else to VoidMachine.
 */
private class VanguardMachine implements TouchMachine {

    VanguardMachine() {
        Log.d(WorkScreen.class.getName(), "VanguardMachine reporting");
    }

    @Override
    public void handleTouchInput(List<Input.TouchEvent> events) {
        int len = events.size();
        Input.TouchEvent event;

        for (int i = 0; i < len; i++) {
            event = events.get(i);

            if (event.type == Input.TouchEvent.TOUCH_DOWN) {

                /*
                for (int j = 0; j < mWorkspace.getSprites().size(); j++) {
                    if (mWorkspace.getSprites().get(j).touches(event.x, event.y)) {
                        mTouchState = ((ClickMachine) mTouchMachines[CLICK])
                                .setTarget(mWorkspace.getSprites().get(j));
                        mTouchState.handleTouchInput(events.subList(i + 1, len));
                        return; }
                }*/

                Sprite s = mWorkspace.getTouchingSprite(event.x, event.y);
                if (s != null) {
                    mTouchState = ((ClickMachine) mTouchMachines[CLICK])
                            .setTarget(s);
                    mTouchState.handleTouchInput(events.subList(i + 1, len));
                    return; }

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
private class VoidMachine implements TouchMachine {
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
private class ClickMachine implements TouchMachine {

    Sprite mTarget;

    TouchMachine setTarget(Sprite target) {
        mTarget = target;
        mTarget.mFocused = true;
        return this;
    }

    @Override
    public void handleTouchInput(List<Input.TouchEvent> events) {
        int len = events.size();
        Input.TouchEvent event;

        for (int i = 0; i < len; i++) {
            event = events.get(i);
            if (event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                if (!mTarget.touches(event.x, event.y)) {
                    mTouchState = ((DragMachine) mTouchMachines[DRAG]).setOrigin(mTarget);
                    mTouchState.handleTouchInput(events.subList(i + 1, len));
                    return; }}

            else if (event.type == Input.TouchEvent.TOUCH_UP) {
                // Do click event
                mTarget.click();
                mTarget.mFocused = false;

                mTouchState = mTouchMachines[VANGUARD];
                mTouchState.handleTouchInput(events.subList(i + 1, len));
                return; }
        }
    }
}

/**
 * Handles a drag sequence. Will return control to Vanguard as soon as the drag sequence finishes.
 */
private class DragMachine implements TouchMachine {

    Sprite mOrigin;
    Point mInitial = new Point();

    TouchMachine setOrigin(Sprite origin) {
        mOrigin = origin;
        mInitial.set(mOrigin.getX(), mOrigin.getY());
        return this;
    }

    @Override
    public void handleTouchInput(List<Input.TouchEvent> events) {
        int len = events.size();
        Input.TouchEvent event;

        for (int i = 0; i < len; i++) {
            event = events.get(i);
            if (event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                mOrigin.setPosition(event.x, event.y); }

            else if (event.type == Input.TouchEvent.TOUCH_UP) {
                mOrigin.mFocused = false;

                mTouchState = mTouchMachines[VANGUARD];
                mTouchState.handleTouchInput(events.subList(i + 1, len));
                return; }
        }
    }
}

/**
 * Indicate that the workscreen is disabled. No response to any touch events.
 */
private class HoverMachine implements TouchMachine {

    @Override
    public void handleTouchInput(List<Input.TouchEvent> events) {
    }

}

private class SleepMachine implements TouchMachine {

    @Override
    public void handleTouchInput(List<Input.TouchEvent> events) {
    }
}

private static class DragSprite extends Sprite {

    public DragSprite(Game game, Workspace ws) {
        super(game, ws);
    }

    public DragSprite(Game game, Workspace ws, Pixmap costume) {
        super(game, ws, costume);
    }

    @Override
    public void present(float deltaTime) {
        if (mFocused) {
            mGame.getGraphics().drawRect(
                    mBoundingBox.left - 5,
                    mBoundingBox.top - 5,
                    mBoundingBox.width() + 10,
                    mBoundingBox.height() + 10,
                    0xFFAA0000); }

        mGame.getGraphics().drawPixmap(
                mCostume,
                mBoundingBox.left,
                mBoundingBox.top);
    }

    @Override
    public void click() {
        Log.d(WorkScreen.class.getName(), "DragSprite is clicked");
    }

}

private static class LabelSprite extends Sprite {

    public LabelSprite(Game game, Workspace ws) {
        super(game, ws);
    }

    public LabelSprite(Game game, Workspace ws, Pixmap costume) {
        super(game, ws, costume);
    }

    public LabelSprite setText(String text) {
        mText = text;
        return this; }

    public LabelSprite setTextSize(float size) {
        mTextSize = size;
        return this; }

    public LabelSprite setWrapContent(boolean wrap) {
        return this;
    }

    private float mTextSize = 20.0f;
    private int mPadding = 5;
    private String mText;

    @Override
    public void present(float deltaTime) {
        mGame.getGraphics().drawRect(
                mBoundingBox.left,
                mBoundingBox.top,
                mBoundingBox.width(),
                mBoundingBox.height(),
                0xFF8FFFFF);

        mGame.getGraphics().drawText(
                mText,
                mBoundingBox.left + mPadding,
                mBoundingBox.top + mPadding + (int) mTextSize,
                mTextSize,
                0xFF000000);

    }

}

private static class RandomSpawningSprite extends Sprite {

    /**
     * @param game for access to sound and graphics utilities
     * @param ws   for access to other denizens of the workspace
     */
    public RandomSpawningSprite(Game game, Workspace ws) {
        super(game, ws);
    }

    public RandomSpawningSprite(Game game, Workspace ws, Pixmap costume) {
        super(game, ws, costume);
    }

    @Override
    public void present(float deltatime) {
        if (mFocused) {
            mGame.getGraphics().drawRect(
                    mBoundingBox.left - 5,
                    mBoundingBox.top - 5,
                    mBoundingBox.width() + 10,
                    mBoundingBox.height() + 10,
                    0xFFAA0000); }

        mGame.getGraphics().drawPixmap(
                mCostume,
                mBoundingBox.left,
                mBoundingBox.top);
    }

    @Override
    public void click() {
        Random r = new Random();
        mWorkspace.addSprite(new RandomSpawningSprite(
                mGame,
                mWorkspace,
                mCostume)
            .setPosition(
                    r.nextInt(AndroidGame.MAJOR_AXIS),
                    r.nextInt(AndroidGame.MINOR_AXIS)));
    }

}

}
