package logic;

import entity.Player;
import entity.TableEntity;
import station.Station;
import station.SupplyStation;
import station.ToppingStation;
import java.util.List;

public class PlayerLogic {

    private final Player player;
    private final GameEngine engine;
    private int speed = 4;

    public PlayerLogic(Player player, GameEngine engine) {
        this.player = player;
        this.engine = engine;
    }

    public void update(boolean[] keysPressed, int mapWidth, int mapHeight) {
        int dx = 0;
        int dy = 0;

        if (keysPressed[0]) {
            dy -= speed;
            player.setDirection("up");
        }
        if (keysPressed[1]) {
            dy += speed;
            player.setDirection("down");
        }
        if (keysPressed[2]) {
            dx -= speed;
            player.setDirection("left");
        }
        if (keysPressed[3]) {
            dx += speed;
            player.setDirection("right");
        }

        player.setCurrentAnimation(dx != 0 || dy != 0 ? "walk" : "idle");

        int newX = player.getX() + dx;
        int newY = player.getY() + dy;

        if (newX >= 10 && newX <= mapWidth - player.getWidth() - 10) {
            player.setX(newX);
        }
        if (newY >= 88 && newY <= mapHeight - player.getHeight() - 20) {
            player.setY(newY);
        }
    }

    public void tryInteract(List<Station> extraStations, String subChoice) {
        int px = player.getX() + player.getWidth() / 2;
        int py = player.getY() + player.getHeight() / 2;

        List<Station> all = engine.getDaftarStasiun();
        Station nearest = findNearest(all, px, py, 65);
        if (nearest == null && extraStations != null) {
            nearest = findNearest(extraStations, px, py, 65);
        }

        if (nearest != null) {
            nearest.interact(player, subChoice);
            return;
        }

        TableEntity table = findNearestTable(engine.getDaftarMeja(), px, py, 75);
        if (table == null) {
            return;
        }

        if (table.getStatus().equalsIgnoreCase("Dirty")) {
            table.bersihkanMeja();
            return;
        }

        if (table.getStatus().equalsIgnoreCase("Occupied")) {
            engine.layaniMeja(table.getIdMeja());
        }
    }

    private Station findNearest(List<Station> stations, int px, int py, double radius) {
        if (stations == null) {
            return null;
        }
        Station nearest = null;
        double min = radius;
        for (Station s : stations) {
            double d = Math.hypot(px - (s.getX() + s.getWidth() / 2), py - (s.getY() + s.getHeight() / 2));
            if (d < min) {
                min = d;
                nearest = s;
            }
        }
        return nearest;
    }

    private TableEntity findNearestTable(List<TableEntity> tables, int px, int py, double radius) {
        TableEntity nearest = null;
        double min = radius;
        for (TableEntity t : tables) {
            double d = Math.hypot(px - (t.getX() + t.getWidth() / 2), py - (t.getY() + t.getHeight() / 2));
            if (d < min) {
                min = d;
                nearest = t;
            }
        }
        return nearest;
    }
}
