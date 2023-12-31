package display;

import util.Buffer;

import java.awt.*;

public class ImageDisplay extends Canvas {
    private final int imageWidth, imageHeight;
    private Graphics2D g;

    private Dimension pixelSize;

    public void paint(byte[] data) {
        if (data.length != this.imageHeight * this.imageWidth * 3) throw new IllegalArgumentException("data must contain full frame");
        if (this.g == null) this.g = (Graphics2D) this.getGraphics();

        int index = 0;
        for (int vert = 0; vert < this.imageHeight; vert++) {
            for (int hor = 0; hor < this.imageWidth; hor++) {
                this.g.setColor(new Color(Byte.toUnsignedInt(data[index + 2]), Byte.toUnsignedInt(data[index + 1]), Byte.toUnsignedInt(data[index])));
                this.g.fillRect(hor * this.pixelSize.width, vert * this.pixelSize.height, this.pixelSize.width, this.pixelSize.height);
                index += 3;
            }
        }
    }

    public ImageDisplay(int imageWidth, int imageHeight) {
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
