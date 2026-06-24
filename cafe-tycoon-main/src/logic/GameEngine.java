package logic;

import entity.Customer;
import entity.Player;
import entity.TableEntity;
import station.CoffeeStation;
import station.MilkStation;
import station.Station;
import station.ToppingStation;
import java.util.ArrayList;

/**
 * Game engine — diselaraskan dengan protojaden {@code GameEngine}.
 */
public class GameEngine {

    private final ArrayList<TableEntity> daftarMeja = new ArrayList<>();
    private final ArrayList<Station> daftarStasiun = new ArrayList<>();
    private Player playerBarista;

    private int currentLevel;
    private int pelangganTersisaLevel;
    private int targetPoinLevel;
    private int poinAwal;

    private static final int[][] TABLE_LAYOUT_X = {
        {200, 380},
        {160, 340, 520},
        {140, 300, 460, 620},
        {120, 280, 440, 600},
        {100, 240, 380, 520}
    };

    private static final int TABLE_Y = 220;

    public GameEngine() {
        this.playerBarista = new Player(300, 400);
        this.currentLevel = 1;
        initLevel(this.currentLevel);
    }

    public void initLevel(int level) {
        this.currentLevel = level;
        // this.daftarMeja.clear(); // Removed to preserve table upgrades across levels
        this.daftarStasiun.clear();
        this.playerBarista.clearHand();
        this.playerBarista.moveTo(300, 400);

        System.out.println("=== MEMULAI LEVEL " + level + " ===");

        if (daftarMeja.isEmpty()) {
            addMeja(1, 1, 0);
            addMeja(2, 2, 1);
        } else {
            for (TableEntity m : daftarMeja) {
                m.resetTable();
            }
        }

        if (level == 1) {
            pelangganTersisaLevel = 5;
            targetPoinLevel = 250;
            poinAwal = 0;
        } else if (level == 2) {
            pelangganTersisaLevel = 7;
            targetPoinLevel = 400;
            poinAwal = 0;
        } else if (level == 3) {
            pelangganTersisaLevel = 10;
            targetPoinLevel = 600;
            poinAwal = 0;
        } else if (level == 4) {
            pelangganTersisaLevel = 12;
            targetPoinLevel = 800;
            poinAwal = 0;
        } else if (level >= 5) {
            this.currentLevel = 5;
            pelangganTersisaLevel = 15;
            targetPoinLevel = 1200;
            poinAwal = 0;
        }

        daftarStasiun.add(new CoffeeStation(320, 135));
        daftarStasiun.add(new MilkStation(460, 135));
        daftarStasiun.add(new ToppingStation(600, 135));
        redistributeTables();
    }

    private void addMeja(int id, int kapasitas, int layoutIndex) {
        int x = TABLE_LAYOUT_X[currentLevel - 1][layoutIndex];
        daftarMeja.add(new TableEntity(id, kapasitas, x, TABLE_Y));
    }

    public void redistributeTables() {
        int count = daftarMeja.size();
        if (count == 0) return;

        int gap = 150;
        int totalWidth = (count - 1) * gap;
        int startX = 480 - (totalWidth / 2) - 32; // Centered for 960 width

        for (int i = 0; i < count; i++) {
            daftarMeja.get(i).setX(startX + i * gap);
        }
    }

    public void tambahMejaBaru() {
        int idBaru = daftarMeja.size() + 1;
        int lastX = 100;
        if (!daftarMeja.isEmpty()) {
            lastX = daftarMeja.get(daftarMeja.size() - 1).getX() + 160;
        }
        daftarMeja.add(new TableEntity(idBaru, 2, lastX, TABLE_Y));
        redistributeTables();
    }

    public void pemicuPelangganBaru(Customer rombonganBaru) {
        if (pelangganTersisaLevel <= 0) {
            return;
        }

        boolean berhasilDuduk = false;
        for (TableEntity table : daftarMeja) {
            if (table.getStatus().equalsIgnoreCase("Free")
                    && table.getKapasitasKursi() >= rombonganBaru.getJumlahOrang()) {
                berhasilDuduk = table.occupyMeja(rombonganBaru);
                if (berhasilDuduk) {
                    System.out.println("Rombongan berisi " + rombonganBaru.getJumlahOrang()
                            + " orang berhasil duduk di Meja ID " + table.getIdMeja());
                    pelangganTersisaLevel--;
                    break;
                }
            }
        }

        if (!berhasilDuduk) {
            System.out.println("Tidak ada meja yang muat.");
        }
    }

    public void tambahPoinLevel(int poin) {
        poinAwal += poin;
    }

    public LevelResult cekKondisiEndLevel(int activeCustomers) {
        if (activeCustomers > 0 || pelangganTersisaLevel > 0) {
            return LevelResult.IN_PROGRESS;
        }

        boolean semuaMejaKosong = true;
        for (TableEntity t : daftarMeja) {
            if (t.getStatus().equalsIgnoreCase("Occupied")) {
                semuaMejaKosong = false;
                break;
            }
        }

        if (!semuaMejaKosong) {
            return LevelResult.IN_PROGRESS;
        }

        System.out.println("=== HARI SELESAI ===");
        System.out.println("Poin kamu " + poinAwal + " / Target: $" + targetPoinLevel);

        if (poinAwal >= targetPoinLevel) {
            System.out.println("Selamat! Kamu Lolos Level " + currentLevel);
            if (currentLevel < 5) {
                return LevelResult.LEVEL_WON;
            }
            System.out.println("LUAR BIASA! Anda Telah Menamatkan Game Cafe Tycoon!");
            return LevelResult.GAME_WON;
        }

        System.out.println("Gagal! Pendapatan tidak mencapai target. Silakan ulangi level ini.");
        return LevelResult.LEVEL_FAILED;
    }

    public void layaniMeja(int idMeja) {
        if (idMeja < 1 || idMeja > daftarMeja.size()) {
            return;
        }
        daftarMeja.get(idMeja - 1).sajikanKopiKeMeja(playerBarista, this);
    }

    public void retryLevel() {
        poinAwal = 0;
        initLevel(currentLevel);
    }

    /** Paksa hentikan spawn pelanggan baru tanpa reset poin. */
    public void forceEndLevel() {
        this.pelangganTersisaLevel = 0;
    }

    public void advanceLevel() {
        if (currentLevel < 5) {
            initLevel(currentLevel + 1);
        }
    }

    public ArrayList<TableEntity> getDaftarMeja() {
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

    public enum LevelResult {
        IN_PROGRESS,
        LEVEL_WON,
        LEVEL_FAILED,
        GAME_WON
    }
}
