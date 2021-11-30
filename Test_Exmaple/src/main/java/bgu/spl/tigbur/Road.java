package bgu.spl.tigbur;

import java.util.Vector;

public class Road {


    public class VehicleData{
        public Vehicle v;
        public int location;
        public int lane;

        public VehicleData(Vehicle v, int location, int lane){
            this.v = v;
            this.location = location;
            this.lane = lane;
        }

        public Vehicle getV() {
            return v;
        }

        public int getLocation() {
            return location;
        }

        public int getLane() {
            return lane;
        }    
        
        public void setLocation(int location) {
            this.location = location;
        }
    }

    private int maxSpeed;
    private int lanes;
    private int length;
    private Vector<VehicleData> vehicles;

    public Road(int maxSpeed, int lanes, int length) {
        this.maxSpeed = maxSpeed;
        this.lanes = lanes;
        this.length = length;
        this.vehicles = new Vector<>();
    }


    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getLanes() {
        return lanes;
    }

    public int getLength() {
        return length;
    }



    public Vector<VehicleData> getVehicles() {
        return vehicles;
    }

    /**
     * @param vehicle the vehicle
     * @param lane - the lane number     * 
     * @pre (v : this.getVehicles) v.location != 0 && v.lane!=lane
     * @post vehicles.size() == vehicles.size()@pre + 1
     * @post vehicles.last_element().v == vehicle
     */
    public void addVehicle(Vehicle vehicle,int lane) {
        for(VehicleData vd : vehicles){
            if(vd.getLocation() == 0 && vd.getLane() == lane){
                throw new IllegalArgumentException("The location in lane is occupied");
            }
        }
        vehicles.add(new VehicleData(vehicle,0,lane));
    }

    /**
     * 
     * @param vehicle
     * @param time
     * 
     * @pre vehicle.v is in the road
     * @inv (t : this.getVehicles) t != v && t.getLane() == v.getLane() 
     * && (t.getLocation() < v.getLocation() || t.getLocation() > (v.getLocation()+@pre(v.getSpeed())*time))
     * @post v.location = @pre(v.location)+@pre(v.getSpeed())*time
     * @post v.location <= this.length
     */
    public void driveVehicle(VehicleData vehicle, int time) {
        //TODO: implement this method
    }

    /**
     * 
     * @param vehicle
     * @param time > 0
     * 
     * @post (v : this.getVehicles) v.location <= this.length
     */
        
     /**
      * 
      * @param time > 0
      * @pre  (v : this.getVehicles) v.location + v.getSpeed()*time <= this.length
      * @post  (v : this.getVehicles) v.location = @pre(v.location) + v.getSpeed()*time
      */
    public void driveAllVehicles(int time) {
        Vector<VehicleData> vec = this.getVehicles();
        for (VehicleData v : this.getVehicles()) {
            if(v.getLocation()+v.getV().getSpeed()*time > this.getLength()){
                throw new IllegalArgumentException("The vehicle is out of the road");
            } 
        }
        for (VehicleData v : this.getVehicles()) {
            v.setLocation(v.getLocation()+v.getV().getSpeed()*time);
        }
    }

    /**
     * @post  (v : this.getVehicles) v.location = this.length - @pre(v.location)
     */
    public void swapDirection()
    {

    }

    /**
     * @post vehicle.v.getSpeed() = @pre(vehicle.v.getSpeed()) +speed
     * @pre vehicle.v.getSpeed()+speed <= this.getMaxSpeed()
     * @pre vehicle.v.getSpeed()+speed >= 0
     */
    public void addSpeed(VehicleData vehicle,int speed)    
    {
        if(vehicle.getV().getSpeed()+speed > this.getMaxSpeed()){
            throw new IllegalArgumentException("The vehicle's speed is too high");
        }
        if(vehicle.getV().getSpeed()+speed < 0){
            throw new IllegalArgumentException("The vehicle's speed is too low");
        }
        vehicle.getV().setSpeed(vehicle.getV().getSpeed()+speed);
    }    
}
