package entity;

import logic.DrinkRecipe;
import logic.GameEngine;
import logic.InventoryManager;

/**
 * Meja — logika {@code Table} protojaden + koordinat GUI proto1.
 */
public class TableEntity {

    private int idMeja;
    private int kapasitasKursi;
    private String status;
    private Customer currentCustomer;

    private int x;
    private int y;
    private int width = 64;
    private int height = 64;
    private int seatX;
    private int seatY;

    public TableEntity(int idMeja, int kapasitasKursi, int x, int y) {
        this.idMeja = idMeja;
        this.kapasitasKursi = kapasitasKursi;
        this.x = x;
        this.y = y;
        this.status = "Free";
        this.currentCustomer = null;
        this.seatX = x;
        this.seatY = y - 20;
    }

    public boolean occupyMeja(Customer c) {
        if (!this.status.equalsIgnoreCase("Free")) {
            System.out.println("Meja tidak tersedia!");
        }

        if (c.getJumlahOrang() > this.kapasitasKursi) {
            System.out.println("Terlalu banyak orang!");
            return false;
        }

        this.currentCustomer = c;
        this.status = "Occupied";
        return true;
    }

    public void sajikanKopiKeMeja(Player player, GameEngine engine) {
        if (!this.status.equalsIgnoreCase("Occupied") || currentCustomer == null) {
            System.out.println("No customer on this table!");
            return;
        }

        String kopiDiTangan = player.getItemOnHand();
        String namaMenuRacikan = DrinkRecipe.terjemahkanRacikan(kopiDiTangan);

        if (namaMenuRacikan.equalsIgnoreCase(currentCustomer.getJenisPesanan())) {
            currentCustomer.kurangiPorsi();
            player.clearHand();

            System.out.println("1 Porsi " + currentCustomer.getJenisPesanan() + " berhasil disajikan!");
            System.out.println("Sisa porsi yang harus dibuat untuk meja ini: " + currentCustomer.getPorsiTersisa());

            if (currentCustomer.getPorsiTersisa() == 0) {
                System.out.println("Semua pesanan di Meja " + idMeja + " selesai! Pelanggan mulai membayar");
                currentCustomer.setState(Customer.CustomerState.LEAVING_HAPPY);
                bayarPesanan(engine);
                kosongkanMeja();
            }
        } else {
            System.out.println("Salah kopi!, customer minta " + currentCustomer.getJenisPesanan());
        }
    }

    public void kosongkanMeja() {
        this.currentCustomer = null;
        this.status = "Dirty";
    }

    public void bersihkanMeja() {
        this.status = "Free";
    }

    public void bayarPesanan(GameEngine engine) {
        if (this.currentCustomer != null) {
            int jumlahOrang = this.currentCustomer.getJumlahOrang();
            int totalUang = jumlahOrang * 50;
            int totalPoin = jumlahOrang * 50;
            InventoryManager.getInstance().tambahUang(totalUang);
            engine.tambahPoinLevel(totalPoin);
        }
    }

    public int getIdMeja() {
        return idMeja;
    }

    public int getTableId() {
        return idMeja;
    }

    public int getKapasitasKursi() {
        return kapasitasKursi;
    }

    public String getStatus() {
        return status;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public boolean isOccupied() {
        return status.equalsIgnoreCase("Occupied");
    }

    public boolean isDirty() {
        return status.equalsIgnoreCase("Dirty");
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSeatX() {
        return seatX;
    }

    public int getSeatY() {
        return seatY;
    }
}
