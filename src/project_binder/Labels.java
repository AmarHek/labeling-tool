package project_binder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Labels {
    protected ArrayList<String> labels;
    private File source_file;

    public Labels(){
        source_file = new File("src/resources/labels.txt");
        labels = new ArrayList<>();
        extract_labels();
    }

    public Labels(File user_file){
        source_file = user_file;
        labels = new ArrayList<>();
        extract_labels();
    }

    public File getSource_file() {
        return source_file;
    }

    public ArrayList<String> getLabels(){
        return labels;
    }

    public void add_label(String label){
        if(!labels.contains(label.trim())){
            labels.add(label);
        }
    }

    public void remove_label(String label){

    }

    private void extract_labels(){
        try{
            FileReader fileReader = new FileReader(source_file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if(i == 30){
                    break;
                }
                if (!line.trim().isEmpty()) {
                    labels.add(line);
                    i++;
                }
            }
            fileReader.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
