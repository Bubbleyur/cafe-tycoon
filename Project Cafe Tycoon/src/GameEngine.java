import java.util.ArrayList;

public class GameEngine {
    ArrayList<Table> daftarMeja;
    ArrayList<Station> daftarStasiun;
    Player playerBarista;

    private int currentLevel;
    private int pelangganTersisaLevel;
    private int targetPoinLevel;
    private int poinAwal;

    public GameEngine() {
        this.daftarMeja = new ArrayList<>();
        this.daftarStasiun = new ArrayList<>();
        this.playerBarista = new Player(300, 400, "None");
        this.currentLevel = 1;
        
        initLevel(this.currentLevel);
    }

    public void initLevel(int level) {
        this.currentLevel = level;
        this.daftarMeja.clear(); // Hancurkan meja lama
        this.playerBarista.clearHand(); // Bersihkan tangan barista
        this.playerBarista.moveTo(300, 400  );

        System.out.println("=== MEMULAI LEVEL " + level + " ===");

        if (level == 1) {
            this.pelangganTersisaLevel = 5;
            this.targetPoinLevel = 250;
            // Level 1: 2 Meja kapasitas 1
            daftarMeja.add(new Table(1, 1)); 
            daftarMeja.add(new Table(2, 2));
        } 
        else if (level == 2) {
            this.pelangganTersisaLevel = 7;
            this.targetPoinLevel = 400;
            // Level 2: 3 Meja (Meja 3 kapasitas 2)
            daftarMeja.add(new Table(1, 2));
            daftarMeja.add(new Table(2, 2));
            daftarMeja.add(new Table(3, 3));
        }
        else if (level == 3) {
            this.pelangganTersisaLevel = 10;
            this.targetPoinLevel = 600;
            // Level 3: 3 Meja dengan kapasitas lebih tinggi
            daftarMeja.add(new Table(1, 2));
            daftarMeja.add(new Table(2, 2));
            daftarMeja.add(new Table(3, 3));
            daftarMeja.add(new Table(4, 4));
        }
        else if (level == 4) {
            this.pelangganTersisaLevel = 12;
            this.targetPoinLevel = 800;
            // Level 4: 4 Meja
            daftarMeja.add(new Table(1, 2));
            daftarMeja.add(new Table(2, 3));
            daftarMeja.add(new Table(3, 4));
            daftarMeja.add(new Table(4, 4));
        }
        else if (level == 5) {
            this.pelangganTersisaLevel = 15;
            this.targetPoinLevel = 1200;
            // Level 5: 5 Meja kapasitas besar
            daftarMeja.add(new Table(1, 3));
            daftarMeja.add(new Table(2, 4));
            daftarMeja.add(new Table(3, 4));
            daftarMeja.add(new Table(4, 4));
        }

        daftarStasiun.add(new CoffeeStation(100, 100));
        daftarStasiun.add(new MilkStation(200, 100));
        daftarStasiun.add(new ToppingStation(300, 100));
    }

    public void gameLoopDetik(){
         
    }

    public void pemicuPelangganBaru(Customer rombonganBaru){
        if(this.pelangganTersisaLevel <= 0){
            return;
        }

        boolean berhasilDuduk = false;

        for (Table table : daftarMeja) {
            if(table.getStatus().equalsIgnoreCase("Free") && table.getKapasitasKursi() >= rombonganBaru.getJumlahOrang()){
                berhasilDuduk = table.occupyMeja(rombonganBaru);
                if(berhasilDuduk){
                    System.out.println("Rombongan berisi " + rombonganBaru.getJumlahOrang() + " orang berhasil duduk di Meja ID " + table.getIdMeja());
                    this.pelangganTersisaLevel--;
                    break;
                }
            }
        }

        if(!berhasilDuduk){
            System.out.println("Tidak ada meja yang muat.");
        }
    }

    public void tambahPoinLevel(int poin){
        this.poinAwal += poin;
    }

    public void cekKondisiEndLevel() {
        boolean semuaMejaKosong = true;
        for (Table t : daftarMeja) {
            if (t.getStatus().equalsIgnoreCase("Occupied")) {
                semuaMejaKosong = false;
                break;
            }
        }

        if (this.pelangganTersisaLevel == 0 && semuaMejaKosong) {
            System.out.println("=== HARI SELESAI ===");
            System.out.println("Poin kamu " + poinAwal + " / Target: $" + this.targetPoinLevel);

            if (poinAwal >= this.targetPoinLevel) {
                System.out.println("Selamat! Kamu Lolos Level " + this.currentLevel);
                
                if (this.currentLevel < 5) {
                    // Pindah ke Main Menu atau Toko Logistik sebelum lanjut level berikutnya
                    pindahKeMenuAntarLevel(); 
                } else {
                    System.out.println("LUAR BIASA! Anda Telah Menamatkan Game Cafe Tycoon!");
                }
            } else {
                System.out.println("Gagal! Pendapatan tidak mencapai target. Silakan ulangi level ini.");
                initLevel(this.currentLevel); // Reset ulang level yang sama
            }
        }
    }

    private void pindahKeMenuAntarLevel() {
        System.out.println("Mengalihkan Layar ke Main Menu / Toko Logistik...");
        // Di sini nanti logika Java GUI kalian memunculkan panel Toko/Main Menu
        // Di dalam toko itu, user bisa klik tombol "Mulai Level " + (currentLevel + 1)
    }

    public void layaniMeja(int idMeja){
        daftarMeja.get(idMeja - 1).sajikanKopiKeMeja(playerBarista, this);
        cekKondisiEndLevel();
    }

    public ArrayList<Table> getDaftarMeja() {
        return daftarMeja;
    }

    public ArrayList<Station> getDaftarStasiun() {
        return daftarStasiun;
    }

    public Player getPlayerBarista() {
        return playerBarista;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getPoinAwal() {
        return poinAwal;
    }

    public int getTargetPoinLevel() {
        return targetPoinLevel;
    }

    public int getPelangganTersisaLevel() {
        return pelangganTersisaLevel;
    }
}
