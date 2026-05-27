package logic;

public class DifficultyManager {

    private int level = 1;
    private int score = 0;
    
    // Configurable gameplay speed values
    private int customerSpawnInterval = 600; // in ticks (~10 seconds at 60 FPS)
    private double customerPatienceDecayRate = 0.05; // patience lost per tick
    
    public DifficultyManager() {
        adjustDifficulty();
    }

    public void addScore(int points) {
        this.score += points;
        // Level up every 100 points
        int newLevel = (score / 100) + 1;
        if (newLevel != level) {
            level = newLevel;
            adjustDifficulty();
        }
    }

    private void adjustDifficulty() {
        // As level increases, customers spawn faster and lose patience quicker
        customerSpawnInterval = Math.max(240, 600 - (level * 50)); // cap spawn at min 4 sec
        customerPatienceDecayRate = 0.05 + (level * 0.015); // patience decreases faster
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        adjustDifficulty();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        adjustDifficulty();
    }

    public int getCustomerSpawnInterval() {
        return customerSpawnInterval;
    }

    public void setCustomerSpawnInterval(int customerSpawnInterval) {
        this.customerSpawnInterval = customerSpawnInterval;
    }

    public double getCustomerPatienceDecayRate() {
        return customerPatienceDecayRate;
    }

    public void setCustomerPatienceDecayRate(double customerPatienceDecayRate) {
        this.customerPatienceDecayRate = customerPatienceDecayRate;
    }
}
