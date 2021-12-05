package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    Future<Integer> f;
    Integer result;
    boolean isResolved = false;
    @Before
    public void setUp() throws Exception {
        f = new Future<>();
        result = 555;
    }

    @After
    public void tearDown() throws Exception {
        // reset future
        f = new Future<>();
    }

    @Test
    public void get() {
        Thread t = new Thread(()->
        {
            try{
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            f.resolve(result);
            isResolved = true;
        });
        t.start();
        try {
            t.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(isResolved,false);
        try {
            t.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(isResolved,true);
        assertEquals(f.get(), result);
    }

    @Test
    public void resolve() {
        f.resolve(result);
        assertEquals(f.get(),result);
    }

    @Test
    public void isDone() {
        assertEquals(f.isDone(), false);
        f.resolve(result);
        assertEquals(f.isDone(), true);
    }

    @Test
    public void testGet() {
        long timeout = 1;
        TimeUnit unit = TimeUnit.SECONDS;
        Thread t1 = new Thread() {
            public void run()
            {
                try {
                    unit.sleep(timeout*5);
                }
              catch (InterruptedException e)
              {
                 f.resolve(result);
              }
            }
        };
        t1.start();
        assertEquals(f.get(timeout,unit), null);
        try{
          unit.sleep(timeout);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        t1.interrupt();
        assertEquals(f.get(timeout,unit), result);                                               ///ORAN ORAN ORAN ORAN ORAN ORAN ORAN//

    }
}