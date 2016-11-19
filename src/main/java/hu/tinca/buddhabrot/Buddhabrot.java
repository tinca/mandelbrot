package hu.tinca.buddhabrot;

import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * Created on Feb 8, 2004
 * <p/>
 * Source code given to public domain. Enjoy.
 *
 * @author Aaron Davidson <aaron@spaz.ca>
 */
public class Buddhabrot extends JFrame {

    private Buddhabrot(final int w, final int h, final int tiles) {
        setBackground(Color.gray);
        setMinimumSize(new Dimension(h, w));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        Graphics g = getGraphics();
        g.translate(h, 0);
        AffineTransform transform = createTransform();

        for (int idx = 1; idx <= tiles; idx++) {
            final int i = idx;
            BufferedImage off = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Calculator c = new Calculator(g, transform, off, w, h, tiles, i);
            c.renderLoop(500);
        }

    }

    private AffineTransform createTransform() {
        AffineTransform identity = new AffineTransform();
        AffineTransform trans = new AffineTransform();
        trans.setTransform(identity);
        trans.rotate(Math.toRadians(90));

        return trans;
    }

    public static void main(String[] a) {
        SwingUtilities.invokeLater(() -> {
            Buddhabrot bb = new Buddhabrot(1000, 800, 1);
        });
    }


    /**
     * Override update method to prevent flicker
     */
    public void update(Graphics g) {
        repaint();
    }


    ///////////////////////////////////////////////////////////
    class Calculator {
        private static final int RED_DWELL = 8000;
        private static final int GREEN_DWELL = 2000;
        private static final int BLUE_DWELL = 500;

        private boolean stop = false;

        // exposure counters for each pixel & color
        private int[][] exposureBlue;
        private int[][] exposureRed;
        private int[][] exposureGreen;

        // max values for normalization
        private int maxexposureBlue;
        private int maxexposureRed;
        private int maxexposureGreen;

        // number of actual exposures
//        private int exposures = 0;
        // out image buffer for rendering
        private BufferedImage off;
        private int xStart = 0;
        private int xEnd = 0;
        private int tileWidth = 0;
        private int width = 0;
        private int height = 0;
        private Graphics2D g;
        private AffineTransform transform;


        Calculator(Graphics g, AffineTransform tr, BufferedImage off, int w, int h, int tiles, int tileIndex) {
            this.g = (Graphics2D) g;
            transform = tr;
            this.off = off;
            tileWidth = w / tiles;
            width = tileWidth * tiles;
            height = h;
            if (tiles == 1) { // whole picture
                xStart = 0;
                xEnd = w;
            }
            else {
                xStart = tileWidth * (tileIndex - 1);
                xEnd = xStart + tileWidth;
            }

            init(tileWidth, h);
            transform = createTransform();
        }


        private void init(int w, int h) {
            exposureBlue = new int[w][h];
            exposureRed = new int[w][h];
            exposureGreen = new int[w][h];
        }


        /**
         * We just keep adding samples and redrawing the screen. Forever.
         */
        void renderLoop(int runs) {
            while (!stop) {
                refresh(tileWidth, height);
                plot(runs);
            }
        }

        /**
         * Generates another round of sample exposures to add to the image
         *
         * @param samples number of samples to take
         */
        void plot(int samples) {
            double x, y;
            // iterate through some plots
            for (int n = 0; n < samples; n++) {
                // Choose a random point in same range
                x = random(-2.0, 1.0);
                y = random(-1.5, 1.5);

                if (iterate(x, y, false, BLUE_DWELL, exposureBlue)) {
                    iterate(x, y, true, BLUE_DWELL, exposureBlue);
//                    exposures++;
                }
                if (iterate(x, y, false, GREEN_DWELL, exposureGreen)) {
                    iterate(x, y, true, GREEN_DWELL, exposureGreen);
//                    exposures++;
                }
                if (iterate(x, y, false, RED_DWELL, exposureRed)) {
                    iterate(x, y, true, RED_DWELL, exposureRed);
//                    exposures++;
                }
            }
        }

        /**
         * Test a single coordinate against a given dwell value.
         *
         * @param x0     random x coordinate
         * @param y0     random y coordinate
         * @param drawIt if true, we fill in values
         * @param dwell  the dwell (bailout) value
         * @param expose exposure array to fill in results
         * @return true if we escaped before bailout
         */
        private boolean iterate(double x0, double y0, boolean drawIt, int dwell, int[][] expose) {
            double x = 0;
            double y = 0;
            double xnew, ynew;
            int ix, iy;

            for (int i = 0; i < dwell; i++) {
                xnew = x * x - y * y + x0;
                ynew = 2 * x * y + y0;

                if (drawIt && (i > 3)) {
                    ix = (int) (width * (xnew + 2.0) / 3.0);            // !!!!
                    iy = (int) (height * (ynew + 1.5) / 3.0);
                    if (ix >= 0 && iy >= 0 && ix < tileWidth && iy < height) {      // !!!!
                        expose[ix][iy]++; // rotate and expose point
                    }
                }
                if ((xnew * xnew + ynew * ynew) > 4) {
                    return true; // escapes
                }
                x = xnew;
                y = ynew;
            }
            return false;  // does not escape
        }

        /**
         * Find the largest exposure values for normalization
         */
        private void findMaxExposure() {
            maxexposureBlue = maxexposureRed = maxexposureGreen = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < tileWidth; j++) {
                    maxexposureBlue = Math.max(maxexposureBlue, exposureBlue[j][i]);
                    maxexposureRed = Math.max(maxexposureRed, exposureRed[j][i]);
                    maxexposureGreen = Math.max(maxexposureGreen, exposureGreen[j][i]);
                }
            }
        }


        /**
         * Pick a random value between min and max.
         */
        final double random(double min, double max) {
            return min + (Math.random() * Math.abs(max - min));
        }

        /**
         * Update screen bitmap with latest results
         */
        public synchronized void refresh(int width, int height) {
            findMaxExposure();
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    double blue = exposureBlue[j][i] / (maxexposureBlue / 2.5);
                    if (blue > 1) {
                        blue = 1;
                    }
                    double red = exposureRed[j][i] / (maxexposureRed / 2.5);
                    if (red > 1) {
                        red = 1;
                    }
                    double green = exposureGreen[j][i] / (maxexposureGreen / 2.5);
                    if (green > 1) {
                        green = 1;
                    }
                    Color c = new Color((int) (red * 255), (int) (green * 255), (int) (blue * 255));
                    off.setRGB(j, i, c.getRGB());
                }
            }

            g.drawImage(off, transform, null);
//            g.drawImage(off, xStart, 0, null);
        }

        private BufferedImage getCopy(BufferedImage image) {
            BufferedImage copy = new BufferedImage(off.getWidth(), off.getHeight(), off.getType());
            copy.setData(off.getData());
            return copy;
        }

    }

}
