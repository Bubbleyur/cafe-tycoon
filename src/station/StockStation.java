package station;

import entity.Player;

public class StockStation extends Station {

    private int stockCount = 99; // Amount of cups or beans available
    private int maxStock = 99;
    private String stockType = "Cups"; // "Cups", "Coffee Beans", etc.

    public StockStation(String stationId, String name, int x, int y) {
        super(stationId, name, x, y);
    }

    @Override
    public void interact(Player player) {
        // If player has nothing, give them a fresh new empty cup to start brewing
        if (player.getCarriedItem().equals("None") && stockCount > 0) {
            player.setCarriedItem("Empty Cup");
            stockCount--; // Decrement stock
        }
    }

    public void refillStock(int amount) {
        this.stockCount = Math.min(maxStock, this.stockCount + amount);
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = Math.min(maxStock, Math.max(0, stockCount));
    }

    public int getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(int maxStock) {
        this.maxStock = maxStock;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }
}
