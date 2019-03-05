package project_binder;

import javax.swing.*;
import java.awt.EventQueue;

public class Interface extends JFrame{
    private JPanel panel1;
    private JTextField previous;
    private Database data;

    public Interface() {
        initUI();
    }

    private void initUI() {

        var openButton = new JButton("Open");
        var testButton = new JButton("Test");

        openButton.addActionListener((event) -> make_database());
        testButton.addActionListener((event) -> JOptionPane.showMessageDialog(null, image_path));

        createLayout(openButton);

        setTitle("XRay-Tinder");
        setSize(1600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private String make_database(){
        JFileChooser chooser = new JFileChooser("./");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(null);
        return chooser.getSelectedFile().getAbsolutePath();
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
