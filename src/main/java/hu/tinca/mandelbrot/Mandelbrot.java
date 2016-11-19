package hu.tinca.mandelbrot;

import hu.tinca.math.Complex;

import java.awt.*;
import java.util.logging.Logger;


/**
 * Mandelbrot algorithm that can calculate partial complex areas of the area initially defined
 * and provides appropriate rgb values for displaying.
 */
public class Mandelbrot {
    private static Logger LOG = Logger.getLogger(Mandelbrot.class.getName());
    private static int MAX_ITERATIONS = 255;   // maximum number of iterations
    private int width, height;

    /**
     * 
     * @param w
     * @param h
     */
    public Mandelbrot(int w, int h) {
        width = w;
        height = h;
    }

    /**
     * Calculates an RGB array using the total and partial dimensions.
     * 
     * @param xc
     * @param yc
     * @param size
     * @param lo
     * @param hi
     * @return
     */
    public int[][] calculate(double xc, double yc, double size, int lo, int hi) {
        int[][] rgb = new int[hi-lo][height];
        for (int i = lo; i < hi; i++) {
            for (int j = 0; j < height; j++) {
                double x0 = xc - size / 2 + size * i / width;
                double y0 = yc - size / 2 + size * j / height;
                Complex z0 = new Complex(x0, y0);
                Color c = createColor(z0, MAX_ITERATIONS);
                rgb[i-lo][j] = c.getRGB();
            }
        }

        LOG.exiting(this.getClass().getName(), "calculate");
        return rgb;
    }


    /**
     * Returns number of iterations to check if c = a + ib is in Mandelbrot set
     * http://math.bu.edu/DYSYS/explorer/tour1.html
     * @param z0
     * @param it
     * @return
     */
    private int mand(Complex z0, int it) {
        Complex z = z0.copy();
        for (int t = 0; t < it; t++) {
            if (z.mod() > 2) {
                return t;
            }
            z = z.times(z).plus(z0);
        }
        return it;
    }

    
     /////////////////////////////////////

    private Color createColor(Complex z, int it) {
        int m = mand(z, it);
        double d = Math.log(2*Math.log(2) - Math.log(Math.log(z.mod() + 2))) + m;
        d /= Math.log(2);
        float f = (float)(d/(it-35));
        // another
//        double d = m + 1 - Math.log(Math.log(z.mod()));
//        d = d / Math.log(2);
//        if ( d == NaN)
//        System.out.println(m + " " + f);
//        System.out.println(m);
        return Color.getHSBColor( f, 0.8f, 1);
//        return new Color(m, 80*m/255, 120*m/255 );
    }

    //////////////////////////////////

    private Color createColor2(int v) {
        return Color.getHSBColor(v / 21F, 0.6F, 1F);
    }


}
