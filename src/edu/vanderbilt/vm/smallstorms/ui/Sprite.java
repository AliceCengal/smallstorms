package edu.vanderbilt.vm.smallstorms.ui;

import android.graphics.Point;
import android.graphics.Rect;
import edu.vanderbilt.vm.smallstorms.framework.Game;
import edu.vanderbilt.vm.smallstorms.framework.Pixmap;

/**
 * Date: 7/7/13
 * Time: 3:53 AM
 */
public class Sprite {

Game mGame;

Point mPosition;
Rect mBoundingBox;
Pixmap mCostume;

boolean mFocused;

public Sprite(Game game) {
    mGame = game;
}

public Sprite(Game game, int x, int y, Pixmap costume) {
    this(game);
    mPosition = new Point(x, y);
    mCostume = costume;
    mBoundingBox = new Rect();
    adjustBoundingBox();
}

void update(float deltaTime) {}

void present(float deltaTime) {}

boolean touches(Point point) {
    return touches(point.x, point.y);
}

boolean touches(int x, int y) {
    return mBoundingBox.contains(x, y);
}

void adjustBoundingBox() {
    mBoundingBox.set(
            mPosition.x - mCostume.getWidth()/2,    // left
            mPosition.y - mCostume.getHeight()/2,   // top
            mPosition.x + mCostume.getWidth()/2,    // right
            mPosition.y + mCostume.getHeight()/2);  // bottom
}

void click() {}

void hover(Sprite theOneOnTop) {}

void drop(Sprite theOneAtTheBottom, Point location) {}

}
