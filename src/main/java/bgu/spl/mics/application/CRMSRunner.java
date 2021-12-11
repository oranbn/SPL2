package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static int tickTime;
    public static int duration;
    public static List<StudentService> studentServices;
    public static List<GPUService> GPUServices;
    public static List<CPUService> CPUServices;
    public static List<ConferenceService> ConferenceServices;
    public static TimeService timeService;
    public static ExecutorService executor;
    public static void main(String[] args) {
        initFields();
        initObjects();
        runServices();
        timeService.isDone();
        executor.shutdown();
        System.out.println("oran3");
        try (Writer writer = new FileWriter("Output.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(studentServices.get(0).getStudent().getPapersRead(), writer);
        }
        catch (Exception e)
        {

        }
        System.out.println("oran2");
    }


    private static void runServices() {
        int threads=CPUServices.size()+GPUServices.size()+ConferenceServices.size()+studentServices.size()+1;
        executor = Executors.newFixedThreadPool(threads);
        for(CPUService cpuService: CPUServices)
            executor.execute(cpuService);
        for(GPUService gpuService: GPUServices)
            executor.execute(gpuService);
        for(ConferenceService conferenceService: ConferenceServices)
            executor.execute(conferenceService);
        for(StudentService studentService: studentServices)
            executor.execute(studentService);
        executor.execute(timeService);

    }

    public static void initFields(){
        studentServices = new ArrayList<>();
        GPUServices = new ArrayList<>();
        CPUServices = new ArrayList<>();
        ConferenceServices = new ArrayList<>();
    }
    public static void initObjects(){
        try {
            File inputFile = new File("C:\\Users\\yoavi\\OneDrive - post.bgu.ac.il\\Study\\Semester C\\SPL\\Projects\\hw2-OFFICIAL\\SPL2\\example_input.json");
            JsonElement fileElement = JsonParser.parseReader(new FileReader(inputFile));
            JsonObject fileObject = fileElement.getAsJsonObject();
            initStudents(fileObject.get("Students").getAsJsonArray());
            System.out.println("oran");
            initGPUS(fileObject.get("GPUS").getAsJsonArray());
            initCPUS(fileObject.get("CPUS").getAsJsonArray());
            initConferences(fileObject.get("Conferences").getAsJsonArray());
            tickTime = fileObject.get("TickTime").getAsInt();
            duration = fileObject.get("Duration").getAsInt();
            System.out.println("oran");
            timeService = new TimeService(tickTime,duration);
            System.out.println("oran");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    public static List<Model> initModel(JsonArray jsonArrayOfModels, Student student) {
        List<Model> modelList = new ArrayList<>();
        for(JsonElement modelElement : jsonArrayOfModels)
        {
            JsonObject modelJsonObject = modelElement.getAsJsonObject();
            String name = modelJsonObject.get("name").getAsString();
            String type = modelJsonObject.get("type").getAsString();
            int size = modelJsonObject.get("size").getAsInt();
            Data.Type dataType;
            if(type.equals("images"))
                dataType = Data.Type.Images;
            else if(type.equals("Text"))
                dataType = Data.Type.Text;
            else
                dataType = Data.Type.Tabular;
            Data data = new Data(dataType, 0, size);
            Model model = new Model(name,data,student,Model.Status.PreTrained, Model.Results.None);
            modelList.add(model);
        }
        return modelList;
    }
    public static void initStudents(JsonArray jsonArrayOfStudents){
        int index = 0;
        for(JsonElement studentElement : jsonArrayOfStudents)
        {
            JsonObject studentJsonObject = studentElement.getAsJsonObject();
            String name = studentJsonObject.get("name").getAsString();
            String department = studentJsonObject.get("department").getAsString();
            String status = studentJsonObject.get("status").getAsString();
            Student.Degree degree;
            if(status.equals("PhD"))
                degree = Student.Degree.PhD;
            else
                degree = Student.Degree.MSc;
            Student student = new Student(name, department, degree);
            List<Model> model = initModel(studentJsonObject.get("models").getAsJsonArray(), student);
            String serviceName = "studentService"+index;
            StudentService studentService = new StudentService(serviceName, student, model);
            studentServices.add(studentService);
            index++;
        }
    }
    public static void initGPUS(JsonArray jsonArrayOfGPUS){
        int id = 0;
        for(JsonElement GPUElement : jsonArrayOfGPUS) {
            String name = GPUElement.getAsString();
            GPU.Type type;
            if(name.equals("GTX1080"))
                type = GPU.Type.GTX1080;
            else if(name.equals("RTX2080"))
                type = GPU.Type.RTX2080;
            else
                type = GPU.Type.RTX3090;
            GPU gpu = new GPU(type, Cluster.getInstance(), id);
            String GPUServiceName = "GPUService"+id;
            GPUService GPUService = new GPUService(GPUServiceName, gpu);
            GPUServices.add(GPUService);
            id++;
        }
    }
    public static void initCPUS(JsonArray jsonArrayOfCPUS){
        int id = 0;
        for(JsonElement CPUElement : jsonArrayOfCPUS) {
            int cores = CPUElement.getAsInt();
            CPU cpu = new CPU(cores, Cluster.getInstance());
            String CPUServiceName = "CPUService"+id;
            CPUService CPUService = new CPUService(CPUServiceName, cpu);
            CPUServices.add(CPUService);
            id++;
        }
    }
    public static void initConferences(JsonArray jsonArrayOfConferences){
        int id = 0;
        for(JsonElement conferenceElement : jsonArrayOfConferences) {
            JsonObject conferenceJsonObject = conferenceElement.getAsJsonObject();
            String name = conferenceJsonObject.get("name").getAsString();
            int date = conferenceJsonObject.get("date").getAsInt();
            ConfrenceInformation confrenceInformation = new ConfrenceInformation(name, date);
            String conferenceServiceName = "ConferenceService"+id;
            ConferenceService conferenceService = new ConferenceService(conferenceServiceName, confrenceInformation);
            ConferenceServices.add(conferenceService);
            id++;
        }
    }
}