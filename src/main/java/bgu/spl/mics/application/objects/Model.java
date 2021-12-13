package bgu.spl.mics.application.objects;
/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    /**
     * Enum representing the Data Status.
     */
    public enum Status {
        PreTrained, Training, Trained, Tested;
    }
    /**
     * Enum representing the Data Results.
     */
    public enum Results {
        None, Good, Bad;
    }

    private final String name;
    private final Data data;
    private final Student student;
    private Status status;
    private Results results;

    public Model(String name, Data data, Student student, Status status, Results results){
        this.name = name;
        this.data = data;
        this.student = student;
        this.status = status;
        this.results = results;
    }
    public Data getData() {
        return data;
    }
    public Status getStatus() {
        return status;
    }
    public String getName() {
        return name;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public Student getStudent() {
        return student;
    }

    public Results getResults() {return results;}

    public void setResults(Results results) {this.results = results;}
}
