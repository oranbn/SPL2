package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    public Data(Type type, int processed, int size){
        this.type = type;
        this.processed = processed;
        this.size = size;
    }

    public Type getType() {
        return type;
    }
    public int getSize() {
        return size;
    }
    public void process()
    {
        processed += 1000;
    }
}
