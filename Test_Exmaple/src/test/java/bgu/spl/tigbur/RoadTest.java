package bgu.spl.tigbur;
import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import bgu.spl.tigbur.Road.VehicleData;

public class RoadTest {

    private static Road road;

    @Before
    public void setUp() throws Exception {
        road = new Road(100,4,1000);
        road.addVehicle(new Car(), 1);
        road.addVehicle(new Car(), 2);
        road.addVehicle(new Car(), 3);
        road.addVehicle(new Car(), 4);
    }

    @Test
    public void testAddVehicle() {
        int size = road.getVehicles().size();
        assertThrows("Expected to fail when the location in lane is occupied",Exception.class, () -> road.addVehicle(new Car(), 1));
        for (VehicleData v : road.getVehicles()) {
            road.addSpeed(v, 10);
        }
        road.driveAllVehicles(5);
        road.addVehicle(new Car(), 1);
        road.addVehicle(new Car(), 2);
        road.addVehicle(new Car(), 3);
        Car v = new Car();        
        road.addVehicle(v, 4);
        assertEquals(size + 4, road.getVehicles().size());
        assertEquals(road.getVehicles().lastElement().v, v);
        assertThrows("Should throw exception (road length)",Exception.class, () -> road.driveAllVehicles(1000));
    }    
    
    @Test
    public void testAddSpeed() {
        VehicleData v = road.getVehicles().firstElement();
        road.addSpeed(v, 10);
        assertEquals(10, v.v.getSpeed());
        assertThrows(Exception.class, () -> road.addSpeed(v, 1000));
        assertThrows(Exception.class, () -> road.addSpeed(v, -1000));
    }

    @Test
    public void testdriveVehicle() {
        Vector<VehicleData> vec = road.getVehicles();
        for (VehicleData v : road.getVehicles()) {
            road.addSpeed(v, 10);
        }
        road.driveAllVehicles(5);
        road.addVehicle(new Car(), 1);
        assertThrows("Should throw exception (car crash)",Exception.class, () -> road.driveVehicle(road.getVehicles().lastElement(),10));
        int curLocation = vec.firstElement().getLocation();
        road.driveVehicle(road.getVehicles().firstElement(),100);
        assertEquals(curLocation + 100, vec.firstElement().getLocation());
        assertThrows("Should throw exception (road length)",Exception.class, () -> road.driveVehicle(road.getVehicles().lastElement(),500));
    }

    @Test
    public void testdriveAllVehicle() {
        Vector<VehicleData> vec = road.getVehicles();
        int locations[] = new int[vec.size()];
        for (VehicleData v : road.getVehicles()) {
            road.addSpeed(v, 10);
            locations[vec.indexOf(v)] = v.getLocation();
        }
        int time = 5;
        road.driveAllVehicles(time);
        for (VehicleData v : road.getVehicles()) {
            assertEquals(locations[vec.indexOf(v)] + time * v.getV().getSpeed(),v.getLocation());
        }
        assertThrows("Should throw exception (a vehicle exceeded the road size)",Exception.class, () -> road.driveAllVehicles(5000));
    }

    @Test
    public void testSwapDirection()
    {
        road.swapDirection();
        assertEquals(road.getVehicles().firstElement().location,0);
        assertEquals(road.getVehicles().lastElement().location,0);
        for (VehicleData v : road.getVehicles()) {
            road.addSpeed(v, 10);
        }
        road.driveAllVehicles(5);
        int[] oldLocations = new int[road.getVehicles().size()];
        for (int i = 0; i < road.getVehicles().size(); i++) {
            oldLocations[i] = road.getVehicles().get(i).location;
        }
        road.swapDirection();
        for (int i = 0; i < road.getVehicles().size(); i++) {
            assertEquals(oldLocations[i], road.getLength() - road.getVehicles().get(i).location);
        }

    }




    
}
