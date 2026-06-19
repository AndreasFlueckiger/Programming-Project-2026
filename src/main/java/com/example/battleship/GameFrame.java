
package com.example.battleship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * Main game window for Battleship Game
 * Handles turn-based gamplay, bot AI, mine logic, and UI rendering
 * Supports Player vs Player & Player vs Bot
 */



public class GameFrame extends JFrame {

    //Alexis
    // Label for board coordinates
    private static final String[] ROW_LABELS = {"A","B","C","D","E","F","G","H","I","J"};

    private final GameMode mode; //PVP or PVB
    private final String   name1, name2;
    private final Board    board1, board2;
    private final BotAI    botAI;

    private boolean player1Turn = true;
    private boolean gameOver    = false;
    private int     score       = 0; // Player's score (only neccesary for PVB, but kept for both modes)
    private int     rounds      = 0;

    // UI components: button grids
    private final JButton[][] btn1 = new JButton[10][10];
    private final JButton[][] btn2 = new JButton[10][10];

    private JTextArea logArea;
    private JLabel    statusLabel;
    private JLabel    scoreLabel; //Score display
    private JLabel    turnLabel;
    private JLabel    roundLabel;

    private JPanel p1Tray, p2Tray; // Ship healthbar at bottom


/**
 * Constructs the game frame with the given mode, boards and player names.
 * @param mode GameMode PVP or PVB
 * @param board1 Board belonging to player 1 (left side)
 * @param board2 Board belonging to player 2 or Bot (right side)
 * @param name1 Name of Player 1
 * @param name2 Name of Player 2 or Bot
 */

    public GameFrame(GameMode mode, Board board1, Board board2,
                     String name1, String name2) {
        this.mode   = mode;
        this.board1 = board1;
        this.board2 = board2;
        this.name1  = name1;
        this.name2  = name2;
        this.botAI  = (mode == GameMode.PLAYER_VS_BOT)
                      ? new BotAI(board1, new Random()) : null;

        setTitle("Battleship War");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Construct the UI
        buildWindow();

        if (mode == GameMode.PLAYER_VS_PLAYER) {
            // In PVP, start with a dialog to pass control
            SwingUtilities.invokeLater(this::showPassDialog);
            //hides the board of the second player when the PVP starts
            hideBoard(btn2, board2);
        } else {
            // In PVB, show intro messages and let Player begin
            log("MISSION START — Click enemy grid to fire.", Theme.GREEN);
            log("MINES: Hitting one will damage YOUR own fleet!", Theme.MINE_YELLOW);
        }
    }

/** 
 * Builds the entire window layout: top bar, two boards, log panel, bottom ship trays.
 */
    //Alexis
    private void buildWindow() {
        // Root panel for grid lines and scanlines
        JPanel root = new JPanel(new BorderLayout(6, 6)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 50, 0, 20));
                for (int x = 0; x < getWidth(); x += 40) g.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 40) g.drawLine(0, y, getWidth(), y);
                Theme.paintScanlines(g, getWidth(), getHeight());
            }
        };
        root.setBackground(Theme.BG);
        root.setBorder(new EmptyBorder(8, 8, 0, 8));

        root.add(buildTopBar(), BorderLayout.NORTH);

        // Center area: Left board, log Panel and right board
        JPanel centre = new JPanel(new GridLayout(1, 3, 8, 0));
        centre.setOpaque(false);
        centre.add(buildBoardPanel(name1 + "'S FLEET", btn1, board1));
        centre.add(buildLogPanel());
        centre.add(buildBoardPanel(name2 + "'S WATERS", btn2, board2));
        root.add(centre, BorderLayout.CENTER);

        root.add(buildBottomBar(), BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setMinimumSize(new Dimension(1020, 620));
        setLocationRelativeTo(null);
        setVisible(true);

        // Paint ships on their side
        paintOwnBoard(board1, btn1);
        paintOwnBoard(board2, btn2);
    }


    /**
     * creates top bar with game title, turn indicator and round counter
     */
    //Alexis
    private JPanel buildTopBar() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.GREEN_DIM));

        JLabel title = Theme.titleLabel("BATTLESHIP WAR", 20);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 2));
        right.setOpaque(false);

        turnLabel = new JLabel("TURN: " + name1);
        turnLabel.setFont(Theme.FONT_SCORE);
        turnLabel.setForeground(Theme.GREEN);
        right.add(turnLabel);

        roundLabel = new JLabel("ROUND: 0");
        roundLabel.setFont(Theme.FONT_SCORE);
        roundLabel.setForeground(Theme.TEXT_DIM);
        right.add(roundLabel);

        p.add(right, BorderLayout.EAST);
        return p;
    }

/**
 * Builds a single board panel with row and column labels and a grid of buttons
 * @param title Title shown above the board
 * @param buttons 2D array to sttore buttons which is filled
 * @param board Board Model to associate with clicks
 * @return JPanel with the baord UI
 */
    //Alexis
    private JPanel buildBoardPanel(String title, JButton[][] buttons, Board board) {
        JPanel outer = new JPanel(new BorderLayout(0, 4));
        outer.setOpaque(false);
        outer.setBorder(Theme.titledPadded(title, 2));

        // Column labels (1-10)
        JPanel colRow = new JPanel(new GridLayout(1, 11, 2, 0));
        colRow.setOpaque(false);
        colRow.add(new JLabel(""));
        for (int c = 1; c <= 10; c++) {
            JLabel l = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            l.setFont(Theme.FONT_BODY);
            l.setForeground(Theme.TEXT_DIM);
            colRow.add(l);
        }

        JPanel body = new JPanel(new BorderLayout(2, 0));
        body.setOpaque(false);

        // Row labels (A-J)
        JPanel rowLabels = new JPanel(new GridLayout(10, 1, 2, 2));
        rowLabels.setOpaque(false);
        for (String rn : ROW_LABELS) {
            JLabel l = new JLabel(rn, SwingConstants.CENTER);
            l.setFont(Theme.FONT_LABEL);
            l.setForeground(Theme.GREEN);
            l.setPreferredSize(new Dimension(22, 38));
            rowLabels.add(l);
        }

        // Button Grid
        JPanel grid = new JPanel(new GridLayout(10, 10, 2, 2));
        grid.setBackground(Theme.BG);

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                JButton btn = Theme.cellButton(true);
                final int row = r, col = c;
                final Board clickedBoard = board;
                btn.addActionListener(e -> onPlayerClick(row, col, clickedBoard));
                buttons[r][c] = btn;
                grid.add(btn);
            }
        }

        body.add(rowLabels, BorderLayout.WEST);
        body.add(grid, BorderLayout.CENTER);

        JPanel full = new JPanel(new BorderLayout(0, 2));
        full.setOpaque(false);
        full.add(colRow, BorderLayout.NORTH);
        full.add(body, BorderLayout.CENTER);

        outer.add(full, BorderLayout.CENTER);
        return outer;
    }

    /**
     * Builds the central log panel showing battle messages and legend panel (explains cell colors). 
     */
    //Matteo
    private JPanel buildLogPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Theme.PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GREEN_DIM, 1),
                new EmptyBorder(8, 10, 8, 10)));

        statusLabel = new JLabel("AWAITING ORDERS", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_LABEL);
        statusLabel.setForeground(Theme.GREEN);
        p.add(statusLabel, BorderLayout.NORTH);

        logArea = Theme.logArea();
        JScrollPane sp = Theme.scrollPane(logArea);
        p.add(sp, BorderLayout.CENTER);

        p.add(buildLegend(), BorderLayout.SOUTH);
        return p;
    }

    //Marco
    private JPanel buildLegend() {
        JPanel p = new JPanel(new GridLayout(5, 1, 2, 2));
        p.setBackground(Theme.PANEL);
        p.setBorder(new EmptyBorder(6, 0, 0, 0));
        p.add(legendRow(Theme.WATER, "WATER"));
        p.add(legendRow(Theme.MISS, "MISS"));
        p.add(legendRow(Theme.RED, "HIT"));
        p.add(legendRow(Theme.RED_DARK, "SUNK"));
        p.add(legendRow(Theme.MINE_BLAST, "MINE BLAST"));
        return p;
    }

    /**
     * Helper to create a single row in the legend
     */
    //Marco
    private JPanel legendRow(Color c, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        row.setBackground(Theme.PANEL);
        JLabel sw = new JLabel("  ");
        sw.setOpaque(true);
        sw.setBackground(c);
        sw.setBorder(BorderFactory.createLineBorder(Theme.GREEN_DIM));
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_BODY);
        lbl.setForeground(Theme.TEXT_DIM);
        row.add(sw);
        row.add(lbl);
        return row;
    }

    /**
     * Builds the bottom bar showing ship health trays, score and menu button
     */
    //Matteo
    private JPanel buildBottomBar() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(new Color(0, 8, 0));
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.GREEN));

        p1Tray = buildShipTray(name1, board1);
        p.add(p1Tray, BorderLayout.WEST);

        JPanel centre = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 4));
        centre.setBackground(new Color(0, 8, 0));

        JLabel ef = new JLabel("ENEMY FLEET", SwingConstants.CENTER);
        ef.setFont(Theme.FONT_SCORE);
        ef.setForeground(Theme.RED);
        centre.add(ef);

        scoreLabel = new JLabel("SCORE: 0", SwingConstants.CENTER);
        scoreLabel.setFont(Theme.FONT_SCORE);
        scoreLabel.setForeground(Theme.SCORE);
        centre.add(scoreLabel);

        JButton menuBtn = Theme.button("MENU");
        menuBtn.setFont(Theme.FONT_BODY);
        menuBtn.setPreferredSize(new Dimension(80, 26));
        menuBtn.addActionListener(e -> {
            dispose();
            new MenuFrame();
        });
        centre.add(menuBtn);

        p.add(centre, BorderLayout.CENTER);

        p2Tray = buildShipTray(name2, board2);
        p.add(p2Tray, BorderLayout.EAST);

        return p;
    }

    /**
     * Creates a ship health tray for a player
     * Contains colored bars representing each ship's health
     */
    //Alexis
    private JPanel buildShipTray(String playerName, Board board) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        p.setBackground(new Color(0, 8, 0));

        JLabel lbl = new JLabel(playerName + ": ");
        lbl.setFont(Theme.FONT_BODY);
        lbl.setForeground(Theme.TEXT_DIM);
        p.add(lbl);

        // width's corresponding to ships lengths: Carrier (5) -> 50, Battleship (4) -> 40, etc.
        int[] widths = {50, 40, 32, 32, 22};
        for (int i = 0; i < 5; i++) {
            JPanel bar = new JPanel();
            bar.setPreferredSize(new Dimension(widths[i], 16));
            bar.setBackground(Theme.SHIP_OWN);
            bar.setBorder(BorderFactory.createLineBorder(Theme.GREEN_DIM, 1));
            bar.setName("ship_" + i); //Mark which is later identified
            p.add(bar);
        }
        return p;
    }

    /**
     * Changes the color of a specific ship health bar to indicate it is sunk
     * @param tray the tray panel containing the bars
     * @param shipIndex INdex of the ship (0-4)
     */
    //Alexis
    private void sinkTrayBar(JPanel tray, int shipIndex) {
        int found = 0;
        for (Component c : tray.getComponents()) {
            if (c instanceof JPanel bar && bar.getName() != null
                    && bar.getName().startsWith("ship_")) {
                if (found == shipIndex) {
                    bar.setBackground(Theme.RED_DARK);
                    bar.setBorder(BorderFactory.createLineBorder(Theme.RED, 1));
                    return;
                }
                found++;
            }
        }
    }

    /**
     * Paints a player own board, showing their ships (#) and mines (*).
     * Called at start and after each turn in PvP when it's the players respective turn
     */

    //Marco
    private void paintOwnBoard(Board board, JButton[][] buttons) {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                Cell cell = board.grid[r][c];
                if (cell.hasMine) {
                    buttons[r][c].setBackground(Theme.MINE_YELLOW);
                    buttons[r][c].setText("*");
                    buttons[r][c].setForeground(Color.BLACK);
                } else if (cell.ship != null) {
                    buttons[r][c].setBackground(Theme.SHIP_OWN);
                    buttons[r][c].setText("#");
                    buttons[r][c].setForeground(Theme.BG);
                }
            }
        }
    }

    /**
     * Applies a shot result to a UI button (change color/text)
     * @param targetBoard The board model that was shot
     * @param buttons Button grid for that board
     * @param row Row index (0-9)
     * @param col Column index (0-9)
     * @param result Shotresult from Board.shoot()
     */
    //Marco
    private void applyShot(Board targetBoard, JButton[][] buttons,
                           int row, int col, ShotResult result) {
        JButton btn = buttons[row][col];
        switch (result) {
            case MISS -> {
                btn.setBackground(Theme.MISS);
                btn.setText("o");
                btn.setForeground(Theme.GREEN);
            }
            case HIT -> {
                btn.setBackground(Theme.RED);
                btn.setText("X");
                btn.setForeground(Color.WHITE);
            }
            case SUNK -> {
                // Mark all cells of the sunk ship in dark red
                markSunk(targetBoard, buttons, targetBoard.shipAt(row, col));
                int idx = countSunkIndex(targetBoard, targetBoard.shipAt(row, col));
                JPanel tray = (targetBoard == board2) ? p2Tray : p1Tray;
                sinkTrayBar(tray, idx);
            }
            default -> {}
        }
    }

    /**
     * Mark all cells belonging to a sunk ship with dark red background.
     */
    //Alexis
    private void markSunk(Board board, JButton[][] buttons, Ship ship) {
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++)
                if (board.shipAt(r, c) == ship) {
                    buttons[r][c].setBackground(Theme.RED_DARK);
                    buttons[r][c].setText("X");
                    buttons[r][c].setForeground(Theme.RED);
                }
    }

    /**
     * Reveals all ship positions (for endgame) by showing '#' on the unshot cells.
     */
    //Alexis
    private void revealBoard(Board board, JButton[][] buttons) {
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++)
                if (board.shipAt(r, c) != null && !board.wasShotAt(r, c)) {
                    buttons[r][c].setBackground(new Color(0, 70, 0));
                    buttons[r][c].setText("#");
                    buttons[r][c].setForeground(Theme.GREEN_DIM);
                }
    }

    /**
     * Hides the opppenent's board by clearing unshot cells (used during turn pass in PvP)
     */
    //Marco
    private void hideBoard(JButton[][] buttons, Board board) {
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++)
                if (!board.wasShotAt(r, c)) {
                    buttons[r][c].setBackground(Theme.WATER);
                    buttons[r][c].setText("");
                }
    }

    /**
     * Finds the index (order in board.ships list) of a given ship
     * skipping already sunk ships to align with health bar order.
     */
    //Matteo
    private int countSunkIndex(Board board, Ship ship) {
        int idx = 0;
        for (Ship s : board.ships) {
            if (s == ship) return idx;
            if (s.isSunk()) idx++;
        }
        return idx;
    }

    /**
     * Applies counter-hits after a mine explosion
     * Mines damage the attacker's own fleet
     * @param targetBoard Attacker's own board (the one which recieves damage)
     * @param targetButtons Button grid of the attacker's board
     * @param hits List of coordinates [row,col] to hit
     */
    //Marco
    private void applyCounterHits(Board targetBoard, JButton[][] targetButtons, List<int[]> hits) {
        for (int[] coord : hits) {
            int r = coord[0], c = coord[1];
            if (!targetBoard.wasShotAt(r, c)) {
                ShotResult res = targetBoard.shoot(r, c);
                applyShot(targetBoard, targetButtons, r, c, res);
                if (res == ShotResult.HIT || res == ShotResult.SUNK) {
                    score += 50;
                    if (res == ShotResult.SUNK) score += 100;
                    scoreLabel.setText("SCORE: " + score);
                }
                log(">>> Mine backfire: " + cell(r, c) + " → " + resultWord(res), Theme.MINE_YELLOW);
            }
        }
    }

    /**
     * Click handler using direct board object comparison.
     * This is the corrected method – it uses board references, not IDs.
     */
    //Matteo
    private void onPlayerClick(int row, int col, Board clickedBoard) {
        if (gameOver) return;

        Board enemyBoard = null;
        JButton[][] enemyButtons = null;
        String attackerName = null;

        if (mode == GameMode.PLAYER_VS_BOT) {
            // Only the right board (board2) is clickable as enemy
            if (clickedBoard == board2) {
                enemyBoard = board2;
                enemyButtons = btn2;
                attackerName = name1;
            }
        } else { // PvP
            if (player1Turn && clickedBoard == board2) {
                // Player 1 attacks board2 (right)
                enemyBoard = board2;
                enemyButtons = btn2;
                attackerName = name1;
            } else if (!player1Turn && clickedBoard == board1) {
                // Player 2 attacks board1 (left)
                enemyBoard = board1;
                enemyButtons = btn1;
                attackerName = name2;
            }
        }

        if (enemyBoard == null) return; // Clicked on own board or invalid

        if (enemyBoard.wasShotAt(row, col)) return; // Prevents shooting the same cell twice

        // Perform the shot
        ShotResult result = enemyBoard.shoot(row, col); 
        applyShot(enemyBoard, enemyButtons, row, col, result);

        // Track the score (used for PvB)
        if (result == ShotResult.HIT || result == ShotResult.SUNK) score += 50;
        if (result == ShotResult.SUNK) score += 100;
        scoreLabel.setText("SCORE: " + score);

        // Log message
        String extra = switch (result) {
            case SUNK -> " *** " + enemyBoard.shipAt(row, col).getName() + " SUNK! ***";
            case MINE_TRIGGER -> " *** MINE DETONATED! Your own fleet takes damage! ***";
            default -> "";
        };
        log("[" + attackerName + "] " + cell(row, col) + " → " + resultWord(result) + extra,
                result == ShotResult.MISS ? Theme.MISS : Theme.RED);

        // Handle the mine backfire (counter-hits)
        if (result == ShotResult.MINE_TRIGGER) {
            List<int[]> counterHits = enemyBoard.consumeCounterHits();
            Board attackerBoard = (mode == GameMode.PLAYER_VS_BOT) ? board1 :
                                  (player1Turn ? board1 : board2);
            JButton[][] attackerButtons = (mode == GameMode.PLAYER_VS_BOT) ? btn1 :
                                          (player1Turn ? btn1 : btn2);
            applyCounterHits(attackerBoard, attackerButtons, counterHits);
        }

        // Win condition
        if (enemyBoard.allShipsSunk()) {
            endGame(attackerName + " WINS!");
            return;
        }

        // Next turn
        if (mode == GameMode.PLAYER_VS_BOT) {
            statusLabel.setText("BOT CALCULATING...");
            Timer t = new Timer(500, e -> botTurn());
            t.setRepeats(false);
            t.start();
        } else {
            player1Turn = !player1Turn;
            rounds++;
            roundLabel.setText("ROUND: " + rounds);
            turnLabel.setText("TURN: " + (player1Turn ? name1 : name2));
            turnLabel.setForeground(player1Turn ? Theme.GREEN : Theme.RED);

            // Hide both boards, then show the 'next turn' dialog
            hideBoard(btn1, board1);
            hideBoard(btn2, board2);
            Timer t = new Timer(300, e -> showPassDialog());
            t.setRepeats(false);
            t.start();
        }
    }

    /**
     * Bots turn in PvB
     * The bot AI selects coordinates, shoots and updates UI
     */
    //Matteo
    private void botTurn() {
        ShotResult result = botAI.takeTurn();
        int[] shot = botAI.getLastShot();
        int r = shot[0], c = shot[1];

        applyShot(board1, btn1, r, c, result);

        String extra = switch (result) {
            case SUNK -> " *** " + board1.shipAt(r, c).getName() + " SUNK! ***";
            case MINE_TRIGGER -> " *** MINE DETONATED! Bot's fleet takes damage! ***";
            default -> "";
        };
        log("[BOT] " + cell(r, c) + " → " + resultWord(result) + extra, Theme.RED);

        // Mine backfore for the Bot (damages the bot's own board)
        if (result == ShotResult.MINE_TRIGGER) {
            List<int[]> counterHits = board1.consumeCounterHits();
            applyCounterHits(board2, btn2, counterHits);
        }

        rounds++;
        roundLabel.setText("ROUND: " + rounds);

        // Check win condition's
        if (board1.allShipsSunk()) {
            endGame("BOT WINS!");
            return;
        }
        if (board2.allShipsSunk()) {
            endGame("YOU WIN!");
            return;
        }

        statusLabel.setText("FIRE AT WILL — YOUR TURN");
    }

    /**
     * Shows a dialog to pass the turn in PvP mode
     * The dialog hides the board of the current player until they click "Ready"
     */
    //Alexis
    private void showPassDialog() {
        String nextName = player1Turn ? name1 : name2;
        Color nextCol = player1Turn ? Theme.GREEN : Theme.RED;

        JDialog dlg = new JDialog(this, "PASS CONTROL", true);
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel p = new JPanel(new BorderLayout(10, 16));
        p.setBackground(Theme.BG);
        p.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel msg = new JLabel(
                "<html><center>" +
                        "<span style='font-size:20px;color:#" + colorHex(nextCol) + "'>" + nextName + "</span>" +
                        " — YOUR MOVE<br><br>" +
                        "<span style='font-size:12px;color:#005520'>ROUND " + rounds +
                        " | Other player must look away</span></center></html>",
                SwingConstants.CENTER);
        msg.setForeground(Theme.GREEN);
        p.add(msg, BorderLayout.CENTER);

        JButton ready = Theme.button("▶ READY — SHOW MY BOARD");
        ready.setFont(Theme.FONT_LABEL);
        ready.addActionListener(e -> {
            dlg.dispose();
            // Reveal the current's player own ships
            if (player1Turn) paintOwnBoard(board1, btn1);
            else paintOwnBoard(board2, btn2);
            statusLabel.setText(nextName + " — SELECT TARGET");
            turnLabel.setText("TURN: " + nextName);
            turnLabel.setForeground(nextCol);
        });
        p.add(ready, BorderLayout.SOUTH);

        dlg.setContentPane(p);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(440, 240));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    /**
     * Ends the game, reveals all the ships and give a replay option for the user
     * @param headline headline Winner message
     */
    //Marco
    private void endGame(String headline) {
        gameOver = true;
        revealBoard(board1, btn1);
        revealBoard(board2, btn2);
        statusLabel.setText(headline);
        log("=== " + headline + " | ROUNDS: " + rounds + " | SCORE: " + score + " ===", Theme.SCORE);

        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    headline + "\n\nRounds: " + rounds + "   Score: " + score + "\n\nPlay again?",
                    "MISSION COMPLETE",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new MenuFrame();
            }
        });
    }

    /**
     * Append a message to the log area with a specific color.
     * @param msg Message text
     * @param c Color (used for HTML styling via Theme's logArea)
     */
    //Marco
    private void log(String msg, Color c) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // Converts row (0-9) and column (0-9) to standard notation (such as A5)
    private String cell(int r, int c) {
        return "" + (char) ('A' + r) + (c + 1);
    }

    /**
     * Converts ShotResult enum to human-readable string for logs.
     */
    private String resultWord(ShotResult r) {
        return switch (r) {
            case MISS -> "MISS";
            case HIT -> "HIT!";
            case SUNK -> "SUNK!";
            case MINE_TRIGGER -> "MINE!";
            case ALREADY_SHOT -> "DUPE";
            default -> "?";
        };
    }

    // Converts a color to its hexadecimal RGB string 
    private String colorHex(Color c) {
        return String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}