package engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

import entity.*;
import station.*;
import logic.*;
import ui.*;

public class GamePanel extends JPanel {

    public enum GameState {
        MENU,
        PLAYING,
        GAMEOVER,
        PAUSED
    }

    private GameState state = GameState.MENU;

    // Dimensions
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    // Entities & Stations
    private Player player;
    private List<TableEntity> tables;
    private List<Station> stations;

    // Logic Managers
    private PlayerLogic playerLogic;
    private CustomerLogic customerLogic;
    private InventoryManager inventoryManager;
    private DifficultyManager difficultyManager;

    // UI Renderers
    private HUD hud;
    private MenuUI menuUI;

    // Input States
    private boolean[] keysPressed = new boolean[4]; // 0: Up, 1: Down, 2: Left, 3: Right

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(62, 39, 35)); // Cozy Dark Wood Base
        setFocusable(true);

        // Load PNG Assets
        AssetManager.loadAssets();

        // Initialize UI overlays
        hud = new HUD();
        menuUI = new MenuUI();

        // Initialize the game objects
        resetGame();

        // Register Keyboard Listeners
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
        // Models
        player = new Player(350, 300);
        
        tables = new ArrayList<>();
        // Neatly aligned diner customer tables
        tables.add(new TableEntity(1, 200, 220));
        tables.add(new TableEntity(2, 380, 220));
        tables.add(new TableEntity(3, 560, 220));

        stations = new ArrayList<>();
        // Kitchen counter layout
        stations.add(new StockStation("ST_CUP", "Cup Stock", 180, 80));
        stations.add(new CoffeeStation("ST_COFFEE", "Coffee Brewer", 300, 80));
        stations.add(new MilkStation("ST_MILK", "Milk Station", 420, 80));
        stations.add(new ToppingStation("ST_TOPPING", "Topping Bar", 540, 80));
        
        // Cashier counter at the entrance
        stations.add(new CashierCounter("ST_CASHIER", "Cashier", 50, 400));

        // Logic Managers
        playerLogic = new PlayerLogic(player);
        customerLogic = new CustomerLogic();
        inventoryManager = new InventoryManager();
        difficultyManager = new DifficultyManager();

        // Clear keys
        for (int i = 0; i < keysPressed.length; i++) {
            keysPressed[i] = false;
        }
    }

    // Called ~60 times per second by GameLoop
    public void updatePhysics() {
        if (state != GameState.PLAYING) return;

        // Update player movement
        playerLogic.update(keysPressed, WIDTH, HEIGHT);

        // Update station progresses
        for (Station s : stations) {
            if (s instanceof CoffeeStation) {
                ((CoffeeStation) s).update();
            } else if (s instanceof MilkStation) {
                ((MilkStation) s).update();
            } else if (s instanceof ToppingStation) {
                ((ToppingStation) s).update();
            }
        }

        // Update customer queues and states
        customerLogic.update(tables, inventoryManager, difficultyManager);

        // Check lose condition: if reputation drops to 0
        if (inventoryManager.getReputation() <= 0) {
            state = GameState.GAMEOVER;
        }
    }

    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();

        if (state == GameState.MENU) {
            if (key == KeyEvent.VK_ENTER) {
                state = GameState.PLAYING;
            }
        } else if (state == GameState.PLAYING) {
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) keysPressed[0] = true;
            if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) keysPressed[1] = true;
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) keysPressed[2] = true;
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) keysPressed[3] = true;

            // Interact (E or Space)
            if (key == KeyEvent.VK_E || key == KeyEvent.VK_SPACE) {
                playerLogic.tryInteract(stations, tables, customerLogic.getCustomers(), inventoryManager);
            }

            // Pause
            if (key == KeyEvent.VK_ESCAPE) {
                state = GameState.PAUSED;
            }
        } else if (state == GameState.PAUSED) {
            if (key == KeyEvent.VK_ESCAPE) {
                state = GameState.PLAYING;
            }
            if (key == KeyEvent.VK_R) {
                resetGame();
                state = GameState.PLAYING;
            }
        } else if (state == GameState.GAMEOVER) {
            if (key == KeyEvent.VK_R) {
                resetGame();
                state = GameState.PLAYING;
            }
        }
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

        // Anti-aliasing for vectors, stars, rounded corners
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Draw Cafe Floor Tiles
        drawFloor(g2);

        // 2. Draw Kitchen Counter Base board
        g2.setColor(new Color(109, 76, 65)); // Wooden brown counter panel
        g2.fill(new Rectangle2D.Float(0, 75, WIDTH, 45));
        g2.setColor(new Color(215, 204, 200)); // Marble counter-top border line
        g2.fillRect(0, 117, WIDTH, 3);
        
        // Cozy carpet at queuing area
        g2.setColor(new Color(93, 64, 55, 120));
        g2.fill(new RoundRectangle2D.Float(15, 135, 110, 440, 12, 12));

        // 3. Draw Table Entities
        for (TableEntity t : tables) {
            // Shadow
            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillOval(t.getX() - 4, t.getY() + 38, t.getWidth() + 8, 18);

            // Sprite
            g2.drawImage(AssetManager.tableSprite, t.getX(), t.getY(), t.getWidth(), t.getHeight(), null);

            // Draw Dirty State overlays
            if (t.getState() == TableEntity.TableState.DIRTY) {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString("DIRTY", t.getX() + 18, t.getY() + 15);
                
                // Draw brown splash bubbles
                g2.setColor(new Color(121, 85, 72, 180));
                g2.fillOval(t.getX() + 15, t.getY() + 20, 8, 5);
                g2.fillOval(t.getX() + 38, t.getY() + 16, 6, 4);
            }
        }

        // 4. Draw Stations & Brewing Progress Bars
        for (Station s : stations) {
            // Sprite base shadow
            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillOval(s.getX() + 2, s.getY() + 48, s.getWidth() - 4, 12);

            // Choose correct image mapping
            java.awt.image.BufferedImage img = null;
            if (s instanceof StockStation) img = AssetManager.stockStationSprite;
            else if (s instanceof CoffeeStation) img = AssetManager.coffeeMachineSprite;
            else if (s instanceof MilkStation) img = AssetManager.milkDispenserSprite;
            else if (s instanceof ToppingStation) img = AssetManager.toppingStationSprite;
            else if (s instanceof CashierCounter) img = AssetManager.cashierCounterSprite;

            if (img != null) {
                g2.drawImage(img, s.getX(), s.getY(), s.getWidth(), s.getHeight(), null);
            }

            // Draw name labels below counters
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(new Color(230, 230, 230));
            g2.drawString(s.getName(), s.getX() + (s.getWidth() - g2.getFontMetrics().stringWidth(s.getName())) / 2, s.getY() - 6);

            // Render Progress Bar above station if busy
            drawStationProgress(g2, s);
        }

        // 5. Draw Active Customers
        for (Customer c : customerLogic.getCustomers()) {
            // Bobbing bounce height
            int bob = c.getBobOffset();
            double scale = c.getRenderScale();
            int cw = (int) (c.getWidth() * scale);
            int ch = (int) (c.getHeight() * scale);
            int cx = c.getX() + (c.getWidth() - cw) / 2;
            int cy = c.getY() + bob;

            // Base shadow
            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillOval(c.getX() + 8, c.getY() + 48, 48, 12);

            // Draw customer body / sprite
            java.awt.image.BufferedImage custFrame = AssetManager.playerIdle != null && AssetManager.playerIdle[0] != null ? 
                AssetManager.playerIdle[0][(int)((System.currentTimeMillis() / 300 + c.getId()) % 2)] : null;
            
            if (custFrame != null && custFrame != AssetManager.playerSprite) {
                // If Sprout Lands sheet is loaded, draw pixel art character with bob animation
                g2.drawImage(custFrame, cx, cy, cw, ch, null);
            } else {
                // Procedural cartoon fallback
                Color bodyColor = new Color(74, 20, 140); // Default Purple
                if (c.getId() % 3 == 1) bodyColor = new Color(13, 71, 161); // Blue customer
                else if (c.getId() % 3 == 2) bodyColor = new Color(216, 27, 96); // Magenta customer

                // Body
                g2.setColor(bodyColor);
                g2.fill(new RoundRectangle2D.Float(cx + 12, cy + 24, cw - 24, ch - 24, 12, 12));
                
                // Head
                g2.setColor(new Color(255, 213, 128));
                g2.fillOval(cx + 18, cy + 4, cw - 36, ch - 36);

                // Hair
                g2.setColor(new Color(62, 39, 35));
                g2.fillArc(cx + 16, cy + 2, cw - 32, 22, 0, 180);

                // Eyes & details
                g2.setColor(Color.BLACK);
                g2.fillOval(cx + 25, cy + 14, 3, 3);
                g2.fillOval(cx + 34, cy + 14, 3, 3);
            }

            // Render customer patience bars and bubble indicators
            drawCustomerHUD(g2, c, bob);
        }

        // 6. Draw Player Barista
        // Determine player direction index (Down=0, Up=1, Left=2, Right=3)
        int dirIndex = 0;
        switch (player.getDirection()) {
            case "down":  dirIndex = 0; break;
            case "up":    dirIndex = 1; break;
            case "left":  dirIndex = 2; break;
            case "right": dirIndex = 3; break;
        }

        // Alternate frame index every 150ms
        int frameIndex = (int) ((System.currentTimeMillis() / 150) % 2);

        // Get the active frame from the loaded spritesheet arrays
        java.awt.image.BufferedImage playerFrame = null;
        if (AssetManager.playerWalk != null && AssetManager.playerWalk[dirIndex] != null) {
            if (player.getCurrentAnimation().equals("walk")) {
                playerFrame = AssetManager.playerWalk[dirIndex][frameIndex];
            } else {
                playerFrame = AssetManager.playerIdle[dirIndex][frameIndex];
            }
        }

        // Fallback safety
        if (playerFrame == null) {
            playerFrame = AssetManager.playerSprite;
        }

        int playerBob = 0;
        // Apply smooth horizontal/vertical bob cycle only as a backup for static fallback sprite
        if (player.getCurrentAnimation().equals("walk") && playerFrame == AssetManager.playerSprite) {
            playerBob = (int) (Math.sin(System.currentTimeMillis() * 0.015) * 4);
        }

        g2.drawImage(
            playerFrame,
            player.getX(),
            player.getY() + playerBob,
            player.getWidth(),
            player.getHeight(),
            null
        );

        // Draw dynamic speech bubble above player showing exact item they hold
        drawPlayerItemBubble(g2, playerBob);

        // 7. Draw HUD Panel
        hud.draw(g2, inventoryManager, difficultyManager, player, WIDTH);

        // 8. Draw Screens overlays based on GameState
        if (state == GameState.MENU) {
            menuUI.drawMenu(g2, WIDTH, HEIGHT);
        } else if (state == GameState.PAUSED) {
            menuUI.drawPaused(g2, WIDTH, HEIGHT);
        } else if (state == GameState.GAMEOVER) {
            menuUI.drawGameOver(g2, WIDTH, HEIGHT, difficultyManager.getScore());
        }

        g2.dispose();
    }

    private void drawFloor(Graphics2D g2) {
        int tileSize = 40;
        for (int row = 0; row < HEIGHT / tileSize; row++) {
            for (int col = 0; col < WIDTH / tileSize; col++) {
                if ((row + col) % 2 == 0) {
                    g2.setColor(new Color(120, 80, 50));
                } else {
                    g2.setColor(new Color(100, 65, 38));
                }
                g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                
                // Fine floor texture border lines
                g2.setColor(new Color(0, 0, 0, 15));
                g2.drawRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }
    }

    private void drawStationProgress(Graphics2D g2, Station s) {
        double progress = 0.0;
        boolean busy = false;

        if (s instanceof CoffeeStation) {
            CoffeeStation cs = (CoffeeStation) s;
            busy = cs.isProcessing() || cs.isBrewCompleted();
            progress = cs.getProcessingProgress();
        } else if (s instanceof MilkStation) {
            MilkStation ms = (MilkStation) s;
            busy = ms.isProcessing() || ms.isMilkAdded();
            progress = ms.getProcessingProgress();
        } else if (s instanceof ToppingStation) {
            ToppingStation ts = (ToppingStation) s;
            busy = ts.isProcessing() || ts.isToppingsAdded();
            progress = ts.getProcessingProgress();
        }

        if (busy) {
            int px = s.getX() + 6;
            int py = s.getY() + 50;
            int pw = s.getWidth() - 12;
            int ph = 7;

            // Draw progress frame
            g2.setColor(new Color(30, 30, 30, 200));
            g2.fill(new RoundRectangle2D.Float(px, py, pw, ph, 4, 4));

            // Determine Fill color
            Color fillCol = new Color(33, 150, 243); // Processing Blue
            if (progress >= 100.0) {
                fillCol = new Color(76, 175, 80); // Success Green (Blinking effect)
                if (System.currentTimeMillis() % 400 < 200) {
                    fillCol = fillCol.brighter();
                }
            }

            g2.setColor(fillCol);
            g2.fill(new RoundRectangle2D.Float(px, py, (int)(pw * (progress / 100.0)), ph, 4, 4));
            g2.setColor(Color.WHITE);
            g2.draw(new RoundRectangle2D.Float(px, py, pw, ph, 4, 4));
        }
    }

    private void drawCustomerHUD(Graphics2D g2, Customer c, int bob) {
        // Patience meter (above character head)
        int px = c.getX() + 10;
        int py = c.getY() - 12 + bob;
        int pw = c.getWidth() - 20;
        int ph = 5;

        // Background bar
        g2.setColor(new Color(40, 40, 40, 180));
        g2.fill(new RoundRectangle2D.Float(px, py, pw, ph, 3, 3));

        // Patience ratio
        double patRatio = c.getPatience() / c.getMaxPatience();
        // Dynamic Patience Color (Green -> Yellow -> Red gradient)
        Color patCol;
        if (patRatio > 0.6) {
            patCol = new Color(76, 175, 80); // Green
        } else if (patRatio > 0.3) {
            patCol = new Color(255, 193, 7); // Yellow
        } else {
            patCol = new Color(244, 67, 54); // Red
        }

        g2.setColor(patCol);
        g2.fill(new RoundRectangle2D.Float(px, py, (int)(pw * patRatio), ph, 3, 3));
        g2.setColor(new Color(255, 255, 255, 100));
        g2.draw(new RoundRectangle2D.Float(px, py, pw, ph, 3, 3));

        // Speech Bubbles for orders
        if (c.getState() == Customer.CustomerState.WAITING_FOR_ORDER || c.getState() == Customer.CustomerState.SEATED) {
            int bx = c.getX() + 45;
            int by = c.getY() - 38 + bob;

            // Bubble body
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(bx, by, 34, 24, 8, 8));
            g2.fillPolygon(new int[]{bx + 8, bx + 14, bx + 12}, new int[]{by + 24, by + 24, by + 28}, 3);
            g2.setColor(new Color(33, 33, 33));
            g2.draw(new RoundRectangle2D.Float(bx, by, 34, 24, 8, 8));

            // Draw miniature order coffee cup indicator
            g2.setColor(new Color(121, 85, 72));
            g2.fillRect(bx + 11, by + 9, 12, 10);
            g2.setColor(new Color(255, 202, 40));
            g2.fillRect(bx + 14, by + 6, 6, 3); // Cup rim
        } else if (c.getState() == Customer.CustomerState.EATING) {
            // Happy eating text bubble
            int bx = c.getX() + 45;
            int by = c.getY() - 38 + bob;

            g2.setColor(new Color(245, 245, 245));
            g2.fill(new RoundRectangle2D.Float(bx, by, 34, 24, 8, 8));
            g2.fillPolygon(new int[]{bx + 8, bx + 14, bx + 12}, new int[]{by + 24, by + 24, by + 28}, 3);
            g2.setColor(new Color(76, 175, 80));
            g2.draw(new RoundRectangle2D.Float(bx, by, 34, 24, 8, 8));

            // Green heart icon
            g2.setColor(new Color(76, 175, 80));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString("♥", bx + 12, by + 16);
        }
    }

    private void drawPlayerItemBubble(Graphics2D g2, int playerBob) {
        String item = player.getCarriedItem();
        if (!item.equals("None")) {
            int bx = player.getX() + 35;
            int by = player.getY() - 32 + playerBob;

            // Draw round item label bubble
            g2.setColor(new Color(33, 33, 40, 240));
            g2.fill(new RoundRectangle2D.Float(bx, by, 84, 20, 8, 8));
            g2.setColor(new Color(255, 255, 255, 60));
            g2.draw(new RoundRectangle2D.Float(bx, by, 84, 20, 8, 8));

            g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
            g2.setColor(Color.WHITE);
            g2.drawString(item, bx + 6, by + 13);
        }
    }

    public GameState getGameState() {
        return state;
    }

    public void setGameState(GameState state) {
        this.state = state;
    }
}