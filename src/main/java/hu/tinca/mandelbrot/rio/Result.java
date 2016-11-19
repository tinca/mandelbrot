package hu.tinca.mandelbrot.rio;

import net.jini.core.entry.Entry;

/**
 *
 */
public class Result implements Entry {
    private static final long serialVersionUID = -474939003295070130L;
    public int[][] rgb;
    public Integer part;

    public Result() {}

    public Result(int[][] rgb, int part) {
        this.rgb = rgb;
        this.part = part;
    }

}
