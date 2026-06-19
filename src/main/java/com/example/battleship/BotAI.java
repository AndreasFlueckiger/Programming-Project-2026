package com.example.battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Target AI for Player vs Bot mode.
 * Returns the ShotResult of each shot and provides the last shot coordinates.
 */

//Matteo and Alexis
public class BotAI {

    private final Board target;
    private final int   size;
    private final Random random;

    private final List<int[]> targetQueue = new ArrayList<>();
    private final List<int[]> huntQueue = new ArrayList<>();
    private boolean targeting = false;

    private int[] lastShot = null;

    public BotAI(Board targetBoard, Random random) {
        this.target = targetBoard;
        this.size   = targetBoard.getSize();
        this.random = random;
        buildHuntQueue();
    }

    private void buildHuntQueue() {
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if ((r + c) % 2 == 0)
                    huntQueue.add(new int[]{r, c});
        Collections.shuffle(huntQueue, random);
    }

    /**
     * Chooses and fires the bot's next shot.
     * @return the ShotResult of the shot
     */
    public ShotResult takeTurn() {
        int[] shot = null;

        // Target phase
        if (targeting) {
            while (!targetQueue.isEmpty()) {
                int[] cand = targetQueue.remove(0);
                if (!target.wasShotAt(cand[0], cand[1])) {
                    shot = cand;
                    break;
                }
            }
            if (shot == null) targeting = false;
        }

        // Hunt phase
        if (shot == null) {
            while (!huntQueue.isEmpty()) {
                int[] cand = huntQueue.remove(0);
                if (!target.wasShotAt(cand[0], cand[1])) {
                    shot = cand;
                    break;
                }
            }
        }

        // Fallback
        if (shot == null) shot = anyUnshot();

        lastShot = shot;
        ShotResult result = target.shoot(shot[0], shot[1]);

        if (result == ShotResult.HIT) {
            targeting = true;
            enqueueNeighbours(shot[0], shot[1]);
        } else if (result == ShotResult.SUNK) {
            targetQueue.clear();
            targeting = false;
        }
        // MINE_TRIGGER / MISS = no change

        return result;
    }

    /**
     * Returns the coordinates of the last shot fired.
     * @return {row, col} or null if never called
     */
    public int[] getLastShot() {
        return lastShot;
    }

    private void enqueueNeighbours(int row, int col) {
        int[][] d = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] dd : d) {
            int nr = row+dd[0], nc = col+dd[1];
            if (nr>=0 && nr<size && nc>=0 && nc<size && !target.wasShotAt(nr,nc))
                targetQueue.add(new int[]{nr,nc});
        }
    }

    private int[] anyUnshot() {
        for (int r=0; r<size; r++)
            for (int c=0; c<size; c++)
                if (!target.wasShotAt(r,c)) return new int[]{r,c};
        return null;
    }
}