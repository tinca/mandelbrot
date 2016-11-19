package hu.tinca.mandelbrot.rio;

import hu.tinca.mandelbrot.Slicer;
import net.jini.core.entry.Entry;
import hu.tinca.mandelbrot.Mandelbrot;

/**
 *
 */
public class MandelbrotTask extends  AbstractTask {
    private static final long serialVersionUID = -8032117769192543366L;

    @SuppressWarnings("WeakerAccess")
    public Integer width, height;
    @SuppressWarnings("WeakerAccess")
    public Double xc, yc, size;
    @SuppressWarnings("WeakerAccess")
    public Slicer.Slice slice;


    public MandelbrotTask(int width, int height, double xc, double yc, double size, Slicer.Slice s) {
        this.width = width;
        this.height = height;
        this.xc = xc;
        this.yc = yc;
        this.size = size;
        slice = s;
    }

    public Entry execute() throws Exception {
        Mandelbrot mb = new Mandelbrot(width, height);
        int[][] rgb = mb.calculate(xc, yc, size, slice.getLoBound(), slice.getHiBound());
        return new Result(rgb, slice.getIndex());
    }


}
