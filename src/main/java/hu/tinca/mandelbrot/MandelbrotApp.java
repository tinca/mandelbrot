package hu.tinca.mandelbrot;

import hu.tinca.mandelbrot.local.LocalCalculator;
import hu.tinca.mandelbrot.space.SpaceCalculator;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;

/**
 *
 */
public class MandelbrotApp extends JFrame {
    private Calculator localCalculator;
    private Calculator distrCalculator;
    private DoubleValuedSlider a, b , size;
    private Viewer panel;
    private ChangeListener changeListener;
    private Calculator distributor;


    private MandelbrotApp(int w, int h) {
        System.setSecurityManager(new SecurityManager());
        setBackground(Color.gray);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init(w, h);
        pack();
        setVisible(true);
    }

    private void init(int w, int h) {
        setLayout(new GridBagLayout());
        panel = new Viewer(new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB));

        JPanel presetPanel = createPresetPanel();
        JPanel paramPanel = createParameterPanel();

        add(panel, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, CENTER, BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        add(presetPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.1, CENTER, HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(paramPanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.1, CENTER, HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        changeListener = createChangeListener();
        addListeners(changeListener);
    }

    private JPanel createPresetPanel() {
        JPanel presetPanel = new JPanel();
        JComboBox preSets = createPreSets();
        presetPanel.add(preSets);

        JPanel distrPanel = createDistributionPanel();
        presetPanel.add(distrPanel);
        return presetPanel;
    }

    private JPanel createDistributionPanel() {
        JPanel distrPanel = new JPanel();
        final JRadioButton localRB = new JRadioButton("Local");
        final JRadioButton distrRB = new JRadioButton("Distributed");
        distrRB.setEnabled(false);

        ActionListener al = e -> {
            boolean lcl = e.getSource() == localRB;
            if ( localCalculator == null ) {
                initCalculators(distrRB);
            }

            distributor = lcl ? localCalculator : distrCalculator;
            setAllSliders(PreSet.PS5);
        };


        localRB.addActionListener(al);
        distrRB.addActionListener(al);
        ButtonGroup bg = new ButtonGroup();
        bg.add(localRB);
        bg.add(distrRB);
        distrPanel.add(localRB);
        distrPanel.add(distrRB);

        return distrPanel;
    }

    private JPanel createParameterPanel() {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new GridBagLayout());
        a = new DoubleValuedSlider(-2.5, 1);
        b = new DoubleValuedSlider(-1.25, 1.25);
        size = new DoubleValuedSlider(-1.5, 3.5);


        paramPanel.add(new JLabel("a:"), new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        paramPanel.add(a, new GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        paramPanel.add(new JLabel("b:"), new GridBagConstraints(
                0, 1, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        paramPanel.add(b, new GridBagConstraints(
                1, 1, 1, 1, 0.0, 0.0, CENTER, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        paramPanel.add(new JLabel("size:"), new GridBagConstraints(
                0, 2, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
        paramPanel.add(size, new GridBagConstraints(
                1, 2, 1, 1, 0.0, 0.0, CENTER, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        return paramPanel;
    }

    private void initCalculators(AbstractButton b) {
        localCalculator = new LocalCalculator(panel, 4);
        MyServiceDiscoveryListener sdl = new MyServiceDiscoveryListener(b);
        distrCalculator = new SpaceCalculator(panel, 4, sdl);
    }

    private JComboBox<PreSet> createPreSets() {
        JComboBox<PreSet> jc = new JComboBox<>();
        jc.addItem(PreSet.PS1);
//        jc.addItem(PreSet.PS2);
        jc.addItem(PreSet.PS3);
        jc.addItem(PreSet.PS4);
        jc.addItem(PreSet.PS5);
        jc.addItem(PreSet.PS6);
        jc.addItemListener(new ItemListener() {

            /**
             * Invoked when an item has been selected or deselected by the user.
             * The code written for this method performs the operations
             * that need to occur when an item is selected (or deselected).
             */
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    PreSet ps = (PreSet)e.getItem();
                    distributor.calculate(ps.getX(), ps.getY(), ps.getSize());
                }
            }
        });

        return jc;
    }

    private ChangeListener createChangeListener() {
        return new ChangeListener() {

            /**
             * Invoked when the target of the listener has changed its state.
             *
             * @param e a ChangeEvent object
             */
            public void stateChanged(ChangeEvent e) {
                DoubleValuedSlider source = (DoubleValuedSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    double av = a.getRealValue();
                    double bv = b.getRealValue();
                    double sv = size.getRealValue();
//                    panel.update(av, bv, sv);
                    System.out.println("slider: {" + av  + ", " + bv + ", " + sv + "}");
                    distributor.calculate(av, bv, sv);
                }
            }
        };
    }

    private void addListeners(ChangeListener cl) {
        a.addChangeListener(cl);
        b.addChangeListener(cl);
        size.addChangeListener(cl);
    }

    private void removeListeners(ChangeListener cl) {
        a.removeChangeListener(cl);
        b.removeChangeListener(cl);
        size.removeChangeListener(cl);
    }

    private void setAllSliders(PreSet ps) {
        removeListeners(changeListener);
        a.setRealValue(ps.getX());
        b.setRealValue(ps.getY());
        size.setRealValue(ps.getSize());
        distributor.calculate(ps.getX(), ps.getY(), ps.getSize());
        addListeners(changeListener);
    }

    public static void main(String[] a) {
        SwingUtilities.invokeLater(() -> new MandelbrotApp(1400, 900));
    }

    private enum PreSet  {
        PS1(-1.0, 0.34, 0.07),
//        PS2(0.035, 0.035, 0.07),
        PS3(-0.25, 0.675, 0.122),
        PS4(-0.5, 0.01, -2.2),
        PS5(-0.4942, -0.1091, 2.0962),
        PS6(-0.8846, 0.2282, -0.0577);

        private double x, y, size;
        private String name;

        PreSet(double x, double y, double size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getSize() {
            return size;
        }

        public String toString() {
            return (ordinal() + 1) + ". pre-set";
        }
    }


    private class MyServiceDiscoveryListener implements ServiceDiscoveryListener {
        private AbstractButton button;

        MyServiceDiscoveryListener(AbstractButton b) {
            button = b;
        }

        public void serviceAdded(ServiceDiscoveryEvent e) {
            button.setEnabled(true);
        }

        public void serviceRemoved(ServiceDiscoveryEvent e) {
            button.setEnabled(false);
        }

        public void serviceChanged(ServiceDiscoveryEvent e) {

        }
    }
}