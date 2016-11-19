package hu.tinca.mandelbrot;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * A panel that uses MediaTracker internally to display the BufferedImage
 * associated with it.
 */
public class Viewer extends JPanel {
    private BufferedImage off;
    private MediaTracker tracker;
    private static final int ID = 0;

    Viewer(BufferedImage img) {
        off = img;
        tracker = new MediaTracker(this);
        tracker.addImage(off, ID);
        setPreferredSize(new Dimension(off.getWidth(), off.getHeight()));
        setBackground(Color.BLACK);
    }


    public void update(Graphics g) {
        repaint();
    }

    public int getImageWidth() {
        return off.getWidth();
    }

    public int getImageHeight() {
        return off.getHeight();
    }

    /**
     * Just blit our image buffer to the screen
     */
    public void paint(Graphics g) {
        if ((tracker.statusID(ID, true) & MediaTracker.ERRORED) != 0) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            return;
        }
        g.drawImage(off, 0, 0, null);
    }

    /**
     * Updates partial image determined by slice with rgb data.
     * @param s
     * @param rgb
     */
    synchronized public void update(Slicer.Slice s, int[][] rgb) {
        BufferedImage img = off.getSubimage(s.getLoBound(), 0, s.getWidth(), off.getHeight());
        int iMax = rgb.length;
        int jMax = rgb[0].length;
        for ( int i = 0; i < iMax; i++ ) {
            for ( int j = 0; j < jMax; j++ ) {
                img.setRGB(i, j, rgb[i][j]);
            }
        }
        repaint();
    }

}
