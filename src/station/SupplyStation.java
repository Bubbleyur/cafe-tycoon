package station;

import entity.Player;
import logic.InventoryManager;

/** Toko logistik — memanggil {@code beliStokBahan} protojaden. */
public class SupplyStation extends Station {

    public SupplyStation(int x, int y) {
        super(x, y, "Supply Shop");
    }

    @Override
    public void interact(Player player, String subChoice) {
        if (!player.getItemOnHand().equalsIgnoreCase("None")) {
            return;
        }
        InventoryManager inv = InventoryManager.getInstance();
        if (subChoice.equalsIgnoreCase("1")) {
            inv.beliStokBahan("Kopi", 30, 10);
        } else if (subChoice.equalsIgnoreCase("2")) {
            inv.beliStokBahan("Susu", 25, 10);
        } else if (subChoice.equalsIgnoreCase("3")) {
            inv.beliStokBahan("Topping", 35, 10);
        } else {
            inv.beliStokBahan("Kopi", 30, 10);
        }
    }
}
