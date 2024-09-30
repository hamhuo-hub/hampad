package hampad;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.TextExtractor;
import org.odftoolkit.simple.text.Paragraph;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;


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

    public static String loadFile(File file) throws Exception {
        String extension = getFileExtension(file);
        switch (extension.toLowerCase()) {
            case "odt":
                return loadODTFile(file);
            default:
                return loadTXTFile(file);
        }
    }

    public static String openFile(File file) throws Exception {
        return loadFile(file);
    }

    public static String openTXTFile(File file) throws IOException {
        return loadTXTFile(file);
    }

    public static String openODTFile(File file) throws IOException {
        try {
            return loadODTFile(file);
        } catch (Exception e) {
            throw new IOException("Error opening ODT file", e);
        }
    }


    private static String loadODTFile(File file) throws Exception {
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = new FileInputStream(file);
             TextDocument document = TextDocument.loadDocument(inputStream)) {

            // 获取文档中的所有段落
            for (Iterator<Paragraph> it = document.getParagraphIterator(); it.hasNext(); ) {
                Paragraph paragraph = it.next(); // 获取当前段落
                content.append(paragraph.getTextContent()).append("\n"); // 添加段落文本
            }
        }
        return content.toString();
    }




    private static String loadTXTFile(File file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // Empty extension
        }
        return name.substring(lastIndexOf + 1);
    }
}