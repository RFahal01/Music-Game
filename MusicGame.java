import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class MusicGame implements Runnable {
    // Constants
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int TIMER_DELAY = 10;

    // Components
    private JFrame frame;
    private JLabel scores;
    private Background background;
    private Timer gameTimer;
    private boolean gamePaused = false;
    private int lastPowerUpScore = 0;
    private boolean powerUpExists = false;

    @Override
    public void run() {
        // Setup frame and components
        frame = createFrame();
        scores = new JLabel();
        background = new Background(scores);
        Player player = background.getPlayer();

        // Add key listener to frame
        addKeyListenerToFrame();
        Paddle.firstPaddle(player);

        // Add components to frame
        frame.add(background, BorderLayout.CENTER);
        frame.add(createMenu(), BorderLayout.NORTH);

        // Start game timer
        gameTimer = createGameTimer();
        gameTimer.start();

        // Display frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

    private JFrame createFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setResizable(false);
        return frame;
    }

    private void addKeyListenerToFrame() {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Player player = background.getPlayer();
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    player.moveLeft();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    player.moveRight();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    background.getPlayer().stopMoving();
                }
            }
        });
    }

    private JPanel createMenu() {
        JPanel panel = new JPanel();
        panel.add(createButton("Start", e -> gameResume()));
        panel.add(createButton("Pause", e -> gamePause()));
        panel.add(createButton("New Game", e -> background.reset()));
        panel.add(scores);
        return panel;
    }

    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        button.setFocusable(false);
        return button;
    }

    private void gamePause() {
        background.pauseGame();
        gameTimer.stop();
        gamePaused = true;
    }

    private void gameResume() {
        background.resumeGame();
        gameTimer.start();
        gamePaused = false;
    }

    private Timer createGameTimer() {
        return new Timer(TIMER_DELAY, e -> {
            Player player = background.getPlayer();
            int score = player.getScore();
            boolean gameJustStarted = score == 0;

            if (!gamePaused) {
                player.applyGravity();
                player.move();

                // Check collisions with paddles
                for (Paddle paddle : background.getPaddleList()) {
                    if (player.isCollidingWithPaddle(paddle)) {
                        player.handleCollision();
                    }
                }

                // Check if player is out of bounds
                if (player.isOutOfBounds(frame.getHeight())) {
                    handleGameOver(player);
                }

                // Handle player going off screen
                if (player.wentoffscreenLeft()) {
                    player.setX(frame.getWidth());
                }
                if (player.wentoffscreenRight(frame.getWidth())) {
                    player.setX(0);
                }
                
                // When spawning a power-up
                PowerUp powerUp = PowerUp.checkSpawn(score, gameJustStarted, powerUpExists);
                if (powerUp != null) {
                    powerUpExists = true;
                    background.getPowerUps().add(powerUp); // Add the power-up to the game
                }

                // Handle power-ups
                ArrayList<PowerUp> powerUps = background.getPowerUps();
                Iterator<PowerUp> iterator = powerUps.iterator();
                while (iterator.hasNext()) {
                    PowerUp currentPowerUp = iterator.next();
                    if (currentPowerUp.isCollidingWithPlayer(player)) {
                        player.addScore(15);
                        player.speedUp();
                        iterator.remove();
                        Player.playPowerUpSound();
                        powerUpExists = false; // Set powerUpExists to false when the power-up is collected
                    }
                    currentPowerUp.move();
                }

                // Update game state based on player score
                updateGameState(player);

                // Repaint background
                background.repaint();
            }
        });
    }

    private void handleGameOver(Player player) {
        // Stop game timer and play game over jingle
        gameTimer.stop();
        Player.gameOverJingle();

        // Show game over dialog
        int option = JOptionPane.showConfirmDialog(frame, "Game Over! Your score is: " + player.getScore() + ". Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            restart();
        } else {
            background.setGameOver(true);
            showEncouragementMessage(player.getScore());
            System.exit(0);
        }
    }

    private void showEncouragementMessage(int score) {
        // Show encouragement message based on score
        if (score < 43) {
            JOptionPane.showMessageDialog(frame, "<html><body style='width: 200px;'>Well, darling, perhaps it's time to explore some other gaming realms, you know, spread those wings a bit. I mean, let's be real, you're not exactly hitting the high score charts here. But hey, thanks for giving it a whirl! Maybe next time, hmm?</body></html>");
        } else if (score < 87) {
            JOptionPane.showMessageDialog(frame, "<html><body style='width: 200px;'>You held your own out there. Room for improvement, but hey, we all start somewhere. Keep practicing, and you'll be leveling up in no time. Looking forward to seeing your progress next time!</body></html>");
        } else if (score < 175) {
            JOptionPane.showMessageDialog(frame, "<html><body style='width: 200px;'>You crushed it! Can't wait to see you back for more action! Game on!</body></html>");
        }
    }

    private void updateGameState(Player player) {
        // Update game state based on player score
        if ((player.getScore() - lastPowerUpScore) % 75 == 0 && player.getScore() != 0) {
            int powerUpsToSpawn = 1;
            for (int i = 0; i < powerUpsToSpawn; i++) {
                int x = (int) (Math.random() * FRAME_WIDTH);
                int y = 0;
                PowerUp newPowerUp = new PowerUp(x, y);
                background.getPowerUps().add(newPowerUp);
            }
            lastPowerUpScore = player.getScore();
        }

        // Adjust game difficulty based on player score
        if (player.getScore() < 100) {
            Paddle.setPWidth(70);
            background.resetColor();
        } else if (player.getScore() < 200) {
            Paddle.setPWidth(60);
            background.setColors(195, 195, 170);
        } else if (player.getScore() < 300) {
            Paddle.setPWidth(50);
            background.setColors(145, 145, 120);
        } else if (player.getScore() < 400) {
            Paddle.setPWidth(40);
            background.setColors(95, 95, 70);
        } else if (player.getScore() < 500) {
            Paddle.setPWidth(30);
            background.setColors(45, 45, 20);
        } else if (player.getScore() >= 500) {
            Paddle.setPWidth(20);
            background.setColors(0, 0, 0);
        }
    }

    public void restart() {
        // Restart the game
        if (!gameTimer.isRunning() && !background.isGameOver()) {
            background.reset();
            background.resetPowerUps();
            gameTimer.restart();
            gamePaused = false;
            lastPowerUpScore = 0;
            frame.requestFocusInWindow();
        }
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}