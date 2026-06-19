package com.example.battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a 10×10 Battleship board.
 *
 * Manages ship placement, mine placement, and shot resolution.
 * Mine mechanics (fixed):
 *   - Each player places 3 mines on their own board during setup.
 *   - When the OPPONENT fires at a cell containing a mine → MINE_TRIGGER:
 *     the mine explodes, causing RANDOM HITS on the ATTACKER's own board
 *     (3 random unshot cells per detonated mine).
 *   - No free intel is given; the explosion damages the shooter.
 */
public class Board {

    private static final String[] SHIP_NAMES = {
        "Carrier", "Battleship", "Destroyer", "Submarine", "Patrol Boat"
    };

    public static final int MINE_COUNT = 3;

    private final int size;
    final Cell[][] grid;
    final List<Ship> ships = new ArrayList<>();
    private int minesPlaced = 0;

    /** Temporary storage for counter‑hits when a mine explodes. */
    private final List<int[]> pendingCounterHits = new ArrayList<>();
    private final Random random = new Random();

    public Board(int size) {
        this.size = size;
        grid = new Cell[size][size];
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                grid[r][c] = new Cell();
    }

    // ── Ship placement ────────────────────────────────────────────────────────
    //Alexis and Matteo
    public void placeFleetRandomly(int[] shipLengths, Random random) {
        for (int i = 0; i < shipLengths.length; i++) {
            placeSingleShipRandomly(shipLengths[i], SHIP_NAMES[i], random);
        }
    }

    private void placeSingleShipRandomly(int length, String name, Random random) {
        while (true) {
            boolean horiz   = random.nextBoolean();
            int startRow    = random.nextInt(size);
            int startCol    = random.nextInt(size);
            if (canPlaceShip(length, startRow, startCol, horiz)) {
                doPlaceShip(length, name, startRow, startCol, horiz);
                return;
            }
        }
    }

    public boolean placeShip(int length, String name, int startRow, int startCol, boolean horizontal) {
        if (!canPlaceShip(length, startRow, startCol, horizontal)) return false;
        doPlaceShip(length, name, startRow, startCol, horizontal);
        return true;
    }

    private boolean canPlaceShip(int length, int startRow, int startCol, boolean horiz) {
        int endRow = horiz ? startRow          : startRow + length - 1;
        int endCol = horiz ? startCol + length - 1 : startCol;
        if (endRow >= size || endCol >= size) return false;
        for (int i = 0; i < length; i++) {
            int r = horiz ? startRow     : startRow + i;
            int c = horiz ? startCol + i : startCol;
            if (grid[r][c].ship != null) return false;
        }
        return true;
    }

    private void doPlaceShip(int length, String name, int startRow, int startCol, boolean horiz) {
        Ship ship = new Ship(length, name);
        ships.add(ship);
        for (int i = 0; i < length; i++) {
            int r = horiz ? startRow     : startRow + i;
            int c = horiz ? startCol + i : startCol;
            grid[r][c].ship = ship;
        }
    }

    // ── Mine placement ────────────────────────────────────────────────────────
    //Marco
    public boolean placeMine(int row, int col) {
        if (minesPlaced >= MINE_COUNT)           return false;
        if (grid[row][col].ship    != null)      return false;
        if (grid[row][col].hasMine)              return false;
        grid[row][col].hasMine = true;
        minesPlaced++;
        return true;
    }

    public void placeMinesRandomly(Random random) {
        int placed = 0;
        while (placed < MINE_COUNT) {
            int r = random.nextInt(size);
            int c = random.nextInt(size);
            if (grid[r][c].ship == null && !grid[r][c].hasMine) {
                grid[r][c].hasMine = true;
                placed++;
            }
        }
        minesPlaced = MINE_COUNT;
    }

    public int getMinesPlaced() { return minesPlaced; }

    // ── Shooting & fixed mine logic ──────────────────────────────────────────

    /**
     * Fires a shot at (row, col).
     *
     * Resolution order:
     *   1. Already shot → ALREADY_SHOT
     *   2. Mine present → MINE_TRIGGER (explodes, damages attacker, chain reaction)
     *   3. Ship present → HIT or SUNK
     *   4. Empty water  → MISS
     *
     * When a mine triggers, the attacker will later call consumeCounterHits()
     * to retrieve the coordinates that must be hit on the attacker's own board.
     */
    //Alexis
    public ShotResult shoot(int row, int col) {
        Cell cell = grid[row][col];
        if (cell.wasShot) return ShotResult.ALREADY_SHOT;

        cell.wasShot = true;

        if (cell.hasMine && !cell.mineDetonated) {
            cell.mineDetonated = true;
            pendingCounterHits.clear();
            detonateMine(row, col);          // fills pendingCounterHits
            return ShotResult.MINE_TRIGGER;
        }

        if (cell.ship != null) {
            cell.ship.registerHit();
            return cell.ship.isSunk() ? ShotResult.SUNK : ShotResult.HIT;
        }

        return ShotResult.MISS;
    }

    /**
     * Recursively detonates a mine and all adjacent mines.
     * Each detonated mine adds 3 random counter‑hits (to be applied on attacker's board).
     */
    //Marco
    private void detonateMine(int row, int col) {
        addRandomCounterHits(3);   // this mine damages the attacker

        // Chain reaction: detonate any undetonated mines in the 3×3 neighbourhood
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = row + dr, nc = col + dc;
                if (nr >= 0 && nr < size && nc >= 0 && nc < size) {
                    Cell neighbour = grid[nr][nc];
                    if (neighbour.hasMine && !neighbour.mineDetonated) {
                        neighbour.mineDetonated = true;
                        detonateMine(nr, nc);
                    }
                }
            }
        }
    }

    /**
     * Adds `count` random unshot cells from THIS board to pendingCounterHits.
     * These represent the damage inflicted on the attacker.
     */
    //Alexis
    private void addRandomCounterHits(int count) {
        int added = 0;
        int attempts = 0;
        while (added < count && attempts < 1000) {
            int r = random.nextInt(size);
            int c = random.nextInt(size);
            if (!wasShotAt(r, c)) {
                pendingCounterHits.add(new int[]{r, c});
                added++;
            }
            attempts++;
        }
        // If we run out of unshot cells, just add fewer hits (also known as graceful degradation)
    }

    /**
     * Returns the list of counter‑hits (damage to the attacker) from the last mine
     * explosion and clears the internal list.
     */
    public List<int[]> consumeCounterHits() {
        List<int[]> result = new ArrayList<>(pendingCounterHits);
        pendingCounterHits.clear();
        return result;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean allShipsSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    public Ship    shipAt(int r, int c)    { return grid[r][c].ship; }
    public boolean wasShotAt(int r, int c) { return grid[r][c].wasShot; }
    public boolean hasMineAt(int r, int c) { return grid[r][c].hasMine; }
    public boolean mineDetonatedAt(int r, int c) { return grid[r][c].mineDetonated; }
    public int     getSize()               { return size; }
}