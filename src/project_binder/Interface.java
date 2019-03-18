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

        createMenuBar();
        var openButton = new JButton("Open");
        var testButton = new JButton("Test");

        openButton.addActionListener((event) -> set_database());
        testButton.addActionListener((event) -> JOptionPane.showMessageDialog(null, data.img_dir));

        createLayout(testButton);

        setTitle("XRay-Tinder");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createMenuBar(){
        var menubar = new JMenuBar();
        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        var eMenuItem = new JMenuItem("Select");
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Choose File Directory");
        eMenuItem.addActionListener((event) -> set_database());

        fileMenu.add(eMenuItem);
        menubar.add(fileMenu);

        setJMenuBar(menubar);
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

        gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(arg[0]));

        gl.setVerticalGroup(gl.createSequentialGroup().addComponent(arg[0]));
    }

    public static void main(String[] args){
         EventQueue.invokeLater(() -> {
             var ex = new Interface();
             ex.setVisible(true);
         });
    }
}
