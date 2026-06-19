package com.example.battleship;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Main menu — two screens:
 *
 * 1. SPLASH — "BATTLESHIP WAR" title with a PLAY button.
 * 2. MODE SELECT — two large cards: "vs BOT" and "vs PLAYER".
 *
 * Both use the dark military / CRT-green aesthetic from Theme.
 */
public class MenuFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel     root  = new JPanel(cards);

    public MenuFrame() {
        setTitle("Battleship War");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        root.setBackground(Theme.BG);
        root.add(buildSplash(),    "splash");
        root.add(buildModeSelect(),"mode");

        setContentPane(root);
        pack();
        setMinimumSize(new Dimension(700, 520));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── Splash screen ─────────────────────────────────────────────────────────
    //Matteo
    private JPanel buildSplash() {
        // Custom panel that draws a subtle grid texture behind everything
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g, getWidth(), getHeight());
                Theme.paintScanlines(g, getWidth(), getHeight());
            }
        };
        p.setBackground(Theme.BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 10, 0);

        // Ship silhouettes row (simple horizontal line of coloured rectangles)
        gbc.gridy = 0;
        p.add(buildShipSilhouettes(), gbc);

        // "BATTLESHIP" line
        gbc.gridy = 1; gbc.insets = new Insets(30, 0, 0, 0);
        JLabel line1 = new JLabel("BATTLESHIP", SwingConstants.CENTER);
        line1.setFont(Theme.FONT_TITLE);
        line1.setForeground(Theme.TEXT_WHITE);
        p.add(line1, gbc);

        // "WAR" line
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        JLabel line2 = new JLabel("WAR", SwingConstants.CENTER);
        line2.setFont(Theme.FONT_TITLE);
        line2.setForeground(Theme.TEXT_WHITE);
        p.add(line2, gbc);

        // PLAY button — large green square with triangle.
        gbc.gridy = 3; gbc.insets = new Insets(40, 0, 30, 0);
        JButton play = buildPlayButton();
        p.add(play, gbc);

        return p;
    }

    /**
     * The green-bordered square PLAY button with a triangle icon.
     */
    //Matteo
    private JButton buildPlayButton() {
        JButton btn = new JButton("▶") {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Theme.GREEN);
                g.setFont(new Font("SansSerif", Font.BOLD, 36));
                FontMetrics fm = g.getFontMetrics();
                String t = "▶";
                int x = (getWidth()  - fm.stringWidth(t)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(t, x, y);
            }
        };
        btn.setPreferredSize(new Dimension(100, 100));
        btn.setBackground(new Color(0, 30, 0));
        btn.setBorder(BorderFactory.createLineBorder(Theme.GREEN, 3));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0, 60, 0)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(0, 30, 0)); }
        });
        btn.addActionListener(e -> cards.show(root, "mode"));
        return btn;
    }

    /** Simple SVG-style ship silhouettes using coloured rectangles. */
    //Alexis
    private JPanel buildShipSilhouettes() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        p.setOpaque(false);
        Color[] cols = { Theme.GREEN_DIM, Theme.TEXT_DIM, Theme.GREEN_DIM,
                         Theme.TEXT_DIM, Theme.GREEN_DIM };
        int[]   wids = { 80, 60, 50, 65, 45 };
        for (int i = 0; i < cols.length; i++) {
            final Color c = cols[i];
            final int   w = wids[i];
            JPanel ship = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(c);
                    int h = getHeight();
                    // hull
                    g.fillRect(0, h/2, w, h/3);
                    // bridge
                    g.fillRect(w/3, h/4, w/3, h/4);
                    // bow (angled)
                    int[] xs = {w, w-8, w};
                    int[] ys = {h/2, h/2+h/3, h/2+h/3};
                    g.fillPolygon(xs, ys, 3);
                }
            };
            ship.setOpaque(false);
            ship.setPreferredSize(new Dimension(w, 36));
            p.add(ship);
        }
        return p;
    }

    // ── Mode select screen ─────────────────────────────────────
    //Alexis
    private JPanel buildModeSelect() {
        JPanel p = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g, getWidth(), getHeight());
                Theme.paintScanlines(g, getWidth(), getHeight());
            }
        };
        p.setBackground(Theme.BG);
        p.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Title
        JLabel title = Theme.titleLabel("GAME MODE", 42);
        title.setForeground(Theme.GREEN);
        title.setBorder(new EmptyBorder(0, 0, 30, 0));
        p.add(title, BorderLayout.NORTH);

        // Two mode cards
        JPanel cardRow = new JPanel(new GridLayout(1, 2, 30, 0));
        cardRow.setOpaque(false);
        cardRow.add(buildModeCard("vs BOT",
            "Solo vs AI",
            "Hunt & target strategy.\nOne player, one brain.",
            Theme.GREEN,
            GameMode.PLAYER_VS_BOT));
        cardRow.add(buildModeCard("vs PLAYER",
            "Local 2-Player",
            "Two players, one PC.\nPass & play — no peeking!",
            Theme.RED,
            GameMode.PLAYER_VS_PLAYER));
        p.add(cardRow, BorderLayout.CENTER);

        // Back link
        JButton back = Theme.button("← BACK");
        back.addActionListener(e -> cards.show(root, "splash"));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.setOpaque(false);
        south.add(back);
        p.add(south, BorderLayout.SOUTH);

        return p;
    }

    /**
     * Builds one mode-selection card.
     * Styled like the Classic/Advanced cards in Image 4:
     * dark background, coloured border, icon area, label below.
     */
    //Matteo
    private JPanel buildModeCard(String title, String sub, String desc,
                                  Color accent, GameMode mode) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw a subtle inner grid to mimic the "ocean map" texture
                g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 15));
                for (int x = 0; x < getWidth(); x += 20)  g.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 20) g.drawLine(0, y, getWidth(), y);
            }
        };
        card.setBackground(new Color(5, 12, 5));
        card.setBorder(BorderFactory.createLineBorder(accent, 3));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;

        // Icon area — simple drawn icon
        gbc.gridy = 0; gbc.insets = new Insets(20, 20, 12, 20);
        card.add(buildCardIcon(accent, mode), gbc);

        // Title
        gbc.gridy = 1; gbc.insets = new Insets(0, 10, 4, 10);
        JLabel titleL = new JLabel(title, SwingConstants.CENTER);
        titleL.setFont(Theme.FONT_HEAD);
        titleL.setForeground(accent);
        card.add(titleL, gbc);

        // Sub
        gbc.gridy = 2; gbc.insets = new Insets(0, 10, 6, 10);
        JLabel subL = new JLabel(sub, SwingConstants.CENTER);
        subL.setFont(Theme.FONT_SCORE);
        subL.setForeground(accent.darker());
        card.add(subL, gbc);

        // Desc
        gbc.gridy = 3; gbc.insets = new Insets(0, 14, 16, 14);
        JTextArea descA = new JTextArea(desc);
        descA.setEditable(false);
        descA.setFocusable(false);
        descA.setBackground(new Color(5, 12, 5));
        descA.setForeground(Theme.TEXT_DIM);
        descA.setFont(Theme.FONT_BODY);
        descA.setLineWrap(true);
        descA.setWrapStyleWord(true);
        card.add(descA, gbc);

        // SELECT button
        gbc.gridy = 4; gbc.insets = new Insets(0, 20, 20, 20);
        JButton sel = (accent == Theme.GREEN) ? Theme.button("SELECT") : Theme.buttonRed("SELECT");
        sel.addActionListener(e -> {
            dispose();
            new SetupFrame(mode, 1, null);
        });
        card.add(sel, gbc);

        // Hover brighten
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(accent.brighter(), 3));
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(accent, 3));
            }
        });

        return card;
    }

    /**
     * Draws a simple pictogram icon for the mode card.
     * Bot mode: crosshair target circles.
     * PvP mode: two silhouettes facing each other.
     */
    //Alexis
    private JPanel buildCardIcon(Color accent, GameMode mode) {
        JPanel icon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(2f));
                if (mode == GameMode.PLAYER_VS_BOT) {
                    // Radar-style crosshair
                    g2.drawOval(cx-40, cy-40, 80, 80);
                    g2.drawOval(cx-25, cy-25, 50, 50);
                    g2.drawOval(cx-10, cy-10, 20, 20);
                    g2.drawLine(cx-48, cy, cx+48, cy);
                    g2.drawLine(cx, cy-48, cx, cy+48);
                    // Sweep line
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 100));
                    g2.fillArc(cx-40, cy-40, 80, 80, 0, 70);
                } else {
                    // Two ship silhouettes facing each other
                    drawShipIcon(g2, cx-50, cy, accent, false);
                    drawShipIcon(g2, cx+10, cy, accent, true);
                    // VS text
                    g2.setColor(accent);
                    g2.setFont(new Font("Impact", Font.PLAIN, 16));
                    g2.drawString("VS", cx - 10, cy + 6);
                }
            }
            @Override public Dimension getPreferredSize() { return new Dimension(140, 100); }
        };
        icon.setOpaque(false);
        return icon;
    }
    //Matteo
    private void drawShipIcon(Graphics2D g2, int x, int y, Color c, boolean flip) {
        g2.setColor(c);
        int dir = flip ? -1 : 1;
        // Hull
        g2.fillRect(x, y - 4, 38, 8);
        // Bridge
        g2.fillRect(x + dir * 8 + (flip ? 10 : 0), y - 12, 14, 8);
        // Bow
        int[] bx = {x + 38*dir + (flip?-38:0), x + (38+8)*dir + (flip?-38:0), x + 38*dir + (flip?-38:0)};
        int[] by = {y - 4, y, y + 4};
        g2.fillPolygon(bx, by, 3);
    }

    // ── Shared background grid ────────────────────────────────────────────────

    /** Draws the faint green grid that appears on every screen background. */
    //Alexis
    private static void drawGrid(Graphics g, int w, int h) {
        g.setColor(new Color(0, 60, 0, 40));
        for (int x = 0; x < w; x += 40) g.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 40) g.drawLine(0, y, w, y);
    }
}
