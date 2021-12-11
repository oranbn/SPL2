package bgu.spl.mics;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

/**
 * A "Marker" interface extending {@link Message}. A micro-service that sends an
 * Event message expects to receive a result of type {@code <T>} when a
 * micro-service that received the request has completed handling it.
 * When sending an event, it will be received only by a single subscriber in a
 * Round-Robin fashion.
 */
public interface Event<T> extends Message {
    /**
     * @return the mode; which the event is built around
     * @PRE: none
     * @POST: none
     */
    Model getModel();
    Future<T> getFuture();
    void setFuture(Future<T> future);
    Student getStudent();
}
