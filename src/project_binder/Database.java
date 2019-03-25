package project_binder;


import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import java.io.IOException;

import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Database {
    protected File img_dir;
    protected File[] image_files;
    protected Map<String, Integer> labels;
    protected ArrayList<File> previous;
    private int previous_counter;

    // static Scanner scanner = new Scanner(System.in);

    public Database(File img_dir){
        this.img_dir = img_dir;
        this.image_files = img_dir.listFiles();
        this.labels = new HashMap<>();
        this.previous = new ArrayList<>();
        this.previous_counter = 0;
    }

    public Database(){
        this.labels = new HashMap<>();
        this.previous = new ArrayList<>();
        this.previous_counter = 0;
    }

    protected void set_images(File img_dir){
        this.img_dir = img_dir;
        this.image_files = img_dir.listFiles();
    }

    protected void add_label_entry(File file, int label){
        String file_name = file.getName();
        this.labels.put(file_name, label);
        if(!this.previous.contains(file)){
            add_previous_file(file);
        }
    }

    protected int get_previous_counter(){
        return previous_counter;
    }

    protected void resetCounter(){
        previous_counter = 0;
    }

    protected void incrementCounter(){
        if(previous_counter < previous.size())
        previous_counter++;
    }

    protected void decrementCounter(){
        if(previous_counter > 0) {
            previous_counter--;
        }
    }

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

    protected void save_to_json(File save_file){

        JSONObject savefile = new JSONObject();

        Map meta = new LinkedHashMap(4);

        meta.put("image directory", this.img_dir.getAbsolutePath());
        meta.put("number of checked files", previous.size());

        int num_findings = 0;
        for(File file: previous){
            num_findings += labels.get(file.getName());
        }

        meta.put("number of 'finding'", num_findings);
        meta.put("number of 'no finding'", previous.size() - num_findings);

        savefile.put("Metadata", meta);

        JSONArray file_entries = new JSONArray();

        for(File file: previous){
            Map m = new LinkedHashMap(2);
            String filename = file.getName();
            int label = labels.get(filename);
            m.put("file name", filename);
            m.put("label", label);
            file_entries.add(m);
        }

        savefile.put("Files", file_entries);

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

    protected void load_from_json(File savefile){
        // preliminary: clear previous files and labels
        this.clear_labels();
        this.clear_previous_files();

        try {
            // load the file
            Object obj = new JSONParser().parse(new FileReader(savefile.getAbsolutePath()));

            // typecast obj to JSONObject
            JSONObject loaded = (JSONObject) obj;

            // extract image directory
            Map meta = ((Map) loaded.get("Metadata"));
            String img_dir = (String) meta.get("image directory");

            // Set image directory and load file(name)s
            this.set_images(new File(img_dir));

            // getting the file arrays
            JSONArray file_entries = (JSONArray) loaded.get("Files");

            // looping over all file entries
            for(int i=0; i < file_entries.size(); i++){
                // extract the map from each entry and iterate
                Map m = (Map) file_entries.get(i);
                String filename = (String) m.get("file name");
                int label = (int) (long) m.get("label");
                File file = new File(img_dir + '/' + filename);
                this.add_label_entry(file, (int)label);
            }

        }
        catch (IOException ioe){
            System.err.println("Caught IOException" + ioe.getMessage());
        }
        catch (ParseException pe){
            System.err.println("Caught ParseException" + pe.getMessage());
        }
    }

    public static void main(String[] args){

        File test = new File("C:/Users/Amar/Git/xray-tinder/test.json");
        Database data = new Database();
        data.load_from_json(test);
        for (File file: data.previous){
            System.out.println(file.getPath());
        }
    }

}




