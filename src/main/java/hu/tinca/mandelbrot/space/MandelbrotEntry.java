package hu.tinca.mandelbrot.space;

import hu.tinca.mandelbrot.Slicer;
import net.jini.core.entry.Entry;

/**
 *
 */
public class MandelbrotEntry implements Entry {
    private static final long serialVersionUID = -8032117769192543366L;
    public Integer width, height;
    public Double xc, yc, size;
    public Slicer.Slice slice;

    public MandelbrotEntry() {
    }

    public MandelbrotEntry(int width, int height, double xc, double yc, double size, Slicer.Slice s) {
        this.width = width;
        this.height = height;
        this.xc = xc;
        this.yc = yc;
        this.size = size;
        slice = s;
    }


}
