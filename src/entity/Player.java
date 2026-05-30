package entity;

/**
 * Player barista — logika tangan dari protojaden + animasi/GUI dari proto1.
 */
public class Player {

    private int x;
    private int y;
    private int width = 64;
    private int height = 64;
    private String itemOnHand = "None";

    private String currentAnimation = "idle";
    private String direction = "down";

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveTo(int targetX, int targetY) {
        this.x = targetX;
        this.y = targetY;
    }

    public void clearHand() {
        itemOnHand = "None";
    }

    public String getItemOnHand() {
        return itemOnHand;
    }

    public void setItemOnHand(String itemOnHand) {
        this.itemOnHand = itemOnHand;
    }

    /** Alias untuk kode GUI proto1. */
    public String getCarriedItem() {
        return itemOnHand;
    }

    public void setCarriedItem(String item) {
        this.itemOnHand = item;
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

    public int getHeight() {
        return height;
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
}
