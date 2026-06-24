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

    private static final int MAX_STOK = 100;

    public void beliStokBahan(String jenisBahan, int harga, int jumlahBeli) {
        // Cek stok penuh sebelum potong uang
        int currentStok;
        if (jenisBahan.equalsIgnoreCase("Kopi")) {
            currentStok = this.stokBijiKopi;
        } else if (jenisBahan.equalsIgnoreCase("Susu")) {
            currentStok = this.stokSusu;
        } else if (jenisBahan.equalsIgnoreCase("Topping")) {
            currentStok = this.stokTopping;
        } else {
            System.out.println("Jenis bahan tidak dikenal: " + jenisBahan);
            return;
        }

        if (currentStok >= MAX_STOK) {
            System.out.println("Stok " + jenisBahan + " sudah penuh (100)! Uang tidak dipotong.");
            return;
        }

        if (this.saldoUang >= harga) {
            this.saldoUang -= harga;
            if (jenisBahan.equalsIgnoreCase("Kopi")) {
                this.stokBijiKopi = Math.min(MAX_STOK, this.stokBijiKopi + jumlahBeli);
            } else if (jenisBahan.equalsIgnoreCase("Susu")) {
                this.stokSusu = Math.min(MAX_STOK, this.stokSusu + jumlahBeli);
            } else if (jenisBahan.equalsIgnoreCase("Topping")) {
                this.stokTopping = Math.min(MAX_STOK, this.stokTopping + jumlahBeli);
            }
        } else {
            System.out.println("Uang tidak cukup untuk beli stok " + jenisBahan + "!");
        }
    }

    public void tambahUang(int jumlah) {
        this.saldoUang += jumlah;
    }

    public void kurangiUang(int jumlah) {
        if (this.saldoUang >= jumlah) {
            this.saldoUang -= jumlah;
        }
    }

    public static void setInstance(InventoryManager instance) {
        InventoryManager.instance = instance;
    }

    public int getStokBijiKopi() {
        return stokBijiKopi;
    }

    public void setStokBijiKopi(int stokBijiKopi) {
        this.stokBijiKopi = stokBijiKopi;
    }

    public int getStokSusu() {
        return stokSusu;
    }

    public void setStokSusu(int stokSusu) {
        this.stokSusu = stokSusu;
    }

    public int getStokTopping() {
        return stokTopping;
    }

    public void setStokTopping(int stokTopping) {
        this.stokTopping = stokTopping;
    }

    public int getSaldoUang() {
        return saldoUang;
    }

    public void setSaldoUang(int saldoUang) {
        this.saldoUang = saldoUang;
    }
}
