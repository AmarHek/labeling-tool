package project_binder;

import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.util.Map;
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

    private void add_previous_file(File file_name){
        this.previous.add(file_name);
    }

    private void clear_previous_files(){
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

    private void save_labels(){
        

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




