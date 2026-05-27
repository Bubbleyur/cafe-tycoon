package entity;

public class Customer {

    public enum CustomerState {
        SPAWNED,              // Just entered, waiting in line
        WAITING_FOR_TABLE,    // Waiting to be seated
        SEATED,               // Sitting at a table, thinking of order
        WAITING_FOR_ORDER,    // Ordered, waiting for player to bring the drink
        EATING,               // Consuming the drink
        LEAVING_HAPPY,        // Finished and paid
        LEAVING_ANGRY         // Patience ran out, leaving without paying
    }

    private int x;
    private int y;
    private int width = 64;
    private int height = 64;
    private int id;
    private String name;

    // Patience state (0 - 100)
    private double patience = 100.0;
    private double maxPatience = 100.0;
    private double patienceDecreaseRate = 0.05; // Depletion rate per frame/tick

    // Dining state
    private CustomerState state = CustomerState.SPAWNED;
    private int assignedTableId = -1; // -1 means unassigned
    private String orderedItem = "None"; // e.g. "Espresso", "Latte", "Cappuccino"

    // Animation & Visual Scale
    private double renderScale = 1.0;
    private int bobOffset = 0;

    public Customer(int id, String name, int x, int y) {
        this.id = id;
        this.name = name;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPatience() {
        return patience;
    }

    public void setPatience(double patience) {
        this.patience = Math.max(0.0, Math.min(maxPatience, patience));
    }

    public double getMaxPatience() {
        return maxPatience;
    }

    public void setMaxPatience(double maxPatience) {
        this.maxPatience = maxPatience;
    }

    public double getPatienceDecreaseRate() {
        return patienceDecreaseRate;
    }

    public void setPatienceDecreaseRate(double patienceDecreaseRate) {
        this.patienceDecreaseRate = patienceDecreaseRate;
    }

    public CustomerState getState() {
        return state;
    }

    public void setState(CustomerState state) {
        this.state = state;
    }

    public int getAssignedTableId() {
        return assignedTableId;
    }

    public void setAssignedTableId(int assignedTableId) {
        this.assignedTableId = assignedTableId;
    }

    public String getOrderedItem() {
        return orderedItem;
    }

    public void setOrderedItem(String orderedItem) {
        this.orderedItem = orderedItem;
    }

    public double getRenderScale() {
        return renderScale;
    }

    public void setRenderScale(double renderScale) {
        this.renderScale = renderScale;
    }

    public int getBobOffset() {
        return bobOffset;
    }

    public void setBobOffset(int bobOffset) {
        this.bobOffset = bobOffset;
    }
}
