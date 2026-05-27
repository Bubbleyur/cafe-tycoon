package logic;

import entity.Player;
import entity.TableEntity;
import entity.Customer;
import station.Station;
import station.CoffeeStation;
import station.MilkStation;
import station.ToppingStation;
import station.StockStation;
import java.util.List;

public class PlayerLogic {

    private Player player;
    private int speed = 4;

    public PlayerLogic(Player player) {
        this.player = player;
    }

    public void update(boolean[] keysPressed, int mapWidth, int mapHeight) {
        int dx = 0;
        int dy = 0;

        if (keysPressed[0]) {
            dy -= speed; // UP (W)
            player.setDirection("up");
        }
        if (keysPressed[1]) {
            dy += speed; // DOWN (S)
            player.setDirection("down");
        }
        if (keysPressed[2]) {
            dx -= speed; // LEFT (A)
            player.setDirection("left");
        }
        if (keysPressed[3]) {
            dx += speed; // RIGHT (D)
            player.setDirection("right");
        }

        // Set Animation State
        if (dx != 0 || dy != 0) {
            player.setCurrentAnimation("walk");
        } else {
            player.setCurrentAnimation("idle");
        }

        // Apply movement with boundary collisions (margins for walls and counters)
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;

        // Keep inside screen bounds
        if (newX >= 10 && newX <= mapWidth - player.getWidth() - 10) {
            player.setX(newX);
        }
        if (newY >= 80 && newY <= mapHeight - player.getHeight() - 20) {
            player.setY(newY);
        }
    }

    public void tryInteract(List<Station> stations, List<TableEntity> tables, List<Customer> customers, InventoryManager inventory) {
        int px = player.getX() + player.getWidth() / 2;
        int py = player.getY() + player.getHeight() / 2;

        // 1. Check interaction with Stations first
        Station nearestStation = null;
        double minStationDist = 60.0; // Interaction radius

        for (Station s : stations) {
            int sx = s.getX() + s.getWidth() / 2;
            int sy = s.getY() + s.getHeight() / 2;
            double dist = Math.hypot(px - sx, py - sy);
            if (dist < minStationDist) {
                minStationDist = dist;
                nearestStation = s;
            }
        }

        if (nearestStation != null) {
            // Deduct inventory when starting processes if needed
            if (nearestStation instanceof CoffeeStation && player.getCarriedItem().equals("Empty Cup")) {
                CoffeeStation cs = (CoffeeStation) nearestStation;
                if (!cs.isHasCup() && inventory.getCoffeeBeans() >= 2) {
                    inventory.useBeans(2); // Brew uses 2 coffee beans
                    nearestStation.interact(player);
                }
            } else if (nearestStation instanceof MilkStation && player.getCarriedItem().equals("Brewed Coffee")) {
                MilkStation ms = (MilkStation) nearestStation;
                if (!ms.isHasCup() && inventory.getMilk() >= 1) {
                    inventory.useMilk(1); // Adding milk uses 1 milk unit
                    nearestStation.interact(player);
                }
            } else {
                nearestStation.interact(player);
            }
            return; // Interaction completed
        }

        // 2. Check interaction with Tables
        TableEntity nearestTable = null;
        double minTableDist = 70.0;

        for (TableEntity t : tables) {
            int tx = t.getX() + t.getWidth() / 2;
            int ty = t.getY() + t.getHeight() / 2;
            double dist = Math.hypot(px - tx, py - ty);
            if (dist < minTableDist) {
                minTableDist = dist;
                nearestTable = t;
            }
        }

        if (nearestTable != null) {
            // Serve a customer at this table
            if (nearestTable.isOccupied() && nearestTable.getAssignedCustomerId() != -1) {
                Customer cust = null;
                for (Customer c : customers) {
                    if (c.getId() == nearestTable.getAssignedCustomerId()) {
                        cust = c;
                        break;
                    }
                }

                if (cust != null) {
                    // Customer wants order, player serves them
                    if (cust.getState() == Customer.CustomerState.WAITING_FOR_ORDER && 
                        player.getCarriedItem().equals("Completed Cafe Drink")) {
                        
                        player.setCarriedItem("None");
                        cust.setState(Customer.CustomerState.EATING);
                        cust.setPatience(100.0); // Reset patience when food is delivered
                    }
                }
            } 
            // Clean dirty table
            else if (nearestTable.getState() == TableEntity.TableState.DIRTY) {
                nearestTable.setState(TableEntity.TableState.CLEAN);
            }
        }
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
