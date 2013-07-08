package edu.vanderbilt.vm.smallstorms.framework;

import edu.vanderbilt.vm.smallstorms.framework.Graphics.PixmapFormat;

public interface Pixmap {
    public int getWidth();

    public int getHeight();

    public PixmapFormat getFormat();

    public void dispose();
}
