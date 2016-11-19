package hu.tinca.mandelbrot.space;

import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.space.JavaSpace;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class JsHelper {
    private static final Logger logger = Logger.getLogger(JsHelper.class.getName());
    private TransactionManager txnManager;
    private JavaSpace space;
    ServiceDiscoveryManager sdm;
    ServiceDiscoveryListener sdl;

    public JsHelper(ServiceDiscoveryListener l) {
       init(l);
    }

    private void init(ServiceDiscoveryListener l) {
        sdl = l;
        try {
            DiscoveryListener dl = new MyDiscoveryListener();
            LookupDiscoveryManager dm = new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS, null, dl);
            sdm = new ServiceDiscoveryManager(dm, new LeaseRenewalManager());

        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "", e);
        }

    }
    
    /**
     *
     */
    public JavaSpace getSpace() {
        return space;
    }

    public Transaction getTransaction() throws LeaseDeniedException, RemoteException {
        return TransactionFactory.create(txnManager, Lease.FOREVER).transaction;
    }

 
    //////////////////////////////////////////////////////////////////////
    class MyDiscoveryListener implements DiscoveryListener {

        public void discovered(DiscoveryEvent event) {
//            ServiceItem spaceItem = null;
//            ServiceItem txnMngrItem = null;
            ServiceTemplate tmpl = new ServiceTemplate(null, new Class[]{JavaSpace.class}, null);
//            spaceItem = sdm.lookup(tmpl, null);

//            if ( spaceItem == null ) {
//            logger.severe("Can't find space service!");
//                return;
//            }
//            space = (JavaSpace) spaceItem.service;
//            txnMngrItem = sdm.lookup(new ServiceTemplate(null, new Class[]{TransactionManager.class}, null), null);
//            if (txnMngrItem == null) {
//                logger.severe("Can't find txn service!");
//                return;
//            }
//            txnManager = (TransactionManager) spaceItem.service;


            ServiceRegistrar[] srs = event.getRegistrars();
            if ( srs.length == 0 ) {
                logger.info("Discovered no registrar");
            }

            try {
                space = (JavaSpace)srs[0].lookup(tmpl);
            }
            catch (RemoteException e) {
//TODO
            }
            tmpl = new ServiceTemplate(null, new Class[]{TransactionManager.class}, null);
            try {
                txnManager = (TransactionManager)srs[0].lookup( tmpl);
            }
            catch (RemoteException e) {
//TODO
            }
            sdl.serviceAdded(null);

//            for (int i = 0; i < event.getRegistrars().length; i++) {
//                ServiceRegistrar registrar = event.getRegistrars()[i];
//                logger.info("Discovered registrar " + registrar.getServiceID());
//                try {
//                    Class[] proxies = registrar.getServiceTypes(tmpl, null);
//                    for (int j = 0; j < proxies.length; j++) {
//                        Class proxyClass = proxies[j];
//                        logger.info("Found proxy of class " + proxyClass);
//                    }
//                }
//                catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
        }

        public void discarded(DiscoveryEvent event) {
            logger.info("Discarded " + event);
            sdl.serviceRemoved(null);
        }
    }
}
