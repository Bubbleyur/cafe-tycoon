import java.util.ArrayList;

public class GameEngine {
    ArrayList<Table> daftarMeja;
    ArrayList<Station> daftarStasiun;
    Player playerBarista;

    private int currentLevel;
    // protected int pelangganTersisaLevel;
    private int antreanLuarTersisa;
    private int targetPoinLevel;
    private int poinAwal;

    public GameEngine() {
        this.daftarMeja = new ArrayList<>();
        this.daftarStasiun = new ArrayList<>();
        this.playerBarista = new Player(300, 400, "None");
        this.currentLevel = 1;

        if(this.daftarMeja.isEmpty()){
            this.daftarMeja.add(new Table(1, 1));
            this.daftarMeja.add(new Table(2, 2));
        }
        
        initLevel(this.currentLevel);
    }

    public void initLevel(int level) {
        this.currentLevel = level;
        this.poinAwal = 0;
        this.daftarStasiun.clear(); 
        this.playerBarista.clearHand(); 
        this.playerBarista.moveTo(300, 400);

        if(this.daftarMeja.isEmpty()){
            this.daftarMeja.add(new Table(1, 1));
            this.daftarMeja.add(new Table(2, 2));
        }

        for(Table m : daftarMeja){
            m.kosongkanMeja();
            m.bersihkanMeja();;
        }

        System.out.println("=== MEMULAI LEVEL " + level + " ===");

        if (level == 1) {
            // this.pelangganTersisaLevel = 5;
            this.antreanLuarTersisa = 5;
            this.targetPoinLevel = 250;

            // if(daftarMeja.isEmpty()){
            //     daftarMeja.add(new Table(1, 1));
            //     daftarMeja.add(new Table(1, 2));
            // }
        } 
        else if (level == 2) {
            // this.pelangganTersisaLevel = 7;
            this.antreanLuarTersisa = 7;
            this.targetPoinLevel = 400;

            // while (daftarMeja.size() < 3) {
            //     daftarMeja.add(new Table(daftarMeja.size() + 1, 1));
            // }
        }
        else if (level == 3) {
            // this.pelangganTersisaLevel = 10;
            this.antreanLuarTersisa = 10;
            this.targetPoinLevel = 600;
        }
        else if (level == 4) {
            // this.pelangganTersisaLevel = 12;
            this.antreanLuarTersisa = 12;
            this.targetPoinLevel = 800;
        }
        else if (level >= 5) { 
            this.currentLevel = 5; 
            // this.pelangganTersisaLevel = 15;
            this.antreanLuarTersisa = 15;
            this.targetPoinLevel = 1200;
        }

        daftarStasiun.add(new CoffeeStation(100, 100));
        daftarStasiun.add(new MilkStation(200, 100));
        daftarStasiun.add(new ToppingStation(300, 100));
    }

    public void tambahMejaBaru(){
        int idBaru = daftarMeja.size()+1;
        daftarMeja.add(new Table(idBaru, 1));
    }

    public void gameLoopDetik(){
    }

    public void pemicuPelangganBaru(Customer c) {
        Table mejaTersedia = null;
        for (Table meja : daftarMeja) {
            if (meja.getStatus().equalsIgnoreCase("Free")) {
                mejaTersedia = meja;
                break; 
            }
        }

        if (mejaTersedia == null) {
            return; 
        }

        int hitungReroll = 0; 
        while (c.getJumlahOrang() > mejaTersedia.getKapasitasKursi()) {
            c.rerollJumlah();
            c.acakPesanan(); 
            hitungReroll++;
            if (hitungReroll > 50) { 
                break;
            }
        }
        mejaTersedia.occupyMeja(c);
    }

    public void tambahPoinLevel(int poin){
        this.poinAwal += poin;
    }

    // Explicitly handles only the increment state condition change
    public void cekKondisiEndLevel() {
        if (this.antreanLuarTersisa == 0) {
            if (poinAwal >= targetPoinLevel) { 
                currentLevel++; 
            }
        }
    }

    // public void kurangiAntreanLevel() {
    //     if (this.pelangganTersisaLevel > 0) {
    //         this.pelangganTersisaLevel--;
    //     }
    // }

    public void kurangiAntreanMasukKafe() {
        if (this.antreanLuarTersisa > 0) {
            this.antreanLuarTersisa--;
        }   
    }

    public void pelangganSelesai() {
        if (this.antreanLuarTersisa > 0) {
            this.antreanLuarTersisa--;
        }
    }   

    public void layaniMeja(int idMeja){
        daftarMeja.get(idMeja - 1).sajikanKopiKeMeja(playerBarista, this);
    }

    public ArrayList<Table> getDaftarMeja() { return daftarMeja; }
    public ArrayList<Station> getDaftarStasiun() { return daftarStasiun; }
    public Player getPlayerBarista() { return playerBarista; }
    public int getCurrentLevel() { return currentLevel; }
    public int getPoinAwal() { return poinAwal; }
    public int getTargetPoinLevel() { return targetPoinLevel; }
    // public int getPelangganTersisaLevel() { return pelangganTersisaLevel; }
    public int getAntreanLuarTersisa() { return antreanLuarTersisa; }
    public void setAntreanLuarTersisa(int antreanLuarTersisa) { this.antreanLuarTersisa = antreanLuarTersisa; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
}