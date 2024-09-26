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
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.xml.stream.Location;
import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PadService extends JFrame{
    private JFrame frame;
    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenuItem newMenu;
    private JMenuItem saveMenu;
    private JMenuItem exitMenu;
    private JMenuItem helpMenu;
    private JMenuItem searchMenu;
    private JMenuItem openMenu;
    private JMenuItem printMenuItem;
    private JMenuItem exportMenuItem;
    private JMenu aboutMenu;
    private RSyntaxTextArea syntaxTextArea;
    private File file;
    private boolean changed = false;
    public PadService() {
        frame = new JFrame("New Pad");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);


        //menu bar
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        menuFile = new JMenu("File");
            //openfile
        openMenu = new JMenuItem("Open");
        openMenu.addActionListener(actionEvent -> openAction());
        menuFile.add(openMenu);
            //newfile
        newMenu = new JMenuItem("New File");
        newMenu.addActionListener(actionEvent -> {
            try {
                newAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        menuFile.add(newMenu);
            //save file
        saveMenu = new JMenuItem("Save");
        saveMenu.addActionListener(actionEvent -> {
            try {
                saveAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        menuFile.add(saveMenu);
            //search file
        searchMenu = new JMenuItem("Search");
        searchMenu.addActionListener(actionEvent -> searchAction());
        menuFile.add(searchMenu);
            //exit file
        exitMenu = new JMenuItem("Exit");
        System.out.println("hit exit");
        exitMenu.addActionListener(actionEvent -> {
            if (changed) {
                int O = JOptionPane.showConfirmDialog(syntaxTextArea,"Some Text Still on the page, Want Save them?");
                if(O == JOptionPane.YES_OPTION){
                    try {
                        saveAction();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            int i = JOptionPane.showConfirmDialog(syntaxTextArea,"are you sure you want to exit?");
            if (i == JOptionPane.YES_OPTION) {
                frame.dispose();
            }
            System.out.println("cancl ed");
        });
        menuFile.add(exitMenu);

        //export menu
        printMenuItem = new JMenuItem("Print");
        exportMenuItem = new JMenuItem("Export to PDF");
        printMenuItem.addActionListener(actionEvent -> {
            PadPrinter printer = new PadPrinter();
            printer.doPrint();
        });
        exportMenuItem.addActionListener(actionEvent -> {
            try {
                exportAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        menuFile.add(exportMenuItem);
        menuFile.add(printMenuItem);
        menuBar.add(menuFile);
            //about
        aboutMenu = new JMenu("About");
        helpMenu = new JMenuItem("Help");
                //jump url
        helpMenu.addActionListener(actionEvent -> {
            System.out.println("hit help");
            URI uri = null;
            try {
                uri = new URI("https://hamhuo-hub.github.io/static-webapp/");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            Desktop dt = Desktop.getDesktop();
            try {
                dt.browse(uri.toURL().toURI());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

            //help menu
        aboutMenu.add(helpMenu);
        JMenuItem aboutMenuItem = new JMenuItem("Hamhuo & JiaPeng" + "Designed and Provide");
        aboutMenu.add(aboutMenuItem);
        menuBar.add(aboutMenu);


        initTextarea();

        syntaxTextArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                try {
                    changed = true;
                    if(syntaxTextArea.getText().length() > 16){
                    frame.setTitle(syntaxTextArea.getText(0,16));}
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        //add rollable panel
        RTextScrollPane sp = new RTextScrollPane(syntaxTextArea);
        frame.add(sp);
        frame.setVisible(true);
    }

    public void initTextarea(){
        changed = false;
        //text Area
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:MM_dd/MM/yyyy");
        String dateTime = LocalDateTime.now().format(formatter);
        syntaxTextArea = new RSyntaxTextArea();
        syntaxTextArea.setText(dateTime);
        syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        syntaxTextArea.setCodeFoldingEnabled(true);
    }
    private void exportAction() throws IOException {
        // create a new pdf file
        PDDocument doc = new PDDocument();
        System.out.println("Create document");

        // get text from text area
        String text = syntaxTextArea.getText();

        // create input stream for pdf file
        final byte[] image = text.getBytes();
        System.out.println(image); // Output byte array for debugging

        try {
            // Create a new PDF page
            PDPage page = new PDPage();
            doc.addPage(page);

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
            int i = chooser.showSaveDialog(frame);
            if (i == JFileChooser.APPROVE_OPTION) {
                doc.save(chooser.getSelectedFile()); // Save document as output.pdf
                System.out.println("PDF is exported successfully.");
            }
        } finally {
            // Close the document
            doc.close();
        }
    }
    private void newAction() throws IOException {
        System.out.println("new");
        frame.setTitle("New Pad");
        if(changed){
            int i = JOptionPane.showConfirmDialog(syntaxTextArea,"Some Text Still on the page, Want Save them?");
            if(i == JOptionPane.YES_OPTION){ saveAction();}
        }
        System.out.println("clear");
        initTextarea();
    }

    private void openAction(){
        System.out.println("open");
            JFileChooser chooser = new JFileChooser();
            int i = chooser.showOpenDialog(frame);
            if (i == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
                System.out.println("file opened: " + file.getName());

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
                System.out.println("open canceled");
            }
    }

    private void saveAction() throws IOException {
        System.out.println("save");
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("TXT File", "txt"));
        int i = chooser.showSaveDialog(frame);
        if (i == JFileChooser.APPROVE_OPTION) {
            {
                try {
                    saveFileCore(chooser);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (i == JFileChooser.CANCEL_OPTION) {
            System.out.println("cancel ed");
        }
    }

        private void searchAction(){
            System.out.println("search");
            String text = syntaxTextArea.getText();
           PadSearch search = new PadSearch(this,syntaxTextArea);
        }

        public void saveFileCore(JFileChooser chooser) throws IOException {
            file = chooser.getSelectedFile();
            System.out.println("file create");
            //A funny fact is you do not have to associated file into Textarea.
            //save file and write to TextArea is separated
            PrintWriter out = new PrintWriter(new FileWriter(file));
            if (file.canWrite()) {
                out.write(syntaxTextArea.getText());
                System.out.println(syntaxTextArea.getText());
                //remember to close upstream
                out.close();
            }
        }
    }

