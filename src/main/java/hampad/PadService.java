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
    private JFrame frame; // Main frame for the application
    private JMenuBar menuBar; // Menubar for the frame
    private JMenu menuFile, aboutMenu; // Menus for file operations and about information
    private JMenuItem newMenuItem, saveMenuItem, exitMenuItem, helpMenuItem, searchMenu, openMenuItem, printMenuItem, aboutMenuItem;
    private RSyntaxTextArea syntaxTextArea; // Text area with syntax highlighting capabilities
    private File file; // File currently being edited
    private boolean changed = false; // Flag to check if current document is changed

    public PadService() throws IOException {
        frame = new JFrame("New Pad"); // Create new JFrame with title 'New Pad'
        initComponents(); // Initialize UI components

        // Setup frame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Set size
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true); // Make frame visible
    }

    public void initComponents() throws IOException {
        menuBar = new JMenuBar(); // Create a new menu bar
        frame.setJMenuBar(menuBar); // Set the menu bar for the frame

        // Initialize File menu items
        menuFile = new JMenu("File");
        openMenuItem = new JMenuItem("Open File");
        newMenuItem = new JMenuItem("New File");
        saveMenuItem = new JMenuItem("Save File");
        searchMenu = new JMenuItem("Search");
        exitMenuItem = new JMenuItem("Exit without Saving");
        printMenuItem = new JMenuItem("Print");

        // Add items to File menu
        menuFile.add(openMenuItem);
        menuFile.add(newMenuItem);
        menuFile.add(saveMenuItem);
        menuFile.add(searchMenu);
        menuFile.add(exitMenuItem);
        menuFile.add(printMenuItem);
        menuBar.add(menuFile);

        // Initialize About menu
        aboutMenu = new JMenu("About");
        helpMenuItem = new JMenuItem("Help");
        aboutMenuItem = new JMenuItem("Hamhuo & JiaPeng Designed and Provide");

        // Add items to About menu
        aboutMenu.add(helpMenuItem);
        aboutMenu.add(aboutMenuItem);
        menuBar.add(aboutMenu);

        // Initialize text area with syntax highlighting
        syntaxTextArea = new RSyntaxTextArea();
        syntaxTextArea.setCodeFoldingEnabled(true);
        syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
        RTextScrollPane sp = new RTextScrollPane(syntaxTextArea);
        initTextarea();
        frame.add(sp);

        // Add action listeners for menu items
        openMenuItem.addActionListener(actionEvent -> openAction());
        newMenuItem.addActionListener(actionEvent -> newAction());
        saveMenuItem.addActionListener(actionEvent -> saveAction());
        searchMenu.addActionListener(actionEvent -> searchAction());
        exitMenuItem.addActionListener(actionEvent -> exitAction());
        printMenuItem.addActionListener(actionEvent -> PadPrinter.newPadPrinter().doPrint());
        helpMenuItem.addActionListener(actionEvent -> helpAction());

        // Listener to monitor caret movements in the text area
        syntaxTextArea.addCaretListener(e -> {
            changed = true;
            updateFrameTitle(); // Update the frame title on text change
        });
    }

    private void updateFrameTitle() {
        String title = "New Pad";
        if (file != null) {
            title = file.getName();
        } else if (syntaxTextArea.getText().length() > 0) {
            title = syntaxTextArea.getText().substring(0, Math.min(16, syntaxTextArea.getText().length()));
        }
        frame.setTitle(title); // Set the frame title
    }

    public void initTextarea() {
        syntaxTextArea.setText(""); // Clear the text area

        // Display current date and time at the start
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:MM_dd/MM/yyyy");
        String dateTime = LocalDateTime.now().format(formatter);
        syntaxTextArea.setText(dateTime);
        syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        syntaxTextArea.setCodeFoldingEnabled(true);
        changed = false;
        file = null;
        updateFrameTitle(); // Update frame title to reflect new file
    }

    private void newAction() {
        if (changed) {
            int response = JOptionPane.showConfirmDialog(frame, "Some Text Still on the page, Want Save them?");
            if (response == JOptionPane.YES_OPTION) {
                saveAction(); // Save current document if user chooses to
            }
        }
        initTextarea(); // Initialize text area for a new document
    }

    private void openAction() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile(); // Get the selected file
            try {
                String content = PadFileIO.openFile(file); // Load the content of the file
                syntaxTextArea.setText(content); // Set content in the text area

                // Set syntax highlighting based on file extension
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
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE); // No syntax highlighting if no match
                }
                frame.setTitle(file.getName()); // Update frame title with file name
                changed = false; // Reset changed flag
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAction() {
        JFileChooser chooser = PadFileIO.getInstance().getFileChooser(); // Get file chooser
        int result = chooser.showSaveDialog(frame); // Show save dialog

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = chooser.getSelectedFile().getAbsolutePath(); // Get file path
                String description = chooser.getFileFilter().getDescription(); // Get file description
                String content = syntaxTextArea.getText(); // Get content from text area
                PadFileIO.saveFile(content, filePath, description); // Save the content to the specified file
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void searchAction() {
        PadSearch search = new PadSearch(this, syntaxTextArea); // Create and display search dialog
    }

    private void exitAction() {
        frame.dispose(); // Dispose the frame (exit the application)
    }

    private void helpAction() {
        try {
            URI uri = new URI("https://hamhuo-hub.github.io/static-webapp/");
            Desktop dt = Desktop.getDesktop();
            dt.browse(uri.toURL().toURI()); // Open help website in default browser
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
