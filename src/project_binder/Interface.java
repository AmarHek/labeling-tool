package project_binder;

import javax.swing.*;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.io.File;
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.IOException;

import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;

public class Interface extends JFrame{

    private Database data;
    private JPanel panel;

    private JLabel image_label;
    private File image_buffer;

    private String placeholder;

    public Interface() {
        initUI();
    }

    private void initUI() {

        createMenuBar();

        createLayout();

        setTitle("XRay-Tinder");
        //TODO: Variable size
        //setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createMenuBar(){

        var menubar = new JMenuBar();

        var fileMenu = new JMenu("File");

        var selectMenuItem = new JMenuItem("Database");
        var saveMenuItem = new JMenuItem("Save");
        var loadMenuItem = new JMenuItem("Load");

        var exitMenuItem = new JMenuItem("Exit");

        selectMenuItem.addActionListener((event) -> set_database());
        saveMenuItem.addActionListener((event) -> save_progress());
        loadMenuItem.addActionListener((event) -> load_progress());

        exitMenuItem.addActionListener((event) -> System.exit(0));

        fileMenu.add(selectMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        menubar.add(fileMenu);

        setJMenuBar(menubar);
    }

    private void createLayout(){

        JButton startBtn = new JButton("Next");
        JButton findingBtn = new JButton("Finding");
        JButton nofindingBtn = new JButton("No Finding");

        JButton previousBtn = new JButton("Previous");

        placeholder = "src/resources/Platzhalter.png";
        image_buffer = new File(placeholder);
        image_label = new JLabel(new ImageIcon(image_buffer.getPath()));

        startBtn.addActionListener((event) -> display_random_image());
        //TODO: Those three functions
        findingBtn.addActionListener((event) -> addLabel(1));
        nofindingBtn.addActionListener((event) -> addLabel(0));
        previousBtn.addActionListener((event) -> showPrevious());

        var pane = getContentPane();
        var gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        //TODO: Improve Layout -> Dynamic
        //TODO: Add Skip button? Add button(s) to scroll through previous images
        //TODO: Add Reset button (or menu)
        gl.setHorizontalGroup(
                gl.createParallelGroup()
                        .addGroup(gl.createSequentialGroup()
                                .addComponent(startBtn)
                                .addComponent(previousBtn)
                                .addPreferredGap(RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(findingBtn)
                                .addComponent(nofindingBtn))
                        .addComponent(image_label)
        );

        gl.setVerticalGroup(
                gl.createSequentialGroup()
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(startBtn)
                                .addComponent(previousBtn)
                                .addComponent(findingBtn)
                                .addComponent(nofindingBtn))
                        .addComponent(image_label)
        );

        gl.linkSize(startBtn, findingBtn, nofindingBtn);

        pack();
    }

    private void display_random_image(){
        if(this.data==null){
            var pnl = (JPanel) getContentPane();
            JOptionPane.showMessageDialog(pnl, "Please first choose a directory from File and then Select",
                    "No Directory", JOptionPane.WARNING_MESSAGE);
        }
        else{
            this.data.resetCounter();
            image_buffer = this.data.get_random_file();
            try {
            this.image_label.setIcon(new ImageIcon(image_buffer.getPath()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "File not found");
                System.out.println(e);
            }
        }
    }


    // Methods for handling Database: creating it, setting labels, saving and loading files

    private void set_database(){
        JFileChooser chooser = new JFileChooser("./");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(panel);
        File img_dir = chooser.getSelectedFile();
        data = new Database(img_dir);
        display_random_image();
    }

    private void addLabel(int label){
        if(image_buffer.getPath().equals(placeholder)){
            JOptionPane.showMessageDialog(null, "No suitable image selected yet.",
                    "No Image", JOptionPane.WARNING_MESSAGE);
        }
        else{
            this.data.add_label_entry(image_buffer, label);
            display_random_image();
        }
    }

    private void save_progress(){
        JFileChooser chooser = new JFileChooser("./");
        chooser.showOpenDialog(panel);
        File save_dir = chooser.getSelectedFile();
        this.data.save_labels_to_json(save_dir);
    }

    //TODO: load progress
    private void load_progress(){
        ;
    }




    private void showPrevious(){
        if (data.previous.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No Previous images yet.",
                    "No Previous Image", JOptionPane.WARNING_MESSAGE);
        }
        else{
            int len = this.data.previous.size();
            this.data.incrementCounter();
            File prev_img = this.data.previous.get(len-this.data.previous_counter);
            this.image_label.setIcon(new ImageIcon(prev_img.getPath()));
        }
    }


    //Main method

    public static void main(String[] args){
         EventQueue.invokeLater(() -> {
             var ex = new Interface();
             ex.setVisible(true);
         });
    }
}
