package hu.tinca.mandelbrot;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Divides a whole (integer) into approximately equal parts (integer values). Provides lower and upper
 * bound coordinates for parts.
 * 
 */
public class Slicer implements Iterable<Slicer.Slice>, Serializable {
    private int whole;
    private int parts;
    private int[] slices;

    public Slicer(int whole, int parts) {
        this.whole = whole;
        this.parts = parts <1 ? 1 : parts;
        slices = calcSlices();
    }

    private int[] calcSlices() {
        int slice = whole / parts;
        int rest = whole - slice * parts;
        int[] partsArr = new int[parts];
        for (int i = 0; i < parts; i++) {
            partsArr[i] = slice;
        }
        if ( rest != 0 ) {
            partsArr[parts-1]+= rest;
        }
        return partsArr;
    }

    public Iterator<Slice> iterator() {
        return new SliceIterator();
    }


    public Slice getSlice(int part) {
        return new Slice(part, slices[part-1], getLowBound(part), getHiBound(part));
    }


    private int getLowBound(int part) {
        if ( part <= 1 ) {
            return 0;
        }

        int sum = 0;
        for (int i = 0; i < part-1; i++) {
            sum += slices[i];
        }
        return sum;
    }

    private int getHiBound(int part) {
        if ( part == 1 ) {
            return slices[part-1];
        }

        int sum = 0;
        for (int i = 0; i < part; i++) {
            sum += slices[i];
        }
        return sum;
    }

    public class Slice implements Serializable {
        private int index;
        private int width;
        private int loBound;
        private int hiBound;

        Slice(int i, int w, int lo, int hi) {
            index = i;
            width = w;
            loBound = lo;
            hiBound = hi;
        }

        public int getWidth() {
            return width;
        }

        public int getLoBound() {
            return loBound;
        }

        public int getHiBound() {
            return hiBound;
        }

        public int getIndex() {
            return index;
        }
    }

    private class SliceIterator implements Iterator<Slice> {
        private int index = 1;

        public boolean hasNext() {
            return index < parts + 1;
        }

        public Slice next() {
            if ( !hasNext() ) {
                throw new NoSuchElementException();
            }

            return getSlice(index++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
