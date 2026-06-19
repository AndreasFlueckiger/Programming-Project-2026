package com.example.battleship;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Main design system for the military aesthetic.
 *
 * Inspired by the screenshots:
 *   - Very dark background (#0a0e0a) — almost black with a green tint
 *   - Neon green (#00ff41) as the primary accent (CRT monitor feel)
 *   - Red (#cc0000 / #ff2200) for danger, hits, enemy
 *   - Stencil-style bold font for titles (Impact)
 *   - Scanline / grid texture feel via tight grid lines
 *
 * All UI components are created via static factory methods here so the
 * look is consistent across every screen.
 */
public final class Theme {

    private Theme() {}

    // Colours 
    //Alexis
    /** Page / window background — near-black with green tint. */
    public static final Color BG             = new Color(8,  14,  8);
    /** Panel / card background. */
    public static final Color PANEL          = new Color(14, 22, 14);
    /** Slightly lighter surface (grid backgrounds). */
    public static final Color SURFACE        = new Color(0,  30,  0);

    /** Primary neon-green accent — borders, titles, active cells. */
    public static final Color GREEN          = new Color(0,  210,  50);
    /** Brighter green for hover / highlight. */
    public static final Color GREEN_BRIGHT   = new Color(0,  255,  65);
    /** Dimmed green for subtle borders. */
    public static final Color GREEN_DIM      = new Color(0,  100,  20);
    /** Very dim green for grid lines. */
    public static final Color GREEN_GRID     = new Color(0,   70,  10);

    /** Neon red — hits, sunk ships, danger. */
    public static final Color RED            = new Color(220,  30,  10);
    /** Brighter red for hover. */
    public static final Color RED_BRIGHT     = new Color(255,  50,  20);
    /** Dark red for sunk ship fill. */
    public static final Color RED_DARK       = new Color(100,   5,   0);

    /** Water / un-shot cell background. */
    public static final Color WATER          = new Color(0,   28,   8);
    /** Miss marker colour. */
    public static final Color MISS           = new Color(0,   80,  20);
    /** Own-ship colour on player board. */
    public static final Color SHIP_OWN       = new Color(0,  140,  30);
    /** Mine yellow. */
    public static final Color MINE_YELLOW    = new Color(200, 160,   0);
    /** Mine blast zone. */
    public static final Color MINE_BLAST     = new Color(180, 130,   0);

    /** Score / secondary text colour. */
    public static final Color SCORE          = new Color(200, 200,  50);

    /** Text colour — use on dark backgrounds. */
    public static final Color TEXT           = new Color(0,  210,  50);
    public static final Color TEXT_DIM       = new Color(0,  120,  30);
    public static final Color TEXT_WHITE     = new Color(220, 230, 220);


    // Fonts 
    //Matteo
    /** Large stencil-style title font (like the screenshots). */
    public static final Font FONT_TITLE  = new Font("Impact", Font.PLAIN, 52);
    /** Medium title. */
    public static final Font FONT_HEAD   = new Font("Impact", Font.PLAIN, 28);
    /** Section labels. */
    public static final Font FONT_LABEL  = new Font("Impact", Font.PLAIN, 18);
    /** Normal UI text / log. */
    public static final Font FONT_BODY   = new Font("Courier New", Font.PLAIN, 12);
    /** Cell buttons. */
    public static final Font FONT_CELL   = new Font("Courier New", Font.BOLD,  11);
    /** Score / stats. */
    public static final Font FONT_SCORE  = new Font("Courier New", Font.BOLD,  14);


    // Borders
    //Marco
    /** Standard 1px green border. */
    public static Border borderGreen() {
        return BorderFactory.createLineBorder(GREEN, 1);
    }

    /** 2px bright green border (selected / active). */
    public static Border borderGreenBright() {
        return BorderFactory.createLineBorder(GREEN_BRIGHT, 2);
    }

    /** Thin dim-green border for grid cells. */
    public static Border borderCell() {
        return BorderFactory.createLineBorder(GREEN_GRID, 1);
    }

    /** Titled border in green stencil style. */
    public static TitledBorder titledBorder(String title) {
        TitledBorder tb = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GREEN, 1), title);
        tb.setTitleColor(GREEN);
        tb.setTitleFont(FONT_LABEL);
        return tb;
    }

    /** Compound: titled + inner padding. */
    public static Border titledPadded(String title, int pad) {
        return BorderFactory.createCompoundBorder(
            titledBorder(title),
            BorderFactory.createEmptyBorder(pad, pad, pad, pad));
    }

    // Component factories
    //Matteo
    /**
     * Creates a panel with standard dark background.
     */
    public static JPanel panel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG);
        return p;
    }

    /**
     * Creates a label with green text and Impact font at the given size.
     */
    public static JLabel titleLabel(String text, int size) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Impact", Font.PLAIN, size));
        l.setForeground(GREEN);
        return l;
    }

    /**
     * Creates a body-text label (Courier New, green).
     */
    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT);
        return l;
    }

    /**
     * Creates a military-style button — dark bg, green border & text,
     * with hover brightening and click darkening via mouse.
     */
    //Alexis
    public static JButton button(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_LABEL);
        btn.setBackground(PANEL);
        btn.setForeground(GREEN);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GREEN, 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(GREEN_DIM);
                btn.setForeground(GREEN_BRIGHT);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GREEN_BRIGHT, 2),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(PANEL);
                btn.setForeground(GREEN);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GREEN, 2),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)));
            }
        });

        return btn;
    }

    /**
     * Red variant button — used for destructive or enemy-related actions.
     */
    //Matteo
    public static JButton buttonRed(String text) {
        JButton btn = button(text);
        btn.setForeground(RED);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RED, 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(60, 0, 0));
                btn.setForeground(RED_BRIGHT);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(RED_BRIGHT, 2),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(PANEL);
                btn.setForeground(RED);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(RED, 2),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)));
            }
        });
        return btn;
    }

    /**
     * Creates a styled cell button for the 10×10 grid.
     *
     * @param enemy true = clickable enemy grid (no ship markers)
     */
    //Alexis
    public static JButton cellButton(boolean enemy) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setFont(FONT_CELL);
        btn.setFocusPainted(false);
        btn.setBackground(WATER);
        btn.setForeground(GREEN);
        btn.setOpaque(true);
        btn.setBorder(borderCell());
        if (enemy) btn.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        return btn;
    }

    /**
     * Creates a text area styled as a military log readout (CRT green on black).
     */
    //Matteo
    public static JTextArea logArea() {
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setBackground(new Color(0, 8, 0));
        ta.setForeground(GREEN);
        ta.setFont(FONT_BODY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setCaretColor(GREEN);
        return ta;
    }

    /**
     * Wraps a component in a scroll pane styled to the theme.
     */
    //Alexis
    public static JScrollPane scrollPane(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(borderGreen());
        sp.setBackground(BG);
        sp.getViewport().setBackground(new Color(0, 8, 0));
        sp.getVerticalScrollBar().setBackground(PANEL);
        return sp;
    }

    /**
     * Applies the dark background to a frame and sets its title bar colour
     * (depends on OS).
     */
    //Matteo
    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BG);
        frame.setBackground(BG);
    }

    /**
     * Paints a simple scanline / CRT overlay on top of a component.
     * Call from paintComponent after super.paintComponent.
     *
     * @param g  graphics context already clipped to the component
     * @param w  component width
     * @param h  component height
     */
    //Matteo
    public static void paintScanlines(Graphics g, int w, int h) {
        g.setColor(new Color(0, 0, 0, 30));
        for (int y = 0; y < h; y += 3) {
            g.drawLine(0, y, w, y);
        }
    }
}
