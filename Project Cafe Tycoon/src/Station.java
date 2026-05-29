public abstract class Station {
    int x, y;
    String stationName;

    public Station(int x, int y, String stationName) {
        this.x = x;
        this.y = y;
        this.stationName = stationName;
    }

    public abstract void interact(Player player, String subChoice);
}
