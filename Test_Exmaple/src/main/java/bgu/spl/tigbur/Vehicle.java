package bgu.spl.tigbur;

public interface Vehicle {

    /**
     * @return the vehicle's speed
     */
    int getSpeed();

    /**
     * @return the vehicle's meter
     */
    int getMeter();

    /**
     * @return the vehicle's engine status (true if the engine is fine, false if it is broken)
     */
    boolean engineStatus();

    /**
     * @return the number of passengers in the vehicle
     */
    int getPassengers();

    /**
     * @return true if the vehicle is empty
     */
    default boolean isEmpty()
    {
        return getPassengers() == 0;
    }

    /**
     * @return the vehicle's capacity
     */
    int getCapacity();

    /**
     * @param speed the vehicle's speed
     * @pre: None
     * @post: this.getSpeed() == speed
     */
    void setSpeed(int speed);
    
    /**
     * @param time time the care will driver
     * @pre: this.getSpeed() > 0
     * @inv: this.engineStatus() == true
     * @post: this.getMeter() == @pre(this.getMeter()) + @pre(this.getSpeed()) * time
     */
    void drive(int time);

    /**
     * @param passengers the number of passengers to remove from the vehicle
     * @pre: this.getPassengers() >= passengers
     * @post: this.getPassengers() == @pre(this.getPassengers()) - passengers
     */
    void removePassengers(int passengers);

    /**
     * @param passengers the number of passengers to add to the vehicle
     * @pre: this.getPassengers() + passengers <= this.getCapacity()
     * @post: this.getPassengers() == @pre(this.getPassengers()) + passengers
     */
    void addPassengers(int passengers);

    /**
     * @pre: this.getSpeed() == 0
     * @pre: this.engineStatus() == false
     * @pre: isEmpty() == true
     * @post: this.engineStatus() == true
     */
    void fixEngine();

    /**
     * @pre: this.getSpeed() > 0
     * @pre: this.engineStatus() == true
     * @post: this.engineStatus() == false
     */
    void breakEngine();

}
