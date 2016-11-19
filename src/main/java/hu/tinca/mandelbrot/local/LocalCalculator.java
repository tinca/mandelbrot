package hu.tinca.mandelbrot.local;

import hu.tinca.mandelbrot.Calculator;
import hu.tinca.mandelbrot.Viewer;
import hu.tinca.mandelbrot.Mandelbrot;
import hu.tinca.mandelbrot.Slicer;

/**
 * The master that creates locally running tasks which are configured to calculate partial Mandelbrot
 * sets and are run in separate threads.
 * For simplicity tasks uses Viewer for displaying the results.
 * associated.
 */
public class LocalCalculator implements Calculator {
    private Slicer slicer;
    private Viewer viewer;

    public LocalCalculator(Viewer v, int parts) {
        viewer = v;
        slicer = new Slicer(v.getWidth(), parts);
    }


    /**
     * Starts a Mandelbrot calculation with the starting complex coordinates.
     * @param xc
     * @param yc
     * @param size
     */
    public void calculate(double xc, double yc, double size) {
        for (Slicer.Slice s: slicer) {
            createWorker(xc,  yc, size, s);
        }

    }


    private void createWorker(final double xc, final double yc, final double size, final Slicer.Slice s) {
        Thread t = new Thread() {
            public void run() {
                Mandelbrot mb = new Mandelbrot(viewer.getImageWidth(), viewer.getImageHeight());
                int[][] rgb = mb.calculate(xc, yc, size, s.getLoBound(), s.getHiBound());
                viewer.update(s, rgb);
                // TODO: show progress within task!
            }
        };
        t.start();
    }

}
