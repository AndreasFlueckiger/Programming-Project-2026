package com.example.battleship;

/**
 * Determines whether the second player is a human (Player vs Player on one PC)
 * or a computer-controlled AI (Player vs Bot).
 */
//Matteo
public enum GameMode {
    /** Two human players share the same keyboard/mouse, passing the PC between turns. */
    PLAYER_VS_PLAYER,
    /** One human faces the built-in hunt/target AI bot. */
    PLAYER_VS_BOT
}
