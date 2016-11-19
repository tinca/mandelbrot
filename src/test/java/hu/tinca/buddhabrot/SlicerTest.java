package hu.tinca.buddhabrot;

import hu.tinca.mandelbrot.Slicer;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 *
 */
public class SlicerTest {


    @Test
    public void slice() {
        Slicer sl = new Slicer(800, 3);
        int idx = 1;
        int w = 0;
        for ( Slicer.Slice s : sl ) {
            assertEquals(s.getIndex(), idx++);
            w+= s.getWidth();
        }

        assertEquals(w, 800);
    }
}
