package com.example.battleship;

/**
 * Represents a single ship on the board.
 * Tracks its length, display name, and how many times it has been hit.
 */
//Matteo
public class Ship {

    private final int length;
    private final String name;
    private int hits = 0;

    public Ship(int length, String name) {
        this.length = length;
        this.name   = name;
    }

    /** Records one successful hit against this ship. */
    public void registerHit() { hits++; }

    /** @return true when every cell of the ship has been hit. */
    public boolean isSunk() { return hits >= length; }

    public int    getLength() { return length; }
    public String getName()   { return name; }
}
