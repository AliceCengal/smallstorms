package edu.vanderbilt.vm.smallstorms.ui;

import android.graphics.Point;
import android.graphics.Rect;
import edu.vanderbilt.vm.smallstorms.framework.*;
import edu.vanderbilt.vm.smallstorms.framework.impl.AndroidGame;
import edu.vanderbilt.vm.smallstorms.model.Workspace;

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
    //initTouchMachines();

    mWorkspace = new Workspace();
    mTouchState = new TouchMachine();

    /*mWorkspace
            .addSprite(new DragSprite(game, mWorkspace, Assets.mPointerStart)
                    .setPosition(150, 50))

            .addSprite(new DragSprite(game, mWorkspace, Assets.mAppIcon)
                    .setPosition(300, 300))

            .addSprite(new LabelSprite(game, mWorkspace, Assets.mPointerEnd)
                    .setText("Label")
                    .setPosition(500, 300))

            .addSprite(new RandomSpawningSprite(mGame, mWorkspace, Assets.mAppIcon)
                    .setPosition(200, 200));*/

    SpriteFactoryController sfc = new SpriteFactoryController(
            new DragSprite(mGame, mWorkspace, Assets.mPointerStart)
                    .setPosition(50, 50),
            mGame,
            mWorkspace);

    mWorkspace.addSprite(sfc.mFactorySprite);
    mTouchState.addTouchListener(sfc);

    sfc = new SpriteFactoryController(
            new DragSprite(mGame, mWorkspace, Assets.mPointerArrow)
                    .setPosition(50, 150),
            mGame,
            mWorkspace);

    mWorkspace.addSprite(sfc.mFactorySprite);
    mTouchState.addTouchListener(sfc);

    sfc = new SpriteFactoryController(
            new DragSprite(mGame, mWorkspace, Assets.mPointerEnd)
                    .setPosition(50, 250),
            mGame,
            mWorkspace);

    mWorkspace.addSprite(sfc.mFactorySprite);
    mTouchState.addTouchListener(sfc);

    sfc = new SpriteFactoryController(
            new DragSprite(mGame, mWorkspace, Assets.mAppIcon)
                    .setPosition(50, 350),
            mGame,
            mWorkspace);

    mWorkspace.addSprite(sfc.mFactorySprite);
    mTouchState.addTouchListener(sfc);
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
    g.drawRect(0, 0, 100, g.getHeight(), 0xfff7991e);

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

public static class SpriteController implements TouchMachine.TouchListener {

    public SpriteController(Sprite slave) {
        mSprite = slave;
    }

    private Sprite mSprite;

    @Override
    public void setPosition(Point position) {
        mSprite.setPosition(position);
    }

    @Override
    public Rect getTouchBox() {
        return mSprite.mBoundingBox;
    }

    @Override
    public int getX() {
        return mSprite.getX();
    }

    @Override
    public int getY() {
        return mSprite.getY();
    }

    @Override
    public void touch() {
        mSprite.mFocused = true;
    }

    @Override
    public void click() {
        mSprite.mFocused = false;
    }

    @Override
    public TouchMachine.DragUpdater startDrag() {
        return new TouchMachine.DragUpdater() {
            @Override public void update(Point position) {
                mSprite.setPosition(position); }};
    }

    @Override
    public void endDrag() {
        mSprite.mFocused = false;
    }

    @Override
    public void enterHover(TouchMachine.TouchListener hoverer) {
        mSprite.mFocused = true;
    }

    @Override
    public void leaveHover() {
        mSprite.mFocused = false;
    }

    @Override
    public void drop(TouchMachine.TouchListener dropper) {
        dropper.setPosition(new Point(mSprite.getX(), mSprite.getY() + 50));
    }
}

public static class SpriteFactoryController implements TouchMachine.TouchListener {

    Sprite mFactorySprite;
    Workspace mWorkspace;
    Game mGame;

    public SpriteFactoryController(Sprite sprite, Game g, Workspace ws) {
        mFactorySprite = sprite;
        mGame = g;
        mWorkspace = ws;
    }

    @Override
    public void setPosition(Point position) {
        mFactorySprite.setPosition(position);
    }

    @Override
    public Rect getTouchBox() {
        return mFactorySprite.mBoundingBox;
    }

    @Override
    public int getX() {
        return mFactorySprite.getX();
    }

    @Override
    public int getY() {
        return mFactorySprite.getY();
    }

    @Override
    public void touch() {
        mFactorySprite.mFocused = true;
    }

    @Override
    public void click() {
        mFactorySprite.mFocused = false;
    }

    @Override
    public TouchMachine.DragUpdater startDrag() {
        final Sprite s = new DragSprite(mGame, mWorkspace, mFactorySprite.mCostume)
                .setPosition(mFactorySprite.getX(), mFactorySprite.getY());

        mWorkspace.addSprite(s);

        return new TouchMachine.DragUpdater() {
            @Override public void update(Point position) {
                s.setPosition(position); }};
    }

    @Override
    public void endDrag() {
        mFactorySprite.mFocused = false;
    }

    @Override
    public void enterHover(TouchMachine.TouchListener hoverer) {
    }

    @Override
    public void leaveHover() {
    }

    @Override
    public void drop(TouchMachine.TouchListener dropper) {
    }
}

}
