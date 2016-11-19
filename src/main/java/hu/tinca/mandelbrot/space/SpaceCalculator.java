package hu.tinca.mandelbrot.space;

import hu.tinca.mandelbrot.Calculator;
import hu.tinca.mandelbrot.Slicer;
import hu.tinca.mandelbrot.Viewer;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The master that creates distributed running tasks which are configured to calculate partial Mandelbrot
 * sets.
 * For simplicity master uses Viewer for displaying the results.
 * associated.
 */
public class SpaceCalculator implements Calculator {
    private static final Logger logger = Logger.getLogger(SpaceCalculator.class.getName());
    Slicer slicer;
    Viewer viewer;
    JsHelper jsHelper;

    public SpaceCalculator(Viewer v, int parts, ServiceDiscoveryListener l) {
        slicer = new Slicer(v.getWidth(), parts);
        this.viewer = v;
        jsHelper = new JsHelper(l);
    }

    /**
     * Here starts the distribution magic: entries containing data for partial Mandelbrot calculation
     * are written to the space.
     *
     * @param xc
     * @param yc
     * @param size
     */
    public void calculate(double xc, double yc, double size) {
        Transaction txn = null;
        try {
            JavaSpace space = jsHelper.getSpace();
            txn = jsHelper.getTransaction();
            for (Slicer.Slice s : slicer) {
                // create task
                MandelbrotEntry task = new MandelbrotEntry(viewer.getWidth(), viewer.getHeight(), xc, yc, size, s);
                // place into space
                // avoid partial task distribution, use transaction
                space.write(task, txn, Lease.FOREVER);
            }
            txn.commit();
            // get the fruits
            collectResult(space);
        }
        catch (Exception e) {
            // normally a retry policy is need, but it's OK now
            logger.log(Level.SEVERE, "", e);
            try {
                txn.abort();
            }
            catch (Exception e1) {
                logger.log(Level.SEVERE, "txn abort", e1);
            }
        }
    }


    private void collectResult(JavaSpace space) throws RemoteException, TransactionException, UnusableEntryException, InterruptedException {
        // TODO: why snapshot?
        Entry snapshot = space.snapshot(new ResultEntry());
        ResultEntry res;
        while ((res = (ResultEntry) space.take(snapshot, null, 5000)) != null) {
            logger.info("Got result of part " + res.part);
            // do not care order, but need to know which task
            viewer.update(slicer.getSlice(res.part), res.rgb);
        }
    }

}
