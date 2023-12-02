package display;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Display extends Frame implements Runnable {

    private final Scope transmitterScope, channelScope;

    public void update(double transmitterValue, double channelValue, double timeStep) {
        transmitterScope.update(transmitterValue, timeStep);
        channelScope.update(channelValue, timeStep);
    }

    private void init() {
        this.setSize(1600, 900);
        Color scopeColour = new Color(0x000088);

        transmitterScope.setSize(this.getWidth() / 4, this.getHeight() / 2);
        transmitterScope.setLocation(this.getWidth() / 3, 10);
        transmitterScope.setBackground(scopeColour);
        transmitterScope.setVisible(true);
        this.add(transmitterScope);

        channelScope.setSize(this.getWidth() / 4, this.getHeight() / 2);
        channelScope.setLocation(2*this.getWidth() / 3, 10);
        channelScope.setBackground(scopeColour);
        channelScope.setVisible(true);
        this.add(channelScope);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        this.setLayout(null);
        this.setVisible(true);
    }

    @Override
    public void run() {
        init();
    }

    public Display(String title) throws HeadlessException {
        super(title);
        this.transmitterScope = new Scope();
        this.channelScope = new Scope();
    }
}
