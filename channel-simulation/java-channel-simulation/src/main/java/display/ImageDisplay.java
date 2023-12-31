package display;

import util.Buffer;

import java.awt.*;

public class ImageDisplay extends Canvas {
    private final int imageWidth, imageHeight;
    private Graphics2D g;
    private final Buffer dataBuffer;

    private Dimension pixelSize;
    int count = 0;

    public void paint() {
        if (this.g == null) this.g = (Graphics2D) this.getGraphics();

        for (int vert = 0; vert < this.imageHeight; vert++) {
            for (int hor = 0; hor < this.imageWidth; hor++) {
                byte[] rgb = this.dataBuffer.getChunk(3);
                this.g.setColor(new Color(Byte.toUnsignedInt(rgb[2]), Byte.toUnsignedInt(rgb[1]), Byte.toUnsignedInt(rgb[0])));
                this.g.fillRect(hor * this.pixelSize.width, vert * this.pixelSize.height, this.pixelSize.width, this.pixelSize.height);
            }
        }
    }

    public ImageDisplay(Buffer dataBuffer, int imageWidth, int imageHeight) {
        this.dataBuffer = dataBuffer;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return this.imageWidth;
    }

    public int getImageHeight() {
        return this.imageHeight;
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        this.pixelSize = new Dimension(d.width / this.imageWidth, d.height / this.imageHeight);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.pixelSize = new Dimension(width / this.imageWidth, height / this.imageHeight);
    }
}
