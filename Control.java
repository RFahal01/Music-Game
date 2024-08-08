import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Control {
    // Coordinates of the object's upper left corner
    int x;
    int y;

    // Gravity constant
    static final double GRAVITY = 0.4;

    // Dimensions of the object
    int width;
    static int height;

    // Velocity of the object
    int velocityX; // Pixels to move horizontally each time move() is called
    double velocityY; // Pixels to move vertically each time move() is called

    // Maximum permissible x, y values
    int rightBound;
    int bottomBound;

    // Constructor to initialize object's properties
    public Control(int x, int y, int velocityX, int velocityY, int width, int height) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.width = width;
        Control.height = height;
    }

    // Set the bounds for the object within the game area
    public void setBounds(int width, int height) {
        rightBound = width - this.width;
        bottomBound = height - Control.height;
    }

    // Set the velocity of the object
    public void setVelocity(int velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    // Move the object based on its velocity
    public void move() {
        // Apply gravity to velocityY
        velocityY += GRAVITY;

        x += velocityX;
        y += velocityY;

        // Clip the object's position to keep it within the bounds of the game area
        clip();
    }

    // Ensure the object stays within the bounds of the game area
    public void clip() {
        if (x < 0)
            x = 0;
        else if (x > rightBound)
            x = rightBound;

        if (y < 0)
            y = 0;
        else if (y > bottomBound)
            y = bottomBound;
    }

    public Intersection intersects(Control control) {
        Rectangle rectangle = control.getBounds(); // Assuming getBounds() returns a Rectangle

        // Check for intersection between this object and another object
        if (rectangle.x > x + width
                || rectangle.y > y + height
                || rectangle.x + rectangle.width < x
                || rectangle.y + rectangle.height < y)
            return Intersection.NONE;

        // Compute the direction of intersection
        double dx = rectangle.x + rectangle.width / 2 - (x + width / 2);
        double dy = rectangle.y + rectangle.height / 2 - (y + height / 2);

        double theta = Math.atan2(dy, dx);
        double diagTheta = Math.atan2(height, width);

        // If the object is a Player and it intersects with a Paddle, make it bounce
        if (this instanceof Player && control instanceof Paddle) {
            // Reverse the y velocity to make the player bounce
            velocityY = -Math.abs(velocityY);
        }

        // Determine the direction of intersection based on the angle
        // Return the corresponding Intersection value
        if (-diagTheta <= theta && theta <= diagTheta)
            return Intersection.RIGHT;
        if (Math.PI - diagTheta <= theta || theta <= diagTheta - Math.PI)
            return Intersection.LEFT;
        if (diagTheta <= theta && theta <= Math.PI - diagTheta)
            return Intersection.DOWN;
        if (diagTheta - Math.PI <= theta && theta <= diagTheta)
            return Intersection.UP;

        return Intersection.NONE;
    }

    // Abstract methods to be implemented by subclasses

    public abstract void draw(Graphics g);

    // Method to get the bounds of the object
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}