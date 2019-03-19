package project_binder;


import java.io.File;
import java.io.PrintWriter;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Database {
    protected File img_dir;
    protected File[] image_files;
    protected Map<String, Integer> labels = new HashMap<>();
    protected ArrayList<File> previous;
    protected int previous_counter;

    // static Scanner scanner = new Scanner(System.in);

    public Database(File img_dir){
        this.img_dir = img_dir;
        image_files = img_dir.listFiles();
        previous = new ArrayList<>();
        previous_counter = 0;
    }

    protected void add_label_entry(File file, int label){
        String file_name = file.getName();
        this.labels.put(file_name, label);
        if(!this.previous.contains(file)){
            add_previous_file(file);
        }
    }

    protected void resetCounter(){ previous_counter = 0; }

    protected void incrementCounter(){ previous_counter++; }

    protected void decrementCounter(){ previous_counter--; }

    protected void clear_labels(){
        labels.clear();
    }

    protected void add_previous_file(File file_name){
        this.previous.add(file_name);
    }

    protected void clear_previous_files(){
        this.previous.clear();
    }

    protected File get_random_file(){
        Random generator = new Random();
        int randomIndex = generator.nextInt(this.image_files.length);
        File next_image = this.image_files[randomIndex];
        while (this.previous.contains(next_image)){
            randomIndex = generator.nextInt(this.image_files.length);
            next_image = this.image_files[randomIndex];
        }
        return next_image;
    }

    protected void save_labels_to_json(File save_file){
        JSONObject savefile = new JSONObject();

        savefile.put("image directory", this.img_dir);
        savefile.put("number of checked files", previous.size());

        int num_findings = 0;
        for(int i=0; i<previous.size(); i++){
            num_findings += labels.get(previous.get(i));
        }

        savefile.put("number of label = finding", num_findings);
        savefile.put("number of label = no finding", previous.size() - num_findings);

        JSONArray file_entries = new JSONArray();

        for(int i=0; i<previous.size(); i++){
            Map m = new LinkedHashMap(2);
            String filename = previous.get(i).getName();
            int label = labels.get(filename);
            m.put("file name", filename);
            m.put("label", label);
            file_entries.add(m);
        }

        savefile.put("Labeled Files", file_entries);

        String path = save_file.getAbsolutePath();
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(path);
            pw.write(savefile.toJSONString());
        }
        catch (IOException e){
            System.err.println("Caught IOException" + e.getMessage());
        }
        finally {
            if(pw != null){
                pw.flush();
                pw.close();
            }
        }
    }

    public static void main(String[] args){

        Database data = new Database(new File("C:/Users/maste/Datasets/ChestXRay/images/Test/"));
        data.add_label_entry(data.image_files[1], 0);
        data.add_label_entry(data.image_files[0], 1);

        for (int i=0; i!=10; i++){
            System.out.println(data.get_random_file());
        }
    }

}




