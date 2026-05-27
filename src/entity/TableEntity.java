package entity;

public class TableEntity {

    public enum TableState {
        CLEAN,
        OCCUPIED,
        DIRTY
    }

    private int tableId;
    private int x;
    private int y;
    private int width = 64;
    private int height = 64;

    private TableState state = TableState.CLEAN;
    private boolean occupied = false;
    private int assignedCustomerId = -1; // -1 if no customer assigned

    // Coordinates for customer chairs (visual placement)
    private int seatX;
    private int seatY;

    public TableEntity(int tableId, int x, int y) {
        this.tableId = tableId;
        this.x = x;
        this.y = y;
        
        // Default customer seat is centered above/at the table slightly shifted
        this.seatX = x;
        this.seatY = y - 20;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
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

    public TableState getState() {
        return state;
    }

    public void setState(TableState state) {
        this.state = state;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public int getAssignedCustomerId() {
        return assignedCustomerId;
    }

    public void setAssignedCustomerId(int assignedCustomerId) {
        this.assignedCustomerId = assignedCustomerId;
    }

    public int getSeatX() {
        return seatX;
    }

    public void setSeatX(int seatX) {
        this.seatX = seatX;
    }

    public int getSeatY() {
        return seatY;
    }

    public void setSeatY(int seatY) {
        this.seatY = seatY;
    }
}
