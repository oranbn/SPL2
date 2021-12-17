package bgu.spl.mics;

import java.util.Iterator;
import java.util.List;

public class RoundRobinImpl<MicroService> implements Iterable<MicroService> {
    private List<MicroService> coll;
    private int index = 0;

    public RoundRobinImpl(List<MicroService> coll) {
        this.coll = coll;
    }
    public Iterator<MicroService> iterator() {
        return new Iterator<MicroService>() {
            @Override
            public boolean hasNext() {
                return true;
            }
            @Override
            public MicroService next() {
                if (index >= coll.size()) {
                    index = 0;
                }
                MicroService res = coll.get(index++);
                return res;
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}