package logic;

public class InventoryManager {

    private int money = 0;
    private int coffeeBeans = 50; // Current units
    private int maxCoffeeBeans = 100;
    
    private int milk = 30; // Current units
    private int maxMilk = 60;
    
    private int reputation = 5; // Reputation stars out of 5
    private int maxReputation = 5;

    public InventoryManager() {
        // Initial defaults
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public boolean spendMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }

    public boolean useBeans(int amount) {
        if (this.coffeeBeans >= amount) {
            this.coffeeBeans -= amount;
            return true;
        }
        return false;
    }

    public void refillBeans(int amount) {
        this.coffeeBeans = Math.min(maxCoffeeBeans, this.coffeeBeans + amount);
    }

    public boolean useMilk(int amount) {
        if (this.milk >= amount) {
            this.milk -= amount;
            return true;
        }
        return false;
    }

    public void refillMilk(int amount) {
        this.milk = Math.min(maxMilk, this.milk + amount);
    }

    public void adjustReputation(int change) {
        this.reputation = Math.max(0, Math.min(maxReputation, this.reputation + change));
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getCoffeeBeans() {
        return coffeeBeans;
    }

    public void setCoffeeBeans(int coffeeBeans) {
        this.coffeeBeans = Math.min(maxCoffeeBeans, Math.max(0, coffeeBeans));
    }

    public int getMaxCoffeeBeans() {
        return maxCoffeeBeans;
    }

    public void setMaxCoffeeBeans(int maxCoffeeBeans) {
        this.maxCoffeeBeans = maxCoffeeBeans;
    }

    public int getMilk() {
        return milk;
    }

    public void setMilk(int milk) {
        this.milk = Math.min(maxMilk, Math.max(0, milk));
    }

    public int getMaxMilk() {
        return maxMilk;
    }

    public void setMaxMilk(int maxMilk) {
        this.maxMilk = maxMilk;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = Math.max(0, Math.min(maxReputation, reputation));
    }

    public int getMaxReputation() {
        return maxReputation;
    }

    public void setMaxReputation(int maxReputation) {
        this.maxReputation = maxReputation;
    }
}
