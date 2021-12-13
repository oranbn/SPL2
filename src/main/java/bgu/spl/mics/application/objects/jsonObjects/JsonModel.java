package bgu.spl.mics.application.objects.jsonObjects;

import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class JsonModel {
    private String name;
    private Data data;
    private Model.Status status;
    private Model.Results results;

    public JsonModel(Model model) {
        this.name = model.getName();
        this.data = model.getData();
        this.status = model.getStatus();
        this.results = model.getResults();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Model.Status getStatus() {
        return status;
    }

    public void setStatus(Model.Status status) {
        this.status = status;
    }

    public Model.Results getResults() {
        return results;
    }

    public void setResults(Model.Results results) {
        this.results = results;
    }
}
