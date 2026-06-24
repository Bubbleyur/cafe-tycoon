package station;

import entity.Player;

public class CashierCounter extends Station {

    private boolean interactedRecently = false;
    private long interactionTime = 0;
    private static final int INTERACTION_DISPLAY_TIME = 3000; // 3 seconds

    public CashierCounter(int x, int y) {
        super(x, y, "Cashier");
    }

    @Override
    public void interact(Player player, String subChoice) {
        // Signal that the cashier has been interacted with
        this.interactedRecently = true;
        this.interactionTime = System.currentTimeMillis();
        System.out.println("Cashier interacted with!");
    }

    public boolean isInteractedRecently() {
        // Check if interaction is still within display time
        if (!interactedRecently) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - interactionTime;
        if (elapsed > INTERACTION_DISPLAY_TIME) {
            interactedRecently = false;
            return false;
        }
        return true;
    }

    public void resetInteractionState() {
        this.interactedRecently = false;
    }
}
