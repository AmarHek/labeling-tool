package project_binder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.File;

import static javax.swing.LayoutStyle.ComponentPlacement.*;

public class Interface extends JFrame{

    private Database data;
    private JPanel panel;

    // image variables
    private JLabel image_label;
    private File image_buffer;

    private JLabel current_file;
    private JLabel current_label;

    private String placeholder;

    public Interface() {
        initUI();
    }

    private void initUI() {

        createMenuBar();

        createLayout();

        this.setTitle("XRay-Tinder");
        //TODO: Variable size
        //setSize(1000, 800);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        WindowListener exitListener = exit();
        this.addWindowListener(exitListener);
    }

    private void createMenuBar(){

        var menubar = new JMenuBar();

        var fileMenu = new JMenu("File");

        //TODO Have "New" create a new file after choosing image directory
        var selectMenuItem = new JMenuItem("New");
        var saveMenuItem = new JMenuItem("Save");
        var loadMenuItem = new JMenuItem("Load");

        var clearMenuItem = new JMenuItem("Clear");

        selectMenuItem.addActionListener((event) -> set_database());
        saveMenuItem.addActionListener((event) -> save_progress());
        loadMenuItem.addActionListener((event) -> load_progress());

        clearMenuItem.addActionListener((event) -> clear_progress());

        fileMenu.add(selectMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(clearMenuItem);

        menubar.add(fileMenu);

        setJMenuBar(menubar);
    }

    private void createLayout(){

        // defining all Layout items
        JButton randomBtn = new JButton("Random");
        JButton nextBtn = new JButton("Next");
        JButton previousBtn = new JButton("Previous");
        JButton findingBtn = new JButton("Finding");
        JButton nofindingBtn = new JButton("No Finding");

        placeholder = "src/resources/Platzhalter.png";
        image_buffer = new File(placeholder);
        image_label = new JLabel(new ImageIcon(image_buffer.getPath()));

        current_file = new JLabel("Current Image:  ");
        current_label = new JLabel("Finding:  ");

        Action next = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e)
                display_next();
            }
        };

        // adding values to status labels

        var pane = getContentPane();
        var gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        //TODO: Improve Layout -> Dynamic

        // Creating the Layout
        gl.setHorizontalGroup(
                gl.createParallelGroup()
                        .addGroup(gl.createSequentialGroup()
                                    .addComponent(randomBtn)
                                    .addComponent(previousBtn)
                                    .addComponent(nextBtn)
                                    .addPreferredGap(RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(findingBtn)
                                    .addComponent(nofindingBtn))
                        .addComponent(image_label)
                        .addGroup(gl.createSequentialGroup()
                                    .addComponent(current_file)
                                    .addPreferredGap(RELATED, 20, 50)
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

        // adding actions to buttons
        randomBtn.addActionListener((event) -> display_random());
        randomBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        nextBtn.addActionListener((event) -> display_next());
        nextBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "pressed");
        previousBtn.addActionListener((event) -> display_previous());
        previousBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "released");
        findingBtn.addActionListener((event) -> addLabel(1));
        findingBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "released");
        nofindingBtn.addActionListener((event) -> addLabel(0));
        nofindingBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "released");

    }

    private void display_random(){
        if(this.data==null){
            JOptionPane.showMessageDialog(panel, "Please first create a database from File: New.",
                    "Missing Database", JOptionPane.WARNING_MESSAGE);
        }
        else{
            this.data.resetCounter();
            image_buffer = this.data.get_random_file();
            try {
            this.image_label.setIcon(new ImageIcon(image_buffer.getPath()));
            this.setStatus();
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
            this.setStatus();
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
            this.setStatus();
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

    private void clear_progress(){
        if(this.data == null){
            JOptionPane.showMessageDialog(panel, "Please first create a database from File: New.",
                    "Missing Database", JOptionPane.WARNING_MESSAGE);
        }
        int dialogResult = JOptionPane.showConfirmDialog(panel, "Clear all progress?",
                null, JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            this.data.clear_previous_files();
            this.data.clear_labels();
        }
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

    // change status bar
    private void setStatus(){
        String filename = image_buffer.getName();
        String label;
        if(!data.previous.contains(image_buffer)){
            label = " ";
        }
        else if(data.labels.get(image_buffer.getName()) == 0){
            label = "No";
        }
        else{
            label = "Yes";
        }
        this.current_file.setText("Current File: " + filename);
        this.current_label.setText("Finding: " + label);
    }

    private WindowListener exit(){
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(panel,
                        "Do you want to save your progress before exiting?");
                if(result == JOptionPane.YES_OPTION){
                    save_progress();
                    System.exit(0);
                }
                else if(result == JOptionPane.NO_OPTION){
                    System.exit(0);
                }
            }
        };
    }

    //Main method

    public static void main(String[] args){
         EventQueue.invokeLater(() -> {
             var ex = new Interface();
             ex.setVisible(true);
         });
    }
}
