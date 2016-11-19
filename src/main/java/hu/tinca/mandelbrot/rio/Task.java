package hu.tinca.mandelbrot.rio;

/**
 *
 */
import net.jini.core.entry.Entry;

/**
 * Must have public visibility due to JINI requirements.
 */
@SuppressWarnings("WeakerAccess")
public interface Task extends Entry {
    Entry execute() throws Exception;
}
