package display;

import util.Buffer;

import java.awt.*;

public class ImageDisplay extends Canvas {
    private final int imageWidth, imageHeight;
    private final Graphics2D g;
    private final Buffer dataBuffer;

    private Dimension pixelSize;

    public void paint() {
        this.g.clearRect(0, 0, this.getWidth(), this.getHeight());

        for (int vert = 0; vert < this.getHeight(); vert += this.pixelSize.height) {
            for (int hor = 0; hor < this.getWidth(); hor += this.pixelSize.width) {
                byte[] rgb = this.dataBuffer.getChunk(3);
                this.g.setColor(new Color(rgb[0], rgb[1], rgb[2]));
                this.g.drawRect(hor, vert, this.pixelSize.width, this.pixelSize.height);
            }
        }
    }

    public ImageDisplay(Buffer dataBuffer, int imageWidth, int imageHeight) {
        this.dataBuffer = dataBuffer;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.g = (Graphics2D) super.getGraphics();
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
