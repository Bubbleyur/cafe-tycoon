package station;

import entity.Player;

public abstract class Station {

    protected int x;
    protected int y;
    protected int width = 64;
    protected int height = 64;
    protected String stationName;

    public Station(int x, int y, String stationName) {
        this.x = x;
        this.y = y;
        this.stationName = stationName;
    }

    public abstract void interact(Player player, String subChoice);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return stationName;
    }
}
