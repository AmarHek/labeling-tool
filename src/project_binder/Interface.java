package project_binder;

import javax.swing.*;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.io.File;

public class Interface extends JFrame{
    private Database data;
    private JPanel panel1;
    private JTextField previous;

    public Interface() {
        initUI();
    }

    private void initUI() {

        var selectButton = new JButton("Select");
        var saveButton = new JButton("Save");
        var loadButton = new JButton("Load");
        var showButton = new JButton("Show");

        selectButton.addActionListener((event) -> set_database());
        showButton.addActionListener((event) -> display_random_image());

        createLayout(selectButton, saveButton, showButton);

        setTitle("XRay-Tinder");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void display_random_image(){
        if(this.data==null){
            var pnl = (JPanel) getContentPane();
            JOptionPane.showMessageDialog(pnl, "No File directory chosen!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        else{
            var pnl = getContentPane();
            File rnd_image = this.data.get_random_file();
            String rnd_image_path = rnd_image.getAbsolutePath();
            JOptionPane.showMessageDialog(null, rnd_image_path);
        }
    }

    private void set_database(){
        JFileChooser chooser = new JFileChooser("./");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(null);
        File img_dir = new File(chooser.getSelectedFile().getAbsolutePath());
        data = new Database(img_dir);
    }

    private void createLayout(JComponent... arg){

        var pane = getContentPane();
        var gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        var sg = gl.createSequentialGroup();
        var pg = gl.createParallelGroup();
        for(int i=0; i<arg.length; i++){
            sg.addComponent(arg[i]);
            pg.addComponent(arg[i]);
        }

        gl.setHorizontalGroup(sg);
        gl.setVerticalGroup(pg);

        gl.linkSize(arg);

        pack();

    }

    public static void main(String[] args){
         EventQueue.invokeLater(() -> {
             var ex = new Interface();
             ex.setVisible(true);
         });
    }
}
