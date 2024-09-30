package hampad;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.text.Paragraph;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;


public class PadFileIO {

    private static final Logger logger = LogManager.getLogger(PadFileIO.class);
    private JFileChooser chooser;

    public JFileChooser getFileChooser() {
        chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("TXT File", "txt"));
        chooser.setFileFilter(new FileNameExtensionFilter("ODT File", "odt"));
        chooser.setFileFilter(new FileNameExtensionFilter("PDF File", "pdf"));
        return chooser;
    }

    public static PadFileIO getInstance() {
        return new PadFileIO();
    }

    public static void saveFile(String content, String path, String description) throws IOException {
        File selectedFile;

        // Check if the file has an extension
        if (!path.contains(".")) {
            switch (description) {
                case "TXT File":
                    path += ".txt";
                    break;
                case "ODT File":
                    path += ".odt";
                    break;
                case "PDF File":
                    path += ".pdf";
                    break;
                default:
                    path += ".txt";
            }
        }

        selectedFile = new File(path);

        switch (description) {
            case "PDF File":
                exportPDF(content, selectedFile);
                break;
            case "ODT File":
                odtFile(content, selectedFile);
                break;
            default:
                txtFile(content, selectedFile);
        }
    }

    private static void exportPDF(String content, File file) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);

                // Load a Unicode font
                PDFont font = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
                contentStream.setFont(font, 12);

                String[] lines = content.split("\n");
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -15);
                }

                contentStream.endText();
            }

            doc.save(file);
            logger.info("PDF file saved successfully: {}", file.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Error exporting PDF file", e);
        }
    }

    private static void txtFile(String content, File file) {
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.write(content);
            logger.info("TXT file saved successfully: {}", file.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Error saving TXT file", e);
        }
    }

    private static void odtFile(String content, File file) {
        try {
            TextDocument document = TextDocument.newTextDocument();
            String[] lines = content.split("\n");
            for (String line : lines) {
                Paragraph paragraph = document.addParagraph(line);
                paragraph.setTextContent(line);
            }
            document.save(file);
            logger.info("ODT file saved successfully: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Error saving ODT file", e);
        }
    }
}