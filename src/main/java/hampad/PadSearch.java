package hampad;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PadSearch extends JFrame {
    private JTextField textField;
    private JButton searchButton;
    private Matcher matcher;
    public PadSearch(JFrame frame, RSyntaxTextArea syntaxTextArea) {
        setLayout(new BorderLayout());
        setTitle("Pad Search");
        setSize(400, 130);
        setLocationRelativeTo(frame);
        textField = new JTextField(15);
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("hit search button");
                doSearch(syntaxTextArea);
            }
        });
        add(textField);
        add(searchButton, BorderLayout.SOUTH);
        setVisible(true);
    }
    public void doSearch(RSyntaxTextArea syntaxTextArea) {
        String text = textField.getText();
        String searchText = syntaxTextArea.getText();
        System.out.println("into search text: ");
        if(!searchText.isEmpty()) {
            System.out.println("into search");
                matcher = Pattern.compile(Pattern.quote(text)).matcher(searchText);
                System.out.println(searchText);

            // If a match is found, highlight the text
            if (matcher.find()) {
                try {
                    syntaxTextArea.select(matcher.start(), matcher.end());
                    syntaxTextArea.requestFocusInWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "No more occurrences found.");
            }
        }
    }
}
