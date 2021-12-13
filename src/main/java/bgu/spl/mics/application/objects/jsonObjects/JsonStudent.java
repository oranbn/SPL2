package bgu.spl.mics.application.objects.jsonObjects;

import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.ArrayList;
import java.util.List;

public class JsonStudent {
    private final String name;
    private final String department;
    private final Student.Degree status;
    private final int publications;
    private final int papersRead;
    private final List<JsonModel> trainedModels;

    public JsonStudent(String name, String department, Student.Degree status, int publications, int papersRead) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.publications = publications;
        this.papersRead = papersRead;
        this.trainedModels = new ArrayList<>();
    }
    public JsonStudent(Student student){
        this.name = student.getName();
        this.department = student.getDepartment();
        this.status = student.getStatus();
        this.publications = student.getPublications();
        this.papersRead = student.getPapersRead();
        this.trainedModels = new ArrayList<>();
    }
    public List<JsonModel> getTrainedModels() {
        return trainedModels;
    }
    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Student.Degree getStatus() {
        return status;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }
    public void addTrainedModel(JsonModel m){
        trainedModels.add(m);
    }
}
