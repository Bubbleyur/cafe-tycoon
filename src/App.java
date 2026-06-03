import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class App {
    private GameEngine engine;
    private JFrame frame;
    private JPanel containerPanel; 
    private CardLayout cardLayout;
    private boolean gameSedangBerjalan = false;

    // Identitas Layar (Card)
    private final String SCREEN_MAIN_MENU = "MainMenu";
    private final String SCREEN_GAMEPLAY = "Gameplay";
    private final String SCREEN_SHOP = "StockShop";
    private final String SCREEN_RENOVATE = "RenovateShop"; // NEW

    // Layout Panels
    private JPanel panelSampingGame;
    private JPanel panelInfoGame;
    private JPanel panelInfoStatusMenu;
    private JPanel panelInfoToko;
    private JPanel panelDaftarUpgradeMeja; // NEW

    // UI Labels
    private JLabel lblMenuCash, lblMenuStokKopi, lblMenuStokSusu, lblMenuStokTopping;
    private JLabel lblGameLevel, lblGamePoin, lblGamePelanggan, lblGameTanganBarista;
    private JLabel lblGameCash, lblGameStokKopi, lblGameStokSusu, lblGameStokTopping;
    private JLabel lblShopCash, lblShopKopi, lblShopSusu, lblShopTopping;
    private JLabel lblRenovateCash; // NEW

    private JPanel panelMeja, panelStasiun;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().inisialisasiGUI());
    }

    public App() {
        this.engine = new GameEngine();
    }

    public void inisialisasiGUI() {
        frame = new JFrame("Cafe Tycoon - Diner Dash Edition");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 650);

        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);

        inisialisasiSemuaLabel();

        JPanel panelMainMenu = buatLayarMainMenu();
        JPanel panelGameplay = buatLayarGameplay();
        JPanel panelStockShop = buatLayarStockShop();
        JPanel panelRenovateShop = buatLayarRenovateShop(); // NEW

        containerPanel.add(panelMainMenu, SCREEN_MAIN_MENU);
        containerPanel.add(panelGameplay, SCREEN_GAMEPLAY);
        containerPanel.add(panelStockShop, SCREEN_SHOP);
        containerPanel.add(panelRenovateShop, SCREEN_RENOVATE); // NEW

        frame.add(containerPanel);
        cardLayout.show(containerPanel, SCREEN_MAIN_MENU);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void inisialisasiSemuaLabel() {
        InventoryManager inv = InventoryManager.getInstance();

        lblMenuCash = new JLabel("Cash Counter: $" + inv.getSaldoUang());
        lblMenuStokKopi = new JLabel("Stok Biji Kopi: " + inv.getStokBijiKopi() + " unit");
        lblMenuStokSusu = new JLabel("Stok Susu: " + inv.getStokSusu() + " unit");
        lblMenuStokTopping = new JLabel("Stok Topping: " + inv.getStokTopping() + " unit");

        lblGameLevel = new JLabel("Level: " + engine.getCurrentLevel(), SwingConstants.CENTER);
        lblGamePoin = new JLabel("Poin: " + engine.getPoinAwal(), SwingConstants.CENTER);
        lblGamePelanggan = new JLabel("Sisa Antrean: " + engine.getAntreanLuarTersisa(), SwingConstants.CENTER);
        lblGameTanganBarista = new JLabel("Tangan: " + engine.getPlayerBarista().getItemOnHand(), SwingConstants.CENTER);

        lblGameCash = new JLabel("Cash: $" + inv.getSaldoUang());
        lblGameStokKopi = new JLabel("Kopi: " + inv.getStokBijiKopi());
        lblGameStokSusu = new JLabel("Susu: " + inv.getStokSusu());
        lblGameStokTopping = new JLabel("Topping: " + inv.getStokTopping());

        lblShopCash = new JLabel("Uang Cafe: $", SwingConstants.CENTER);
        lblShopKopi = new JLabel("Stok Kopi: ", SwingConstants.CENTER);
        lblShopSusu = new JLabel("Stok Susu: ", SwingConstants.CENTER);
        lblShopTopping = new JLabel("Stok Topping: ", SwingConstants.CENTER);

        lblRenovateCash = new JLabel("Uang Cafe: $" + inv.getSaldoUang(), SwingConstants.CENTER); // NEW
        lblRenovateCash.setFont(new Font("Arial", Font.BOLD, 18));
    }

    JButton btnStart = new JButton("Start Game");
    private JPanel buatLayarMainMenu() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(245, 222, 179)); 
        panel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JLabel lblJudul = new JLabel("CAFE TYCOON", SwingConstants.CENTER);
        lblJudul.setFont(new Font("Serif", Font.BOLD, 48));
        lblJudul.setForeground(new Color(139, 69, 19));
        panel.add(lblJudul, BorderLayout.NORTH);

        JPanel panelTombol = new JPanel(new GridLayout(4, 1, 0, 15)); // Diubah jadi 4 slot tombol
        panelTombol.setOpaque(false);

        JButton btnShop = new JButton("Stock Shop (Beli Bahan)");
        JButton btnRenovate = new JButton("Renovation Shop (Beli/Upgrade Meja)"); // NEW
        JButton btnExit = new JButton("Exit Game");

        Font fontTombol = new Font("Arial", Font.BOLD, 18);
        btnStart.setFont(fontTombol);
        btnShop.setFont(fontTombol);
        btnRenovate.setFont(fontTombol);
        btnRenovate.setBackground(new Color(210, 180, 140));
        btnExit.setFont(fontTombol);

        panelTombol.add(btnStart);
        panelTombol.add(btnShop);
        panelTombol.add(btnRenovate); // NEW
        panelTombol.add(btnExit);
        panel.add(panelTombol, BorderLayout.CENTER);

        panelInfoStatusMenu = new JPanel(new GridLayout(5, 1, 10, 10));
        panelInfoStatusMenu.setBorder(BorderFactory.createTitledBorder("Cafe Status & Counter"));
        panelInfoStatusMenu.setPreferredSize(new Dimension(300, 0));

        Font fontInfo = new Font("Arial", Font.PLAIN, 14);
        lblMenuCash.setFont(new Font("Arial", Font.BOLD, 16));
        lblMenuStokKopi.setFont(fontInfo);
        lblMenuStokSusu.setFont(fontInfo);
        lblMenuStokTopping.setFont(fontInfo);

        panelInfoStatusMenu.add(lblMenuCash);
        panelInfoStatusMenu.add(new JSeparator());
        panelInfoStatusMenu.add(lblMenuStokKopi);
        panelInfoStatusMenu.add(lblMenuStokSusu);
        panelInfoStatusMenu.add(lblMenuStokTopping);
        panel.add(panelInfoStatusMenu, BorderLayout.EAST);

        btnStart.addActionListener(e -> {
            if (!gameSedangBerjalan) {
                gameSedangBerjalan = true;
                engine.initLevel(engine.getCurrentLevel()); // Mulai dari Level 1[cite: 26]
            } else {
                System.out.println("Melanjutkan gameplay...");
            }
            
            // SINKRONISASI UI SEBELUM LAYAR DI-SWITCH![cite: 26]
            perbaruiSemuaTeksUI();
            segarkanKomponenDinamisGameplay();
            
            cardLayout.show(containerPanel, SCREEN_GAMEPLAY);
        });

        btnShop.addActionListener(e -> {
            perbaruiSemuaTeksUI();
            cardLayout.show(containerPanel, SCREEN_SHOP);
        });

        btnRenovate.addActionListener(e -> {
            perbaruiSemuaTeksUI();
            segarkanMenuRenovasiMeja();
            cardLayout.show(containerPanel, SCREEN_RENOVATE);
        });

        btnExit.addActionListener(e -> System.exit(0));

        return panel;
    }

    private JPanel buatLayarGameplay() {
        JPanel panelUtama = new JPanel(new BorderLayout(10, 10));

        panelInfoGame = new JPanel(new GridLayout(1, 4, 10, 10));
        panelInfoGame.setBorder(BorderFactory.createTitledBorder("Status Level"));
        
        panelInfoGame.add(lblGameLevel);
        panelInfoGame.add(lblGamePoin);
        panelInfoGame.add(lblGamePelanggan);
        panelInfoGame.add(lblGameTanganBarista);

        panelSampingGame = new JPanel(new GridLayout(5, 1, 5, 5));
        panelSampingGame.setBorder(BorderFactory.createTitledBorder("Live Counter"));
        panelSampingGame.setPreferredSize(new Dimension(200, 0));

        JButton btnBeliStokInstan = new JButton("Buka Toko Stok");
        JButton btnKeMainMenu = new JButton("Kembali ke Menu");

        Font fontLive = new Font("Monospaced", Font.BOLD, 15);
        lblGameCash.setFont(fontLive);
        lblGameStokKopi.setFont(fontLive);
        lblGameStokSusu.setFont(fontLive);
        lblGameStokTopping.setFont(fontLive);

        panelSampingGame.add(lblGameCash);
        panelSampingGame.add(lblGameStokKopi);
        panelSampingGame.add(lblGameStokSusu);
        panelSampingGame.add(lblGameStokTopping);

        JPanel panelAksiSamping = new JPanel(new GridLayout(2, 1, 5, 5));
        panelAksiSamping.add(btnBeliStokInstan);
        panelAksiSamping.add(btnKeMainMenu);
        panelSampingGame.add(panelAksiSamping);

        JPanel panelAreaGame = new JPanel(new GridLayout(2, 1, 10, 10));
        panelMeja = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelMeja.setBorder(BorderFactory.createTitledBorder("Meja Pelanggan"));
        panelStasiun = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelStasiun.setBorder(BorderFactory.createTitledBorder("Stasiun Pembuatan"));
        panelAreaGame.add(panelMeja);
        panelAreaGame.add(panelStasiun);

        JPanel panelKontrolBawah = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnPelangganBaru = new JButton("Datangkan Pelanggan Baru");
        JButton btnBuangRacikan = new JButton("Buang Racikan");
        JButton btnSelesaiLevel = new JButton("Selesaikan Level & Evaluasi");
        btnSelesaiLevel.setBackground(new Color(255, 215, 0));
        btnSelesaiLevel.setFont(new Font("Arial", Font.BOLD, 13));

        panelKontrolBawah.add(btnPelangganBaru);
        panelKontrolBawah.add(btnBuangRacikan);
        panelKontrolBawah.add(btnSelesaiLevel);

        panelUtama.add(panelInfoGame, BorderLayout.NORTH);
        panelUtama.add(panelAreaGame, BorderLayout.CENTER);
        panelUtama.add(panelSampingGame, BorderLayout.EAST);
        panelUtama.add(panelKontrolBawah, BorderLayout.SOUTH);

        btnPelangganBaru.addActionListener(e -> {
            if (engine.getAntreanLuarTersisa() <= 0) {
                JOptionPane.showMessageDialog(frame, "Antrean di luar sudah kosong untuk level ini!");
                return;
            }

            Table mejaKosong = null;
            for (Table m : engine.getDaftarMeja()) {
                if (m.getStatus().equalsIgnoreCase("Free")) {
                    mejaKosong = m;
                    break;
                }
            }

            if (mejaKosong != null) {
                Customer c = new Customer();
                boolean sukses = mejaKosong.occupyMeja(c);
                if (sukses) {
                    engine.kurangiAntreanMasukKafe(); 
                    perbaruiSemuaTeksUI();
                    segarkanKomponenDinamisGameplay();
                    JOptionPane.showMessageDialog(frame, "Pelanggan baru datang (" + c.getJumlahOrang() + " orang) di Meja " + mejaKosong.getIdMeja());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Semua meja penuh! Bersihkan meja atau selesaikan pesanan terlebih dahulu.");
            }
        });

        btnBuangRacikan.addActionListener(e -> {
            engine.getPlayerBarista().clearHand();
            perbaruiSemuaTeksUI();
        });

        btnSelesaiLevel.addActionListener(e -> {
            if (engine.getAntreanLuarTersisa() > 0) {
                JOptionPane.showMessageDialog(frame, "Belum bisa menyelesaikan level! Masih ada " 
                        + engine.getAntreanLuarTersisa() + " pelanggan tersisa.");
                return;
            }

            for (Table m : engine.getDaftarMeja()) {
                if (!m.getStatus().equalsIgnoreCase("Free")) {
                    JOptionPane.showMessageDialog(frame, "Selesaikan semua meja! Pastikan semua meja sudah dibersihkan.");
                    return;
                }
            }

            final int levelSebelumAksi = engine.getCurrentLevel();
            boolean targetTercapai = engine.getPoinAwal() >= engine.getTargetPoinLevel();
            
            engine.cekKondisiEndLevel(); 

            if (targetTercapai) {
                JOptionPane.showMessageDialog(frame, "Selamat! Anda menyelesaikan Level " + levelSebelumAksi + "!\nKembali ke Menu Utama.");
            } else {
                JOptionPane.showMessageDialog(frame, "Target poin gagal dicapai! Level " + levelSebelumAksi + " akan diulang.");
            }

            gameSedangBerjalan = false; 
            engine.initLevel(engine.getCurrentLevel()); 
            perbaruiSemuaTeksUI();
            cardLayout.show(containerPanel, SCREEN_MAIN_MENU);
        });

        btnBeliStokInstan.addActionListener(e -> {
            perbaruiSemuaTeksUI();
            cardLayout.show(containerPanel, SCREEN_SHOP);
        });

        btnKeMainMenu.addActionListener(e -> cardLayout.show(containerPanel, SCREEN_MAIN_MENU));

        return panelUtama;
    }

    private JPanel buatLayarStockShop() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel lblToko = new JLabel("STOCK LOGISTIC SHOP", SwingConstants.CENTER);
        lblToko.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(lblToko, BorderLayout.NORTH);

        panelInfoToko = new JPanel(new GridLayout(1, 4, 10, 10));
        panelInfoToko.setBorder(BorderFactory.createTitledBorder("Dompet & Inventaris Saat Ini"));
        panelInfoToko.add(lblShopCash);
        panelInfoToko.add(lblShopKopi);
        panelInfoToko.add(lblShopSusu);
        panelInfoToko.add(lblShopTopping);
        panel.add(panelInfoToko, BorderLayout.CENTER);

        JPanel panelBeli = new JPanel(new GridLayout(2, 3, 20, 20));
        panelBeli.setBorder(BorderFactory.createTitledBorder("Beli Bahan (Harga: $150 per 10 unit)"));

        JButton btnBeliKopi = new JButton("Beli 10 Biji Kopi ($150)");
        JButton btnBeliSusu = new JButton("Beli 10 Susu ($150)");
        JButton btnBeliTopping = new JButton("Beli 10 Topping ($150)");
        JButton btnKembali = new JButton("<< Selesai / Kembali");
        btnKembali.setBackground(new Color(255, 204, 204));

        panelBeli.add(btnBeliKopi);
        panelBeli.add(btnBeliSusu);
        panelBeli.add(btnBeliTopping);
        panelBeli.add(new JLabel("")); 
        panelBeli.add(btnKembali);

        panel.add(panelBeli, BorderLayout.SOUTH);

        InventoryManager inv = InventoryManager.getInstance();

        btnBeliKopi.addActionListener(e -> { inv.beliStokBahan("Kopi", 150, 10); perbaruiSemuaTeksUI(); });
        btnBeliSusu.addActionListener(e -> { inv.beliStokBahan("Susu", 150, 10); perbaruiSemuaTeksUI(); });
        btnBeliTopping.addActionListener(e -> { inv.beliStokBahan("Topping", 150, 10); perbaruiSemuaTeksUI(); });

        btnKembali.addActionListener(e -> {
            perbaruiSemuaTeksUI();
            if (gameSedangBerjalan) cardLayout.show(containerPanel, SCREEN_GAMEPLAY);
            else cardLayout.show(containerPanel, SCREEN_MAIN_MENU);
        });

        return panel;
    }

    // NEW LAYOUT METHOD: Renovation Shop Screen Panel GUI Build
    private JPanel buatLayarRenovateShop() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(230, 240, 250));

        JLabel lblJudul = new JLabel("RENOVATION & FURNITURE SHOP", SwingConstants.CENTER);
        lblJudul.setFont(new Font("Arial", Font.BOLD, 26));
        panel.add(lblJudul, BorderLayout.NORTH);

        JPanel panelAtas = new JPanel(new BorderLayout(10, 10));
        panelAtas.setOpaque(false);
        panelAtas.add(lblRenovateCash, BorderLayout.NORTH);

        JButton btnBeliMejaBaru = new JButton("Beli Tambah Meja Baru (+ $500)");
        btnBeliMejaBaru.setFont(new Font("Arial", Font.BOLD, 16));
        btnBeliMejaBaru.setBackground(new Color(144, 238, 144));
        panelAtas.add(btnBeliMejaBaru, BorderLayout.CENTER);
        panel.add(panelAtas, BorderLayout.NORTH);

        panelDaftarUpgradeMeja = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panelDaftarUpgradeMeja.setBorder(BorderFactory.createTitledBorder("Daftar Upgrade Kursi Meja Kafe Saat Ini (Biaya Upgrade: $300)"));
        
        JScrollPane scrollPane = new JScrollPane(panelDaftarUpgradeMeja);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton btnKembali = new JButton("<< Kembali ke Menu Utama");
        btnKembali.setFont(new Font("Arial", Font.BOLD, 14));
        btnKembali.setBackground(new Color(255, 204, 204));
        panel.add(btnKembali, BorderLayout.SOUTH);

        InventoryManager inv = InventoryManager.getInstance();

        btnBeliMejaBaru.addActionListener(e -> {
            if (inv.getSaldoUang() >= 500) {
                inv.kurangiUang(500); // Pastikan kamu menambahkan metode kurangiUang di InventoryManager jika dibutuhkan, atau bisa di-tweak
                engine.tambahMejaBaru();

                perbaruiSemuaTeksUI();
                segarkanMenuRenovasiMeja();

                JOptionPane.showMessageDialog(frame, "Meja baru berhasil ditambahkan!");
            } else {
                JOptionPane.showMessageDialog(frame, "Uang cafe tidak mencukupi untuk menambah meja!");
            }
        });

        btnKembali.addActionListener(e -> {
            perbaruiSemuaTeksUI();
            segarkanKomponenDinamisGameplay();

            cardLayout.show(containerPanel, SCREEN_MAIN_MENU);
        });

        return panel;
    }

    // NEW VISUAL UPDATER: Regenerates individual dynamic upgrade modules 
    private void segarkanMenuRenovasiMeja() {
        panelDaftarUpgradeMeja.removeAll();
        InventoryManager inv = InventoryManager.getInstance();

        ArrayList<Table> daftarMeja = engine.getDaftarMeja();
        for (Table meja : daftarMeja) {
            JPanel panelItem = new JPanel(new BorderLayout(5, 5));
            panelItem.setPreferredSize(new Dimension(180, 120));
            panelItem.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

            String deskripsiMeja = "<html><center><b>Meja " + meja.getIdMeja() + "</b><br>"
                    + "Kapasitas: " + meja.getKapasitasKursi() + " Kursi<br>"
                    + "Level Meja: Lv " + meja.getLevel() + "</center></html>";
            JLabel lblDesc = new JLabel(deskripsiMeja, SwingConstants.CENTER);
            panelItem.add(lblDesc, BorderLayout.CENTER);

            JButton btnUpgrade = new JButton("Upgrade ($300)");
            if (meja.getLevel() >= 4) {
                btnUpgrade.setText("MAX LEVEL");
                btnUpgrade.setEnabled(false);
            }

            btnUpgrade.addActionListener(e -> {
                if (inv.getSaldoUang() >= 300) {
                    boolean sukses = meja.upgradeMeja();
                    if (sukses) {
                        inv.kurangiUang(300);

                        perbaruiSemuaTeksUI();
                        segarkanMenuRenovasiMeja();

                        JOptionPane.showMessageDialog(frame, "Meja " + meja.getIdMeja() + " berhasil di-upgrade ke Level " + meja.getLevel());
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Uang cafe tidak mencukupi untuk upgrade!");
                }
            });

            panelItem.add(btnUpgrade, BorderLayout.SOUTH);
            panelDaftarUpgradeMeja.add(panelItem);
        }

        panelDaftarUpgradeMeja.revalidate();
        panelDaftarUpgradeMeja.repaint();
    }
    
    private void perbaruiSemuaTeksUI() {
        InventoryManager inv = InventoryManager.getInstance();

        lblMenuCash.setText("Cash Counter: $" + inv.getSaldoUang());
        lblMenuStokKopi.setText("Stok Biji Kopi: " + inv.getStokBijiKopi() + " unit");
        lblMenuStokSusu.setText("Stok Susu: " + inv.getStokSusu() + " unit");
        lblMenuStokTopping.setText("Stok Topping: " + inv.getStokTopping() + " unit");

        lblGameLevel.setText("Level: " + engine.getCurrentLevel());
        lblGamePoin.setText("Poin: " + engine.getPoinAwal() + " / Target: " + engine.getTargetPoinLevel());
        // lblGamePelanggan.setText("Sisa Pelanggan: " + engine.getPelangganTersisaLevel() + " | Antrean Luar: " + engine.getAntreanLuarTersisa());
        lblGamePelanggan.setText("Sisa Antrean: " + engine.getAntreanLuarTersisa());
        lblGameTanganBarista.setText("Tangan: " + engine.getPlayerBarista().translateItemOnHand(engine.getPlayerBarista().getItemOnHand()));
        
        lblGameCash.setText("Cash: $" + inv.getSaldoUang());
        lblGameStokKopi.setText("Kopi: " + inv.getStokBijiKopi());
        lblGameStokSusu.setText("Susu: " + inv.getStokSusu());
        lblGameStokTopping.setText("Topping: " + inv.getStokTopping());

        lblShopCash.setText("Uang Cafe: $" + inv.getSaldoUang());
        lblShopKopi.setText("Stok Kopi: " + inv.getStokBijiKopi());
        lblShopSusu.setText("Stok Susu: " + inv.getStokSusu());
        lblShopTopping.setText("Stok Topping: " + inv.getStokTopping());

        lblRenovateCash.setText("Uang Cafe: $" + inv.getSaldoUang());

        if (panelSampingGame != null) { panelSampingGame.revalidate(); panelSampingGame.repaint(); }
        if (panelInfoGame != null) { panelInfoGame.revalidate(); panelInfoGame.repaint(); }

        if (btnStart != null) {
            if (gameSedangBerjalan) {
                btnStart.setText("Continue Game (Level " + engine.getCurrentLevel() + ")");
                btnStart.setBackground(new Color(144, 238, 144)); // Beri warna hijau penanda continue
            } else {
                btnStart.setText("Start Game");
                btnStart.setBackground(null); // Warna default swing
            }
        }

        lblMenuCash.setText("Cash Counter: $" + inv.getSaldoUang());
    }

    private void segarkanKomponenDinamisGameplay() {
        panelMeja.removeAll();
        panelStasiun.removeAll();

        ArrayList<Table> daftarMeja = engine.getDaftarMeja();

        for (Table meja : daftarMeja) {
            String teksMeja = "<html><center><b>Meja " + meja.getIdMeja() + "</b><br>"
                    + "Kursi: " + meja.getKapasitasKursi() + " (Lv " + meja.getLevel() + ")<br>"
                    + "[" + meja.getStatus() + "]</center></html>";
            
            JButton btnMeja = new JButton(teksMeja);
            btnMeja.setPreferredSize(new Dimension(130, 95));

            if (meja.getStatus().equalsIgnoreCase("Free")) {
                btnMeja.setBackground(new Color(144, 238, 144));
            } 
            else if (meja.getStatus().equalsIgnoreCase("Occupied")) {
                btnMeja.setBackground(new Color(255, 165, 0));
                if (meja.getCurrentCustomer() != null) {
                    btnMeja.setToolTipText("Pesanan: " + meja.getCurrentCustomer().getJenisPesanan() 
                            + " (" + meja.getCurrentCustomer().getPorsiTersisa() + " porsi)");
                }
            } 
            else if (meja.getStatus().equalsIgnoreCase("Dirty")) {
                btnMeja.setBackground(new Color(220, 20, 60));
            }

            btnMeja.addActionListener(e -> {
                if (meja.status.equalsIgnoreCase("Occupied")) {
                    if (meja.currentCustomer != null && meja.currentCustomer.getPorsiTersisa() == 0) {
                        meja.bayarPesanan(engine);
                        meja.kosongkanMeja(); 
                        engine.pelangganSelesai();
                        JOptionPane.showMessageDialog(frame, "Pembayaran Berhasil! Meja sekarang kotor.");
                    } else {
                        meja.sajikanKopiKeMeja(engine.getPlayerBarista(), engine);
                        if (meja.currentCustomer != null && meja.currentCustomer.getPorsiTersisa() == 0) {
                            meja.bayarPesanan(engine);
                            meja.kosongkanMeja();
                            engine.pelangganSelesai();
                            JOptionPane.showMessageDialog(frame, "Pesanan Terpenuhi! Meja sekarang kotor.");
                        }
                    }
                }
                else if (meja.status.equalsIgnoreCase("Dirty")) {
                    meja.bersihkanMeja();
                    JOptionPane.showMessageDialog(frame, "Meja " + meja.getIdMeja() + " selesai dibersihkan!");
                }
                
                perbaruiSemuaTeksUI();
                segarkanKomponenDinamisGameplay();
            });
            
            panelMeja.add(btnMeja);
        }

        ArrayList<Station> daftarStasiun = engine.getDaftarStasiun();
        ArrayList<String> stasiunSudahAda = new ArrayList<>();

        for (Station stasiun : daftarStasiun) {
            if (stasiunSudahAda.contains(stasiun.stationName)) continue; 
            stasiunSudahAda.add(stasiun.stationName);

            JButton btnStasiun = new JButton(stasiun.stationName);
            btnStasiun.setPreferredSize(new Dimension(140, 55));
            btnStasiun.setBackground(new Color(173, 216, 230));

            btnStasiun.addActionListener(e -> {
                if (stasiun instanceof ToppingStation) {
                    String[] toppings = {"Caramel", "Chocolate", "Whipped Cream", "Boba"};
                    String pilihan = (String) JOptionPane.showInputDialog(frame, "Pilih Topping:", "Counter Topping",
                            JOptionPane.QUESTION_MESSAGE, null, toppings, toppings[0]);
                    if (pilihan != null) stasiun.interact(engine.getPlayerBarista(), pilihan);
                } else {
                    stasiun.interact(engine.getPlayerBarista(), "");
                }
                perbaruiSemuaTeksUI();
                segarkanKomponenDinamisGameplay();
            });
            panelStasiun.add(btnStasiun);
        }   

        panelMeja.revalidate(); panelMeja.repaint();
        panelStasiun.revalidate(); panelStasiun.repaint();
    }
}