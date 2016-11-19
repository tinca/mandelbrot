package hu.tinca.mandelbrot.rio;

import java.util.logging.Logger;

import net.jini.space.JavaSpace;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import org.rioproject.associations.AssociationProxyUtil;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.jsb.ServiceBeanAdapter;
import org.rioproject.resources.util.ThrowableUtil;
import org.rioproject.watch.CounterWatch;
import org.rioproject.watch.StopWatch;

/**
 * Generic worker service that looks for AbstractTasks. How many instances
 * of worker is required is decided at deployment time.
 */
public class Worker extends ServiceBeanAdapter {
    private Thread job;
    private JavaSpace space;
    private TransactionManager txnManager;
    private CounterWatch rate;
    //    private ThroughputWatch throughput;
    private StopWatch meanTime;
    private long TIMEOUT;
    private Logger logger = Logger.getLogger(getClass().getName());

    public Object start(final ServiceBeanContext context) throws Exception {
        Object o = super.start(context);
        job.start();
        return o;
    }

    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        TIMEOUT = (Long) context.getConfiguration().getEntry(
                "tutorial.grid",
                "timeout",
                Long.class);
        job = new Thread(
                new TaskJob(context.getServiceBeanConfig().getInstanceID()));
        rate = new CounterWatch("rate");
//        throughput = new ThroughputWatch("throughput");
        meanTime = new StopWatch("meanTime");
        context.getWatchRegistry().register(rate);
//        context.getWatchRegistry().register(throughput);
        context.getWatchRegistry().register(meanTime);
    }

    /* Called by Rio. */
    public void setSpace(JavaSpace space) {
        logger.info("Set JavaSpace");
        this.space = space;
    }

    /* Called by Rio. */
    public void setTransactionManager(TransactionManager txnManager) {
        logger.info("Set Transaction Manager");
        this.txnManager = txnManager;
    }

    private class TaskJob implements Runnable {
        long id;

        TaskJob(long id) {
            this.id = id;
        }

        public void run() {
            try {
                logger.info("TaskJob [" + id + "], shapshotting task...");
                // wait for services to come up
                while (space == null || txnManager == null) {
                    Thread.sleep(500);
                }
                // we look for AbstractTask
                Entry template = space.snapshot(new AbstractTask());
                while (true) {
                    Transaction txn = TransactionFactory.create(
                                    AssociationProxyUtil.getService(txnManager),
                                    Lease.FOREVER).transaction;
                    Task task = (Task) space.takeIfExists(template, txn, TIMEOUT);
                    if (task == null) {
                        txn.abort();
                        Thread.sleep(200);
                        continue;
                    }
                    logger.info("Processing task: " + task);
                    meanTime.startTiming();
                    Entry result = task.execute();
                    meanTime.stopTiming();
                    space.write(result, txn, TIMEOUT);
                    txn.commit();
                    rate.increment();
//                    throughput.increment();
                }
            }
            catch (Exception e) {
                Throwable cause = ThrowableUtil.getRootCause(e);
                logger.info("TaskJob [" + id + "] exiting, caught exception " +
                        cause.getClass().getName() + ": " + cause.getMessage());
            }
        }
    }

//    public class ThroughputWatch extends PeriodicWatch {
//        private int numberOfCalls = 0;
//
//        public ThroughputWatch(String id) {
//            super(id);
//            super.setPeriod(1000);
//        }
//
//        public void checkValue() {
//            super.addWatchRecord(new Calculable("taux", numberOfCalls / (getPeriod() / 1000)));
//            numberOfCalls = 0;
//        }
//
//        public void increment() {
//            numberOfCalls++;
//        }
//    }
}
