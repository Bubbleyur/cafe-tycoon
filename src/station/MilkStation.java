package station;

import entity.Player;
import logic.InventoryManager;

/** Sama dengan protojaden {@code MilkStation}. */
public class MilkStation extends Station {

    public MilkStation(int x, int y) {
        super(x, y, "Milk Counter");
    }

    @Override
    public void interact(Player player, String subChoice) {
        InventoryManager inventory = InventoryManager.getInstance();
        String itemTangan = player.getItemOnHand();

        if (itemTangan.equalsIgnoreCase("Kopi")) {
            if (inventory.getStokSusu() > 0) {
                inventory.kurangiStokSusu(1);
                player.setItemOnHand("Kopi + Susu");
            } else {
                System.out.println("Stok susu habis");
            }
        } else if (itemTangan.equalsIgnoreCase("None")) {
            System.out.println("Player doesn't have a coffee!");
        } else {
            System.out.println("Player is already holding a coffee with milk");
        }
    }
}
