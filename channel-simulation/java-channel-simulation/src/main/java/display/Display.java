package display;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Display extends Frame {

    private final Scope transmitterScope, channelScope;

    public void update(double transmitterValue, double channelValue, double timeStep) {
        transmitterScope.update(transmitterValue, timeStep);
        channelScope.update(channelValue, timeStep);
        transmitterScope.paint(transmitterScope.getGraphics());
        channelScope.paint(channelScope.getGraphics());
    }

    public void init() {
        this.setSize(1600, 900);

        transmitterScope.setSize(this.getWidth() / 4, this.getHeight() / 2);
        transmitterScope.setLocation(this.getWidth() / 4, 10);
        this.add(transmitterScope);
        channelScope.setSize(this.getWidth() / 3, this.getHeight() / 2);
        channelScope.setLocation(2 * this.getWidth() / 3, 10);
        this.add(channelScope);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        this.setVisible(true);
    }

    public Display(String title) throws HeadlessException {
        super(title);
        this.transmitterScope = new Scope();
        this.channelScope = new Scope();
        init();
    }
}
