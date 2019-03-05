package project_binder;

import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.util.Map;
import java.util.ArrayList;
import java.util.Random;

public class Database {
    private File img_dir;
    private File[] image_files;
    private Map<String, Integer> labels = new HashMap();
    private ArrayList<File> previous;

    static Scanner scanner = new Scanner(System.in);

    private void add_label_entry(File file, int label){
        String file_name = file.getName();
        this.labels.put(file_name, label);
    }

    private void clear_labels(){
        labels.clear();
    }

    private void add_previous_file(File file_name){
        this.previous.add(file_name);
    }

    private void clear_previous_files(){
        this.previous.clear();
    }

    private File get_random_file(){
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

    protected Database(){
        previous = new ArrayList<>();
        String img_dir_name = "C:/Users/Amar/Datasets/ChestXRay/images_labeled/";
        System.out.println("Directory is set to" + img_dir_name + ", change it? [y/n]");
        if (scanner.nextLine().equals("y")){
            System.out.println("Enter new directory");
            img_dir_name = scanner.nextLine();
            System.out.println("New directory is " + img_dir_name);
        }
        img_dir = new File(img_dir_name);
        image_files = img_dir.listFiles();
    }

    public static void main(String[] args){

        Database data = new Database();
        data.add_label_entry(data.image_files[1], 0);
        data.add_label_entry(data.image_files[0], 1);
        for (int i=0; i!=10; i++){
            System.out.println(data.get_random_file());
        }
    }

}




