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
    protected ArrayList<File> image_files;
    protected Map<String, Integer> labels;
    protected ArrayList<File> labeled;
    protected Map<String, ArrayList<String>> findings;
    private int labeled_counter;

    // static Scanner scanner = new Scanner(System.in);

    public Database(File img_dir){
        this.img_dir = img_dir;
        this.image_files = new ArrayList<>();
        File[] files = img_dir.listFiles();
        Collections.addAll(image_files, files);
        this.labels = new HashMap<>();
        this.findings = new HashMap<>();
        this.labeled = new ArrayList<>();
        this.labeled_counter = 0;
    }

    public Database(){
        this.labels = new HashMap<>();
        this.labeled = new ArrayList<>();
        this.findings = new HashMap<>();
        this.labeled_counter = 0;
        this.image_files = new ArrayList<>();
    }

    protected void set_images(File img_dir){
        this.img_dir = img_dir;
        File[] files = img_dir.listFiles();
        Collections.addAll(image_files, files);
    }

    protected void add_label_entry(File file, int label, ArrayList<String> findings){
        String file_name = file.getName();
        this.labels.put(file_name, label);
        this.findings.put(file_name, findings);
        if(!this.labeled.contains(file)){
            this.labeled.add(file);
            this.image_files.remove(file);
        }
    }

    protected int get_labeled_counter(){
        return labeled_counter;
    }

    protected void resetCounter(){
        labeled_counter = 0;
    }

    protected void incrementCounter(){
        if(labeled_counter < labeled.size())
        labeled_counter++;
    }

    protected void decrementCounter(){
        if(labeled_counter > 0) {
            labeled_counter--;
        }
    }

    protected void clear_labels(){
        labels.clear();
        findings.clear();
    }

    protected void clear_labeled(){
        this.labeled.clear();
    }

    protected File get_random_file(){
        Random generator = new Random();
        int randomIndex = generator.nextInt(this.image_files.size());
        return this.image_files.get(randomIndex);
    }

    protected ArrayList<String> get_findings(File file){
        return this.findings.get(file.getName());
    }

    protected boolean is_finished(){
        return image_files.size() == 0;
    }

    protected void save_to_json(File save_file){

        JSONObject savefile = new JSONObject();

        Map meta = new LinkedHashMap(4);

        meta.put("image directory", this.img_dir.getAbsolutePath());
        meta.put("number of checked files", labeled.size());

        int num_findings = 0;
        for(File file: labeled){
            num_findings += labels.get(file.getName());
        }

        meta.put("number of 'finding'", num_findings);
        meta.put("number of 'no finding'", labeled.size() - num_findings);

        savefile.put("Metadata", meta);

        JSONArray file_entries = new JSONArray();

        for(File file: labeled){
            Map m = new LinkedHashMap(2);
            String filename = file.getName();
            int label = labels.get(filename);
            ArrayList<String> findings = this.findings.get(filename);
            JSONArray js_findings = new JSONArray();
            js_findings.addAll(findings);
            m.put("file name", filename);
            m.put("label", label);
            m.put("findings", js_findings);
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
                obj = m.get("findings");
                ArrayList<String> findings = (ArrayList<String>) obj;
                File file = new File(img_dir + '/' + filename);
                this.add_label_entry(file, label, findings);
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

        File test = new File("C:/Users/Amar/Git/xray-labeler/test.json");
        Database data = new Database();
        data.load_from_json(test);
        for (File file: data.labeled){
            System.out.println(file.getPath());
        }
    }

}




