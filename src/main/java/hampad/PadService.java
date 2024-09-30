package hampad;



import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;


import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import java.awt.*;

import java.io.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PadService extends JFrame {
    private JFrame frame;
    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenu aboutMenu;
    private JMenuItem newMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem helpMenuItem;
    private JMenuItem searchMenu;
    private JMenuItem openMenuItem;
    private JMenuItem printMenuItem;
    private JMenuItem aboutMenuItem;
    private RSyntaxTextArea syntaxTextArea;
    private File file;
    private boolean changed = false;

    /**
     * a basic PadService with empty textArea, menu and print
     *
     * @author Hamhuo
     * @date 9/30/2024 10:36 AM
     */
    public PadService() throws IOException {

        //an empty frame
        frame = new JFrame("New Pad");
        initComponents();

        //new frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }

    public void initComponents() throws IOException {

        //menu bar
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        //File menu
        menuFile = new JMenu("File");
        openMenuItem = new JMenuItem("Open File");
        newMenuItem = new JMenuItem("New File");
        saveMenuItem = new JMenuItem("Save File");
        searchMenu = new JMenuItem("Search");
        exitMenuItem = new JMenuItem("Exit without Saving");
        printMenuItem = new JMenuItem("Print");


        menuFile.add(openMenuItem);
        menuFile.add(newMenuItem);
        menuFile.add(saveMenuItem);
        menuFile.add(searchMenu);
        menuFile.add(exitMenuItem);
        menuFile.add(printMenuItem);
        menuBar.add(menuFile);

        //about menu
        aboutMenu = new JMenu("About");
        helpMenuItem = new JMenuItem("Help");
        aboutMenuItem = new JMenuItem("Hamhuo & JiaPeng" + "Designed and Provide");

        aboutMenu.add(helpMenuItem);
        aboutMenu.add(aboutMenuItem);
        menuBar.add(aboutMenu);


        //textArea
        syntaxTextArea = new RSyntaxTextArea();
        syntaxTextArea.setCodeFoldingEnabled(true);
        syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
        RTextScrollPane sp = new RTextScrollPane(syntaxTextArea);
        initTextarea();
        frame.add(sp);

        //openfile
        openMenuItem.addActionListener(actionEvent -> openAction());

        //newfile Listener
        newMenuItem.addActionListener(actionEvent -> newAction());

        //save file Listener
        saveMenuItem.addActionListener(actionEvent -> saveAction());

        //search file Listener
        searchMenu.addActionListener(actionEvent -> searchAction());

        //exit file Listener
        exitMenuItem.addActionListener(actionEvent -> exitAction());

        //export file to pdf Listener
        printMenuItem.addActionListener(actionEvent -> PadPrinter.newPadPrinter().doPrint());


        //helpItem Listener
        helpMenuItem.addActionListener(actionEvent -> helpAction());


        syntaxTextArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                changed = true;
                updateFrameTitle();
            }
        });
    }

    private void updateFrameTitle() {
        String title = "New Pad";
        if (file != null) {
            title = file.getName();
        } else if (syntaxTextArea.getText().length() > 0) {
            title = syntaxTextArea.getText().substring(0, Math.min(16, syntaxTextArea.getText().length()));
        }
        frame.setTitle(title);
    }


    public void initTextarea() {

        //empty textArea
        syntaxTextArea.setText("");

        //text Area
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:MM_dd/MM/yyyy");
        String dateTime = LocalDateTime.now().format(formatter);
        syntaxTextArea.setText(dateTime);
        syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        syntaxTextArea.setCodeFoldingEnabled(true);
        changed = false;
        file = null;
        updateFrameTitle();
    }

    private void newAction() {
        System.out.println("new");
        frame.setTitle("New Pad");
        if (changed) {
            int i = JOptionPane.showConfirmDialog(syntaxTextArea, "Some Text Still on the page, Want Save them?");
            if (i == JOptionPane.YES_OPTION) {
                saveAction();
            }
        }
        System.out.println("clear");
        initTextarea();
    }


    private void openAction() {

        JFileChooser chooser = new JFileChooser();

        int i = chooser.showOpenDialog(frame);

        if (i == JFileChooser.APPROVE_OPTION) {

            file = chooser.getSelectedFile();

            try {
                String content = PadFileIO.openFile(file);
                syntaxTextArea.setText(content);


                // Set code highlight based on file extension
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".java")) {
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                } else if (fileName.endsWith(".xml")) {
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
                } else if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
                } else if (fileName.endsWith(".js")) {
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
                } else if (fileName.endsWith(".css")) {
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);
                } else if (fileName.endsWith(".py")) {
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
                } else {
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE); // 如果没有匹配到，就不使用高亮
                }
                frame.setTitle(file.getName());
                changed = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    private void saveAction() {

        JFileChooser chooser = PadFileIO.getInstance().getFileChooser();
        int i = chooser.showSaveDialog(frame);


        if (i == JFileChooser.APPROVE_OPTION){
            try {
                String filePath = chooser.getSelectedFile().getAbsolutePath();
                String description = chooser.getFileFilter().getDescription();
                String content = syntaxTextArea.getText();
                PadFileIO.saveFile(content,filePath,description);
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }


    private void searchAction() {
        PadSearch search = new PadSearch(this, syntaxTextArea);
    }



    private void exitAction() {
       frame.dispose();

    }

    private void helpAction() {
        try {
            URI uri = new URI("https://hamhuo-hub.github.io/static-webapp/");
            Desktop dt = Desktop.getDesktop();
            dt.browse(uri.toURL().toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

