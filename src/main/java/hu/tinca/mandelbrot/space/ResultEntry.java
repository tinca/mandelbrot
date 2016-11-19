package hu.tinca.mandelbrot.space;

import net.jini.core.entry.Entry;

/**
 *
 */
public class ResultEntry implements Entry {
    public int[][] rgb;
    public Integer part;

    public ResultEntry() {
    }

    public ResultEntry(int[][] rgb, int part) {
        this.rgb = rgb;
        this.part = part;
    }

}
