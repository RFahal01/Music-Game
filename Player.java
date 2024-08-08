import javax.sound.midi.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.*;
import javax.swing.Timer; // Import for Timer
import java.awt.event.ActionListener; // Import for ActionListener
import java.awt.event.ActionEvent; // Import for ActionEvent

// The Player class represents the player in the game.
public class Player extends Control {
    // Constants for the player's size, gravity, jump strength, and speed
    public static final int SIZE = 21;
    private static final double GRAVITY = 0.35;
    static final double JUMP_STRENGTH = 7.5;
    private static final int SPEED = 4;
    private double fallSpeed = GRAVITY;
    private boolean canCollideWithPaddle = true;

    // Variables for the player's vertical velocity, score, direction, and color
    private double velocityY;
    private int score = 0;
    private int direction = 0;

    //PowerUp constants
    private static final int BOOSTED_SPEED = 7;

    // Constructor for the Player class
    public Player(int x, int y, int velocityX, int velocityY) {
        super(x, y, velocityX, velocityY, SIZE, SIZE);
        this.velocityY = velocityY;
        initializeSound();
    }

    // Method to increase the player's score
    public void increaseScore() {
        score++;
    }

    // Method to apply gravity to the player
    public void applyGravity() {
        velocityY += GRAVITY;
    }

    public void slowFall() {
        fallSpeed = 0; // Stop fall speed

        // Create a timer to reset the fall speed after 5 seconds
        Timer resetFallSpeedTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fallSpeed = GRAVITY; // Reset to default fall speed
            }
        });
        resetFallSpeedTimer.setRepeats(false);
        resetFallSpeedTimer.start();
    }

    // Method to reverse the player's vertical velocity to simulate a bounce
    public void bounce() {
        velocityY = -JUMP_STRENGTH;
        playNote(this); // Pass the Player object as an argument
    }

    // Method to move the player based on velocity and direction
    public void move() {
        setY((int) (getY() + velocityY));
        setX(getX() + direction * SPEED);
    }

    // Method to move the player to the left
    public void moveLeft() {
        direction = -1;
    }

    // Method to move the player to the right
    public void moveRight() {
        direction = 1;
    }

    // Method to stop the player's horizontal movement
    public void stopMoving() {
        direction = 0;
    }

    // Method to draw the player
    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(0x664321));
        g.fillRect(x, y, SIZE, SIZE);
    }

    // Method to check if the player is colliding with a paddle
    public boolean isCollidingWithPaddle(Paddle paddle) {
        boolean isColliding = getBounds().intersects(paddle.getBounds());
        if (isColliding && canCollideWithPaddle) {
            bounce();
            canCollideWithPaddle = false;

            // Create a timer to reset the canCollideWithPaddle flag after 500 milliseconds
            Timer resetCollisionTimer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canCollideWithPaddle = true; // Allow collisions again
                }
            });
            resetCollisionTimer.setRepeats(false);
            resetCollisionTimer.start();
        }
        return isColliding;
    }

    // Method to check if the player has fallen off the screen
    public boolean isOutOfBounds(int gameHeight) {
        return getY() > gameHeight;
    }

    // Method to check if player went off the left side
    public boolean wentoffscreenLeft() {
        return getX() < 0;
    }

    // Method to check if player went off the right side
    public boolean wentoffscreenRight(int gameWidth) {
        return getX() > gameWidth;
    }

    // Method to handle a collision with a platform
    public void handleCollision() {
        increaseScore();
    }

    // Method to get the player's score
    public int getScore() {
        return score;
    }

    // Method to get the player's bounding rectangle
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), SIZE, SIZE);
    }

    // Getter and setter methods for the player's x and y coordinates
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public void addScore(int score) {
        this.score += score;
    }

    //PowerUp methods
    public void speedUp() {
        this.velocityX = BOOSTED_SPEED;
        this.canCollideWithPaddle = false;
    }

    // Reset player state
    public void resetSpeed() {
        // Reset player's speed
        this.velocityX = SPEED;
        this.velocityY = GRAVITY;
        this.fallSpeed = GRAVITY;

        // Reset player's direction
        this.direction = 0;

        // Reset player's score
        this.score = 0;

        // Reset player's ability to collide with paddle
        this.canCollideWithPaddle = true;
    }

    public void stopVerticalMovement() {
        this.velocityY = 0;
    }

    public void resumeVerticalMovement() {
        this.velocityY = SPEED;
    }

    public int setScore(int i) {
        return this.score;
    }

    // Sound class
    // Sound class methods
    private static final int VELOCITY_HIGH = 127;
    private static final int VELOCITY_LOW = 90;
    private static final int DURATION_LONG = 500;
    private static final int DURATION_SHORT = 250;

    // Constants for the sound

    private static final int MAX_ACTIVE_NOTES = 6;
    private static final AtomicInteger activeNotes = new AtomicInteger(0);

    // Variables for the synthesizer, MIDI channel, and executor
    private static Synthesizer synth;
    private static MidiChannel channel;
    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // Arrays for the level one notes in the scales
    private static final int[] levelOneNotes = {
        64, 71, 73, 64, 71, 73, 64, 71, 73, 71, 69, 67, 71, 69, 67,
        71, 64, 64, 71, 73, 64, 71, 73, 64, 71, 73, 71, 69, 67, 71,
        69, 67, 71, 64, 64, 71, 73, 64, 71, 73, 64, 71, 73, 71, 69,
        67, 71, 69, 67, 71
    };

    // Arrays for the level two notes in the scales
    private static final int[] levelTwoNotes = {
        46, 49, 51, 52, 54, 46, 49, 51, 52, 54, 46, 49, 51, 52, 54,
        46, 49, 51, 52, 54
    };

    // Arrays for the level three notes in the scales
    private static final int[] levelThreeNotes = {
        42, 45, 47, 48, 50, 42, 45, 47, 48, 50, 42, 45, 47, 48, 50,
        42, 45, 47, 48, 50
    };

    // Arrays for the level four notes in the scales
    private static final int[] levelFourNotes = {
        38, 41, 43, 44, 46, 38, 41, 43, 44, 46, 38, 41, 43, 44, 46,
        38, 41, 43, 44, 46
    };

    // Arrays for the level five notes in the scales
    private static final int[] levelFiveNotes = {
        34, 37, 39, 40, 42, 34, 37, 39, 40, 42, 34, 37, 39, 40, 42,
        34, 37, 39, 40, 42
    };

    private static int currentNoteIndex = 0;

    static {
        initializeSound();
    }

    private static int[] lastSelectedScale = null;

    private static boolean isSoundPlaying = false;

    public static void playNote(Player player) {
        if (isSoundPlaying) {
            return;
        }

        int[] selectedScale = (player.getScore() >= 100) ? levelTwoNotes : levelOneNotes;
        int[] selectedScale2 = (player.getScore() >= 200) ? levelThreeNotes : selectedScale;
        int[] selectedScale3 = (player.getScore() >= 300) ? levelFourNotes : selectedScale2;
        int[] selectedScale4 = (player.getScore() >= 400) ? levelFiveNotes : selectedScale3;
        int[] selectedScale5 = (player.getScore() >= 500) ? levelFiveNotes : selectedScale4;

        int[] currentScale = selectedScale5; // Use the highest available scale

        if (lastSelectedScale != currentScale) {
            stopAllNotes();
            lastSelectedScale = currentScale;
            currentNoteIndex = 0; // Reset the note index when the scale changes
        }

        int note = currentScale[currentNoteIndex];
        int velocity = ((currentNoteIndex % 8 == 0) ? VELOCITY_HIGH : VELOCITY_LOW);
        int duration = ((currentNoteIndex % 2 == 0) ? DURATION_LONG : DURATION_SHORT);

        if (activeNotes.get() < MAX_ACTIVE_NOTES) {
            activeNotes.incrementAndGet();
            playSound(note, velocity, duration);
            isSoundPlaying = true;
        }

        currentNoteIndex = (currentNoteIndex + 1) % currentScale.length;
    }

    // Method to play a sound
    private static void playSound(int note, int velocity, int duration) {
        executor.submit(() -> {
            try {
                channel.allNotesOff();
                channel.noteOn(note, velocity);
                Thread.sleep(duration);
                channel.noteOff(note);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                activeNotes.decrementAndGet();
                isSoundPlaying = false;
            }
        });
    }

    // Method to stop all notes
    private static void stopAllNotes() {
        if (channel != null) {
            channel.allNotesOff();
        }
    }

    // Method to initialize the sound
    private static void initializeSound() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            channel = synth.getChannels()[0];
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to reset the sound
    public static void reset() {
        try {
            // Shut down the executor
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }

            // Close the synth
            if (synth != null) {
                synth.close();
                synth = null;
            }

            // Reset other variables
            currentNoteIndex = 0;
            lastSelectedScale = null;
            activeNotes.set(0);

            // Reinitialize the executor and the sound
            executor = Executors.newSingleThreadScheduledExecutor();
            initializeSound();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to play the game over jingle
    public static void gameOverJingle() {
        stopAllNotes();
        int[] jingleNotes = {45, 57, 41, 52, 36};
        long delay = 0;
        for (int note : jingleNotes) {
            executor.schedule(() -> {
                if (activeNotes.get() < MAX_ACTIVE_NOTES) {
                    activeNotes.incrementAndGet();
                    playSound(note, 127, 300);
                }
            }, delay, TimeUnit.MILLISECONDS);
            delay += 200;
        }
    }

    // Method to play the power-up sound
    public static void playPowerUpSound() {
        stopAllNotes();
        if (activeNotes.get() < MAX_ACTIVE_NOTES) {
            activeNotes.incrementAndGet();
            playSound(90, 127, 500);
        }
    }
}
