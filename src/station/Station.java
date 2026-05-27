package station;

import entity.Player;

public abstract class Station {

    protected int x;
    protected int y;
    protected int width = 64;
    protected int height = 64;
    protected String name;
    protected String stationId;

    public Station(String stationId, String name, int x, int y) {
        this.stationId = stationId;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    // Interaction logic to be overridden by subclasses
    // This allows backend/gameplay logic programmers to implement custom mechanics easily
    public abstract void interact(Player player);

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }
}
