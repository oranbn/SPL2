package bgu.spl.mics;

import java.util.Iterator;
import java.util.List;

public class RoundRobinImpl<T> implements Iterable<T> {
    private List<T> coll;
    private int index = 0;

    public RoundRobinImpl(List<T> coll) {
        this.coll = coll;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return true;
            }
            @Override
            public T next() {
                if (index >= coll.size()) {
                    index = 0;
                }
                T res = coll.get(index++);
                return res;
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}