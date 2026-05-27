package station;

import entity.Player;

public class MilkStation extends Station {

    private double processingProgress = 0.0;
    private double processingSpeed = 1.0; // Faster than coffee brewing
    private boolean processing = false;
    private boolean hasCup = false;
    private boolean milkAdded = false;

    public MilkStation(String stationId, String name, int x, int y) {
        super(stationId, name, x, y);
    }

    @Override
    public void interact(Player player) {
        // If player carries brewed coffee and station is empty, place it to add milk
        if (player.getCarriedItem().equals("Brewed Coffee") && !hasCup) {
            player.setCarriedItem("None");
            hasCup = true;
            processing = true;
            processingProgress = 0.0;
            milkAdded = false;
        } 
        // Once done, retrieve Coffee + Milk
        else if (milkAdded && player.getCarriedItem().equals("None")) {
            player.setCarriedItem("Coffee + Milk");
            hasCup = false;
            milkAdded = false;
            processingProgress = 0.0;
        }
    }

    public void update() {
        if (processing && hasCup) {
            processingProgress += processingSpeed;
            if (processingProgress >= 100.0) {
                processingProgress = 100.0;
                processing = false;
                milkAdded = true;
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

    public boolean isMilkAdded() {
        return milkAdded;
    }

    public void setMilkAdded(boolean milkAdded) {
        this.milkAdded = milkAdded;
    }
}
