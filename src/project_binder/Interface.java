package project_binder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.util.ArrayList;

import static javax.swing.LayoutStyle.ComponentPlacement.*;

public class Interface extends JFrame{

    private Database data = new Database();

    private JPanel panel;

    private JLabel current_file;
    private JLabel current_label;
    private JLabel num_labeled;

    private String placeholder;

    // image variables
    private JLabel image_label;
    private File image_buffer;
    private File latest_image;

    private ArrayList<JCheckBox> labels_box = new ArrayList<>();

    private File save_file;

    private Action next;
    private Action previous;
    private Action finding;
    private Action noFinding;

    private JRadioButtonMenuItem ordered;
    private JRadioButtonMenuItem random;

    private JMenuItem saveMenuItem;

    private GroupLayout gl;
    private GroupLayout.ParallelGroup pGroup;
    private GroupLayout.SequentialGroup sGroup;

    public Interface() {
        initUI();
    }

    private void initUI() {

        createMenuBar();
        setActions();
        createLayout();

        this.setTitle("Labeling Tool");
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

        JMenu fileMenu = new JMenu("Datei");

        JMenuItem selectMenuItem = new JMenuItem("Neue Datei");
        selectMenuItem.addActionListener((event) -> new_file());
        selectMenuItem.setMnemonic(KeyEvent.VK_N);
        selectMenuItem.setToolTipText("Bildordner auswählen und eine neue Speicherdatei anlegen");
        fileMenu.add(selectMenuItem);

        JMenuItem setDatabaseMenuItem = new JMenuItem("Bildordner wählen");
        setDatabaseMenuItem.addActionListener((event) -> setDatabase());
        setDatabaseMenuItem.setMnemonic(KeyEvent.VK_D);
        setDatabaseMenuItem.setToolTipText("Legt einen neuen Ordner fest, aus dem die Bilder gelesen werden.");
        fileMenu.add(setDatabaseMenuItem);

        fileMenu.addSeparator();

        JMenuItem saveAsMenuItem = new JMenuItem("Speichern als");
        saveAsMenuItem.addActionListener((event) -> save_as(false));
        saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
        saveAsMenuItem.setToolTipText("Speichert eine Datei mit neuem Namen");
        fileMenu.add(saveAsMenuItem);

        saveMenuItem = new JMenuItem("Speichern");
        saveMenuItem.addActionListener((event) -> save());
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.setEnabled(false);
        saveMenuItem.setToolTipText("Speichert die aktuelle Datei");
        fileMenu.add(saveMenuItem);

        JMenuItem loadMenuItem = new JMenuItem("Laden");
        loadMenuItem.addActionListener((event) -> load());
        loadMenuItem.setMnemonic(KeyEvent.VK_L);
        loadMenuItem.setToolTipText("Lädt die Daten einer bereits bestehenden Datei");
        fileMenu.add(loadMenuItem);

        fileMenu.addSeparator();

        JMenuItem clearMenuItem = new JMenuItem("Zurücksetzen");
        clearMenuItem.addActionListener((event) -> clear_progress());
        clearMenuItem.setMnemonic(KeyEvent.VK_C);
        clearMenuItem.setToolTipText("Entfernt alle bestehenden labels und startet von vorne");
        fileMenu.add(clearMenuItem);

        menubar.add(fileMenu);

        JMenu editMenu = new JMenu("Bearbeiten");

        JMenuItem labelsFromFileMenuItem = new JMenuItem("Labels von Datei");
        labelsFromFileMenuItem.addActionListener((event) -> setLabelsFromFile());
        labelsFromFileMenuItem.setToolTipText("Lädt alle Labels aus einer gewählten (Text-)Datei");
        editMenu.add(labelsFromFileMenuItem);

        JMenuItem createLabelMenuItem = new JMenuItem("Erstelle Label");
        createLabelMenuItem.addActionListener((event) -> createLabel());
        createLabelMenuItem.setToolTipText("Erstellt ein neues Label");
        editMenu.add(createLabelMenuItem);

        JMenuItem removeLabelMenuItem = new JMenuItem("Entferne Label");
        removeLabelMenuItem.addActionListener((event) -> removeLabel());
        removeLabelMenuItem.setToolTipText("Entfernt ein bereits bestehendes Label");
        editMenu.add(removeLabelMenuItem);

        menubar.add(editMenu);

        JMenu orderMenu = new JMenu("Bildreihenfolge");

        // TODO: check if event needs to be changed to something different

        ordered = new JRadioButtonMenuItem("Folgend");
        ordered.setSelected(true);
        ordered.setToolTipText("Zeigt die Bilder der Reihe nach wie im Ordner an");
        ordered.addActionListener((event) -> display_new());
        orderMenu.add(ordered);

        random = new JRadioButtonMenuItem("Zufällig");
        random.setToolTipText("Zeigt die Bilder in zufälliger Reihenfolge an");
        random.addActionListener((event) -> display_new());
        orderMenu.add(random);

        ButtonGroup imageOrder = new ButtonGroup();
        imageOrder.add(ordered);
        imageOrder.add(random);

        menubar.add(orderMenu);

        setJMenuBar(menubar);
    }

    // creates the actions to be assigned to the buttons and key strokes
    private void setActions(){
        // random = new AbstractAction() {
        //    @Override
        //    public void actionPerformed(ActionEvent e) {
        //        display_new();
        //    }
        //};
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
                addLabel(1, findings);
            }
        };
        noFinding = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLabel(0, new ArrayList<>());
            }
        };
    }

    // creates button layout and sets image placeholder
    private void createLayout(){

        // defining all Layout items
        // JButton randomBtn = new JButton("Random");
        JButton nextBtn = new JButton("Weiter");
        JButton previousBtn = new JButton("Zurück");
        JButton findingBtn = new JButton("Finding");
        JButton noFindingBtn = new JButton("No Finding");

        placeholder = "src/resources/Platzhalter.png";
        image_buffer = new File(placeholder);
        image_label = new JLabel(new ImageIcon(image_buffer.getPath()));

        current_file = new JLabel("Aktuelles Bild:  ");
        current_label = new JLabel("Befund(e):  ");
        num_labeled = new JLabel("Gelabelte Bilder: 0");

        Container pane = getContentPane();
        pane.add(panel);
        gl = new GroupLayout(panel);
        panel.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        //TODO: Improve Layout -> Dynamic

        // Creating the Layout

        setCheckboxes();

        pGroup = gl.createParallelGroup();
        sGroup = gl.createSequentialGroup();

        for(JCheckBox box : labels_box){
            pGroup.addComponent(box);
            sGroup.addComponent(box);
        }

        gl.setHorizontalGroup(
                gl.createSequentialGroup()
                    .addGroup(gl.createParallelGroup()
                        .addGroup(gl.createSequentialGroup()
                                    .addComponent(previousBtn)
                                    .addComponent(nextBtn)
                                    .addPreferredGap(RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(noFindingBtn)
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
                                .addComponent(nextBtn)
                                .addComponent(previousBtn)
                                .addComponent(noFindingBtn)
                                .addComponent(findingBtn))
                        .addComponent(image_label)
                        .addGroup(gl.createParallelGroup()
                                .addComponent(current_file)
                                .addComponent(current_label)
                                .addComponent(num_labeled)))
                    .addGroup(sGroup)
        );

        gl.linkSize(nextBtn, previousBtn, findingBtn, noFindingBtn);
        gl.linkSize(current_file, current_label, num_labeled);

        pack();

        nextBtn.addActionListener(next);
        nextBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "next");
        nextBtn.getActionMap().put("next", next);
        previousBtn.addActionListener(previous);
        previousBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "labeled");
        previousBtn.getActionMap().put("labeled", previous);
        findingBtn.addActionListener(finding);
        findingBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "finding");
        findingBtn.getActionMap().put("finding", finding);
        noFindingBtn.addActionListener(noFinding);
        noFindingBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "noFinding");
        noFindingBtn.getActionMap().put("noFinding", noFinding);

        nextBtn.setToolTipText("Zeigt das nächste ");
        previousBtn.setToolTipText("Geht zurück zu vorherigen (gelabelten) Bildern");
        findingBtn.setToolTipText("Mark image as 'finding detected' " +
                "and add all checked findings (at least one required)");
        noFindingBtn.setToolTipText("Mark image as 'no finding detected'");
    }

    // functions for displaying the images
    private void setImage(File image){
        try {
            this.image_label.setIcon(new ImageIcon(image.getPath()));
            this.setStatus(image);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "Datei nicht gefunden");
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
        this.current_file.setText("Aktuelles Bild: " + filename);
        this.current_label.setText("Befund(e): " + label);
        this.num_labeled.setText("Gelabelte Bilder: " + num_labeled);
    }

    private void fillCheckboxes(File image) {
        ArrayList<String> findings = this.data.get_findings(image);
        for (JCheckBox box : this.labels_box) {
            if(findings.contains(box.getText())) {
                box.setSelected(true);
            }
            else {
                box.setSelected(false);
            }
        }
    }

    private void emptyCheckboxes() {
        for(JCheckBox box : this.labels_box){
            box.setSelected(false);
        }
    }

    // TODO: Add resizing for large images
    // TODO: reload frame auslagern/verbessern
    private void display_new(){
        if(this.data==null){
            JOptionPane.showMessageDialog(panel, "Bitte wählen Sie zuerst einen Bildordner.",
                    "Fehlende Datenbank", JOptionPane.WARNING_MESSAGE);
        }
        else {
            this.data.resetCounter();
            emptyCheckboxes();
            //try {
                //BufferedImage bimg_old = ImageIO.read(image_buffer);
            if (data.is_finished()){
                JOptionPane.showMessageDialog(panel, "Keine ungelabelten Bilder übrig",
                        "Fertig", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                if(ordered.isSelected()){
                    image_buffer = this.data.get_newest_file();
                }
                else if(random.isSelected()) {
                    image_buffer = this.data.get_random_file();
                }
                //BufferedImage bimg_new = ImageIO.read(image_buffer);
                latest_image = image_buffer;
                // TODO this.setImageSize(image_buffer);
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
            this.fillCheckboxes(image_buffer);
            }
        }

    private void display_next(){
        this.data.decrementCounter();
        int len = this.data.labeled.size();
        if (data.get_labeled_counter() == 0) {
            setImage(latest_image);
            image_buffer = latest_image;
            emptyCheckboxes();
        }
        else {
            if (data.get_labeled_counter() > 0) {
                image_buffer = this.data.labeled.get(len - this.data.get_labeled_counter());
                this.setImage(image_buffer);
                this.fillCheckboxes(image_buffer);
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

    private void addLabel(int label, ArrayList<String> findings){
        if(image_buffer.getPath().equals(placeholder)){
            JOptionPane.showMessageDialog(panel, "Kein Bild zum Labeln vorhanden.",
                    "Kein Bild", JOptionPane.WARNING_MESSAGE);
        }
        else{
            this.data.add_label_entry(image_buffer, label, findings);
            display_new();
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
                    "Wählen Sie einen Ort für ihre Speicherdatei.",
                    "Speicher-Dialog", JOptionPane.OK_CANCEL_OPTION);
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
        if (this.data.img_dir != null){
            option = JOptionPane.showConfirmDialog(panel,
                    "Ein aktiver Bildordner ist bereits ausgewählt.\n Möchten Sie den Ordner trotzdem ändern?",
                        "Bestätigungs-Dialog", JOptionPane.OK_CANCEL_OPTION);
        }
        else{
            option = JOptionPane.showConfirmDialog(panel, "Wählen Sie den Bildordner",
                    "Ordner-Dialog", JOptionPane.OK_CANCEL_OPTION);
        }
        if (option == JOptionPane.OK_OPTION) {
            JFileChooser chooser = new JFileChooser("./");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            option = chooser.showOpenDialog(panel);
            if (option == JFileChooser.APPROVE_OPTION) {
                File img_dir = chooser.getSelectedFile();
                data = new Database(img_dir);
                this.display_new();
            }
        }
    }

    private void new_file(){
        if (this.data.img_dir == null) {
            this.setDatabase();
        }
        if (this.data.img_dir != null) {
            this.save_as(true);
        }
    }

    private void clear_progress(){
        if(this.data == null){
            JOptionPane.showMessageDialog(panel, "Bildordner ist noch nicht ausgewählt.",
                    "Fehlende Datenbank", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            int dialogResult = JOptionPane.showConfirmDialog(panel, "Soll der bisherige Fortschritt gelöscht werden?",
                    null, JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                this.data.clear_labeled();
                this.data.clear_labels();
                this.display_new();
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
            File saveFile = chooser.getSelectedFile();
            this.data = new Database();
            this.data.load_from_json(saveFile);
            this.save_file = saveFile;
            display_new();
        }
    }

    private void setLabelsFromFile(){
        // specify a file to load the labels from
        JFileChooser chooser = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        chooser.setFileFilter(filter);
        int option = chooser.showOpenDialog(panel);
        if(option == JFileChooser.APPROVE_OPTION){
            File labelsFile = chooser.getSelectedFile();
            this.data.set_labels_template(labelsFile);
            this.setCheckboxes();
        }
    }

    private void createLabel(){
        String label = JOptionPane.showInputDialog(panel, "Geben Sie das neue Label ein.");
        if(label != null){
            label = label.trim();
            if(!data.labels_template.labels.contains(label.trim())){
                this.data.labels_template.add_label(label);
                this.addCheckbox(label);
            }
        }
    }

    private void addCheckbox(String label){
        JCheckBox newLabel = new JCheckBox(label);
        labels_box.add(newLabel);
        pGroup.addComponent(newLabel);
        sGroup.addComponent(newLabel);
        panel.revalidate();
        panel.repaint();
    }

    private void removeLabel(){
        String label = JOptionPane.showInputDialog(panel, "Geben Sie das Label an, das entfernt werden soll.");
        if(label != null) {
            this.data.remove_label(label);
            this.removeCheckbox(label);
        }
    }

    private void removeCheckbox(String label){
        JCheckBox toRemove = new JCheckBox();
        for(JCheckBox box : labels_box){
            if(box.getText().equals(label)){
                toRemove = box;
            }
        }
        panel.remove(toRemove);
        labels_box.remove(toRemove);
        this.revalidate();
        this.repaint();
    }

    private void setCheckboxes(){
        labels_box.clear();
        for(int i = 0; i<data.labels_template.labels.size(); i++){
            labels_box.add(new JCheckBox(data.labels_template.labels.get(i)));
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



    public static void main(String[] args){
        java.util.Locale.setDefault(java.util.Locale.ENGLISH);
         EventQueue.invokeLater(() -> {
             Interface ex = new Interface();
             ex.setVisible(true);
         });
    }
}
