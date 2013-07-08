package edu.vanderbilt.vm.smallstorms.ui;

import android.graphics.PixelFormat;
import edu.vanderbilt.vm.smallstorms.framework.Game;
import edu.vanderbilt.vm.smallstorms.framework.Graphics;
import edu.vanderbilt.vm.smallstorms.framework.Screen;

/**
 * Date: 7/6/13
 * Time: 6:20 AM
 */
public class LoadingScreen extends Screen {

public LoadingScreen(Game game) {
    super(game);
}

@Override
public void update(float deltaTime) {
    Graphics g = mGame.getGraphics();
    Assets.mAppIcon = g.newPixmap("ic_launcher.png", Graphics.PixmapFormat.ARGB4444);
    Assets.mPointerArrow = g.newPixmap("thumb_arrow.png", Graphics.PixmapFormat.ARGB4444);
    Assets.mPointerStart = g.newPixmap("thumb_start.png", Graphics.PixmapFormat.ARGB4444);
    Assets.mPointerEnd = g.newPixmap("thumb_end.png", Graphics.PixmapFormat.ARGB4444);

    mGame.setScreen(new WorkScreen(mGame));
}

@Override
public void present(float deltaTime) {
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
}
