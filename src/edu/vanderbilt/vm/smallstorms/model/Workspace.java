package edu.vanderbilt.vm.smallstorms.model;

import android.graphics.Point;
import edu.vanderbilt.vm.smallstorms.ui.Sprite;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 7/16/13
 * Time: 11:29 AM
 */
public class Workspace {

private List<Sprite> mSprites = new ArrayList<Sprite>();

public List<Sprite> getSprites() { return mSprites; }

public Sprite getTouchingSprite(Point point) { return getTouchingSprite(point.x, point.y); }

public Sprite getTouchingSprite(int x, int y) {

    Sprite s;

    for (int i = 0; i < mSprites.size(); i++) {
        s = mSprites.get(i);
        if (s.touches(x, y)) {
            return s; }}

    return null; }

public Workspace addSprite(Sprite sprite) {
    mSprites.add(sprite);
    return this; }

public Workspace removeSprite(Sprite sprite) {
    mSprites.remove(sprite);
    return this; }

}
