import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PowerUp extends Control {
    // Constants
    public static final int DIAMETER = 20;
    private static final double SPEED = 3.5; // Speed of the power-up
    private Random random = new Random();
    private int timeFreeze = random.nextInt(25) + 1;
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;


    // Constructor
    public PowerUp(int x, int y) {
        super(x, y, 0, 0, DIAMETER, DIAMETER); // Call to the parent constructor with parameters
    }

    // Draw the power-up
    public void draw(Graphics g) {
        g.setColor(new Color(0, 206, 209)); // teal
        g.fillOval(x, y, DIAMETER, DIAMETER);
    }

    // Move the power-up downwards
    public void move() {
        y += SPEED; // Move the power-up down by increasing its y coordinate
    
        // If the power-up has moved off the bottom of the screen, reset its position to the top
        if (y > FRAME_HEIGHT) {
            y = 0;
            x = (int) (Math.random() * FRAME_WIDTH); // Generate a new random x coordinate
        }
    }

    public void moveDown(int bottomBound, int gameWidth) {
        y += SPEED;
        if (y > bottomBound) {
            y = 0;
            x = random.nextInt(gameWidth - DIAMETER); // Generate a new x position
        }
    }

    public static PowerUp checkSpawn(int score, boolean gameJustStarted, boolean powerUpExists) {
        // Spawn a power-up only if the player's score is a multiple of 50, the game has not just started, and there is no existing power-up
        if (score % 75 == 0 && score != 0 && !gameJustStarted && !powerUpExists) {
            Random random = new Random();
            int x = random.nextInt(Background.getFrameWidth());
            int y = random.nextInt(Background.getFrameHeight());
            return new PowerUp(x, y);
        }
        return null;
    }

        public boolean isCollidingWithPlayer(Player player) {
        if (getBounds().intersects(player.getBounds())) {
            player.stopMoving();
            
            // Move the player to the top of the JFrame
            player.setY(0);

            // Slow down the player's fall
player.slowFall();

            Timer timer = new Timer(5000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    player.stopMoving();
                }
            });
            timer.setRepeats(false);
            timer.start();

                        return true;
        }
        return false;
    }

        public void collideWithPaddles(ArrayList<Paddle> paddles) {
        for (Paddle paddle : paddles) {
            if (getBounds().intersects(paddle.getBounds())) {
                paddle.speedUp();
                Timer timer = new Timer(5000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        paddle.resetSpeed();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    // Get the bounding circle of the PowerUp
    public Rectangle getBounds() {
        return new Ellipse2D.Double(x, y, DIAMETER, DIAMETER).getBounds();
    }

    // Getters and setters
    public int getTimeFreeze() {
        return this.timeFreeze;
    }

    public void setTimeFreeze(int timeFreeze) {
        this.timeFreeze = timeFreeze;
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }
}
