/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.slidingpuzzle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// We are going to create a Sliding Puzzle game 
public class Slidingpuzzle extends JPanel { // our grid will be drawn in a dedicated Panel
  
  // Size of our Sliding Puzzle Game
  private int size;
  // Number of tiles
  private int nbTiles;
  // Grid UI Dimension
  private int dimension;
  // Foreground Color
  private static final Color FOREGROUND_COLOR = new Color(239, 83, 80); // we use arbitrary color
  // Random object to shuffle tiles
  private static final Random RANDOM = new Random();
  // Storing the tiles in a 1D Array of integers
  private int[] tiles;
  // Size of tile on UI
  private int tileSize;
  // Position of the blank tile
  private int blankPos;
  // Margin for the grid on the frame
  private int margin;
  // Grid UI Size
  private int gridSize;
  private boolean gameOver; // true if game over, false otherwise
  private Image image;
  
  public Slidingpuzzle(int size, int dim, int mar) {
    this.size = size;
    dimension = dim;
    margin = mar;
    
    // init tiles 
    nbTiles = size * size - 1; // -1 because we don't count blank tile
    tiles = new int[size * size];
    
    // calculate grid size and tile size
    gridSize = (dim - 2 * margin);
    tileSize = gridSize / size;
    
    setPreferredSize(new Dimension(dimension, dimension + margin));
    setBackground(Color.WHITE);
    setForeground(FOREGROUND_COLOR);
    setFont(new Font("SansSerif", Font.BOLD, 60));
    
    gameOver = true;
    
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        // used to let users interact on the grid by clicking
        // it's time to implement interaction with users to move tiles to solve the game 
        if (gameOver) {
          newGame();
        } else {
          // get position of the click
          int ex = e.getX() - margin;
          int ey = e.getY() - margin;
          
          // click in the grid 
          if (ex < 0 || ex > gridSize  || ey < 0  || ey > gridSize)
            return;
          
          // gets the position in the grid
          int c1 = ex / tileSize;
          int r1 = ey / tileSize;
          
          // gets the position of the blank cell
          int c2 = blankPos % size;
          int r2 = blankPos / size;
          
          // we convert in the 1D coord 
          int clickPos = r1 * size + c1;
          
          int dir = 0;
          
          // we search the direction for multiple tiles to move at once
          if (c1 == c2  &&  Math.abs(r1 - r2) > 0)
            dir = (r1 - r2) > 0 ? size : -size;
          else if (r1 == r2 && Math.abs(c1 - c2) > 0)
            dir = (c1 - c2) > 0 ? 1 : -1;
            
          if (dir != 0) {
            // moving tiles in the direction
            do {
              int newBlankPos = blankPos + dir;
              tiles[blankPos] = tiles[newBlankPos];
              blankPos = newBlankPos;
            } while(blankPos != clickPos);
            
            tiles[blankPos] = 0;
          }
          
          // to check if the game is solved
          gameOver = isSolved();
        }
        
        // we repaint panel
        repaint();
      }
    });
    
    newGame();
  }
  
  private void newGame() {
    do {
      reset(); // resets game to its intial state
      shuffle(); // shuffles tiles
    } while(!isSolvable()); // make it until grid be solvable
    
    gameOver = false;
  }
  
  private void reset() {
    for (int i = 0; i < tiles.length; i++) {
      tiles[i] = (i + 1) % tiles.length;
    }
    
    // we set the blank cell at the last tile
    blankPos = tiles.length - 1;
  }
  
  private void shuffle() {
    // don't include the blank tile in the shuffle, leave in the solved position
    int n = nbTiles;
    
    while (n > 1) {
      int r = RANDOM.nextInt(n--);
      int tmp = tiles[r];
      tiles[r] = tiles[n];
      tiles[n] = tmp;
    }
  }
  
  // Only half permutations of the puzzle are solvable
  // Whenever a tile is preceded by a tile with higher value it counts
  // as an inversion. In our case, with the blank tile in the solved position,
  // the number of inversions must be even for the puzzle to be solvable
  private boolean isSolvable() {
    int countInversions = 0;
    
    for (int i = 0; i < nbTiles; i++) {
      for (int j = 0; j < i; j++) {
        if (tiles[j] > tiles[i])
          countInversions++;
      }
    }
    
    return countInversions % 2 == 0;
  }
  
  private boolean isSolved() {
    if (tiles[tiles.length - 1] != 0) // if blank tile is not in the solved position ==> not solved
      return false;
    
    for (int i = nbTiles - 1; i >= 0; i--) {
      if (tiles[i] != i + 1)
        return false;      
    }
    
    return true;
  }
  
  private void drawGrid(Graphics2D g) {
    for (int i = 0; i < tiles.length; i++) {
      // we convert 1D coords to 2D coords given the size of the 2D Array
      int r = i / size;
      int c = i % size;
      // we convert in coords on the UI
      int x = margin + c * tileSize;
      int y = margin + r * tileSize;
      
      // check special case for blank tile
      if(tiles[i] == 0) {
        if (gameOver) {
          g.setColor(FOREGROUND_COLOR);
          drawCenteredString(g, "\u2713", x, y);
        }
        
        continue;
      }
      
      // for other tiles
      g.setColor(getForeground());
      g.fillRect(x, y, tileSize, tileSize);
      g.setColor(Color.BLACK);
      g.drawRect(x, y, tileSize, tileSize);
      g.setColor(Color.WHITE);
      
      // The image URL - change to where your image file is located!
      String imageURL = "src/main/java/com/mycompany/slidingpuzzle/images/" + tiles[i] + ".jpg";
      
      // This call returns immediately and pixels are loaded in the background
      image = Toolkit.getDefaultToolkit().getImage(imageURL);
      g.drawImage(image, x, y, this);
      
      //drawCenteredString(g, String.valueOf(tiles[i]), x , y);
    }
  }
  
  private void drawStartMessage(Graphics2D g) {
    if (gameOver) {
      g.setFont(getFont().deriveFont(Font.BOLD, 18));
      g.setColor(FOREGROUND_COLOR);
      String s = "Click to start new game";
      g.drawString(s, (getWidth() - g.getFontMetrics().stringWidth(s)) / 2,
          getHeight() - margin);
    }
  }
  
  private void drawCenteredString(Graphics2D g, String s, int x, int y) {
    // center string s for the given tile (x,y)
    FontMetrics fm = g.getFontMetrics();
    int asc = fm.getAscent();
    int desc = fm.getDescent();
    g.drawString(s,  x + (tileSize - fm.stringWidth(s)) / 2, 
        y + (asc + (tileSize - (asc + desc)) / 2));
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2D = (Graphics2D) g;
    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    drawGrid(g2D);
    drawStartMessage(g2D);
  }
  
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setTitle("3x3 Sliding Puzzle");
      frame.setResizable(false);
      frame.add(new Slidingpuzzle(3, 550, 30), BorderLayout.CENTER);
      frame.pack();
      // center on the screen
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }

  
}