package bgu.spl.mics.application.objects;
/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {


    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    /**
     * Enum representing the Data Status.
     */
    enum Status {
        PreTrained, Training, Trained, Tested;
    }
    /**
     * Enum representing the Data Results.
     */
    enum Results {
        None, Good, Bad;
    }

    private String name;
    private Data data;
    private Student student;
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

    public void setStatus(Status status) {
        this.status = status;
    }
    public Student getStudent() {
        return student;
    }
}
