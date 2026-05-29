public class Player {
    private int x, y; 
    private String itemOnHand;

    public Player(int x, int y, String itemOnHand) {
        this.x = x;
        this.y = y;
        this.itemOnHand = "None";
    }

    public void moveTo(int targetX, int targetY){
        this.x = targetX;
        this.y = targetY;
    }

    public void clearHand(){
        itemOnHand = "None";
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

    public String getItemOnHand() {
        return itemOnHand;
    }

    public void setItemOnHand(String itemOnHand) {
        this.itemOnHand = itemOnHand;
    }

}
