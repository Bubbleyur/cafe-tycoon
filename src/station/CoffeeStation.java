package station;

import entity.Player;
import logic.InventoryManager;

/** Sama dengan protojaden {@code CoffeeStation}. */
public class CoffeeStation extends Station {

    public CoffeeStation(int x, int y) {
        super(x, y, "Coffee Counter");
    }

    @Override
    public void interact(Player player, String subChoice) {
        InventoryManager inventory = InventoryManager.getInstance();

        if (player.getItemOnHand().equalsIgnoreCase("None")) {
            if (inventory.getStokBijiKopi() > 0) {
                inventory.kurangiStokKopi(1);
                player.setItemOnHand("Kopi");
            } else {
                System.out.println("Stok kopi habis!");
            }
        } else {
            System.out.println("Player is already holding a coffee");
        }
    }
}
