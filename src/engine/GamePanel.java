package engine;

import entity.Customer;
import entity.Player;
import entity.TableEntity;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import logic.*;
import station.*;
import ui.HUD;
import ui.MenuUI;
import ui.SpriteDialogBox;

public class GamePanel extends JPanel {

    public enum GameState {
        MENU, PLAYING, PAUSED, GAMEOVER, LEVEL_WON, LEVEL_FAILED, GAME_WON
    }

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private GameState state = GameState.MENU;
    private String selectedTopping = ToppingStation.DAFTAR_TOPPING[0];
    private String supplyChoice = "1";

    private GameEngine gameEngine;
    private Player player;
    private List<Station> extraStations;

    private PlayerLogic playerLogic;
    private CustomerLogic customerLogic;
    private DifficultyManager difficultyManager;

    private HUD hud;
    private MenuUI menuUI;

    private final boolean[] keysPressed = new boolean[4];

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(62, 39, 35));
        setFocusable(true);

        AssetManager.loadAssets();
        hud = new HUD();
        menuUI = new MenuUI();
        resetGame();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e);
            }
        });
    }

    public void resetGame() {
        InventoryManager.resetInstance();
        gameEngine = new GameEngine();
        player = gameEngine.getPlayerBarista();
        extraStations = new ArrayList<>();
        extraStations.add(new SupplyStation(180, 80));
        extraStations.add(new CashierCounter(50, 400));

        playerLogic = new PlayerLogic(player, gameEngine);
        customerLogic = new CustomerLogic();
        difficultyManager = new DifficultyManager();
        difficultyManager.applyLevel(1);

        for (int i = 0; i < keysPressed.length; i++) {
            keysPressed[i] = false;
        }
        state = GameState.MENU;
    }

    private void startLevel(int level) {
        InventoryManager.resetInstance();
        gameEngine = new GameEngine();
        if (level > 1) {
            for (int i = 1; i < level; i++) {
                gameEngine.advanceLevel();
            }
        }
        player = gameEngine.getPlayerBarista();
        customerLogic.clearCustomers();
        difficultyManager.applyLevel(gameEngine.getCurrentLevel());
        playerLogic = new PlayerLogic(player, gameEngine);
        state = GameState.PLAYING;
        requestFocusInWindow();
    }

    public void updatePhysics() {
        if (state != GameState.PLAYING) {
            return;
        }

        playerLogic.update(keysPressed, WIDTH, HEIGHT);
        customerLogic.update(gameEngine, difficultyManager);

        GameEngine.LevelResult result = gameEngine.cekKondisiEndLevel(customerLogic.getCustomers().size());
        switch (result) {
            case LEVEL_WON:
                state = GameState.LEVEL_WON;
                break;
            case LEVEL_FAILED:
                state = GameState.LEVEL_FAILED;
                break;
            case GAME_WON:
                state = GameState.GAME_WON;
                break;
            default:
                break;
        }
    }

    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();

        switch (state) {
            case MENU:
                if (key == KeyEvent.VK_ENTER) {
                    startLevel(1);
                }
                break;
            case PLAYING:
                if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) keysPressed[0] = true;
                if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) keysPressed[1] = true;
                if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) keysPressed[2] = true;
                if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) keysPressed[3] = true;

                if (key == KeyEvent.VK_1) {
                    selectedTopping = ToppingStation.DAFTAR_TOPPING[0];
                    supplyChoice = "1";
                }
                if (key == KeyEvent.VK_2) {
                    selectedTopping = ToppingStation.DAFTAR_TOPPING[1];
                    supplyChoice = "2";
                }
                if (key == KeyEvent.VK_3) {
                    selectedTopping = ToppingStation.DAFTAR_TOPPING[2];
                    supplyChoice = "3";
                }
                if (key == KeyEvent.VK_4) {
                    selectedTopping = ToppingStation.DAFTAR_TOPPING[3];
                    supplyChoice = "4";
                }

                if (key == KeyEvent.VK_E || key == KeyEvent.VK_SPACE) {
                    Station near = findNearestStation();
                    String sub = (near instanceof SupplyStation) ? supplyChoice : selectedTopping;
                    playerLogic.tryInteract(extraStations, sub);
                }
                if (key == KeyEvent.VK_ESCAPE) {
                    state = GameState.PAUSED;
                }
                break;
            case PAUSED:
                if (key == KeyEvent.VK_ESCAPE) {
                    state = GameState.PLAYING;
                }
                if (key == KeyEvent.VK_R) {
                    gameEngine.retryLevel();
                    player = gameEngine.getPlayerBarista();
                    customerLogic.clearCustomers();
                    playerLogic = new PlayerLogic(player, gameEngine);
                    state = GameState.PLAYING;
                }
                break;
            case LEVEL_WON:
                if (key == KeyEvent.VK_ENTER) {
                    gameEngine.advanceLevel();
                    player = gameEngine.getPlayerBarista();
                    customerLogic.clearCustomers();
                    difficultyManager.applyLevel(gameEngine.getCurrentLevel());
                    playerLogic = new PlayerLogic(player, gameEngine);
                    state = GameState.PLAYING;
                }
                break;
            case LEVEL_FAILED:
                if (key == KeyEvent.VK_R) {
                    gameEngine.retryLevel();
                    player = gameEngine.getPlayerBarista();
                    customerLogic.clearCustomers();
                    playerLogic = new PlayerLogic(player, gameEngine);
                    state = GameState.PLAYING;
                }
                break;
            case GAME_WON:
            case GAMEOVER:
                if (key == KeyEvent.VK_R) {
                    resetGame();
                }
                break;
            default:
                break;
        }
    }

    private Station findNearestStation() {
        int px = player.getX() + player.getWidth() / 2;
        int py = player.getY() + player.getHeight() / 2;
        Station nearest = null;
        double min = 70;
        List<Station> all = new ArrayList<>(gameEngine.getDaftarStasiun());
        all.addAll(extraStations);
        for (Station s : all) {
            double d = Math.hypot(px - (s.getX() + s.getWidth() / 2), py - (s.getY() + s.getHeight() / 2));
            if (d < min) {
                min = d;
                nearest = s;
            }
        }
        return nearest;
    }

    private void handleKeyRelease(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) keysPressed[0] = false;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) keysPressed[1] = false;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) keysPressed[2] = false;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) keysPressed[3] = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawFloor(g2);

        g2.setColor(new Color(109, 76, 65));
        g2.fill(new Rectangle2D.Float(0, 75, WIDTH, 45));
        g2.setColor(new Color(215, 204, 200));
        g2.fillRect(0, 117, WIDTH, 3);
        g2.setColor(new Color(93, 64, 55, 120));
        g2.fill(new RoundRectangle2D.Float(15, 135, 110, 440, 12, 12));

        for (TableEntity t : gameEngine.getDaftarMeja()) {
            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillOval(t.getX() - 4, t.getY() + 38, t.getWidth() + 8, 18);
            g2.drawImage(AssetManager.tableSprite, t.getX(), t.getY(), t.getWidth(), t.getHeight(), null);

            if (t.isDirty()) {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString("DIRTY", t.getX() + 18, t.getY() + 15);
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 8));
            g2.setColor(new Color(255, 255, 255, 180));
            g2.drawString("Cap:" + t.getKapasitasKursi(), t.getX() + 4, t.getY() + 58);
        }

        List<Station> drawStations = new ArrayList<>(gameEngine.getDaftarStasiun());
        drawStations.addAll(extraStations);
        for (Station s : drawStations) {
            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillOval(s.getX() + 2, s.getY() + 48, s.getWidth() - 4, 12);

            java.awt.image.BufferedImage img = null;
            if (s instanceof SupplyStation) img = AssetManager.stockStationSprite;
            else if (s instanceof CoffeeStation) img = AssetManager.coffeeMachineSprite;
            else if (s instanceof MilkStation) img = AssetManager.milkDispenserSprite;
            else if (s instanceof ToppingStation) img = AssetManager.toppingStationSprite;
            else if (s instanceof CashierCounter) img = AssetManager.cashierCounterSprite;

            if (img != null) {
                g2.drawImage(img, s.getX(), s.getY(), s.getWidth(), s.getHeight(), null);
            }

            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(new Color(230, 230, 230));
            String label = s.getName();
            g2.drawString(label, s.getX() + (s.getWidth() - g2.getFontMetrics().stringWidth(label)) / 2, s.getY() - 6);
        }

        for (Customer c : customerLogic.getCustomers()) {
            drawCustomer(g2, c);
        }

        drawPlayer(g2);
        hud.draw(g2, InventoryManager.getInstance(), gameEngine, player, WIDTH, selectedTopping, supplyChoice);

        switch (state) {
            case MENU:
                menuUI.drawMenu(g2, WIDTH, HEIGHT);
                break;
            case PAUSED:
                menuUI.drawPaused(g2, WIDTH, HEIGHT);
                break;
            case LEVEL_WON:
                menuUI.drawLevelWon(g2, WIDTH, HEIGHT, gameEngine);
                break;
            case LEVEL_FAILED:
                menuUI.drawLevelFailed(g2, WIDTH, HEIGHT, gameEngine);
                break;
            case GAME_WON:
                menuUI.drawGameWon(g2, WIDTH, HEIGHT);
                break;
            default:
                break;
        }

        g2.dispose();
    }

    private void drawFloor(Graphics2D g2) {
        int tileSize = 40;
        for (int row = 0; row < HEIGHT / tileSize; row++) {
            for (int col = 0; col < WIDTH / tileSize; col++) {
                g2.setColor((row + col) % 2 == 0 ? new Color(120, 80, 50) : new Color(100, 65, 38));
                g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }
    }

    private void drawCustomer(Graphics2D g2, Customer c) {
        int bob = c.getBobOffset();
        int cx = c.getX();
        int cy = c.getY() + bob;
        int drawW = 48;
        int drawH = 48;

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillOval(cx + 6, cy + drawH - 8, drawW - 8, 10);

        boolean walking = c.getState() == Customer.CustomerState.WAITING_FOR_TABLE
                || c.getState() == Customer.CustomerState.LEAVING_HAPPY
                || c.getState() == Customer.CustomerState.LEAVING_ANGRY;

        java.awt.image.BufferedImage frame = AssetManager.getChickenFrame(
                c.getId(), walking, System.currentTimeMillis());

        if (frame != null) {
            g2.drawImage(frame, cx + 8, cy + 8, cx + 8 + drawW, cy + 8 + drawH, 0, 0,
                    frame.getWidth(), frame.getHeight(), null);
        } else {
            g2.setColor(new Color(255, 220, 150));
            g2.fillOval(cx + 12, cy + 12, 36, 36);
        }

        if (c.getState() == Customer.CustomerState.WAITING_FOR_ORDER
                || c.getState() == Customer.CustomerState.WAITING_FOR_TABLE
                || c.getState() == Customer.CustomerState.SEATED) {
            String extra = c.getPorsiTersisa() > 1 ? "Porsi x" + c.getPorsiTersisa() : null;
            SpriteDialogBox.drawNpcOrder(g2, cx + drawW / 2, cy + 12,
                    c.getName(), c.getJenisPesanan(), extra);
        }

        drawPatienceHearts(g2, c, cx, cy);
    }

    private void drawPatienceHearts(Graphics2D g2, Customer c, int cx, int cy) {
        double ratio = c.getPatience() / c.getMaxPatience();
        int hearts = (int) Math.ceil(ratio * 3);
        int hx = cx + 4;
        int hy = cy - 4;
        for (int i = 0; i < 3; i++) {
            BufferedImage icon = AssetManager.iconHeartEmpty;
            if (i < hearts) {
                icon = AssetManager.iconHeartFull;
            } else if (i == hearts && ratio > 0 && ratio < 1) {
                icon = AssetManager.iconHeartHalf;
            }
            if (icon != null) {
                g2.drawImage(icon, hx + i * 12, hy, 10, 10, null);
            }
        }
    }

    private void drawPlayer(Graphics2D g2) {
        int dir = 0;
        switch (player.getDirection()) {
            case "up": dir = 1; break;
            case "left": dir = 2; break;
            case "right": dir = 3; break;
            default: dir = 0;
        }
        int frame = (int) ((System.currentTimeMillis() / 150) % 2);
        java.awt.image.BufferedImage frameImg = AssetManager.playerSprite;
        if (AssetManager.playerIdle != null && AssetManager.playerIdle[dir] != null) {
            frameImg = player.getCurrentAnimation().equals("walk")
                    ? AssetManager.playerWalk[dir][frame]
                    : AssetManager.playerIdle[dir][frame];
        }
        if (frameImg == null) {
            frameImg = AssetManager.playerSprite;
        }
        g2.drawImage(frameImg, player.getX(), player.getY(), player.getWidth(), player.getHeight(), null);

        if (!player.getItemOnHand().equalsIgnoreCase("None")) {
            SpriteDialogBox.draw(g2, player.getX() + 30, player.getY() - 42, 120, 40, null,
                    new String[]{player.getItemOnHand()});
        }
    }
}
