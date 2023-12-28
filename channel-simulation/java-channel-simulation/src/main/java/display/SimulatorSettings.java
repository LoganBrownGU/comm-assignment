package display;

import modulator.Modulator;
import modulator.ModulatorFactory;
import modulator.ModulatorType;
import util.Pair;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class SimulatorSettings extends Frame implements Runnable {

    private static final Dimension PARAMS_SIZE = new Dimension(200, 20);
    private static final Dimension PADDING = new Dimension(20, 60);

    private final Menu modulatorMenu = new Menu("Choose modulator...");
    private final Checkbox useECCCheckBox = new Checkbox("use error coding");
    private final Button applySettingsButton = new Button("Apply");
    private final Button runSimulationButton = new Button("Simulate");
    private final ArrayList<String> modulatorParameters = new ArrayList<>();
    public final Object lock = new Object();
    private final HashMap<String, Pair<Label, TextField>> parameterInputs = new HashMap<>();
    private String[] parameterNames;    // need to store names as an array to preserve ordering
    private ModulatorType chosenModulator = ModulatorType.ASK;
    private boolean finished = false;

    private static class ModulatorMenuItem extends MenuItem {
        private final ModulatorType type;

        public ModulatorMenuItem(String label, ModulatorType type) throws HeadlessException {
            super(label);
            this.type = type;
        }

        public ModulatorType getType() {
            return this.type;
        }
    }

    public Modulator getModulator() {
        if (!this.finished) throw new RuntimeException("SimulatorSettings is not finished");

        switch (this.chosenModulator) {
            case ASK:
                return ModulatorFactory.createASKModulator(this.modulatorParameters);
            case FSK:
                break;
            case QAM:
                break;
        }

        return null;
    }

    private final ActionListener applySettings = e -> {
        ArrayList<String> params = new ArrayList<>();
        for (String s : this.parameterNames) {
            Pair<Label, TextField> p = this.parameterInputs.get(s);
            if (p.second.getText().isBlank() || p.second.getText().isEmpty()) return;

            params.add(p.second.getText());
        }

        this.modulatorParameters.clear();
        this.modulatorParameters.addAll(params);
    };

    private final ActionListener runSimulation = e -> {
        this.applySettings.actionPerformed(null);
        if (this.modulatorParameters.isEmpty()) return;

        synchronized (this.lock) {
            this.finished = true;
            this.lock.notifyAll();
        }
    };

    private void createParamInputs(String[] parameters) {
        for (Pair<Label, TextField> p : this.parameterInputs.values()) {
            this.remove(p.first);
            this.remove(p.second);
        }
        this.parameterInputs.clear();

        this.parameterNames = parameters;

        int pos = PADDING.height;
        for (String p : parameters) {
            Label label = new Label(p);
            label.setLocation(PADDING.width, pos);
            label.setSize(PARAMS_SIZE);
            this.add(label);
            label.setVisible(true);

            TextField tField = new TextField();
            tField.setLocation(PARAMS_SIZE.width + PADDING.width, pos);
            tField.setSize(PARAMS_SIZE);
            this.add(tField);
            tField.setVisible(true);

            this.parameterInputs.put(p, new Pair<>(label, tField));

            pos += PARAMS_SIZE.height;
        }
    }

    private void init() {
        this.setLayout(null);
        this.setResizable(false);
        this.setSize(2 * PADDING.width + 2 * PARAMS_SIZE.width, 900);

        String[] parameters = ModulatorType.getParameters(this.chosenModulator);
        createParamInputs(parameters);

        this.useECCCheckBox.setSize(PARAMS_SIZE);
        this.useECCCheckBox.setLocation(PADDING.width, this.parameterInputs.size() * PARAMS_SIZE.height + 2 * PADDING.height);
        this.useECCCheckBox.setVisible(true);
        this.add(this.useECCCheckBox);

        this.applySettingsButton.setSize(PARAMS_SIZE.width / 2, PARAMS_SIZE.height);
        this.applySettingsButton.setLocation(this.getWidth() - this.applySettingsButton.getWidth() - PADDING.width, this.getHeight() - this.applySettingsButton.getHeight() - PADDING.height);
        this.applySettingsButton.addActionListener(this.applySettings);
        this.applySettingsButton.setVisible(true);
        this.add(this.applySettingsButton);

        this.runSimulationButton.setSize(PARAMS_SIZE.width / 2, PARAMS_SIZE.height);
        this.runSimulationButton.setLocation(this.getWidth() - PARAMS_SIZE.width - PADDING.width, this.getHeight() - this.runSimulationButton.getHeight() - PADDING.height);
        this.runSimulationButton.addActionListener(this.runSimulation);
        this.runSimulationButton.setVisible(true);
        this.add(this.runSimulationButton);

        MenuBar menuBar = new MenuBar();
        for (ModulatorType mt : ModulatorType.values()) {
            ModulatorMenuItem mi = new ModulatorMenuItem(mt.name(), mt);
            mi.addActionListener(e -> {
                this.chosenModulator = mi.getType();
                createParamInputs(ModulatorType.getParameters(mi.getType()));
            });

            this.modulatorMenu.add(mi);
        }
        menuBar.add(this.modulatorMenu);
        this.setMenuBar(menuBar);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                synchronized (SimulatorSettings.this.lock) {
                    SimulatorSettings.this.finished = true;
                    SimulatorSettings.this.lock.notifyAll();
                }
            }
        });

        this.setVisible(true);
    }

    @Override
    public void run() {
        init();
    }

    public boolean isFinished() {
        return this.finished;
    }

    public ModulatorType getChosenModulator() {
        return this.chosenModulator;
    }

    public ArrayList<String> getModulatorParameters() {
        return this.modulatorParameters;
    }

    public boolean useECC() {
        return this.useECCCheckBox.getState();
    }
}
