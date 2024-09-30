package hampad;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;


import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
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
        openMenuItem = new JMenuItem("Open");
        newMenuItem = new JMenuItem("New File");
        saveMenuItem = new JMenuItem("Save");
        searchMenu = new JMenuItem("Search");
        exitMenuItem = new JMenuItem("Exit");
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

    private void exportAction() {
        try (PDDocument doc = new PDDocument()) {

            // Create a new PDF page
            PDPage page = new PDPage();
            doc.addPage(page);

            // get text from text area
            String text = syntaxTextArea.getText();

            // create input stream for pdf file
            final byte[] byteStream = text.getBytes();


            // use PDPageContentStream to write file
            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.beginText();

                // Set text start
                contentStream.newLineAtOffset(50, 750); // Adjust location according to requirements

                // Set font and font size
                PDFont font = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
                contentStream.setFont(font, 12);

                // Split the text into lines and write line by line
                String[] lines = text.split("\n");
                for (String line : lines) {
                    contentStream.showText(line); // Write the current line
                    contentStream.newLineAtOffset(0, -15); // Newline, move the Y-axis down
                }

                contentStream.endText();
            }

            // Save and close the PDF
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                doc.save(chooser.getSelectedFile()); // Save document as output.pdf
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    //to-do separate to sigle class
    private void openAction() {

        JFileChooser chooser = new JFileChooser();

        int i = chooser.showOpenDialog(frame);

        if (i == JFileChooser.APPROVE_OPTION) {

            file = chooser.getSelectedFile();

            try {
                // Read the file contents and display them in textArea
                BufferedReader in = new BufferedReader(new FileReader(file));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line).append("\n");
                }
                syntaxTextArea.setText(content.toString());
                in.close();

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
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    private void saveAction() {

        JFileChooser chooser = PadFileIO.getInstance().getFileChooser();
        int i = chooser.showSaveDialog(frame);
        String filePath = chooser.getSelectedFile().getAbsolutePath();
        String description = chooser.getFileFilter().getDescription();
        String content = syntaxTextArea.getText();

        if (i == JFileChooser.APPROVE_OPTION){
            try {
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

