import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.Random;
import javalib.worldimages.*;

/*
README
please uncomment testBigBang at the very end before running in order to play the game.
if on Mac, configure your right-click to be bottom right in settings (flagging won't
work otherwise)
*/

// to represent a single cell.
class Cell {
  ArrayList<Cell> neighbors; // 2 <= neighbors.size() <= 4
  boolean hasMine;
  boolean isRevealed;
  boolean isFlagged;
  int neighboringMines;

  Cell(boolean hasMine) {
    this.hasMine = hasMine;
    this.neighbors = new ArrayList<>();
    this.isRevealed = false;
    this.isFlagged = false;
    this.neighboringMines = 0;
  }

  // EFFECT: adds a neighbor to this cell, given this cell doesn't already have 8 neighbors.
  void addNeighbor(Cell neighbor) {
    if (this.neighbors.size() <= 8) {
      this.neighbors.add(neighbor);
    } else {
      throw new UnsupportedOperationException("Cells can have up to 8 neighbors.");
    }
  }

  // implements the flood feature for the neighbors of this cell
  void flood() {
    for (int i = 1; i < this.neighbors.size(); i++) {
      if (!this.neighbors.get(i).isRevealed
              && !this.neighbors.get(i).hasMine && !this.neighbors.get(i).isFlagged
              && this.neighbors.get(i - 1).neighboringMines == 0) {
        this.neighbors.get(i).isRevealed = true;
      }
    }
  }
}

// represent a minesweeper world.
class MSWorld extends World {
  int columns;
  int rows;
  int mines;
  ArrayList<ArrayList<Cell>> board;
  static final int CELL_SIZE = 30;

  // constructor that checks all values are positive and the # of mines is less than the
  // total # of cells. throws an IllegalArgumentException otherwise.
  MSWorld(int columns, int rows, int mines) {
    if (new Utils().overZero(columns, rows, mines,
        "Must have at least 1 row, column, and mine.")
            && new Utils().notAbove(columns, rows, mines,
        "Number of mines cannot be greater than number of cells.")) {
      this.columns = columns;
      this.rows = rows;
      this.mines = mines;
    }
    initGrid();
  }

  // EFFECT: add rows to this board of new, empty cells.
  void initGrid() {
    this.board = new ArrayList<>();
    for (int i = 0; i < this.rows; i++) {
      ArrayList<Cell> row = new ArrayList<>();
      for (int j = 0; j < this.columns; j++) {
        row.add(new Cell(false)); //initially no cells have mines
      }
      this.board.add(row);
    }
    linkCells();
    placeMines();
  }

  // EFFECT: place mines randomly on the board. ensures no mine is placed on one cell twice.
  void placeMines() {
    Random rand = new Random();
    int minesPlaced = 0;
    while (minesPlaced < this.mines) {
      int randRow = rand.nextInt(this.rows);
      int randCol = rand.nextInt(this.columns);
      Cell cell = this.board.get(randRow).get(randCol);
      if (!cell.hasMine) {
        cell.hasMine = true;
        minesPlaced++;
      }
    }
  }

  // EFFECT: place mines randomly on the board. ensures no mine is placed on one cell twice.
  // SEEDED RANDOM FOR TESTING
  void placeMinesForTesting(Random rand) {
    int minesPlaced = 0;
    while (minesPlaced < this.mines) {
      int randRow = rand.nextInt(this.rows);
      int randCol = rand.nextInt(this.columns);
      Cell cell = this.board.get(randRow).get(randCol);
      if (!cell.hasMine) {
        cell.hasMine = true;
        minesPlaced++;
      }
    }
  }

  //EFFECT: links all the cells on the board.
  void linkCells() {
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        // corners
        // top left
        if (i == 0 && j == 0) {
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j + 1)); // to the right
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j)); // down
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j + 1)); // right down
        }
        // top right
        else if (i == 0 && j == this.columns - 1) {
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j - 1)); // to the left
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j)); // down
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j - 1)); // left down
        }
        // bottom right
        else if (i == this.rows - 1 && j == this.columns - 1) {
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j - 1)); // to the left
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j)); // up
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j - 1)); // left up
        }
        // bottom left
        else if (i == this.rows - 1 && j == 0) {
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j + 1)); // to the right
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j)); // up
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j + 1)); // right up
        }
        // border cells
        // top row
        else if (i == 0) {
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j - 1)); // left
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j + 1)); // right
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j)); // down
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j + 1)); // right down
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j - 1)); // left down
        }
        // bottom row
        else if (i == this.rows - 1) {
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j - 1)); // left
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j + 1)); // right
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j)); // up
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j + 1)); // right up
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j - 1)); // left up
        }
        // left side
        else if (j == 0) {
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j + 1)); // right
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j)); // up
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j)); // down
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j + 1)); // right up
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j + 1)); // right down
        }
        // right side
        else if (j == this.columns - 1) {
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j - 1)); // left
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j)); // up
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j)); // down
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j - 1)); // left up
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j - 1)); // left down
        }
        // non-border, non-corner cells
        else {
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j - 1)); // left
          this.board.get(i).get(j).addNeighbor(this.board.get(i).get(j + 1)); // right
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j)); // up
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j)); // down
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j - 1)); // left down
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j - 1)); // left up
          this.board.get(i).get(j).addNeighbor(this.board.get(i - 1).get(j + 1)); // right up
          this.board.get(i).get(j).addNeighbor(this.board.get(i + 1).get(j + 1)); // right down
        }
      }
    }

  }

  // check mouse clicks
  @Override
  public void onMouseClicked(Posn pos, String button) {
    if (!this.gameOver()) {
      int row = pos.y / CELL_SIZE;
      int col = pos.x / CELL_SIZE;
      if (button.equals("RightButton")) {
        flagCell(row, col);
      } else if (button.equals("LeftButton")) {
        revealCell(row, col);
      }
      checkWinLoss();
    }
  }

  // reveals a cell
  void revealCell(int row, int col) {
    Cell cell = this.board.get(row).get(col);
    if (!cell.isFlagged && !cell.isRevealed) {
      cell.isRevealed = true;
      if (!cell.hasMine) {
        if (this.countAdjacentMines(row, col) == 0) {
          for (Cell neighbor : cell.neighbors) {
            neighbor.flood();
          }
        }
      }
    }
  }

  //EFFECT: Flags or unflags a cell
  void flagCell(int row, int col) {
    Cell cell = this.board.get(row).get(col);
    if (!cell.isRevealed) {
      cell.isFlagged = !cell.isFlagged;
    }
  }

  //EFFECT: Checks for win/loss conditions and ends the game accordingly
  void checkWinLoss() {
    if (this.gameWon()) {
      this.endOfWorld("You win!");
    } else if (this.gameLost()) {
      this.endOfWorld("Game over! You hit a mine.");
    }
  }

  //Determines if the game is won
  boolean gameWon() {
    for (ArrayList<Cell> row : this.board) {
      for (Cell cell : row) {
        if (!cell.hasMine && !cell.isRevealed) {
          return false;
        }
      }
    }
    return true;
  }

  // Determines if the game is lost
  boolean gameLost() {
    for (ArrayList<Cell> row : this.board) {
      for (Cell cell : row) {
        if (cell.hasMine && cell.isRevealed) {
          return true;
        }
      }
    }
    this.makeScene();
    return false;
  }

  //Determines if the game is over (either won or lost)
  boolean gameOver() {
    return this.gameWon() || this.gameLost();
  }

  //Counts the number of adjacent mines to a cell
  int countAdjacentMines(int row, int col) {
    int count = 0;
    for (Cell neighbor : this.board.get(row).get(col).neighbors) {
      if (neighbor.hasMine) {
        count++;
      }
    }
    this.board.get(row).get(col).neighboringMines = count;
    return count;
  }

  //Counts the number of adjacent mines to a cell
  int countAdjacentMines(Cell cell) {
    int count = 0;
    for (Cell neighbor : cell.neighbors) {
      if (neighbor.hasMine) {
        count++;
      }
    }
    cell.neighboringMines = count;
    return count;
  }

  //to create the WorldScene.
  @Override
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(columns * CELL_SIZE, rows * CELL_SIZE);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        Cell cell = board.get(i).get(j);
        WorldImage cellImage = drawCell(cell);
        scene.placeImageXY(cellImage,
            j * CELL_SIZE + CELL_SIZE / 2, i * CELL_SIZE + CELL_SIZE / 2);
      }
    }
    return scene;
  }

  // EFFECT: draws a cell and places it on the initial image.
  WorldImage drawCell(Cell cell) {
    WorldImage cellImage;
    if (cell.hasMine && cell.isRevealed && !cell.isFlagged) {
      cellImage = new OverlayImage(
          new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.OUTLINE, Color.BLACK),
          new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, Color.RED));
    } else if (!cell.hasMine && cell.isRevealed && !cell.isFlagged) {
      cellImage = new OverlayImage(
          new TextImage(this.countAdjacentMines(cell) + "", Color.WHITE),
          new OverlayImage(
          new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.OUTLINE, Color.BLACK),
          new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, Color.BLUE)));
    } else if (cell.isFlagged && !cell.isRevealed) {
      cellImage = new OverlayImage(
          new TextImage("▲", Color.ORANGE),
          new OverlayImage(
              new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.OUTLINE, Color.BLACK),
              new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, Color.LIGHT_GRAY)));
    } else {
      cellImage = new OverlayImage(
          new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.OUTLINE, Color.BLACK),
          new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, Color.LIGHT_GRAY));
    }
    return cellImage;
  }

  // to create the ending scene for the game
  public WorldScene lastScene(String msg) {
    WorldScene scene = new WorldScene(columns * CELL_SIZE, rows * CELL_SIZE);
    scene.placeImageXY(
        new TextImage(msg, Color.BLUE), scene.width / 2, scene.height / 2);
    return scene;
  }

}

// hold utility methods.
class Utils {
  Utils() {}

  // returns true if all args > 0. throws exception with given message otherwise.
  boolean overZero(int a, int b, int c, String msg) {
    if (a > 0 && b > 0 && c > 0) {
      return true;
    }
    throw new IllegalArgumentException(msg);
  }

  // returns true if m > r * c. throws exception with given message otherwise.
  boolean notAbove(int c, int r, int m, String msg) {
    if (m < r * c) {
      return true;
    }
    throw new IllegalArgumentException(msg);
  }
}

//examples and tests
class ExamplesMinesweeper {
  ExamplesMinesweeper(){}

  Utils util = new Utils();
  MSWorld testWorld;

  // initialize the world
  void init() {
    testWorld = new MSWorld(30, 16, 10);
  }

  // to test the MSWorld constructor and ensure it throws the correct exceptions.
  void testMSWorldConstructor(Tester t) {
    init();
    t.checkConstructorException(
        new IllegalArgumentException("Must have at least 1 row, column, and mine."),
        "MSWorld", 10, 20, 0);
    t.checkConstructorException(
        new IllegalArgumentException("Must have at least 1 row, column, and mine."),
        "MSWorld", 0, 20, 10);
    t.checkConstructorException(
        new IllegalArgumentException("Must have at least 1 row, column, and mine."),
        "MSWorld", 10, -5, 15);
    t.checkConstructorException(
        new IllegalArgumentException("Number of mines cannot be greater than number of cells."),
        "MSWorld", 10, 20, 300);
  }

  // tests utils class methods + exceptions
  void testUtils(Tester t) {
    t.checkException(new IllegalArgumentException("bad"), util, "overZero", 0, 0, 1, "bad");
    t.checkException(new IllegalArgumentException("bad"), util, "notAbove", 1, 1, 20, "bad");
    t.checkExpect(util.overZero(1, 2, 3, "bad"), true);
    t.checkExpect(util.overZero(3000, 1, 9, "bad"), true);
    t.checkExpect(util.notAbove(1, 1, 0, "bad"), true);
    t.checkExpect(util.notAbove(10, 20, 199, "bad"), true);
  }

  //test addNeighbor
  void testAddNeighbor(Tester t) {
    Cell cell = new Cell(false);
    t.checkExpect(cell.neighbors.size(), 0);
    cell.addNeighbor(new Cell(false));
    t.checkExpect(cell.neighbors.size(), 1);
    cell.addNeighbor(new Cell(false));
    t.checkExpect(cell.neighbors.size(), 2);
  }

  // to test the initGrid method
  void testInitGrid(Tester t) {
    MSWorld newWorld = new MSWorld(2,4,2);
    newWorld.initGrid();
    t.checkExpect(newWorld.board.size(), 4);
    t.checkExpect(newWorld.board.get(0).size(), 2);
    newWorld.columns = 10;
    newWorld.initGrid();
    t.checkExpect(newWorld.board.size(), 4);
    t.checkExpect(newWorld.board.get(0).size(), 10);
    newWorld.rows = 12;
    t.checkExpect(newWorld.board.size(), 4); // didn't call yet...
    t.checkExpect(newWorld.board.get(0).size(), 10);
    newWorld.initGrid();
    t.checkExpect(newWorld.board.size(), 12);
  }

  // to test the link method
  void testLink(Tester t) {
    init();
    MSWorld newWorld = new MSWorld(2, 2, 1);
    // in a 2x2 board all cells should be each others neighbors, so each has 3.
    t.checkExpect(newWorld.board.get(0).get(0).neighbors.size(), 3);
    t.checkExpect(newWorld.board.get(0).get(1).neighbors.size(), 3);
    t.checkExpect(newWorld.board.get(1).get(0).neighbors.size(), 3);
    t.checkExpect(newWorld.board.get(1).get(1).neighbors.size(), 3);

    // bigger board
    t.checkExpect(testWorld.board.get(0).get(0).neighbors.size(), 3); // corner
    t.checkExpect(testWorld.board.get(0).get(1).neighbors.size(), 5); // side
    t.checkExpect(testWorld.board.get(10).get(1).neighbors.size(), 8); // middle
  }

  //test the drawCell method
  void testDrawCell(Tester t) {
    init();
    MSWorld newWorld = new MSWorld(2, 2, 1);
    Cell cell1 = new Cell(false);
    Cell cell2 = new Cell(true);
    cell2.isRevealed = true;

    WorldImage cellImage1 = newWorld.drawCell(cell1);
    t.checkExpect(cellImage1, new OverlayImage(
        new RectangleImage(MSWorld.CELL_SIZE, MSWorld.CELL_SIZE,
            OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(MSWorld.CELL_SIZE, MSWorld.CELL_SIZE,
            OutlineMode.SOLID, Color.LIGHT_GRAY)));

    WorldImage cellImage2 = newWorld.drawCell(cell2);
    t.checkExpect(cellImage2, new OverlayImage(
        new RectangleImage(MSWorld.CELL_SIZE, MSWorld.CELL_SIZE,
            OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(MSWorld.CELL_SIZE, MSWorld.CELL_SIZE,
            OutlineMode.SOLID, Color.RED)));
  }

  // to test the placeMines method
  void testPlaceMines(Tester t) {
    init();
    MSWorld newWorld = new MSWorld(2,2,1);
    Random rand = new Random(1);
    newWorld.mines = rand.nextInt(2);
    System.out.println(newWorld.mines);
    newWorld.placeMinesForTesting(rand);
    //t.checkExpect(newWorld.board.get(1).get(1).hasMine, false);
  }

  //Test the makeScene method
  void testMakeScene(Tester t) {
    init();

    MSWorld testWorld = new MSWorld(3, 2, 1);

    testWorld.board.get(0).get(0).hasMine = false;
    testWorld.board.get(0).get(1).hasMine = false;
    testWorld.board.get(0).get(2).hasMine = false;
    testWorld.board.get(1).get(0).hasMine = false;
    testWorld.board.get(1).get(1).hasMine = false;
    testWorld.board.get(1).get(2).hasMine = false;

    WorldScene expectedScene = new WorldScene(90, 60);
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 3; j++) {
        Cell cell = this.testWorld.board.get(i).get(j);
        WorldImage cellImage = this.testWorld.drawCell(cell);
        expectedScene.placeImageXY(cellImage,
            j * 30 + 30 / 2, i * 30 + 30 / 2);
      }
    }
    WorldScene actualScene = testWorld.makeScene();
    t.checkExpect(actualScene, expectedScene);
  }

  //Test the flood method in the Cell class
  void testFlood(Tester t) {
    init();
    testWorld.board = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      ArrayList<Cell> row = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        row.add(new Cell(false));
      }
      testWorld.board.add(row);
    }

    testWorld.board.get(1).get(1).isRevealed = true;
    testWorld.board.get(1).get(1).isFlagged = false;
    testWorld.board.get(0).get(0).isRevealed = true;
    testWorld.board.get(0).get(0).isFlagged = false;
    testWorld.board.get(0).get(1).isRevealed = true;
    testWorld.board.get(0).get(1).isFlagged = false;
    testWorld.board.get(0).get(2).isRevealed = true;
    testWorld.board.get(0).get(2).isFlagged = false;
    testWorld.board.get(1).get(0).isRevealed = true;
    testWorld.board.get(1).get(0).isFlagged = false;
    testWorld.board.get(1).get(2).isRevealed = true;
    testWorld.board.get(1).get(2).isFlagged = false;
    testWorld.board.get(2).get(0).isRevealed = true;
    testWorld.board.get(2).get(0).isFlagged = false;
    testWorld.board.get(2).get(1).isRevealed = true;
    testWorld.board.get(2).get(1).isFlagged = false;
    testWorld.board.get(2).get(2).isRevealed = true;
    testWorld.board.get(2).get(2).isFlagged = false;


    testWorld.board.get(1).get(1).flood();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        Cell cell = testWorld.board.get(i).get(j);
        if (cell != testWorld.board.get(1).get(1)) {
          t.checkExpect(cell.isRevealed, true);
        }
      }
    }
  }

  //test the onMouseClicked method
  void testOnMouseClicked(Tester t) {
    init();

    testWorld.onMouseClicked(new Posn(0, 0), "left");
    t.checkExpect(testWorld.board.get(0).get(0).isFlagged, false);

    testWorld.onMouseClicked(new Posn(2, 2), "right");
    t.checkExpect(testWorld.board.get(2).get(2).isFlagged, false);

    testWorld.board.get(3).get(3).isRevealed = true;
    testWorld.onMouseClicked(new Posn(3, 3), "left");
    t.checkExpect(testWorld.board.get(3).get(3).isFlagged, false); // Cell should not be flagged
  }

  // to test the revealCell method
  void testRevealCell(Tester t) {
    init();
    testWorld.board = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      ArrayList<Cell> row = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        row.add(new Cell(false));
      }
      testWorld.board.add(row);
    }

    testWorld.board.get(1).get(1).hasMine = true;
    testWorld.revealCell(1, 1);
    t.checkExpect(testWorld.gameOver(), true);

    testWorld.revealCell(0, 2);
    t.checkExpect(testWorld.board.get(0).get(2).isRevealed, true);
  }

  // to test the flagCell method
  void testFlagCell(Tester t) {
    init();
    testWorld = new MSWorld(2,2,1);
    testWorld.board.get(0).get(0).hasMine = false;
    testWorld.flagCell(0,0);
    t.checkExpect(testWorld.board.get(0).get(0).isFlagged, true);
    testWorld.flagCell(0,0);
    t.checkExpect(testWorld.board.get(0).get(0).isFlagged, false);
  }

  // to test the gameWon method
  void testGameWon(Tester t) {
    init();
    testWorld = new MSWorld(2,2,1);
    for (ArrayList<Cell> arr : testWorld.board) {
      for (Cell c : arr) {
        c.hasMine = false;
        c.isRevealed = true;
      }
    }
    t.checkExpect(testWorld.gameWon(), true);
    for (ArrayList<Cell> arr : testWorld.board) {
      for (Cell c : arr) {
        c.hasMine = false;
        c.isRevealed = false;
      }
    }
    t.checkExpect(testWorld.gameWon(), false);
  }

  // to test the gameLost method
  void testGameLost(Tester t) {
    init();
    t.checkExpect(testWorld.gameLost(), false);
    testWorld.board.get(0).get(0).hasMine = true;
    testWorld.board.get(0).get(0).isRevealed = true;
    t.checkExpect(testWorld.gameLost(), true);
  }

  // to test the gameOver method
  void testGameOver(Tester t) {
    init();
    t.checkExpect(testWorld.gameOver(), false);
    testWorld.board.get(0).get(0).hasMine = true;
    testWorld.board.get(0).get(0).isRevealed = true;
    t.checkExpect(testWorld.gameOver(), true);
    init();
    t.checkExpect(testWorld.gameOver(), false);
    for (ArrayList<Cell> arr : testWorld.board) {
      for (Cell c : arr) {
        c.hasMine = false;
        c.isRevealed = true;
      }
    }
    t.checkExpect(testWorld.gameOver(), true);
    testWorld.board.get(0).get(0).isRevealed = false;
    t.checkExpect(testWorld.gameOver(), false);
  }

  // to test the countAdjacentMines method
  void testCountAdjMines(Tester t) {
    init();
    testWorld = new MSWorld(2,2,1);
    for (ArrayList<Cell> arr : testWorld.board) {
      for (Cell c : arr) {
        c.hasMine = false;
      }
    }
    testWorld.board.get(0).get(0).hasMine = true;
    testWorld.countAdjacentMines(testWorld.board.get(0).get(1));
    t.checkExpect(testWorld.board.get(0).get(0).neighboringMines, 0);
    t.checkExpect(testWorld.board.get(0).get(1).neighboringMines, 1);
    testWorld.board.get(0).get(1).hasMine = true;
    testWorld.countAdjacentMines(testWorld.board.get(1).get(0));
    t.checkExpect(testWorld.board.get(1).get(0).neighboringMines, 2);
  }

  // to test the lastScene method
  void testLastScene(Tester t) {
    init();
    WorldScene scene = new WorldScene(MSWorld.CELL_SIZE * testWorld.columns,
        MSWorld.CELL_SIZE * testWorld.rows);
    scene.placeImageXY(
        new TextImage("You win!", Color.BLUE), scene.width / 2, scene.height / 2);
    t.checkExpect(testWorld.lastScene("You win!"), scene);
    scene = new WorldScene(MSWorld.CELL_SIZE * testWorld.columns,
        MSWorld.CELL_SIZE * testWorld.rows);
    scene.placeImageXY(
        new TextImage("You lose.", Color.BLUE), scene.width / 2, scene.height / 2);
    t.checkExpect(testWorld.lastScene("You lose."), scene);
  }

  // not rly a test: just to see what the board looks like. uncomment to run.

  void testBigBang(Tester t) {
    this.init();
    MSWorld world1 = new MSWorld(16,16,50);
    int worldWidth = MSWorld.CELL_SIZE * world1.columns + 10;
    int worldHeight = MSWorld.CELL_SIZE * world1.rows + 10;
    double tickRate = 0.1;
    world1.bigBang(worldWidth, worldHeight, tickRate);
  }


}