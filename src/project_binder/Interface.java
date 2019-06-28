package project_binder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static javax.swing.LayoutStyle.ComponentPlacement.*;

public class Interface extends JFrame{

    private Database data = new Database();

    private JPanel panel;

    private JLabel current_file;
    private JLabel current_label;
    private JLabel num_labeled;
    private String[] statusBar;

    private String placeholder = "src/resources/Platzhalter.png";
    private File boot_file = new File("src/resources/bootoptions.json");

    // image variables
    private JLabel image_label;
    private File image_buffer;
    private File latest_image;
    // shows the current size relative to original size, ranges from 0 to 100
    private double scale_factor = 100;

    private JButton plusButton;
    private JButton minusButton;
    private JFormattedTextField image_scale;

    private ArrayList<JCheckBox> labels_box = new ArrayList<>();

    private File save_file;

    private Action next;
    private Action previous;
    private Action finding;
    private Action noFinding;

    private JButton findingBtn;
    private JButton noFindingBtn;

    private JRadioButtonMenuItem ordered;
    private JRadioButtonMenuItem random;

    private JMenuItem saveMenuItem;

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
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Locale locale = new Locale("de", "GER");
        JOptionPane.setDefaultLocale(locale);

        WindowListener exitListener = exit();
        this.addWindowListener(exitListener);

        this.loadBootOptions();
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
        clearMenuItem.setToolTipText("Entfernt alle bestehenden Labels und startet von vorne");
        fileMenu.add(clearMenuItem);

        menubar.add(fileMenu);

        JMenu editMenu = new JMenu("Bearbeiten");

        JMenuItem createLabelMenuItem = new JMenuItem("Erstelle Label");
        createLabelMenuItem.addActionListener((event) -> createLabel());
        createLabelMenuItem.setToolTipText("Erstellt ein neues Label");
        editMenu.add(createLabelMenuItem);

        JMenuItem removeLabelMenuItem = new JMenuItem("Entferne Label");
        removeLabelMenuItem.addActionListener((event) -> removeLabel());
        removeLabelMenuItem.setToolTipText("Entfernt ein bereits bestehendes Label");
        editMenu.add(removeLabelMenuItem);

        editMenu.addSeparator();

        JMenuItem labelsFromFileMenuItem = new JMenuItem("Labels aus Datei");
        labelsFromFileMenuItem.addActionListener((event) -> createLabelsFromFile());
        labelsFromFileMenuItem.setToolTipText("Lädt alle Labels aus einer gewählten (Text-)Datei");
        editMenu.add(labelsFromFileMenuItem);

        JMenuItem clearLabels = new JMenuItem("Alle Labels löschen");
        clearLabels.addActionListener((event) -> removeAllLabels(true));
        clearLabels.setToolTipText("Entfernt alle vorhandenen Labels von der Oberfläche.\n" +
                "Gelabelte Bilder behalten ihre Labels, gehen Sie hierfür auf Datei -> Zurücksetzen.");
        editMenu.add(clearLabels);

        editMenu.addSeparator();

        JMenuItem changeStatusBarMenuItem = new JMenuItem("Statusleiste bearbeiten");
        changeStatusBarMenuItem.addActionListener((event) -> editStatusBar());
        changeStatusBarMenuItem.setToolTipText("Erlaubt das Umbenennen der Elemente in der Statusleiste");
        editMenu.add(changeStatusBarMenuItem);

        JMenuItem changeButtonLabelMenuItem = new JMenuItem("Button umbenennen");
        changeButtonLabelMenuItem.addActionListener((event) -> changeButtonLabel());
        changeButtonLabelMenuItem.setToolTipText("Erlaubt das Ändern der Button-Beschriftung");
        editMenu.add(changeButtonLabelMenuItem);

        editMenu.addSeparator();

        JMenuItem resetButtonStatusMenuItem = new JMenuItem("Zurücksetzen");
        resetButtonStatusMenuItem.addActionListener((event) -> resetBootOptions());
        resetButtonStatusMenuItem.setToolTipText("Setzt die Beschriftungen der Buttons und der Statusleiste zurück");
        editMenu.add(resetButtonStatusMenuItem);

        menubar.add(editMenu);

        JMenu orderMenu = new JMenu("Bildreihenfolge");

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
        findingBtn = new JButton("Befund");
        noFindingBtn = new JButton("Kein Befund");

        plusButton = new JButton("+");
        minusButton = new JButton("-");
        image_scale = new JFormattedTextField(Double.toString(scale_factor));

        image_buffer = new File(placeholder);
        image_label = new JLabel(new ImageIcon(image_buffer.getPath()));

        initializeStatusBar();

        Container pane = getContentPane();
        pane.add(panel);
        GroupLayout gl = new GroupLayout(panel);
        panel.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        // Creating the Layout

        pGroup = gl.createParallelGroup();
        sGroup = gl.createSequentialGroup();

        setCheckboxes();

        for(JCheckBox box : labels_box){
            pGroup.addComponent(box);
            sGroup.addComponent(box);
        }

        gl.setHorizontalGroup(
                gl.createSequentialGroup()
                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(gl.createSequentialGroup()
                                    .addComponent(previousBtn)
                                    .addComponent(nextBtn)
                                    .addGap(0, 100, 500)
                                    .addComponent(minusButton)
                                    .addComponent(image_scale)
                                    .addComponent(plusButton)
                                    .addGap(0, 100, 500)
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
                    .addGap(0, 20, 50)
        );

        gl.setVerticalGroup(
                gl.createParallelGroup()
                    .addGroup(gl.createSequentialGroup()
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(nextBtn)
                                .addComponent(previousBtn)
                                .addComponent(minusButton)
                                .addComponent(image_scale)
                                .addComponent(plusButton)
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

        nextBtn.addActionListener(next);
        nextBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"),
                "next");
        nextBtn.getActionMap().put("next", next);
        previousBtn.addActionListener(previous);
        previousBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"),
                "labeled");
        previousBtn.getActionMap().put("labeled", previous);
        findingBtn.addActionListener(finding);
        findingBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"),
                "finding");
        findingBtn.getActionMap().put("finding", finding);
        noFindingBtn.addActionListener(noFinding);
        noFindingBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"),
                "noFinding");
        noFindingBtn.getActionMap().put("noFinding", noFinding);

        plusButton.addActionListener((event) -> expandImage());
        plusButton.setMnemonic(KeyEvent.VK_PLUS);
        plusButton.setToolTipText("Vergößert das Bild");
        minusButton.addActionListener((event) -> shrinkImage());
        minusButton.setMnemonic(KeyEvent.VK_MINUS);
        minusButton.setToolTipText("Verkleinert das Bild");
        image_scale.addActionListener((event) -> setSize());

        nextBtn.setToolTipText("Zeigt das nächste Bild gelabelte Bild an");
        previousBtn.setToolTipText("Geht zurück zu vorherigen (gelabelten) Bildern");
        findingBtn.setToolTipText("Markiert das Bild als 'Befund/Fehler/etc. gefunden'" +
                "und fügt alle markierten Labels zur Datenbank hinzu");
        noFindingBtn.setToolTipText("Markiert das Bild als 'Kein Befund/Fehler/etc. gefunden");

        pack();
    }

    // functions for displaying the images
    private void setImage(File image){
        try {
            BufferedImage buffered = ImageIO.read(image);
            // automatically downscale, if image is larger than screen size
            //this.adjustSizeFactor(buffered);
            buffered = this.resizeImage(buffered);
            this.image_label.setIcon(new ImageIcon(buffered));
            this.setStatus(image);
            if(this.getExtendedState() == 0) {
                pack();
                setLocationRelativeTo(null);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "Datei nicht gefunden");
        }
    }

    // adjust scale_factor, so that image fits on screen
    private void adjustSizeFactor(BufferedImage image){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double max_height = screenSize.getHeight();
        double max_width = screenSize.getWidth();

        double current_height = (double) image.getHeight() * scale_factor / 100;
        double current_width = (double) image.getWidth() * scale_factor / 100;

        while (current_height > max_height * 0.9 || current_width > max_width * 0.9) {
            scale_factor = 0.95 * scale_factor;
            current_height = (double) image.getHeight() * scale_factor / 100;
            current_width = (double) image.getWidth() * scale_factor / 100;
        }

    }

    private BufferedImage resizeImage(BufferedImage image){
        double original_height = (double) image.getHeight();
        double original_width = (double) image.getWidth();

        double new_height = this.scale_factor / 100. * original_height;
        double new_width = this.scale_factor / 100. * original_width;

        BufferedImage resized = new BufferedImage((int) new_width, (int) new_height, image.getType());
        Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, (int) new_width, (int) new_height, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return resized;
    }

    private void expandImage(){
        if(scale_factor < 250.) {
            scale_factor = (scale_factor + 10) - (scale_factor % 10);
            setImage(image_buffer);
            image_scale.setText(Double.toString(scale_factor));
        }
    }

    private void shrinkImage(){
        if(scale_factor > 10.) {
            if ((scale_factor % 10) == 0) {
                scale_factor = scale_factor - 10;
            } else {
                scale_factor = scale_factor - (scale_factor % 10);
            }
            setImage(image_buffer);
            image_scale.setText(Double.toString(scale_factor));
        }
    }

    private void setSize(){
        double new_scale = Double.parseDouble(image_scale.getText());
        if(new_scale >= 1. && new_scale <= 250.){
            scale_factor = new_scale;
            setImage(image_buffer);
        }
        else{
            image_scale.setText(Double.toString(scale_factor));
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
            label = "Nein";
        }
        else{
            label = "Ja";
        }
        this.current_file.setText(statusBar[0] + ": " + filename);
        this.current_label.setText(statusBar[1] + ": " + label);
        this.num_labeled.setText(statusBar[2]+ ": " + num_labeled);
    }

    private void initializeStatusBar(){
        if(current_file == null) {
            statusBar = new String[]{"Aktuelles Bild", "Befund", "Gelabelte Bilder"};
            current_file = new JLabel(statusBar[0] + ": ");
            current_label = new JLabel(statusBar[1] + ": ");
            num_labeled = new JLabel(statusBar[2] + ": 0");
        }
        else{
            this.current_file.setText(statusBar[0] + ": ");
            this.current_label.setText(statusBar[1] +": ");
            this.num_labeled.setText(statusBar[2]+ ": 0");
        }
    }

    private void editStatusBar(){
        String choice = JOptionPane.showInputDialog("Welches Statuselement möchten Sie umbenennen? \n" +
                "(1 = "+statusBar[0]+", 2 = "+statusBar[1]+", 3 = "+statusBar[2]+")");
        if(choice != null){
            choice = choice.trim();
            if(!(choice.equals("1") || choice.equals("2") || choice.equals("3"))){
                JOptionPane.showMessageDialog(panel, "Ungültige Auswahl");
            }
            else {
                String newName = JOptionPane.showInputDialog("Geben Sie die neue Bezeichnung ein.");
                if (newName != null) {
                    statusBar[Integer.parseInt(choice)-1] = newName.trim();
                    if(this.data.img_dir == null) {
                        initializeStatusBar();
                    }
                    else{
                        setStatus(image_buffer);
                    }
                }
            }
        }
    }

    private void changeButtonLabel(){
        String choice = JOptionPane.showInputDialog("Welchen Button möchten Sie umbenennen? \n" +
                "(1 = "+findingBtn.getText()+", 2 = "+noFindingBtn.getText());
        if(choice != null) {
            choice = choice.trim();
            if (choice.equals("1")) {
                String newName = JOptionPane.showInputDialog("Geben Sie den neuen Namen ein.");
                if (newName != null) {
                    findingBtn.setText(newName.trim());
                    revalidate();
                    repaint();
                }
            } else if (choice.equals("2")) {
                String newName = JOptionPane.showInputDialog("Geben Sie den neuen Namen ein.");
                if (newName != null) {
                    noFindingBtn.setText(newName.trim());
                    revalidate();
                    repaint();
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Ungültige Auswahl");
            }
        }
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

    private void display_new(){
        if(this.data==null){
            JOptionPane.showMessageDialog(panel, "Bitte wählen Sie zuerst einen Bildordner.",
                    "Fehlende Datenbank", JOptionPane.WARNING_MESSAGE);
        }
        else {
            this.data.resetCounter();
            emptyCheckboxes();
            if (data.is_finished()){
                if(save_file != null) {
                    JOptionPane.showMessageDialog(panel, "Keine ungelabelten Bilder übrig",
                            "Fertig", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else {
                if(ordered.isSelected()){
                    image_buffer = this.data.get_newest_file();
                }
                else if(random.isSelected()) {
                    image_buffer = this.data.get_random_file();
                }
                latest_image = image_buffer;
                this.setImage(image_buffer);
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
            this.setStatus(image_buffer);
            this.display_new();
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
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.json", "json");
        chooser.setFileFilter(filter);
        int option = chooser.showOpenDialog(panel);
        if(option == JFileChooser.APPROVE_OPTION){
            File saveFile = chooser.getSelectedFile();
            this.removeAllLabels(false);
            this.data = new Database();
            this.data.load_from_json(saveFile);
            this.save_file = saveFile;
            this.saveMenuItem.setEnabled(true);
            for(String label : this.data.labels_template.labels){
                this.createCheckbox(label);
            }
            display_new();
        }
    }

    private void load_on_startup(){
        if(this.save_file.exists()) {
            this.removeAllLabels(false);
            this.data = new Database();
            this.data.load_from_json(this.save_file);
            this.saveMenuItem.setEnabled(true);
            for (String label : this.data.labels_template.labels) {
                this.createCheckbox(label);
            }
            display_new();
        }
    }

    private void saveBootOptions(){
        JSONObject bootOptionsJS = new JSONObject();

        if(save_file != null) {
            bootOptionsJS.put("save_file", save_file.getAbsolutePath());
        }
        else{
            bootOptionsJS.put("save_file", "None");
        }

        Map button_names = new LinkedHashMap(2);
        button_names.put("finding", findingBtn.getText());
        button_names.put("no finding", noFindingBtn.getText());
        bootOptionsJS.put("buttons", button_names);

        JSONArray statusbar = new JSONArray();
        for(String name : this.statusBar) {
            statusbar.add(name);
        }
        bootOptionsJS.put("statusbar", statusbar);

        bootOptionsJS.put("is_ordered", ordered.isSelected());

        String path = boot_file.getAbsolutePath();
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(path);
            pw.write(bootOptionsJS.toJSONString());
        }
        catch (IOException e){
            System.err.println("Caught IOException" + e.getMessage());
        }
        finally {
            if(pw != null){
                pw.flush();
                pw.close();
            }
        }
    }

    private void loadBootOptions(){
        try {
            JSONObject bootOptions = (JSONObject) new JSONParser().parse(new FileReader(boot_file.getAbsolutePath()));

            String save_file = (String) bootOptions.get("save_file");
            JSONArray statusbar = (JSONArray) bootOptions.get("statusbar");
            Map button_names = (Map) bootOptions.get("buttons");

            for(int i=0; i<3; i++) {
                this.statusBar[i] = (String) statusbar.get(i);
            }
            findingBtn.setText((String) button_names.get("finding"));
            noFindingBtn.setText((String) button_names.get("no finding"));

            boolean is_ordered = (boolean) bootOptions.get("is_ordered");
            if(is_ordered){
                ordered.setSelected(true);
            }
            else{
                random.setSelected(true);
            }

            this.initializeStatusBar();

            this.revalidate();
            this.repaint();

            if (!save_file.equals("None")) {
                this.save_file = new File(save_file);
                this.load_on_startup();
            }
        }
        catch (IOException ioe){
            System.err.println("Caught IOException" + ioe.getMessage());
        }
        catch (ParseException pe){
            System.err.println("Caught ParseException" + pe.getMessage());
        }
    }

    private void resetBootOptions(){
        int option = JOptionPane.showConfirmDialog(panel, "Sollen alle Beschriftungen zurückgesetzt werden?");
        if(option == JOptionPane.OK_OPTION) {
            findingBtn.setText("Befund");
            noFindingBtn.setText("Kein Befund");
            this.statusBar[0] = "Aktuelles Bild";
            this.statusBar[1] = "Befund";
            this.statusBar[2] = "Gelabelte Bilder";
            this.ordered.setSelected(true);
            this.save_file = null;
            this.initializeStatusBar();
            this.revalidate();
            this.repaint();
        }
    }

    private void createLabelsFromFile(){
        // specify a file to load the labels from
        JFileChooser chooser = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", "txt", "text");
        chooser.setFileFilter(filter);
        int option = chooser.showOpenDialog(panel);
        if(option == JFileChooser.APPROVE_OPTION){
            File labelsFile = chooser.getSelectedFile();
            this.removeAllLabels(false);
            this.data.set_labels_template(labelsFile);
            for(String label : this.data.labels_template.labels) {
                this.createCheckbox(label);
            }
        }
    }

    private void createLabel(){
        String label = JOptionPane.showInputDialog(panel, "Geben Sie das neue Label ein.");
        if(label != null){
            label = label.trim();
            if(!data.labels_template.labels.contains(label.trim())){
                this.data.labels_template.add_label(label);
                this.createCheckbox(label);
            }
        }
    }

    private void createCheckbox(String label){
        JCheckBox newLabel = new JCheckBox(label);
        labels_box.add(newLabel);
        pGroup.addComponent(newLabel);
        sGroup.addComponent(newLabel);
        panel.revalidate();
        panel.repaint();
        pack();
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
        panel.revalidate();
        panel.repaint();
        pack();
    }

    private void removeAllLabels(boolean askMessage){
        int option;
        if(askMessage){
            option = JOptionPane.showConfirmDialog(panel,
                    "Möchten Sie wirklich alle Labels löschen?");
        }
        else{
            option = JOptionPane.OK_OPTION;
        }

        if(option == JOptionPane.OK_OPTION && labels_box.size() > 0){
            for(JCheckBox box : labels_box){
                this.data.remove_label(box.getText());
                panel.remove(box);
            }
            labels_box.clear();
            panel.revalidate();
            panel.repaint();
            pack();
        }
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
                        "Vor dem Verlassen speichern?");
                if (result == JOptionPane.YES_OPTION) {
                    if(saveMenuItem.isEnabled()){
                        save();
                    }
                    else {
                        save_as(false);
                    }
                    saveBootOptions();
                    System.exit(0);
                } else if (result == JOptionPane.NO_OPTION) {
                    saveBootOptions();
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
