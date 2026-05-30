package logic;

/**
 * Singleton inventory — sama dengan protojaden {@code InventoryManager}.
 */
public class InventoryManager {

    private static InventoryManager instance;

    private int stokBijiKopi;
    private int stokSusu;
    private int stokTopping;
    private int saldoUang;

    private InventoryManager(int stokBijiKopi, int stokSusu, int stokTopping, int saldoUang) {
        this.stokBijiKopi = stokBijiKopi;
        this.stokSusu = stokSusu;
        this.stokTopping = stokTopping;
        this.saldoUang = saldoUang;
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager(50, 50, 50, 1000);
        }
        return instance;
    }

    public static void resetInstance() {
        instance = new InventoryManager(50, 50, 50, 1000);
    }

    public void kurangiStokKopi(int jumlah) {
        if (this.stokBijiKopi >= jumlah) {
            this.stokBijiKopi -= jumlah;
        } else {
            System.out.println("Stok biji kopi habis!");
        }
    }

    public void kurangiStokSusu(int jumlah) {
        if (this.stokSusu >= jumlah) {
            this.stokSusu -= jumlah;
        } else {
            System.out.println("Stok susu habis!");
        }
    }

    public void kurangiStokTopping(int jumlah) {
        if (this.stokTopping >= jumlah) {
            this.stokTopping -= jumlah;
        } else {
            System.out.println("Stok topping habis!");
        }
    }

    public void beliStokBahan(String jenisBahan, int harga, int jumlahBeli) {
        if (this.saldoUang >= harga) {
            this.saldoUang -= harga;
            if (jenisBahan.equalsIgnoreCase("Kopi")) {
                this.stokBijiKopi += jumlahBeli;
            } else if (jenisBahan.equalsIgnoreCase("Susu")) {
                this.stokSusu += jumlahBeli;
            } else if (jenisBahan.equalsIgnoreCase("Topping")) {
                this.stokTopping += jumlahBeli;
            }
        } else {
            System.out.println("Uang tidak cukup untuk beli stok!");
        }
    }

    public void tambahUang(int jumlah) {
        this.saldoUang += jumlah;
    }

    public int getStokBijiKopi() {
        return stokBijiKopi;
    }

    public int getStokSusu() {
        return stokSusu;
    }

    public int getStokTopping() {
        return stokTopping;
    }

    public int getSaldoUang() {
        return saldoUang;
    }
}
