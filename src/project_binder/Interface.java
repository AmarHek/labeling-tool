package project_binder;

import javax.swing.*;
import java.awt.EventQueue;
import java.io.File;
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.IOException;

import static javax.swing.LayoutStyle.ComponentPlacement.*;

public class Interface extends JFrame{

    private Database data;
    private JPanel panel;

    private JLabel image_label;
    private File image_buffer;

    private String cur_img;
    private String cur_label;

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

        //TODO Have "New" create a new file after choosing image directory
        var selectMenuItem = new JMenuItem("New");
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

        // defining all Layout items
        JButton randomBtn = new JButton("Random");
        JButton nextBtn = new JButton("Next");
        JButton findingBtn = new JButton("Finding");
        JButton nofindingBtn = new JButton("No Finding");

        JButton previousBtn = new JButton("Previous");

        placeholder = "src/resources/Platzhalter.png";
        image_buffer = new File(placeholder);
        image_label = new JLabel(new ImageIcon(image_buffer.getPath()));

        cur_img = " ";
        cur_label = " ";

        JLabel current_file = new JLabel("Current Image: " + cur_img);
        JLabel current_label = new JLabel("Finding: " + cur_label);

        // adding actions to buttons
        randomBtn.addActionListener((event) -> display_random());
        nextBtn.addActionListener((event) -> display_next());
        findingBtn.addActionListener((event) -> addLabel(1));
        nofindingBtn.addActionListener((event) -> addLabel(0));
        previousBtn.addActionListener((event) -> display_previous());

        // adding values to status labels

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
                                .addGroup(gl.createParallelGroup()
                                    .addComponent(randomBtn)
                                    .addComponent(current_file))
                                .addComponent(nextBtn)
                                .addComponent(previousBtn)
                                .addPreferredGap(RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(gl.createParallelGroup()
                                    .addComponent(findingBtn)
                                    .addComponent(current_label))
                                .addComponent(nofindingBtn))
                        .addComponent(image_label)
                        .addGroup(gl.createSequentialGroup()
                                .addComponent(current_file)
                                .addPreferredGap(UNRELATED, 50, 300)
                                .addComponent(current_label))
        );

        gl.setVerticalGroup(
                gl.createSequentialGroup()
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(randomBtn)
                                .addComponent(nextBtn)
                                .addComponent(previousBtn)
                                .addComponent(findingBtn)
                                .addComponent(nofindingBtn))
                        .addComponent(image_label)
                        .addGroup(gl.createParallelGroup()
                                .addComponent(current_file)
                                .addComponent(current_label))
        );

        gl.linkSize(randomBtn, nextBtn, previousBtn, findingBtn, nofindingBtn);
        gl.linkSize(current_file, current_label);

        pack();
    }

    private void display_random(){
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

    private void display_previous(){
        this.data.incrementCounter();
        int len = this.data.previous.size();
        if (data.previous.isEmpty() || data.get_previous_counter() > len) {
            display_random();
        }
        else{
            image_buffer = this.data.previous.get(len - this.data.get_previous_counter());
            this.image_label.setIcon(new ImageIcon(image_buffer.getPath()));
        }
    }

    private void display_next(){
        this.data.decrementCounter();
        int len = this.data.previous.size();
        if (data.get_previous_counter() == 0) {
            display_random();
        }
        else{
            image_buffer = this.data.previous.get(len - this.data.get_previous_counter());
            this.image_label.setIcon(new ImageIcon(image_buffer.getPath()));
        }
    }


    // Methods for handling Database: creating it, setting labels, saving and loading files

    private void addLabel(int label){
        if(image_buffer.getPath().equals(placeholder)){
            JOptionPane.showMessageDialog(null, "No suitable image selected yet.",
                    "No Image", JOptionPane.WARNING_MESSAGE);
        }
        else{
            this.data.add_label_entry(image_buffer, label);
            display_random();
        }
    }

    private void set_database(){
        JFileChooser chooser = new JFileChooser("./");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(panel);
        File img_dir = chooser.getSelectedFile();
        data = new Database(img_dir);
        display_random();
    }

    private void save_progress(){
        JFileChooser chooser = new JFileChooser("./");
        chooser.showOpenDialog(panel);
        File save_dir = chooser.getSelectedFile();
        this.data.save_labels_to_json(save_dir);
    }

    private void load_progress(){
        JFileChooser chooser = new JFileChooser("./");
        chooser.showOpenDialog(panel);
        File savefile = chooser.getSelectedFile();
        this.data = new Database();
        this.data.load_from_json(savefile);
        display_random();
    }

    //Main method

    public static void main(String[] args){
         EventQueue.invokeLater(() -> {
             var ex = new Interface();
             ex.setVisible(true);
         });
    }
}
