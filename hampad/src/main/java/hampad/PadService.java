package hampad;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;

public class PadService extends JFrame{
    private JFrame frame;
    private JMenuBar menuBar;
    private JMenuItem newMenu;
    private JMenuItem saveMenu;
    private JMenuItem exitMenu;
    private JMenu aboutMenu;
    private RSyntaxTextArea syntaxTextArea;
    private File file;
    public PadService(){
        frame = new JFrame("New Pad");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        newMenu = new JMenuItem("New File");
        newMenu.addActionListener(actionEvent -> newAction());
        menuBar.add(newMenu);
        saveMenu = new JMenuItem("Save");
        saveMenu.addActionListener(actionEvent -> saveAction());
        menuBar.add(saveMenu);
        exitMenu = new JMenuItem("Exit");
        exitMenu.addActionListener(actionEvent -> frame.dispose());
        menuBar.add(exitMenu);
        aboutMenu = new JMenu("About");
        JMenuItem aboutMenuItem = new JMenuItem("Hamhuo & JiaPeng" + "Designed and Provide");
        aboutMenu.add(aboutMenuItem);
        menuBar.add(aboutMenu);


        //text Area
        syntaxTextArea = new RSyntaxTextArea();
        syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        syntaxTextArea.setCodeFoldingEnabled(true);
        syntaxTextArea.setCodeFoldingEnabled(true);
        syntaxTextArea.addCaretListener(new CaretListener() {

            @Override
            public void caretUpdate(CaretEvent e) {
                try {
                    if(syntaxTextArea.getText().length() > 5){
                    frame.setTitle(syntaxTextArea.getText(0,5));}
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

    private void newAction(){
        System.out.println("new");
        frame.setTitle("New Pad");
        if(!syntaxTextArea.getText().equals("")){
            int i = JOptionPane.showConfirmDialog(syntaxTextArea,"Some Text Still on the page, Want Save them?");
            if(i == JOptionPane.YES_OPTION){ saveAction();}
        }
        System.out.println("clear");
        syntaxTextArea.setText("");

    }

    private void saveAction(){
        System.out.println("save");
        JFileChooser chooser = new JFileChooser();
        int i = chooser.showSaveDialog(frame);
        if(i == JFileChooser.APPROVE_OPTION){
            try{
                file = chooser.getSelectedFile();
                System.out.println("file create");
                //A funny fact is you do not have to associated file into Textarea.
                //save file and write to TextArea is separated
                PrintWriter out = new PrintWriter(new FileWriter(file));
                if(file.canWrite()){
                out.write(syntaxTextArea.getText());
                System.out.println(syntaxTextArea.getText());
                //remember to close upstream
                out.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }


































/*    private JEditorPane contentArea;

    private JFrame frame;

    private String fileName;

    public PadService() throws FileNotFoundException {
        frame = new JFrame();
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // 添加菜單
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Files");
        JMenuItem newItem = new JMenuItem("new File");
        newAction(newItem);

        menu.add(newItem);
        JMenuItem openItem = new JMenuItem("Open File");
        openAction(openItem);
        menu.add(openItem);
        JMenuItem saveItem = new JMenuItem("Save File");
        saveAction(saveItem);
        menu.add(saveItem);
        menuBar.add(menu);
        JMenuItem printItem = new JMenuItem("Print File");
        printAction(printItem);
        menu.add(printItem);
        menuBar.add(menu);


        frame.setJMenuBar(menuBar);

        // 布局
        frame.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        JComboBox<String> fontCom = fontAction();
        toolBar.add(fontCom);
        JComboBox<String> fontSize = fontSizeAction();
        toolBar.add(fontSize);

        fontStyleAction(toolBar);
        JButton colorbtn = fontColorAction();
        toolBar.add(colorbtn);

        frame.add(toolBar, BorderLayout.NORTH);
        // 文件编辑区
        contentArea = new JEditorPane();
        contentArea.setEditorKit(SyntaxConstants.SYNTAX_STYLE_JAVA);
        JScrollPane pane = new JScrollPane(contentArea);
        frame.add(pane);
        frame.setVisible(true);

    }


        private JButton fontColorAction() {
            JButton colorbtn = new JButton("■");
            colorbtn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Color color = colorbtn.getForeground();
                    Color co = JColorChooser.showDialog(PadService.this.frame, "设置字体颜色", color);
                    colorbtn.setForeground(co);
                    contentArea.setForeground(co);
                }
            });
            return colorbtn;
        }

        // 记事本，字体格式
        private void fontStyleAction(JToolBar toolBar) {
            JCheckBox boldBox = new JCheckBox("粗体");
            JCheckBox itBox = new JCheckBox("斜体");
            ActionListener actionListener = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean bold = boldBox.isSelected();
                    boolean it = itBox.isSelected();
                    int style = (bold ? Font.BOLD : Font.PLAIN) | (it ? Font.ITALIC : Font.PLAIN);
                    Font font = contentArea.getFont();
                    contentArea.setFont(new Font(font.getName(), style, font.getSize()));
                    //contentArea.setFont(new Font(font.getName(), style, font.getSize()));
                }
            };
            boldBox.addActionListener(actionListener);
            itBox.addActionListener(actionListener);
            toolBar.add(boldBox);
            toolBar.add(itBox);
        }

        // 记事本，设置字体大小
        private JComboBox<String> fontSizeAction() {
            String[] fontSizes = new String[]{"10", "20", "30", "50"};
            JComboBox<String> fontSize = new JComboBox<>(fontSizes);
            fontSize.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    int size = Integer.valueOf((String) fontSize.getSelectedItem());
                    Font font = PadService.this.contentArea.getFont();
                    PadService.this.contentArea.setFont(new Font(font.getName(), font.getStyle(), size));

                }
            });
            return fontSize;
        }

        // 记事本，设置字体
        private JComboBox<String> fontAction() {
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = environment.getAvailableFontFamilyNames();

            JComboBox<String> fontCom = new JComboBox<>(fontNames);

            fontCom.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    String fontName = (String) fontCom.getSelectedItem();
                    Font font = PadService.this.contentArea.getFont();
                    PadService.this.contentArea.setFont(new Font(fontName, font.getStyle(), font.getSize()));

                }
            });
            return fontCom;
        }

        // 记事本新建操作
        private void newAction(JMenuItem newItem) {
            newItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    contentArea.setText("");
                    frame.setTitle("新建-记事本");

                    fileName = null;
                }
            });
        }

        // 记事本打开文件操作
        private void openAction(JMenuItem openItem) {
            openItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Text & dat", "txt", "dat");
                    chooser.setFileFilter(filter);
                    int returnVal = chooser.showOpenDialog(frame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        String fileName = chooser.getSelectedFile().getPath();
                        PadService.this.fileName = fileName;
                        String content = read(fileName);
                        contentArea.setText(content);
                        PadService.this.frame.setTitle(fileName + "- 记事本");
                    }

                }
            });
        }

        // 菜单 保存操作
        private void saveAction(JMenuItem saveItem) {
            saveItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    if (PadService.this.fileName != null) {
                        String content = PadService.this.contentArea.getText();
                        write(PadService.this.fileName, content);
                    } else {
                        JFileChooser chooser = new JFileChooser();
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text & dat", "txt", "dat");
                        chooser.setFileFilter(filter);
                        int returnVal = chooser.showSaveDialog(frame);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            String fileName = chooser.getSelectedFile().getPath();
                            PadService.this.fileName = fileName;
                            String content = PadService.this.contentArea.getText();
                            write(PadService.this.fileName, content);
                            PadService.this.frame.setTitle(fileName + "- 记事本");
                        }
                    }
                }
            });
        }

        //菜单打印操作
        private void printAction(JMenuItem printItem) {
            printItem.addActionListener(new ActionListener() {
                private PadPrinter printer = new PadPrinter();
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("an action");
                    PageFormat pageFormat = new PageFormat();
                    Graphics g = contentArea.getGraphics();
                    printer.doPrint();

                }
            });
        }


        public String read(String fileName) {

            try (Reader reader = new FileReader(fileName); BufferedReader buff = new BufferedReader(reader);) {
                String str;
                StringBuilder sb = new StringBuilder();
                while ((str = buff.readLine()) != null) {
                    str = decoding(str);
                    sb.append(str + "\n");
                }

                return sb.toString();
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "找不到文件路径" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void write(String fileName, String content) {

            try (Writer writer = new FileWriter(fileName);) {
                content = encoding(content);
                writer.write(content);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public String encoding(String str) {
            String temp = "";
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '\n') {
                    temp += str.charAt(i);
                } else if (0 <= str.charAt(i) && str.charAt(i) <= 255) {
                    temp += (char) ((str.charAt(i) - '0' + 10) % 255);
                } else {
                    temp += str.charAt(i);
                }
            }
            return temp;
        }
        public String decoding(String str) {
            String temp = "";
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '\n') {
                    temp += str.charAt(i);
                } else if (0 <= str.charAt(i) && str.charAt(i) <= 255) {
                    temp += (char) ((str.charAt(i) + '0' - 10 + 255) % 255);
                } else {
                    temp += str.charAt(i);
                }
            }
            return temp;
        }*/
    }

