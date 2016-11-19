package hu.tinca.mandelbrot.space;

import hu.tinca.mandelbrot.Mandelbrot;
import hu.tinca.mandelbrot.Slicer;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.space.JavaSpace;

import java.util.logging.Logger;

/**
 * Jini client, that queries JavaSpaces for Mandelbrot entries, calculates the given
 * part and writes back the result.
 */
public class Worker {
    private static final Logger logger = Logger.getLogger(Worker.class.getName());
    private JsHelper jsHelper;

    private void execute() {
        Thread t = new Thread() {
            public void run() {
                while (true) {
                    executeOne();
                }
            }
        };
        t.start();
    }

    private void executeOne() {
        try {
            JavaSpace space = null;
            space = jsHelper.getSpace();
            Transaction txn = jsHelper.getTransaction();
            logger.info("Waiting for a task...");
            // place into space
            // avoid partial execution, use transaction
            MandelbrotEntry e = (MandelbrotEntry) space.take(new MandelbrotEntry(), txn, Lease.FOREVER);
            logger.info("Calculating task of part " + e.slice.getIndex() + "...");
            ResultEntry re = calculate(e.width, e.height, e.xc, e.yc, e.size, e.slice);
            logger.info("Task finished.");
            // put result
            space.write(re, txn, Lease.FOREVER);
            txn.commit();
        }
        catch (Exception e) {
            // normally a retry policy is need, but it's OK now
            logger.severe(e.getLocalizedMessage());
        }
    }

    private ResultEntry calculate(int width, int height, double xc, double yc, double size, Slicer.Slice s)
            throws Exception {
        
        Mandelbrot mb = new Mandelbrot(width, height);
        int[][] rgb = mb.calculate(xc, yc, size, s.getLoBound(), s.getHiBound());
        return new ResultEntry(rgb, s.getIndex());
    }

    public static void main(String[] s) {
        new Worker().execute();
    }
}
