package hu.tinca.mandelbrot.rio;

/**
 *
 */
import net.jini.core.entry.Entry;

/**
 *
 */
@SuppressWarnings("WeakerAccess")
public class AbstractTask implements Task {
    public String clientID;
    private static final long serialVersionUID = 7293401670929667182L;

    public AbstractTask() {}

    public Entry execute() throws Exception {
        throw new UnsupportedOperationException();
    }
}
