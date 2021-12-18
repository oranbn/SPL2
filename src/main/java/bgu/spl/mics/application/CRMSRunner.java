package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.jsonObjects.JsonConference;
import bgu.spl.mics.application.objects.jsonObjects.JsonModel;
import bgu.spl.mics.application.objects.jsonObjects.JsonOutput;
import bgu.spl.mics.application.objects.jsonObjects.JsonStudent;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    private static List<StudentService> StudentServices;
    private static List<GPUService> GPUServices;
    private static List<CPUService> CPUServices;
    private static List<ConferenceService> ConferenceServices;
    private static List<Thread> ConferenceServiceThreads;
    private static List<Thread> StudentServiceThreads;
    private static List<Thread> GPUServiceThreads;
    private static List<Thread> CPUServiceThreads;
    public static int ConferencesAmount;
    private static Thread timeServiceThread;
    private static TimeService timeService;

    private static ExecutorService executor;
    private static JsonOutput jsonOutput;
    public static void main(String[] args) {
        System.out.println("Starting Program");
        initFields();
        initObjects();
        runServices();
        waitTillDone();
        System.out.println("Making Output file");
        MakeOutputFile();
    }

    private static void MakeOutputFile() {
        jsonOutput = new JsonOutput();
        prepareOutput();
        try (Writer writer = new FileWriter("Output.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(jsonOutput, writer);
        }
        catch (Exception e)
        {

        }
        System.out.println("Done!");
    }

    private static void waitTillDone() {
        for(Thread t : StudentServiceThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(Thread t : GPUServiceThreads)
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        for(Thread t : CPUServiceThreads)
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        for(Thread t : ConferenceServiceThreads)
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        try {
            timeServiceThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static void prepareOutput() {
        for(StudentService studentService: StudentServices) {
            JsonStudent jsonStudent = new JsonStudent(studentService.getStudent());
            for(Model m: studentService.getModelList())
            {
                if(m.getStatus() == Model.Status.Trained || m.getStatus() == Model.Status.Tested)
                    jsonStudent.addTrainedModel(new JsonModel(m));
            }
            jsonOutput.addStudent(jsonStudent);
        }
        for(ConferenceService conferenceService: ConferenceServices){
            JsonConference jsonConference = new JsonConference(conferenceService.getConfrenceInformation());
            for(Model m : conferenceService.getConfrenceInformation().getModelList())
                jsonConference.addPublishedModel(new JsonModel(m));
            jsonOutput.addConference(jsonConference);
        }
    }


    private static void runServices() {
        for(CPUService cpuService : CPUServices)
        {
            Thread thread = new Thread(cpuService);
            CPUServiceThreads.add(thread);
            thread.start();
        }
        for(GPUService gpuService : GPUServices)
        {
            Thread thread = new Thread(gpuService);
            GPUServiceThreads.add(thread);
            thread.start();
        }
        for(ConferenceService conferenceService : ConferenceServices)
        {
            Thread thread = new Thread(conferenceService);
            ConferenceServiceThreads.add(thread);
            thread.start();
        }
        for(StudentService studentService : StudentServices)
        {
            Thread thread = new Thread(studentService);
            StudentServiceThreads.add(thread);
            thread.start();
        }
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timeServiceThread = new Thread(timeService);
        timeServiceThread.start();
    }

    public static void initFields(){
        StudentServices = new ArrayList<>();
        GPUServices = new ArrayList<>();
        CPUServices = new ArrayList<>();
        ConferenceServices = new ArrayList<>();
        ConferenceServiceThreads = new ArrayList<>();
        StudentServiceThreads = new ArrayList<>();
        GPUServiceThreads = new ArrayList<>();
        CPUServiceThreads = new ArrayList<>();
    }
    public static void initObjects(){
        try {
            File inputFile = new File("C:\\Users\\yoavi\\OneDrive - post.bgu.ac.il\\Study\\Semester C\\SPL\\Projects\\hw2-OFFICIAL\\SPL2\\example_input.json");
            JsonElement fileElement = JsonParser.parseReader(new FileReader(inputFile));
            JsonObject fileObject = fileElement.getAsJsonObject();
            initStudents(fileObject.get("Students").getAsJsonArray());
            initGPUS(fileObject.get("GPUS").getAsJsonArray());
            initCPUS(fileObject.get("CPUS").getAsJsonArray());
            initConferences(fileObject.get("Conferences").getAsJsonArray());
            ConferencesAmount = ConferenceServices.size();
            int tickTime = fileObject.get("TickTime").getAsInt();
            int duration = fileObject.get("Duration").getAsInt();
            timeService = new TimeService(tickTime,duration);
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
            if(type.equals("images") || type.equals("Images"))
                dataType = Data.Type.Images;
            else if(type.equals("Text") || type.equals("text"))
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
            StudentServices.add(studentService);
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
            String GPUServiceName = "GPUService"+id;
            GPU gpu = new GPU(type, Cluster.getInstance(), id, GPUServiceName);
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