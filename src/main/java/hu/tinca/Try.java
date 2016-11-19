package hu.tinca;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 *
 */
public class Try {

    static public void main(String[] s) {
       new Try().doIt();
    }

    void doIt() {
        List<StringIt> sit = new ArrayList<StringIt>();
        sit.add(new StringIt("a1", "a2"));
        sit.add(new StringIt("b1", "b2", "b3"));
        sit.add(new StringIt("c1", "c2"));
        MyIt mit = new MyIt(sit);
        while (mit.hasNext()) {
            System.out.println(mit.next());
        }
    }
    class MyIt implements Iterator {
        List<StringIt> l;
        List<String> res;

        MyIt(List<StringIt> l) {
            this.l = new ArrayList<StringIt>(l);
            res = new ArrayList<String>();
            for (StringIt it : l) {
                res.add("");
            }
        }

        public boolean hasNext() {
            return index < l.size();
        }

        public Object next() {
            if ( !hasNext() ) {
                throw new NoSuchElementException();
            }

            create();
            return res;
        }

        public void remove() {

        }

        int index = 0;
        void create() {
            if ( !hasNext() ) {
                return;
            }

            StringIt it = l.get(index);
            if ( it.hasNext()) {
                res.set(index, it.next());
                if ( !isLast(index) ) {
                    index++;
                }
                else {
                    return;
                }
            }
            else {
               it.init();
               index--;
            }
            create();
        }


        boolean isLast(int index) {
            return index == l.size()-1;
        }
    }


    
    class StringIt implements Iterator<String> {
        String[] s;
        int index = 0;

        public StringIt(String... st) {
            s = st;
        }

        public boolean hasNext() {
            return index < s.length;
        }

        public String next() {
            return s[index++]; //TODO: implement
        }

        public void remove() {
            //TODO: implement
        }

        public void init() {
            index = 0;
        }
    }
}
