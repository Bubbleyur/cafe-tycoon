package entity;

import logic.DrinkRecipe;
import java.util.Random;

/**
 * Pelanggan — data pesanan protojaden + posisi/state GUI proto1.
 */
public class Customer {

    public enum CustomerState {
        SPAWNED,
        WAITING_FOR_TABLE,
        SEATED,
        WAITING_FOR_ORDER,
        EATING,
        LEAVING_HAPPY,
        LEAVING_ANGRY
    }

    private static final Random RAND = new Random();

    private int id;
    private String name;
    private int x;
    private int y;
    private int width = 64;
    private int height = 64;

    private String jenisPesanan;
    private int jumlahOrang;
    private int porsiTersisa;

    private double patience = 100.0;
    private double maxPatience = 100.0;
    private double patienceDecreaseRate = 0.05;

    private CustomerState state = CustomerState.SPAWNED;
    private int assignedTableId = -1;

    private double renderScale = 1.0;
    private int bobOffset = 0;

    public Customer(int id, String name, int x, int y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.jumlahOrang = RAND.nextInt(4) + 1;
        this.porsiTersisa = this.jumlahOrang;
        acakPesanan();
    }

    public void acakPesanan() {
        int indeksAcak = RAND.nextInt(DrinkRecipe.DAFTAR_MENU.length);
        this.jenisPesanan = DrinkRecipe.DAFTAR_MENU[indeksAcak];
    }

    public void kurangiPorsi() {
        if (this.porsiTersisa > 0) {
            this.porsiTersisa--;
        }
    }

    public int getPorsiTersisa() {
        return porsiTersisa;
    }

    public String getJenisPesanan() {
        return jenisPesanan;
    }

    public int getJumlahOrang() {
        return jumlahOrang;
    }

    public String getOrderedItem() {
        return jenisPesanan;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getPatience() {
        return patience;
    }

    public void setPatience(double patience) {
        this.patience = Math.max(0.0, Math.min(maxPatience, patience));
    }

    public double getMaxPatience() {
        return maxPatience;
    }

    public double getPatienceDecreaseRate() {
        return patienceDecreaseRate;
    }

    public void setPatienceDecreaseRate(double rate) {
        this.patienceDecreaseRate = rate;
    }

    public CustomerState getState() {
        return state;
    }

    public void setState(CustomerState state) {
        this.state = state;
    }

    public int getAssignedTableId() {
        return assignedTableId;
    }

    public void setAssignedTableId(int assignedTableId) {
        this.assignedTableId = assignedTableId;
    }

    public double getRenderScale() {
        return renderScale;
    }

    public void setRenderScale(double renderScale) {
        this.renderScale = renderScale;
    }

    public int getBobOffset() {
        return bobOffset;
    }

    public void setBobOffset(int bobOffset) {
        this.bobOffset = bobOffset;
    }
}
