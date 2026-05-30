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
        this.daftarMeja.clear();
        this.daftarStasiun.clear();
        this.playerBarista.clearHand();
        this.playerBarista.moveTo(300, 400);

        System.out.println("=== MEMULAI LEVEL " + level + " ===");

        if (level == 1) {
            pelangganTersisaLevel = 5;
            targetPoinLevel = 250;
            addMeja(1, 1, 0);
            addMeja(2, 2, 1);
        } else if (level == 2) {
            pelangganTersisaLevel = 7;
            targetPoinLevel = 400;
            addMeja(1, 2, 0);
            addMeja(2, 2, 1);
            addMeja(3, 3, 2);
        } else if (level == 3) {
            pelangganTersisaLevel = 10;
            targetPoinLevel = 600;
            addMeja(1, 2, 0);
            addMeja(2, 2, 1);
            addMeja(3, 3, 2);
            addMeja(4, 4, 3);
        } else if (level == 4) {
            pelangganTersisaLevel = 12;
            targetPoinLevel = 800;
            addMeja(1, 2, 0);
            addMeja(2, 3, 1);
            addMeja(3, 4, 2);
            addMeja(4, 4, 3);
        } else if (level == 5) {
            pelangganTersisaLevel = 15;
            targetPoinLevel = 1200;
            addMeja(1, 3, 0);
            addMeja(2, 4, 1);
            addMeja(3, 4, 2);
            addMeja(4, 4, 3);
        }

        daftarStasiun.add(new CoffeeStation(300, 80));
        daftarStasiun.add(new MilkStation(420, 80));
        daftarStasiun.add(new ToppingStation(540, 80));
    }

    private void addMeja(int id, int kapasitas, int layoutIndex) {
        int x = TABLE_LAYOUT_X[currentLevel - 1][layoutIndex];
        daftarMeja.add(new TableEntity(id, kapasitas, x, TABLE_Y));
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
