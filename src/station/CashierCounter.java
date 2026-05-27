package station;

import entity.Player;

public class CashierCounter extends Station {

    public CashierCounter(String stationId, String name, int x, int y) {
        super(stationId, name, x, y);
    }

    @Override
    public void interact(Player player) {
        // Cashier counter is a visual element for now
        // Can be extended later for payment interactions
    }
}
