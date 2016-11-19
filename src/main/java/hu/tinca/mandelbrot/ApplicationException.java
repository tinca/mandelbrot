package hu.tinca.mandelbrot;

/**
 *
 */
public class ApplicationException extends Exception{

    public ApplicationException() {
        super();
    }

    public ApplicationException(String s) {
        super(s);
    }

    public ApplicationException(Throwable t) {
        super(t);
    }
}
