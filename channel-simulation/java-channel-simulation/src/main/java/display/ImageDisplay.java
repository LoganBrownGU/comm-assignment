package display;

import util.Buffer;

import java.awt.*;
import java.util.ArrayList;

public class ImageDisplay extends Canvas {
    private final int pixelsWidth, pixelsHeight;
    private final Graphics2D g;
    private final Buffer dataBuffer;

    public void paint() {

    }

    public ImageDisplay(Buffer dataBuffer, int pixelsWidth, int pixelsHeight) {
        this.dataBuffer = dataBuffer;
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
