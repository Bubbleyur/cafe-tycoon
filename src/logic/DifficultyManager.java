package logic;

/** Pengatur spawn & patience untuk lapisan GUI (proto1). */
public class DifficultyManager {

    private int level = 1;
    private int customerSpawnInterval = 480;
    private double customerPatienceDecayRate = 0.05;

    public void applyLevel(int gameLevel) {
        level = gameLevel;
        customerSpawnInterval = Math.max(200, 520 - (level * 60));
        customerPatienceDecayRate = 0.04 + (level * 0.012);
    }

    public int getCustomerSpawnInterval() {
        return customerSpawnInterval;
    }

    public double getCustomerPatienceDecayRate() {
        return customerPatienceDecayRate;
    }
}
