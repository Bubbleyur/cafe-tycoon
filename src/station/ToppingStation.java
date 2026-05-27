package station;

import entity.Player;

public class ToppingStation extends Station {

    private double processingProgress = 0.0;
    private double processingSpeed = 1.5; // Very quick toppings!
    private boolean processing = false;
    private boolean hasCup = false;
    private boolean toppingsAdded = false;

    public ToppingStation(String stationId, String name, int x, int y) {
        super(stationId, name, x, y);
    }

    @Override
    public void interact(Player player) {
        // If player carries Coffee + Milk and station is empty, place it to add toppings
        if (player.getCarriedItem().equals("Coffee + Milk") && !hasCup) {
            player.setCarriedItem("None");
            hasCup = true;
            processing = true;
            processingProgress = 0.0;
            toppingsAdded = false;
        } 
        // Once done, retrieve the final completed specialty drink!
        else if (toppingsAdded && player.getCarriedItem().equals("None")) {
            player.setCarriedItem("Completed Cafe Drink");
            hasCup = false;
            toppingsAdded = false;
            processingProgress = 0.0;
        }
    }

    public void update() {
        if (processing && hasCup) {
            processingProgress += processingSpeed;
            if (processingProgress >= 100.0) {
                processingProgress = 100.0;
                processing = false;
                toppingsAdded = true;
            }
        }
    }

    public double getProcessingProgress() {
        return processingProgress;
    }

    public void setProcessingProgress(double processingProgress) {
        this.processingProgress = processingProgress;
    }

    public double getProcessingSpeed() {
        return processingSpeed;
    }

    public void setProcessingSpeed(double processingSpeed) {
        this.processingSpeed = processingSpeed;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public boolean isHasCup() {
        return hasCup;
    }

    public void setHasCup(boolean hasCup) {
        this.hasCup = hasCup;
    }

    public boolean isToppingsAdded() {
        return toppingsAdded;
    }

    public void setToppingsAdded(boolean toppingsAdded) {
        this.toppingsAdded = toppingsAdded;
    }
}
