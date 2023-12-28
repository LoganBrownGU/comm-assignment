package display;

import java.awt.*;

public class ImageDisplay extends Canvas {
    private final int pixelsWidth, pixelsHeight;
    private final Graphics2D g;

    public void paint() {

    }

    public ImageDisplay(int pixelsWidth, int pixelsHeight) {
        this.pixelsWidth = pixelsWidth;
        this.pixelsHeight = pixelsHeight;
        this.g = (Graphics2D) super.getGraphics();
    }

    public int getPixelsWidth() {
        return this.pixelsWidth;
    }

    public int getPixelsHeight() {
        return this.pixelsHeight;
    }
}
