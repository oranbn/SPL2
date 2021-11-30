package bgu.spl.tigbur;

public class Car implements Vehicle {

    int meter;
    int speed;
    boolean engineStatus=true;

    /**
     * @return the vehicle's speed
     */
    public int getSpeed() {
        return speed;
    }

    @Override
    public int getMeter() {
        // TODO Auto-generated method stub
        return meter;
    }

    @Override
    public boolean engineStatus() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getPassengers() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCapacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setSpeed(int speed) {
        this.speed = speed;
        // TODO Auto-generated method stub
        
    }

    @Override
    public void drive(int time) {
        
        if (speed <= 0) {
            throw new IllegalArgumentException("speed must be positive");
        }
        if (engineStatus == false) {
            throw new IllegalStateException("engine is off");
        }
        meter += speed * time;      
    }

    @Override
    public void removePassengers(int passengers) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addPassengers(int passengers) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void fixEngine() {
        this.engineStatus = true;
        // TODO Auto-generated method stub
        
    }

    @Override
    public void breakEngine() {
        this.engineStatus = false;
        // TODO Auto-generated method stub
        
    }


    public static void main( String[] args )
    {
        System.out.println( "This is car main!" );
    }
}
