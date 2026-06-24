package station;

import entity.Player;

public class TrashBin extends Station {

    public TrashBin(int x, int y) {
        super(x, y, "Trash Bin");
    }

    @Override
    public void interact(Player player, String subChoice) {
        if (!player.getItemOnHand().equalsIgnoreCase("None")) {
            System.out.println("Item thrown away: " + player.getItemOnHand());
            player.clearHand();
        } else {
            System.out.println("Hand is empty, nothing to throw away.");
        }
    }
}
