package project_binder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static javax.swing.LayoutStyle.ComponentPlacement.*;

public class Interface extends JFrame{

    private Database data;
    private JPanel panel;

    // image variables
    private JLabel image_label;
    private File image_buffer;
    private File latest_image;

    private JCheckBox[] labels_box;

    private File save_file;

    // actions
    private Action random;
    private Action next;
    private Action previous;
    private Action finding;
    private Action nofinding;
    //private Action binary;
    //private Action multi_label;

    private JMenuItem saveMenuItem;
    //private JRadioButtonMenuItem binary_item;
    //private JRadioButtonMenuItem multi_label_item;

    //private int mode = 1; // 0 = binary_item, 1 = multi_label_item

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
        //JMenu modeMenu = new JMenu("Mode");

        JMenuItem selectMenuItem = new JMenuItem("New File");
        JMenuItem setdatabaseMenuItem = new JMenuItem("Set Database");
        JMenuItem saveasMenuItem = new JMenuItem("Save as");
        saveMenuItem = new JMenuItem("Save");
        JMenuItem loadMenuItem = new JMenuItem("Load");
        JMenuItem clearMenuItem = new JMenuItem("Clear");

        //binary_item = new JRadioButtonMenuItem("Binary");
        //multi_label_item = new JRadioButtonMenuItem("Multi-Label");

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
        loadMenuItem.addActionListener((event) -> load());
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

        //modeMenu.add(binary_item);
        //modeMenu.add(multi_label_item);

        menubar.add(fileMenu);
        //menubar.add(modeMenu);

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
                ArrayList<String> findings = get_findings();
                if(check_empty(findings)) {
                    addLabel(1, findings);
                }
            }
        };
        nofinding = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] findings = {};
                addLabel(0, findings);
            }
        };
        //binary = new AbstractAction() {
        //    @Override
        //    public void actionPerformed(ActionEvent e) {
        //        mode = 0;
        //    }
        //};
        //multi_label = new AbstractAction() {
        //    @Override
        //    public void actionPerformed(ActionEvent e) {
        //        mode = 1;
        //    }
        //};
    }

    // creates button layout and sets image placeholder
    private void createLayout(){

        // defining all Layout items
        JButton randomBtn = new JButton("Random");
        JButton nextBtn = new JButton("Next");
        JButton previousBtn = new JButton("Previous");
        JButton findingBtn = new JButton("Finding");
        JButton nofindingBtn = new JButton("No Finding");

        String[] labels = {"Cardiomegaly", "Emphysema", "Effusion", "Hernia", "Infiltration", "Mass", "Nodule",
        "Atelectasis", "Pleural_Thickening", "Pneumothorax", "Pneumonia", "Fibrosis", "Edema", "Consolidation"};

        labels_box = new JCheckBox[labels.length];

        for(int i=0; i<14; i++){
            labels_box[i] = new JCheckBox(labels[i]);
        }

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

        GroupLayout.ParallelGroup pGroup = gl.createParallelGroup();
        GroupLayout.SequentialGroup sGroup = gl.createSequentialGroup();

        for(JCheckBox box : labels_box){
            pGroup.addComponent(box);
            sGroup.addComponent(box);
        }

        gl.setHorizontalGroup(
                gl.createSequentialGroup()
                    .addGroup(gl.createParallelGroup()
                        .addGroup(gl.createSequentialGroup()
                                    .addComponent(randomBtn)
                                    .addComponent(previousBtn)
                                    .addComponent(nextBtn)
                                    .addPreferredGap(RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(nofindingBtn)
                                    .addComponent(findingBtn))
                        .addComponent(image_label)
                        .addGroup(gl.createSequentialGroup()
                                    .addComponent(current_file)
                                    .addPreferredGap(RELATED, 20, 100)
                                    .addComponent(current_label)
                                    .addPreferredGap(RELATED, 20, 100)
                                    .addComponent(num_labeled)))
                    .addGroup(pGroup)
        );

        gl.setVerticalGroup(
                gl.createParallelGroup()
                    .addGroup(gl.createSequentialGroup()
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(randomBtn)
                                .addComponent(nextBtn)
                                .addComponent(previousBtn)
                                .addComponent(nofindingBtn)
                                .addComponent(findingBtn))
                        .addComponent(image_label)
                        .addGroup(gl.createParallelGroup()
                                .addComponent(current_file)
                                .addComponent(current_label)
                                .addComponent(num_labeled)))
                    .addGroup(sGroup)
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
        previousBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "labeled");
        previousBtn.getActionMap().put("labeled", previous);
        findingBtn.addActionListener(finding);
        findingBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "finding");
        findingBtn.getActionMap().put("finding", finding);
        nofindingBtn.addActionListener(nofinding);
        nofindingBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "nofinding");
        nofindingBtn.getActionMap().put("nofinding", nofinding);


        randomBtn.setToolTipText("Displays a random image");
        nextBtn.setToolTipText("Displays next image in line or a random one");
        previousBtn.setToolTipText("Displays labeled image (or random if none labeled)");
        findingBtn.setToolTipText("Mark image as 'finding detected' " +
                "and add all checked findings (at least one required)");
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
        String num_labeled = Integer.toString(this.data.labeled.size());
        if(!data.labeled.contains(image)){
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

    private void setCheckboxes(File image) {
        String[] findings = this.data.get_findings(image);
        for (JCheckBox box : this.labels_box) {
            if(Arrays.asList(findings).contains(box.getText())) {
                box.setSelected(true);
            }
            else {
                box.setSelected(false);
            }
        }
    }

    private void resetCheckboxes() {
        for(JCheckBox box : this.labels_box){
            box.setSelected(false);
        }
    }

    // TODO: Add resizing for large images
    // TODO: reload frame auslagern/verbessern
    private void display_random(){
        if(this.data==null){
            JOptionPane.showMessageDialog(panel, "Please first create a database from File: New.",
                    "Missing Database", JOptionPane.WARNING_MESSAGE);
        }
        else {
            this.data.resetCounter();
            resetCheckboxes();
            //try {
                //BufferedImage bimg_old = ImageIO.read(image_buffer);
            if (data.is_finished()){
                JOptionPane.showMessageDialog(panel, "No more images left.",
                        "Finished", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                image_buffer = this.data.get_random_file();
                //BufferedImage bimg_new = ImageIO.read(image_buffer);
                latest_image = image_buffer;
                this.setImage(image_buffer);
                //if (bimg_old.getWidth() != bimg_new.getWidth() || bimg_old.getHeight() != bimg_new.getHeight()) {
                //    this.setVisible(false);
                //    this.setVisible(true);
                //}
                //}
                //catch (IOException ex) {
                //    System.out.println(ex.getMessage());
                //}
            }
        }
    }

    private void display_previous(){
        this.data.incrementCounter();
        int len = this.data.labeled.size();
        if (!data.labeled.isEmpty() || data.get_labeled_counter() > len) {
            image_buffer = this.data.labeled.get(len - this.data.get_labeled_counter());
            this.setImage(image_buffer);
            this.setCheckboxes(image_buffer);
            }
        }

    private void display_next(){
        this.data.decrementCounter();
        int len = this.data.labeled.size();
        if (data.get_labeled_counter() == 0) {
            setImage(latest_image);
            image_buffer = latest_image;
            resetCheckboxes();
        }
        else {
            if (data.get_labeled_counter() > 0) {
                image_buffer = this.data.labeled.get(len - this.data.get_labeled_counter());
                this.setImage(image_buffer);
                this.setCheckboxes(image_buffer);
            }
        }
    }

    // Functions for handling data: creating database, setting labels, saving and loading files, clearing progress
    private ArrayList<String> get_findings(){
        ArrayList<String> findings = new ArrayList<>();
        for(JCheckBox box : this.labels_box){
            if(box.isSelected()){
                findings.add(box.getText());
            }
        }
        return findings;
    }

    private boolean check_empty(ArrayList<String> findings){
        if(findings.size() == 0){
            JOptionPane.showMessageDialog(panel,
                    "You must check at least one finding before pressing 'Finding'.",
                    "No Findings", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else{
            return true;
        }
    }

    private void addLabel(int label, ArrayList<String> findings){
        if(image_buffer.getPath().equals(placeholder)){
            JOptionPane.showMessageDialog(panel, "No suitable image selected yet.",
                    "No Image", JOptionPane.WARNING_MESSAGE);
        }
        else{
            this.data.add_label_entry(image_buffer, label, findings);
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
                this.display_random();
            }
        }
    }

    private void new_file(){
        if (this.data == null) {
            this.setDatabase();
        }
        if (this.data != null) {
            this.save_as(true);
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
                this.data.clear_labeled();
                this.data.clear_labels();
                this.display_random();
            }
        }
    }

    private void load(){
        String default_directory;
        if(this.save_file != null){
            default_directory = this.save_file.getAbsolutePath();
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
                    if(saveMenuItem.isEnabled()){
                        save();
                    }
                    else {
                        save_as(false);
                    }
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
