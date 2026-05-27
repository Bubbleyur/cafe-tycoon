package entity;

public class Player {

    private int x;
    private int y;
    private int width = 64;
    private int height = 64;
    
    // Animation states
    private String currentAnimation = "idle";
    private String direction = "down"; // "down", "up", "left", "right"
    
    // State of what item the player is carrying
    // e.g. "None", "Empty Cup", "Brewed Coffee", "Coffee + Milk", "Completed Cafe Drink"
    private String carriedItem = "None"; 
    private String carriedItemDetails = ""; // Specific order/components

    public Player() {
        this.x = 350; // Centered spawn
        this.y = 250;
    }

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(String currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getCarriedItem() {
        return carriedItem;
    }

    public void setCarriedItem(String carriedItem) {
        this.carriedItem = carriedItem;
    }

    public String getCarriedItemDetails() {
        return carriedItemDetails;
    }

    public void setCarriedItemDetails(String carriedItemDetails) {
        this.carriedItemDetails = carriedItemDetails;
    }
}