package station;

import entity.Player;

public class CoffeeStation extends Station {

    private double processingProgress = 0.0;
    private double processingSpeed = 0.5; // Brew speed per tick
    private boolean processing = false;
    private boolean hasCup = false;
    private boolean brewCompleted = false;

    public CoffeeStation(String stationId, String name, int x, int y) {
        super(stationId, name, x, y);
    }

    @Override
    public void interact(Player player) {
        // If player carries an empty cup and the station is idle/empty, start brewing
        if (player.getCarriedItem().equals("Empty Cup") && !hasCup) {
            player.setCarriedItem("None");
            hasCup = true;
            processing = true;
            processingProgress = 0.0;
            brewCompleted = false;
        } 
        // If brewing is finished and player has free hands, pick up the brewed coffee
        else if (brewCompleted && player.getCarriedItem().equals("None")) {
            player.setCarriedItem("Brewed Coffee");
            hasCup = false;
            brewCompleted = false;
            processingProgress = 0.0;
        }
    }

    // Called from the game loop logic update
    public void update() {
        if (processing && hasCup) {
            processingProgress += processingSpeed;
            if (processingProgress >= 100.0) {
                processingProgress = 100.0;
                processing = false;
                brewCompleted = true;
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

    public boolean isBrewCompleted() {
        return brewCompleted;
    }

    public void setBrewCompleted(boolean brewCompleted) {
        this.brewCompleted = brewCompleted;
    }
}
