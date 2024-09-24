package com.hamhuo.view;

import javax.swing.*;
import java.awt.*;

public class PadMenu extends JPanel {
    private JMenu padMenu;
    private JMenuBar padMenuBar;
    private JMenuItem newItem;
    public PadMenu() {
        padMenuBar = new JMenuBar();
        padMenu = new JMenu("Files");
        newItem = new JMenuItem("NewFile");
        padMenu.add(newItem);
        padMenuBar.add(padMenu);
    }

    public JMenuBar getPadMenuBar() {
        return padMenuBar;
    }

    public void setPadMenuBar(JMenuBar padMenuBar) {
        this.padMenuBar = padMenuBar;
    }

    public JMenu getPadMenu() {
        return padMenu;
    }

    public void setPadMenu(JMenu padMenu) {
        this.padMenu = padMenu;
    }
}
