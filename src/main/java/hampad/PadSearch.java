// Package declaration for the class
package hampad;

// Import necessary classes
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Class for search functionality in a text editor frame, extending JFrame
public class PadSearch extends JFrame {
    // TextField for entering search query
    private JTextField textField;
    // Button to initiate search
    private JButton searchButton;
    // Matcher to perform search operations
    private Matcher matcher;

    // Constructor for PadSearch
    public PadSearch(JFrame frame, RSyntaxTextArea syntaxTextArea) {
        // Set the layout manager
        setLayout(new BorderLayout());
        // Set the title of the frame
        setTitle("Pad Search");
        // Set size of the search window
        setSize(400, 130);
        // Position this near the center of the specified frame
        setLocationRelativeTo(frame);
        // Initialize text field with number of columns
        textField = new JTextField(15);
        // Create a new button for searching
        searchButton = new JButton("Search");

        // Add an ActionListener for the search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Output to the console when the search button is pressed
                System.out.println("Hit search button");
                // Perform the search operation
                doSearch(syntaxTextArea);
            }
        });

        // Add the text field to the frame
        add(textField);
        // Add the search button to the frame at the bottom (SOUTH)
        add(searchButton, BorderLayout.SOUTH);
        // Make the frame visible
        setVisible(true);
    }

    // Method to execute a search operation
    public void doSearch(RSyntaxTextArea syntaxTextArea) {
        // Get text from the text field
        String text = textField.getText();
        // Get all text from the syntax text area to search
        String searchText = syntaxTextArea.getText();
        // Output debugging info
        System.out.println("Into search text: ");
        // Proceed if the text to search is not empty
        if(!searchText.isEmpty()) {
            System.out.println("Into search");
            // Create a Matcher to find the text in syntaxTextArea
            matcher = Pattern.compile(Pattern.quote(text)).matcher(searchText);
            // Output the text being searched
            System.out.println(searchText);

            // If a match is found, highlight the found text
            if (matcher.find()) {
                try {
                    // Select the text in the syntaxTextArea that matches
                    syntaxTextArea.select(matcher.start(), matcher.end());
                    // Focus the syntaxTextArea window
                    syntaxTextArea.requestFocusInWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // If no match is found, show a dialog indicating no more occurrences
                JOptionPane.showMessageDialog(this, "No more occurrences are found.");
            }
        }
    }
}
