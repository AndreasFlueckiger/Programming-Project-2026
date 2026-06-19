package com.example.battleship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * Ship and mine placement screen, styled with a military green theme.
 *
 * Layout mirrors the top-left grid (green grid, red X hits).
 * Instructions are shown to the right of the grid in a status panel.
 *
 * Controls:
 *   - Click grid cell to place current ship / mine.
 *   - Hover shows a green preview.
 *   - Press R (or click Rotate) to toggle.
 *   - "Random All" button skips manual placement.
 */
public class SetupFrame extends JFrame {

    static final int[]    SHIP_LENGTHS = {5, 4, 3, 3, 2};
    static final String[] SHIP_NAMES   = {
        "CARRIER (5)", "BATTLESHIP (4)", "DESTROYER (3)",
        "SUBMARINE (3)", "PATROL BOAT (2)"
    };

    private final GameMode mode;
    private final int      playerNumber;
    private final Board    player1Board;

    private final Board       myBoard  = new Board(10);
    private final JButton[][] cellBtn  = new JButton[10][10];

    private int     shipIndex    = 0;
    private boolean horizontal   = true;
    private boolean placingMines = false;
    private int     minesLeft    = Board.MINE_COUNT;

    private int hoverRow = -1, hoverCol = -1;

    private JLabel instructionLabel;
    private JLabel fleetLabel;
    private JLabel mineLabel;
    private JLabel scoreLabel;   // shows ships placed count

    //Alexis
    public SetupFrame(GameMode mode, int playerNumber, Board player1Board) {
        this.mode         = mode;
        this.playerNumber = playerNumber;
        this.player1Board = player1Board;

        String who = (mode == GameMode.PLAYER_VS_PLAYER)
                ? "PLAYER " + playerNumber + " — DEPLOY FLEET"
                : "DEPLOY YOUR FLEET";

        setTitle("Battleship War — Setup");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = buildRoot(who);
        setContentPane(root);
        pack();
        setMinimumSize(new Dimension(820, 620));
        setLocationRelativeTo(null);
        setVisible(true);

        updateLabels();
    }

    // Root layout 
    //Marco
    private JPanel buildRoot(String who) {
        JPanel root = new JPanel(new BorderLayout(10, 10)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 60, 0, 25));
                for (int x = 0; x < getWidth(); x += 40)  g.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 40) g.drawLine(0, y, getWidth(), y);
                Theme.paintScanlines(g, getWidth(), getHeight());
            }
        };
        root.setBackground(Theme.BG);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        root.add(buildHeader(who), BorderLayout.NORTH);
        root.add(buildGridPanel(),  BorderLayout.CENTER);
        root.add(buildSidebar(),   BorderLayout.EAST);
        root.add(buildFooter(),    BorderLayout.SOUTH);

        return root;
    }

    // Header 
    //Matteo

    private JPanel buildHeader(String who) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 6, 0));

        JLabel title = Theme.titleLabel(who, 22);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(title, BorderLayout.WEST);

        // Score display 
        scoreLabel = new JLabel("SHIPS PLACED: 0 / 5", SwingConstants.RIGHT);
        scoreLabel.setFont(Theme.FONT_SCORE);
        scoreLabel.setForeground(Theme.SCORE);
        p.add(scoreLabel, BorderLayout.EAST);

        return p;
    }

    // Grid Panel
    //Marco
    private JPanel buildGridPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(Theme.titledPadded("YOUR BOARD", 4));

        // Column headers (1-10)
        JPanel colHeaders = new JPanel(new GridLayout(1, 11, 2, 0));
        colHeaders.setOpaque(false);
        colHeaders.add(new JLabel(""));   // corner
        for (int c = 1; c <= 10; c++) {
            JLabel l = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            l.setFont(Theme.FONT_BODY);
            l.setForeground(Theme.TEXT_DIM);
            colHeaders.add(l);
        }

        // Row headers (A-J) + grid
        JPanel rows = new JPanel(new BorderLayout(2, 0));
        rows.setOpaque(false);

        JPanel rowLabels = new JPanel(new GridLayout(10, 1, 2, 2));
        rowLabels.setOpaque(false);
        String[] rowNames = {"A","B","C","D","E","F","G","H","I","J"};
        for (String rn : rowNames) {
            JLabel l = new JLabel(rn, SwingConstants.CENTER);
            l.setFont(Theme.FONT_LABEL);
            l.setForeground(Theme.GREEN);
            l.setPreferredSize(new Dimension(22, 40));
            rowLabels.add(l);
        }

        JPanel grid = new JPanel(new GridLayout(10, 10, 2, 2));
        grid.setBackground(Theme.BG);
        grid.setBorder(new EmptyBorder(0, 2, 0, 0));

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                JButton btn = Theme.cellButton(false);
                final int row = r, col = c;
                btn.addActionListener(e -> onCellClick(row, col));
                btn.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hoverRow=row; hoverCol=col; refreshGrid(); }
                    public void mouseExited(MouseEvent e)  { hoverRow=-1; hoverCol=-1; refreshGrid(); }
                });
                cellBtn[r][c] = btn;
                grid.add(btn);
            }
        }

        rows.add(rowLabels, BorderLayout.WEST);
        rows.add(grid,      BorderLayout.CENTER);

        JPanel full = new JPanel(new BorderLayout(0, 2));
        full.setOpaque(false);
        full.add(colHeaders, BorderLayout.NORTH);
        full.add(rows,       BorderLayout.CENTER);

        outer.add(full, BorderLayout.CENTER);
        return outer;
    }

    // Sidebar layout 
    //Alexis
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(Theme.PANEL);
        side.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.GREEN_DIM, 1),
            new EmptyBorder(12, 12, 12, 12)));
        side.setPreferredSize(new Dimension(210, 0));

        // Instruction box
        instructionLabel = new JLabel("<html></html>");
        instructionLabel.setFont(Theme.FONT_BODY);
        instructionLabel.setForeground(Theme.GREEN);
        instructionLabel.setAlignmentX(LEFT_ALIGNMENT);
        side.add(instructionLabel);
        side.add(Box.createVerticalStrut(16));

        // Fleet list
        JLabel fTitle = Theme.titleLabel("FLEET STATUS", 16);
        fTitle.setAlignmentX(LEFT_ALIGNMENT);
        fTitle.setHorizontalAlignment(SwingConstants.LEFT);
        side.add(fTitle);
        side.add(Box.createVerticalStrut(6));

        fleetLabel = new JLabel("<html></html>");
        fleetLabel.setFont(Theme.FONT_BODY);
        fleetLabel.setForeground(Theme.GREEN);
        fleetLabel.setAlignmentX(LEFT_ALIGNMENT);
        side.add(fleetLabel);
        side.add(Box.createVerticalStrut(16));

        // Mine status
        mineLabel = new JLabel("MINES: 3 remaining");
        mineLabel.setFont(Theme.FONT_SCORE);
        mineLabel.setForeground(Theme.MINE_YELLOW);
        mineLabel.setAlignmentX(LEFT_ALIGNMENT);
        mineLabel.setVisible(false);
        side.add(mineLabel);
        side.add(Box.createVerticalStrut(16));

        // Rotate button
        JButton rotBtn = Theme.button("↻ ROTATE [R]");
        rotBtn.setMaximumSize(new Dimension(186, 38));
        rotBtn.setAlignmentX(LEFT_ALIGNMENT);
        rotBtn.addActionListener(e -> { horizontal = !horizontal; refreshGrid(); updateLabels(); });
        side.add(rotBtn);
        side.add(Box.createVerticalStrut(8));

        // Random button
        JButton rndBtn = Theme.button("⚄ RANDOM ALL");
        rndBtn.setMaximumSize(new Dimension(186, 38));
        rndBtn.setAlignmentX(LEFT_ALIGNMENT);
        rndBtn.addActionListener(e -> randomiseAll());
        side.add(rndBtn);

        // Keyboard shortcut R
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(javax.swing.KeyStroke.getKeyStroke('r'), "rotate");
        getRootPane().getActionMap().put("rotate", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                horizontal = !horizontal; refreshGrid(); updateLabels();
            }
        });

        return side;
    }

    // Footer 
    //Marco
    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.GREEN_DIM));

        JLabel bar = new JLabel("  BATTLESHIP WAR  |  FLEET DEPLOYMENT  ");
        bar.setFont(Theme.FONT_BODY);
        bar.setForeground(Theme.TEXT_DIM);
        p.add(bar);
        return p;
    }

    // Interaction 
    //Alexis
    private void onCellClick(int row, int col) {
        if (placingMines) placeMineAt(row, col);
        else              placeShipAt(row, col);
    }

    //Alexis
    private void placeShipAt(int row, int col) {
        if (shipIndex >= SHIP_LENGTHS.length) return;
        String name = SHIP_NAMES[shipIndex].split(" \\(")[0];
        if (myBoard.placeShip(SHIP_LENGTHS[shipIndex], name, row, col, horizontal)) {
            shipIndex++;
            if (shipIndex >= SHIP_LENGTHS.length) {
                placingMines = true;
                mineLabel.setVisible(true);
            }
            refreshGrid();
            updateLabels();
        }
    }
    //Marco
    private void placeMineAt(int row, int col) {
        if (minesLeft <= 0) return;
        if (myBoard.placeMine(row, col)) {
            minesLeft--;
            mineLabel.setText("MINES: " + minesLeft + " remaining");
            refreshGrid();
            updateLabels();
            if (minesLeft == 0) {
                Timer t = new Timer(300, e -> proceed());
                t.setRepeats(false);
                t.start();
            }
        }
    }
    //Marco
    private void randomiseAll() {
        Random rnd = new Random();
        Board fresh = new Board(10);
        fresh.placeFleetRandomly(SHIP_LENGTHS, rnd);
        fresh.placeMinesRandomly(rnd);

        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++) {
                myBoard.grid[r][c].ship    = fresh.grid[r][c].ship;
                myBoard.grid[r][c].hasMine = fresh.grid[r][c].hasMine;
            }
        myBoard.ships.clear();
        myBoard.ships.addAll(fresh.ships);

        shipIndex    = SHIP_LENGTHS.length;
        placingMines = false;
        minesLeft    = 0;
        mineLabel.setText("MINES: 0 remaining");
        mineLabel.setVisible(true);
        refreshGrid();
        updateLabels();

        Timer t = new Timer(400, e -> proceed());
        t.setRepeats(false);
        t.start();
    }
    //Alexis
    private void proceed() {
        dispose();
        if (mode == GameMode.PLAYER_VS_PLAYER && playerNumber == 1) {
            showPassScreen();
        } else if (mode == GameMode.PLAYER_VS_BOT) {
            Random rnd  = new Random();
            Board  bot  = new Board(10);
            bot.placeFleetRandomly(SHIP_LENGTHS, rnd);
            bot.placeMinesRandomly(rnd);
            new GameFrame(mode, myBoard, bot, "YOU", "BOT");
        } else {
            new GameFrame(mode, player1Board, myBoard, "PLAYER 1", "PLAYER 2");
            //possible position where hide the board of the second player
        }
    }
    //Matteo
    private void showPassScreen() {
        JDialog dlg = new JDialog(this, "PASS TO PLAYER 2", true);
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel p = new JPanel(new BorderLayout(10, 20));
        p.setBackground(Theme.BG);
        p.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel msg = Theme.titleLabel(
            "<html><center>PLAYER 1 READY<br>" +
            "<span style='font-size:14px;color:#007720'>Pass to Player 2.<br>" +
            "Do not let them see your board!</span></center></html>", 22);
        p.add(msg, BorderLayout.CENTER);

        JButton ok = Theme.button("▶  PLAYER 2 READY");
        ok.setFont(Theme.FONT_HEAD);
        ok.addActionListener(e -> {
            dlg.dispose();
            new SetupFrame(mode, 2, myBoard);
        });
        p.add(ok, BorderLayout.SOUTH);

        dlg.setContentPane(p);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(420, 260));
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }

    // Grid rendering
    //Matteo
    private void refreshGrid() {
        // Compute preview
        boolean[] preview = new boolean[100];
        boolean   valid   = false;
        if (!placingMines && shipIndex < SHIP_LENGTHS.length && hoverRow >= 0) {
            int len = SHIP_LENGTHS[shipIndex];
            valid   = canPreview(len, hoverRow, hoverCol, horizontal);
            if (valid) {
                for (int i = 0; i < len; i++) {
                    int r = horizontal ? hoverRow     : hoverRow + i;
                    int c = horizontal ? hoverCol + i : hoverCol;
                    if (r < 10 && c < 10) preview[r*10+c] = true;
                }
            }
        }

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                JButton btn  = cellBtn[r][c];
                Cell    cell = myBoard.grid[r][c];

                if (cell.hasMine) {
                    btn.setBackground(Theme.MINE_YELLOW);
                    btn.setText("*");
                    btn.setForeground(Color.BLACK);
                } else if (cell.ship != null) {
                    btn.setBackground(Theme.SHIP_OWN);
                    btn.setText("#");
                    btn.setForeground(Theme.BG);
                } else if (preview[r*10+c]) {
                    btn.setBackground(valid ? new Color(0, 100, 20) : new Color(100, 10, 0));
                    btn.setText("");
                    btn.setForeground(Theme.GREEN);
                } else if (placingMines && hoverRow==r && hoverCol==c
                           && cell.ship==null && !cell.hasMine) {
                    btn.setBackground(new Color(80, 60, 0));
                    btn.setText("*");
                    btn.setForeground(Theme.MINE_YELLOW);
                } else {
                    btn.setBackground(Theme.WATER);
                    btn.setText("");
                    btn.setForeground(Theme.GREEN);
                }
            }
        }
    }
    //Marco
    private boolean canPreview(int len, int sr, int sc, boolean horiz) {
        int er = horiz ? sr : sr + len - 1;
        int ec = horiz ? sc + len - 1 : sc;
        if (er >= 10 || ec >= 10) return false;
        for (int i = 0; i < len; i++) {
            int r = horiz ? sr : sr + i;
            int c = horiz ? sc + i : sc;
            if (myBoard.grid[r][c].ship != null) return false;
        }
        return true;
    }
    //Alexis
    private void updateLabels() {
        scoreLabel.setText("SHIPS PLACED: " + shipIndex + " / 5"); // Score bar

        // Instruction
        if (placingMines) {
            instructionLabel.setText(minesLeft > 0
                ? "<html><b>PLACE MINES</b><br>Click empty water<br>to plant a mine.<br><br>" +
                  "Mines are hidden<br>from your opponent.</html>"
                : "<html><b>ALL SET!</b><br>Loading game...</html>");
        } else if (shipIndex < SHIP_LENGTHS.length) {
            instructionLabel.setText(
                "<html><b>PLACE SHIP:</b><br>" + SHIP_NAMES[shipIndex] + "<br><br>" +
                "Click grid to deploy.<br>Press R to rotate.<br><br>" +
                "Orientation:<br><b>" + (horizontal ? "HORIZONTAL →" : "VERTICAL ↓") + "</b></html>");
        } else {
            instructionLabel.setText("<html><b>SHIPS DEPLOYED!</b><br>Now plant<br>your 3 mines.</html>");
        }

        // Fleet list
        StringBuilder sb = new StringBuilder("<html>");
        for (int i = 0; i < SHIP_NAMES.length; i++) {
            if (i < shipIndex) {
                sb.append("<font color='#005510'><s>").append(SHIP_NAMES[i]).append("</s></font>");
            } else if (i == shipIndex && !placingMines) {
                sb.append("<font color='#00d232'>▶ <b>").append(SHIP_NAMES[i]).append("</b></font>");
            } else {
                sb.append("<font color='#004010'>").append(SHIP_NAMES[i]).append("</font>");
            }
            sb.append("<br>");
        }
        sb.append("</html>");
        fleetLabel.setText(sb.toString());
    }
}
