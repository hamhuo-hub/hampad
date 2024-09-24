package com.hamhuo.view;

import javax.swing.*;
import java.awt.*;

public class PadMainFrame extends JFrame {

    private PadMenu menuBar;
    public PadMainFrame() {
        //window sized
        setTitle("Pad Main Frame");
        setSize(800, 600);
        setLocationRelativeTo(null);


        //add components
        menuBar = new PadMenu();
        setJMenuBar(menuBar.getPadMenuBar());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}
