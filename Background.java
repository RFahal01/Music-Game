import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

public class Background extends JComponent {
    // Constants for the width and height of the game area
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    
    // The player object
    private Player player;
    // List of all paddles in the game
    public static ArrayList<Paddle> paddleList;
    // Label to display the player's score
    private JLabel scores;

    // Timer for game updates
    private Timer timer;
    // Flag to indicate if the game is over
    private boolean gameOver;

    // RGB values to adjust background color as game progresses
    private int red = 245;
    private int green = 245; //Default RGB for beige color
    private int blue = 220;

    // List of power-ups
     private ArrayList<PowerUp> powerUps = new ArrayList<>();

    // Constructor
    public Background(JLabel scores) {
        this.scores = scores;
    
        // Initialize player and paddles
        player = new Player(WIDTH / 2 - Player.SIZE / 2, 0, 0, 1);
        paddleList = new ArrayList<>();
        initializePaddles();

        // Start the game update timer
        timer = new Timer(10, e -> tick());
        timer.start();
    }

    // Returns the list of paddles
    public ArrayList<Paddle> getPaddleList() {
        return paddleList;
    }

    // Initialize paddles at random positions
    public void initializePaddles() {
        Random random = new Random();
        int numPaddles = 15; 
        int spacing = HEIGHT / (numPaddles + 1); // calculate the spacing between the paddles
     
        for (int i = 0; i < numPaddles; i++) {
            int y = spacing * (i + 1) - Paddle.HEIGHT / 2; // calculate the y-coordinate of the paddle
            int x = random.nextInt(WIDTH - Paddle.WIDTH); // generate a random x-coordinate
            Paddle paddle = new Paddle(x, y);
            paddleList.add(paddle);
        }
    }

    // Game logic
    void tick() {
        player.move();
        for (Paddle p : paddleList) {
            p.moveDown(HEIGHT, WIDTH); // move the paddle downwards
            if (player.isCollidingWithPaddle(p)) {
                player.bounce(); // make the player bounce if it lands on a paddle
                player.increaseScore(); // increase the player's score
            }
        }
        
        repaint();

        scores.setText("Score " + player.getScore());
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(new Color(red,green,blue)); // Light Beige
        g.fillRect(0, 0, WIDTH, HEIGHT);
    
        super.paintComponent(g);
        player.draw(g);

        for (Paddle p : paddleList) {
            p.draw(g);
        }
        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g);
        }
    }

    // Reset game state
    public void reset() {
        // Reset player
        player = new Player(WIDTH / 2 - Player.SIZE / 2, 0, 0, 1);
        
        // Reset paddles
        paddleList.clear();
        initializePaddles(); // Reinitialize paddles
        
        // Reset power-ups
        resetPowerUps(); // Clear power-ups
        
        // Reset color
        resetColor();
        
        // Reset score
        scores.setText("Score 0");
        
        // Reset game over flag
        gameOver = false;
        
        // Repaint the JFrame
        repaint(); 

        Paddle.firstPaddle(player);
    }

    // Get the player object
    public Player getPlayer() {
        return player;
    }

    // Pause game
    public void pauseGame() {
        timer.stop();
    }

    // Resume game
    public void resumeGame() {
        timer.start();
    }
    
    // Check if the game is over
    public boolean isGameOver() {
        return gameOver;
    }

    // Set the game over state
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public ArrayList<PowerUp> getPowerUps() {
        return this.powerUps;
    }

    public void setPowerUps(ArrayList<PowerUp> powerUps) {
        this.powerUps = powerUps;
    }
    
    public static int getFrameWidth(){
        return WIDTH;
    }

    public static int getFrameHeight(){
        return HEIGHT;
    }

    // In the Background class
    public Paddle getPaddleCollidingWithPlayer(Player player) {
    for (Paddle paddle : getPaddleList()) {
        if (player.isCollidingWithPaddle(paddle)) {
            return paddle;
        }
    }
    return null;
    }

    public void setColors(int r, int g, int b){
        red = r;
        green = g;
        blue = b;
    }

    public void resetColor(){
        red = 245;
        green = 245; //Default RGB for beige color
        blue = 220;
    }
    public void resetPowerUps() {
        powerUps.clear();
        repaint();
    }   
}