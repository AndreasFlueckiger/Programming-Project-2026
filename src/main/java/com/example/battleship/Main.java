package com.example.battleship;

import javax.swing.*;
import java.awt.*;

/**
 * Application entry point.
 *
 * Build:  mvn package
 * Run:    java -jar target/battleship-3.0.jar
 */
public class Main {
    private Main() {}

    public static void main(String[] args) {
        // Force dark title bars where possible
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            // Use cross-platform L&F so our custom colours are respected
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Global colour overrides to ensure dark L&F widgets
            UIManager.put("Panel.background",       Theme.BG);
            UIManager.put("OptionPane.background",  Theme.PANEL);
            UIManager.put("OptionPane.messageForeground", Theme.GREEN);
            UIManager.put("Button.background",      Theme.PANEL);
            UIManager.put("Button.foreground",      Theme.GREEN);
            UIManager.put("Button.focus",           Theme.GREEN_DIM);
            UIManager.put("ScrollBar.background",   Theme.PANEL);
            UIManager.put("ScrollBar.thumb",        Theme.GREEN_DIM);
            UIManager.put("Dialog.background",      Theme.BG);
            UIManager.put("Label.foreground",       Theme.GREEN);
            UIManager.put("TextArea.background",    new Color(0, 8, 0));
            UIManager.put("TextArea.foreground",    Theme.GREEN);
            UIManager.put("TextField.background",   Theme.PANEL);
            UIManager.put("TextField.foreground",   Theme.GREEN);
            UIManager.put("ComboBox.background",    Theme.PANEL);
            UIManager.put("ComboBox.foreground",    Theme.GREEN);
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(MenuFrame::new);
    }
}
