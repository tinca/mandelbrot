package hu.tinca.mandelbrot;

import javax.swing.*;
import java.util.Hashtable;

/**
 *  A slider that provides double value handling.
 */
class DoubleValuedSlider extends JSlider {
    private final int FACTOR = 10000;
    private double min, max;

    private DoubleValuedSlider(double min, double max, double z0) {
        super(JSlider.HORIZONTAL);
        this.min = min;
        this.max = max;
        setMinimum(getIntValue(min));
        setMaximum(getIntValue(max));
        setValue(getIntValue(z0));
        init();
    }

    DoubleValuedSlider(double min, double max) {
        this(min, max, 0);
    }

    private void init() {
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(getMinimum(), new JLabel("" + min));
        labelTable.put(getMaximum(), new JLabel("" + max));
        setLabelTable(labelTable);
        setPaintLabels(true);
    }

    double getRealValue() {
        return (double) getValue() / FACTOR;
    }

    private int getIntValue(double v) {
        return (int)Math.round(v*FACTOR);
    }

    void setRealValue(double d) {
        setValue(getIntValue(d));
    }
}