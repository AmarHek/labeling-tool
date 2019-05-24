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
    private File latest_image;

    private File save_file;

    // actions
    private Action random;
    private Action next;
    private Action previous;
    private Action finding;
    private Action nofinding;

    private JMenuItem saveMenuItem;

    private JLabel current_file;
    private JLabel current_label;
    private JLabel num_labeled;

    private String placeholder;

    public Interface() {
        initUI();
    }

    private void initUI() {

        createMenuBar();
        setActions();
        createLayout();

        this.setTitle("XRay-Labeler");
        //TODO: Variable size
        //setSize(1000, 800);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        WindowListener exitListener = exit();
        this.addWindowListener(exitListener);
    }


    // sets the menu bar and respective actions
    private void createMenuBar(){
        JMenuBar menubar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem selectMenuItem = new JMenuItem("New File");
        JMenuItem setdatabaseMenuItem = new JMenuItem("Set Database");
        JMenuItem saveasMenuItem = new JMenuItem("Save as");
        saveMenuItem = new JMenuItem("Save");
        JMenuItem loadMenuItem = new JMenuItem("Load");
        JMenuItem clearMenuItem = new JMenuItem("Clear");

        selectMenuItem.addActionListener((event) -> new_file());
        selectMenuItem.setMnemonic(KeyEvent.VK_N);
        selectMenuItem.setToolTipText("Choose Directory and create new save file");
        setdatabaseMenuItem.addActionListener((event) -> setDatabase());
        selectMenuItem.setMnemonic(KeyEvent.VK_D);
        selectMenuItem.setToolTipText("Set a new directory to load the images from");
        saveasMenuItem.addActionListener((event) -> save_as(false));
        saveasMenuItem.setMnemonic(KeyEvent.VK_A);
        saveasMenuItem.setToolTipText("Save data to a new file");
        saveMenuItem.addActionListener((event) -> save());
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.setEnabled(false);
        saveMenuItem.setToolTipText("Save data to current file");
        loadMenuItem.addActionListener((event) -> open());
        loadMenuItem.setMnemonic(KeyEvent.VK_L);
        loadMenuItem.setToolTipText("Load data and database from a saved file");
        clearMenuItem.addActionListener((event) -> clear_progress());
        clearMenuItem.setMnemonic(KeyEvent.VK_C);
        clearMenuItem.setToolTipText("Resets memory of already labeled images and clears all set labels");

        fileMenu.add(selectMenuItem);
        fileMenu.add(setdatabaseMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveasMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(clearMenuItem);

        menubar.add(fileMenu);

        setJMenuBar(menubar);
    }

    // creates the actions to be assigned to the buttons and key strokes
    private void setActions(){
        random = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                display_random();
            }
        };
        next = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                display_next();
            }
        };
        previous = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                display_previous();
            }
        };
        finding = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLabel(1);
            }
        };
        nofinding = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLabel(0);
            }
        };
    }

    // creates button layout and sets image placeholder
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
        num_labeled = new JLabel("Labeled Files: 0");

        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
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
                                    .addComponent(current_label)
                                    .addPreferredGap(RELATED, 20, 30)
                                    .addComponent(num_labeled))
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
                                .addComponent(current_label)
                                .addComponent(num_labeled))
        );

        gl.linkSize(randomBtn, nextBtn, previousBtn, findingBtn, nofindingBtn);
        gl.linkSize(current_file, current_label, num_labeled);

        pack();

        // adding actions to buttons
        randomBtn.addActionListener(random);
        randomBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "random");
        randomBtn.getActionMap().put("random", random);
        nextBtn.addActionListener(next);
        nextBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "next");
        nextBtn.getActionMap().put("next", next);
        previousBtn.addActionListener(previous);
        previousBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "previous");
        previousBtn.getActionMap().put("previous", previous);
        findingBtn.addActionListener(finding);
        findingBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "finding");
        findingBtn.getActionMap().put("finding", finding);
        nofindingBtn.addActionListener(nofinding);
        nofindingBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "nofinding");
        nofindingBtn.getActionMap().put("nofinding", nofinding);


        randomBtn.setToolTipText("Displays a random image");
        nextBtn.setToolTipText("Displays next image in line or a random one");
        previousBtn.setToolTipText("Displays previous image (or random if none labeled)");
        findingBtn.setToolTipText("Mark image as 'finding detected'");
        nofindingBtn.setToolTipText("Mark image as 'no finding detected'");
    }


    // functions for displaying the images
    private void setImage(File image){
        try {
            this.image_label.setIcon(new ImageIcon(image.getPath()));
            this.setStatus(image);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "File not found");
            System.out.println(e);
        }
    }

    // change status bar
    private void setStatus(File image){
        String filename = image.getName();
        String label;
        String num_labeled = Integer.toString(this.data.previous.size());
        if(!data.previous.contains(image)){
            label = " ";
        }
        else if(data.labels.get(image.getName()) == 0){
            label = "No";
        }
        else{
            label = "Yes";
        }
        this.current_file.setText("Current File: " + filename);
        this.current_label.setText("Finding: " + label);
        this.num_labeled.setText("Labeled Files: " + num_labeled);
    }

    private void display_random(){
        if(this.data==null){
            JOptionPane.showMessageDialog(panel, "Please first create a database from File: New.",
                    "Missing Database", JOptionPane.WARNING_MESSAGE);
        }
        else{
            this.data.resetCounter();
            image_buffer = this.data.get_random_file();
            latest_image = image_buffer;
            this.setImage(image_buffer);
        }
    }

    private void display_previous(){
        this.data.incrementCounter();
        int len = this.data.previous.size();
        if (!data.previous.isEmpty() || data.get_previous_counter() > len) {
            image_buffer = this.data.previous.get(len - this.data.get_previous_counter());
            this.setImage(image_buffer);
        }
    }

    private void display_next(){
        this.data.decrementCounter();
        int len = this.data.previous.size();
        if (data.get_previous_counter() == 0) {
            setImage(latest_image);
        }
        else {
            if (data.get_previous_counter() > 0) {
                image_buffer = this.data.previous.get(len - this.data.get_previous_counter());
                this.setImage(image_buffer);
            }
        }
    }


    // Functions for handling data: creating database, setting labels, saving and loading files, clearing progress

    private void addLabel(int label){
        if(image_buffer.getPath().equals(placeholder)){
            JOptionPane.showMessageDialog(panel, "No suitable image selected yet.",
                    "No Image", JOptionPane.WARNING_MESSAGE);
        }
        else{
            this.data.add_label_entry(image_buffer, label);
            display_random();
        }
    }

    private File check_json_extension(File file){
        String path = file.getAbsolutePath();
        if(path.endsWith(".json")){
            return file;
        }
        else{
            path = path + ".json";
            return new File(path);
        }
    }

    private void save(){
        if(this.save_file != null) {
            this.data.save_to_json(this.save_file);
        }
    }

    private void save_as(boolean showDialog){
        int option;
        if(showDialog){
            option = JOptionPane.showConfirmDialog(panel,
                    "Choose a location for your save file.",
                    "Save Dialog", JOptionPane.OK_CANCEL_OPTION);
        }
        else{
            option = JOptionPane.OK_OPTION;
        }
        if(option == JOptionPane.OK_OPTION) {
            JFileChooser chooser = new JFileChooser("./");
            option = chooser.showSaveDialog(panel);
            if (option == JFileChooser.APPROVE_OPTION) {
                File savefile = chooser.getSelectedFile();
                savefile = check_json_extension(savefile);
                this.data.save_to_json(savefile);
                this.save_file = savefile;
                this.saveMenuItem.setEnabled(true);
            }
        }
    }

    private void setDatabase(){
        int option;
        if (this.data != null){
            option = JOptionPane.showConfirmDialog(panel,
                    "Database already exists. Do you still want to change it?",
                        "Confirm Dialog", JOptionPane.OK_CANCEL_OPTION);
        }
        else{
            option = JOptionPane.showConfirmDialog(panel, "Choose image directory",
                    "Directory Dialog", JOptionPane.OK_CANCEL_OPTION);
        }
        if (option == JOptionPane.OK_OPTION) {
            JFileChooser chooser = new JFileChooser("./");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            option = chooser.showOpenDialog(panel);
            if (option == JFileChooser.APPROVE_OPTION) {
                File img_dir = chooser.getSelectedFile();
                data = new Database(img_dir);
            }
        }
    }

    private void new_file(){
        if (this.data == null) {
            this.setDatabase();
        }
        if (this.data != null) {
            this.save_as(true);
            this.display_random();
        }
    }

    private void clear_progress(){
        if(this.data == null){
            JOptionPane.showMessageDialog(panel, "No database to clear.",
                    "Missing Database", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            int dialogResult = JOptionPane.showConfirmDialog(panel, "Clear all progress?",
                    null, JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                this.data.clear_previous_files();
                this.data.clear_labels();
                this.display_random();
            }
        }
    }

    private void open(){
        String default_directory;
        if(this.save_file != null){
            default_directory = this.data.img_dir.getAbsolutePath();
        }
        else{
            default_directory = "./";
        }
        JFileChooser chooser = new JFileChooser(default_directory);
        int option = chooser.showOpenDialog(panel);
        if(option == JFileChooser.APPROVE_OPTION){
            File savefile = chooser.getSelectedFile();
            this.data = new Database();
            this.data.load_from_json(savefile);
            this.save_file = savefile;
            display_random();
        }
    }


    private WindowListener exit(){
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(panel,
                        "Do you want to save your progress before exiting?");
                if (result == JOptionPane.YES_OPTION) {
                    save_as(false);
                    System.exit(0);
                } else if (result == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }
        };
    }



    //Main method

    public static void main(String[] args){
        java.util.Locale.setDefault(java.util.Locale.ENGLISH);
         EventQueue.invokeLater(() -> {
             Interface ex = new Interface();
             ex.setVisible(true);
         });
    }
}
