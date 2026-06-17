# Programming-Project-2026

# Battleship 

A graphical Battleship board game built with Java 21 and Swing, supporting **Player vs Bot** and **Player vs Player** modes. Each player secretly places 3 **mines** in addition to the standard fleet — triggering a mine punishes the attacking player and hits 3 random cells on their board.

---

## Group members

- Alexis Flueckiger - 22870 - AndreasFlueckiger
- Matteo Fina - - MatteoFindus
- Marco Meneghetti - 23931 - SirMurkusIT

---

## Requirements

- **Java 21** (or any JDK starting from version 17)
- **Maven 3.6+**

Verify in bash using:

java -version
mvn -version

From the project root (the folder containing pom.xml):

in bash write and use

mvn package
This compiles all sources and produces a self-contained über-jar at:

target/battleship-3.0.jar
The über-jar bundles all dependencies so no external classpath is needed.

Run the following in bash to start the project, the game window will open

java -jar target/battleship-3.0.jar

---

## Description in detail
Battleship War is a turn-based naval combat game for one or two players.

Player vs Bot — one human faces a computer opponent that uses a hunt-and-target strategy: it fires at a sparse checkerboard pattern until it scores a hit, then methodically attacks orthogonal neighbours until the ship sinks, before returning to hunt mode.

Player vs Player (local) — two humans share the same computer. After every shot, a "pass the computer" privacy screen hides both boards until the next player confirms they are ready, preventing accidental peeking and cheating.

Mine mechanic — during setup each player secretly plants 3 mines on empty water cells on their own board. When the opponent shoots a mined cell, a MINE_TRIGGER result fires: the attacking player gets hit on 3 random tiles as a punishment for hitting the hidden Mine.

---

## Implementation overview

High-level components
**Board**  — owns the 10×10 Cell grid, the Ship list, and all mine positions. Exposes placeShip, placeMine, shoot, allShipsSunk, and query helpers. This is the main interface between the model and the UI.

**BotAI** — consults Board.wasShotAt and calls Board.shoot internally to decide and execute each bot turn. It maintains its own hunt queue and target queue; the UI only calls BotAI.takeTurn() and reads back the chosen coordinates.

**GameFrame** — the top-level game window. It owns references to both Board objects and the optional BotAI, and delegates all grid rendering and shot processing to private helper methods. It does not contain any game logic beyond calling Board.shoot and reading the returned ShotResult.

**SetupFrame** — drives the interactive fleet/mine placement phase. It writes directly into a Board via Board.placeShip and Board.placeMine, then passes the completed Board to GameFrame.

**Theme** — a stateless utility class. Every colour constant, font, and Swing component factory lives here. No other class contains hard-coded colour or font values.

**MenuFrame** — the splash and mode-selection screens. It constructs a SetupFrame with the chosen GameMode when the player clicks SELECT.

## Third-party libraries
No third-party libraries are used. The project uses only the Java 21 standard library (Java Swing / AWT for the UI, java.util and Random).

## Notable programming techniques
**Enum branching (GameMode, ShotResult)** — using enums instead of boolean flags or integer constants makes every branch in GameFrame and SetupFrame self-documenting and exhaustive.

**Factory-method design system (Theme)** — all Swing component creation goes through static factory methods (Theme.button, Theme.cellButton, Theme.label, etc.). Adding a hover effect to every button in one place means a visual change requires editing exactly one method rather than touching every screen class.

**Timer-based bot delay** — the bot's shot is triggered via javax.swing.Timer with a 500 ms delay rather than Thread.sleep, keeping the Event Dispatch Thread free and the UI fully responsive during the pause.

**Hunt/target AI with checkerboard seeding** — BotAI pre-builds a shuffled list of every cell where (row + col) % 2 == 0. Because every ship of length ≥ 2 must cover at least one such cell, this halves the expected number of hunt shots compared to a fully random approach.

**Pattern matching in switch expressions (Java 21)** — ShotResult cases are handled with Java 21 switch expressions throughout GameFrame, making the result-to-visual mapping compact and exhaustive without nested if/else chains.

---

## Workload distribution


**Game model (Board, Ship, Cell, ShotResult, GameMode)** - Alexis, Matteo, Marco
**Bot AI (BotAI)** -	Alexis, Matteo
**Main menu and setup UI (MenuFrame, SetupFrame)** -	Alexis, Matteo, Marco
**Game screen UI (GameFrame)** -	Alexis, Matteo, Marco
**Design system (Theme)** -	Alexis, Matteo, Marco
**Integration, testing, and README** -	Alexis, Matteo, Marco

---

## How git was used
We used GitHub at the end of the project for distribution purposes, because of the problems we had last year in regards to communication. We worked on VS Code. Then we uploaded all the work to GitHub to test it on different platforms and to share it with the professor.

## Challenges
The main challenge as a group was the organisation and work distribution because we don't live in the same city and we are currently in different years.

Alexis — Setting up the frontend/setting up the UI.

Matteo — Setting up and developing the bot AI logic.

Marco — Making sure that the player's ships and board are difficult to be seen by the other player.

---

## External references
No external libraries, tutorials, or third-party source code were used. 
