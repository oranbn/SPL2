package bgu.spl.tigbur;
import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.After;
import org.junit.Test;


public class CarTest {

    private static Car car;

    @Before
    public void setUp() throws Exception {
        car = new Car();
    }


    @Test
    public void testSetSpeed() {
        car.setSpeed(10);
        assertEquals(10, car.getSpeed());
    }
    
    @Test
    public void testGetSpeed() {
        car.setSpeed(10);
        assertEquals(10, car.getSpeed());
    }

    @Test
    public void testGetCapacity() {
        assertEquals(5, car.getCapacity());
    }

    @Test
    public void testIsEmpty() {
        int numOfPassengers = car.getPassengers();
        car.removePassengers(numOfPassengers);
        assertTrue(car.isEmpty());
        car.addPassengers(1);
        assertFalse(car.isEmpty());
    }

    @Test
    public void testGetPassengers() {
        int numOfPassengers = car.getPassengers();
        car.removePassengers(numOfPassengers);
        assertEquals(0, car.getPassengers());
        car.addPassengers(1);
        assertEquals(1, car.getPassengers());
    }

    @Test
    public void testRemovePassengers() {
        int numOfPassengers = car.getPassengers();
        car.removePassengers(numOfPassengers);
        assertEquals(0, car.getPassengers());
        car.addPassengers(1);
        car.removePassengers(1);
        assertEquals(0, car.getPassengers());
        assertThrows(Exception.class, () -> car.removePassengers(1));
    }

    @Test
    public void testAddPassengers() {
        int numOfPassengers = car.getPassengers();
        car.addPassengers(1);
        assertEquals(numOfPassengers, car.getPassengers()+1);        
        assertThrows(Exception.class, () ->car.addPassengers(car.getCapacity()));
    }

    @Test
    public void testDrive() {
        car.setSpeed(10);
        car.fixEngine();
        car.drive(1);
        // assertEquals(10, car.getMeter());
        car.setSpeed(0);
        assertThrows("Expected to fail the drive when speed <= 0",Exception.class, () ->car.drive(1));
        car.setSpeed(-10);
        assertThrows(Exception.class, () ->car.drive(1));
        int speed = 100;
        int time = 5;
        int prevMeter = car.getMeter();
        car.setSpeed(speed);
        car.drive(5);
        assertEquals("Expected the meter to be the prod of speed and drive time + previous meter",speed*time+prevMeter, car.getMeter());
        car.breakEngine();
        assertThrows(Exception.class, () ->car.drive(1));

    }
}
