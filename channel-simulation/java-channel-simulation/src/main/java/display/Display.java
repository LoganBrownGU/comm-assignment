package display;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Display extends Frame implements Runnable {

    private final Scope transmitterScope, channelScope;
    private final TextField dataIn, dataOut;

    private String byteToString(byte b) {
        String intStr = Integer.toBinaryString(b);
        String outStr = "";
        if (intStr.length() > 8) {
            outStr = intStr.substring(24);
        } else {
            for (int i = 8; i > intStr.length(); i--) outStr += "0";
            outStr += intStr;
        }

        return outStr;
    }

    public void update(double transmitterValue, double channelValue,  byte byteIn, byte byteOut, double timeStep) {
        this.transmitterScope.update(transmitterValue, timeStep);
        this.channelScope.update(channelValue, timeStep);
        this.dataIn.setText(byteToString(byteIn));
        this.dataOut.setText(byteToString(byteOut));
    }

    private void init() {
        this.setSize(1600, 900);
        Color scopeColour = new Color(0x000088);

        this.transmitterScope.setSize(this.getWidth() / 2, this.getHeight() / 2);
        this.transmitterScope.setLocation(0, 10);
        this.transmitterScope.setBackground(scopeColour);
        this.transmitterScope.setVisible(true);
        this.add(this.transmitterScope);

        this.channelScope.setSize(this.getWidth() / 2, this.getHeight() / 2);
        this.channelScope.setLocation(this.getWidth() / 2, 10);
        this.channelScope.setBackground(scopeColour);
        this.channelScope.setVisible(true);
        this.add(this.channelScope);

        this.dataIn.setSize(100, 20);
        this.dataIn.setLocation(0, this.transmitterScope.getY() + this.transmitterScope.getHeight());
        this.dataIn.setVisible(true);
        this.add(this.dataIn);

        this.dataOut.setSize(100, 20);
        this.dataOut.setLocation(this.getWidth() - this.dataOut.getWidth(), this.channelScope.getY() + this.channelScope.getHeight());
        this.dataOut.setVisible(true);
        this.add(this.dataOut);

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
        this.dataIn = new TextField();
        this.dataOut = new TextField();
    }
}
