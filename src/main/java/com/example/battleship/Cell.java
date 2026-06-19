package com.example.battleship;

/**
 * A single cell on a 10×10 Battleship board.
 *
 * A cell can hold a Ship reference, a mine flag, and tracks whether it has
 * already been revealed (shot or mine-exploded).
 */
//Matteo
public class Cell {

    /** The ship occupying this cell, or null if empty. */
    Ship ship = null;

    /** True if a mine was placed here by the owner of this board. */
    boolean hasMine = false;

    /** True once this cell has been targeted (shot or revealed by a mine blast). */
    boolean wasShot = false;

    /** True once this cell's mine has already detonated (prevents double-trigger). */
    boolean mineDetonated = false;
}
