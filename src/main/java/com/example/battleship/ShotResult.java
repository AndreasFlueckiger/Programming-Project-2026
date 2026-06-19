package com.example.battleship;

/**
 * All possible outcomes when a cell is targeted.
 *
 * MISS         – open water, nothing there.
 * HIT          – a ship was struck but not yet sunk.
 * SUNK         – the final hit that sinks a ship entirely.
 * MINE_HIT     – the shooter stepped on their OWN mine (placed on their board
 *                by the opponent); the shooter loses their next turn.
 * MINE_TRIGGER – the shot landed on an enemy mine; the mine explodes and
 *                reveals a 3×3 area around it (bonus intel) but counts as a miss.
 * ALREADY_SHOT – the cell was already targeted; illegal move.
 */
//Matteo
public enum ShotResult {
    MISS,
    HIT,
    SUNK,
    MINE_HIT,
    MINE_TRIGGER,
    ALREADY_SHOT
}
