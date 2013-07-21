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

final private TouchMachine mTouchState;
final private Workspace mWorkspace;

public WorkScreen(Game game) {
    super(game);
    mFPS = new FPSCounter();

    mWorkspace = new Workspace();
    mTouchState = new TouchMachine();

    SpriteController c = new SpriteController(new LabelSprite(game, mWorkspace)
            .setText("Label")
            .setPosition(500, 300));

    mWorkspace.addSprite(c.mSprite);
    mTouchState.addTouchListener(c);

    SpriteFactoryController sfc = new SpriteFactoryController(
            new DragSprite(mGame, mWorkspace)
                    .setCostume(Assets.mPointerStart)
                    .setPosition(50, 40),
            new RegularSpriteFactory(mWorkspace, mGame, Assets.mPointerStart),
            mTouchState);

    mWorkspace.addSprite(sfc.mFactorySprite);
    mTouchState.addTouchListener(sfc);

    sfc = new SpriteFactoryController(
            new DragSprite(mGame, mWorkspace)
                    .setCostume(Assets.mPointerArrow)
                    .setPosition(50, 120),
            new RegularSpriteFactory(mWorkspace, mGame, Assets.mPointerArrow),
            mTouchState);

    mWorkspace.addSprite(sfc.mFactorySprite);
    mTouchState.addTouchListener(sfc);

    sfc = new SpriteFactoryController(
            new DragSprite(mGame, mWorkspace)
                    .setCostume(Assets.mPointerEnd)
                    .setPosition(50, 200),
            new RegularSpriteFactory(mWorkspace, mGame, Assets.mPointerEnd),
            mTouchState);

    mWorkspace.addSprite(sfc.mFactorySprite);
    mTouchState.addTouchListener(sfc);

    sfc = new SpriteFactoryController(
            new DragSprite(mGame, mWorkspace)
                    .setCostume(Assets.mAppIcon)
                    .setPosition(50, 280),
            new RegularSpriteFactory(mWorkspace, mGame, Assets.mAppIcon),
            mTouchState);

    mWorkspace.addSprite(sfc.mFactorySprite);
    mTouchState.addTouchListener(sfc);

    sfc = new SpriteFactoryController(
            new DragSprite(mGame, mWorkspace)
                    .setCostume(Assets.mBlock)
                    .setPosition(50, 360),
            new TextSpriteFactory(mWorkspace, mGame),
            mTouchState);

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

private static class RegularSpriteFactory implements SpriteFactory {

    Pixmap mCostume;
    Workspace mWs;
    Game mGame;

    RegularSpriteFactory(Workspace ws, Game game, Pixmap costume) {
        mWs = ws; mGame = game; mCostume = costume;
    }

    @Override
    public Sprite createSprite() {
        Sprite s = new DragSprite(mGame, mWs).setCostume(mCostume);
        mWs.addSprite(s);
        return s;
    }
}

private static class TextSpriteFactory implements SpriteFactory {

    Workspace mWs;
    Game mGame;

    TextSpriteFactory(Workspace ws, Game game) {
        mWs = ws; mGame = game;
    }

    @Override
    public Sprite createSprite() {
        Sprite s = new LabelSprite(mGame, mWs).setRandomText();
        mWs.addSprite(s);
        return s;
    }
}

private static class DragSprite extends Sprite {

    public DragSprite(Game game, Workspace ws) {
        super(game, ws);
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

        this.mCostume = Assets.mBlock;
        this.mTextBound = new Rect();
    }

    public LabelSprite setText(String text) {
        mText = text;

        int[] textBound = new int[2];
        mGame.getGraphics().textSize(mText, mTextSize, textBound);

        mTextBound.set(0, 0, textBound[0], textBound[1]);
        mWidth = textBound[0] + mPadding*2;
        mHeight = mCostume.getHeight(); // textBound[1] + mPadding*2;
        adjustBoundingBox();
        return this; }

    public LabelSprite setTextSize(float size) {
        mTextSize = size;
        return this; }

    public LabelSprite setRandomText() {
        Random r = new Random();
        StringBuilder str = new StringBuilder();

        int len = (r.nextInt(14) + 4);
        for (int i = 0; i < len; i++) {
            str.append((char) ('a' + r.nextInt(25)));
        }

        this.setText(str.toString());

        return this;
    }

    private float mTextSize = 25.0f;
    private int mPadding = 10;
    private String mText;
    private Rect mTextBound;

    private static final int RAW_HEIGHT = 50;
    private static final int RAW_WIDTH = 87;

    private static final int END_CAP_WIDTH = 10;
    private static final int LEFT_SECTION_WIDTH = 55;
    private static final int BOTTOM_CAP_HEIGHT = 18;
    private static final int TOP_CAP_HEIGHT = 7;

    @Override
    public void present(float deltaTime) {

        drawBackground();

        /*mGame.getGraphics().drawRect(
                mBoundingBox.left - mPadding,
                mBoundingBox.top - mPadding,
                mBoundingBox.width() + mPadding*3,
                mBoundingBox.height() + mPadding*3,
                0xFF8FFFFF);*/

        mGame.getGraphics().drawText(
                mText,
                mBoundingBox.left + mPadding,
                mBoundingBox.bottom - BOTTOM_CAP_HEIGHT,
                mTextSize,
                0xFF000000);

    }

    private void drawBackground() {
        if (mTextBound.width() > (RAW_WIDTH - 2*END_CAP_WIDTH)) {
            final int extraWidth = mTextBound.width() - (RAW_WIDTH - 2*END_CAP_WIDTH);

            mGame.getGraphics().drawPixmap(
                    mCostume,
                    mBoundingBox.left,
                    mBoundingBox.top,
                    0, 0,
                    LEFT_SECTION_WIDTH, RAW_HEIGHT);

            int addWidth = 0;
            while (addWidth < (extraWidth + END_CAP_WIDTH*2)) {

                mGame.getGraphics().drawPixmap(
                        mCostume,
                        mBoundingBox.left + LEFT_SECTION_WIDTH + addWidth - 1,
                        mBoundingBox.top,
                        LEFT_SECTION_WIDTH, 0,
                        END_CAP_WIDTH/2 +4, RAW_HEIGHT);

                addWidth += (END_CAP_WIDTH/2);
            }

            mGame.getGraphics().drawPixmap(
                    mCostume,
                    mBoundingBox.left + RAW_WIDTH + extraWidth - END_CAP_WIDTH,
                    mBoundingBox.top,
                    RAW_WIDTH - END_CAP_WIDTH, 0,
                    END_CAP_WIDTH, RAW_HEIGHT);

        } else {
            mGame.getGraphics().drawPixmap(
                    mCostume,
                    mBoundingBox.left,
                    mBoundingBox.top);
        }
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
                mWorkspace)
                .setCostume(mCostume)
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
    public boolean drop(TouchMachine.TouchListener dropper) {
        dropper.setPosition(new Point(mSprite.getX(), mSprite.getY() + 50));
        return true;
    }
}

public interface SpriteFactory {
    Sprite createSprite();
}

public static class SpriteFactoryController implements TouchMachine.TouchListener {

    Sprite mFactorySprite;
    TouchMachine mMachine;

    SpriteFactory mSpawner;

    public SpriteFactoryController(Sprite factorySprite, SpriteFactory factory, TouchMachine machine) {
        mFactorySprite = factorySprite;
        mMachine = machine;
        mSpawner = factory;
    }

    @Override
    public void setPosition(Point position) {
        //mFactorySprite.setPosition(position);
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
        final Sprite s = mSpawner.createSprite()
                .setPosition(mFactorySprite.getX(), mFactorySprite.getY());

        mMachine.addTouchListener(new SpriteController(s));
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
        mFactorySprite.mFocused = true;
    }

    @Override
    public void leaveHover() {
        mFactorySprite.mFocused = false;
    }

    @Override
    public boolean drop(TouchMachine.TouchListener dropper) {
        return false;
    }
}



}
