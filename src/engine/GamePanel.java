package engine;

import entity.Customer;
import entity.Player;
import entity.TableEntity;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import logic.*;
import station.*;
import ui.HUD;
import ui.InteractableButton;
import ui.MenuUI;
import ui.SpriteDialogBox;

public class GamePanel extends JPanel {

    public enum GameState {
        MENU, PLAYING, PAUSED, GAMEOVER, LEVEL_WON, LEVEL_FAILED, GAME_WON
    }

    public static final int WIDTH = 960;
    public static final int HEIGHT = 640;

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

    // Button system for interactable objects
    private Map<Station, InteractableButton> stationButtons = new HashMap<>();
    // Complaint button system for tables with incorrect orders
    private Map<TableEntity, InteractableButton> complaintButtons = new HashMap<>();
    // Cashier UI state
    private boolean showCashierUI = false;
    // Upgrade levels
    private int speedUpgradeLevel = 0;
    private int patienceUpgradeLevel = 0;
    private int lastMouseX = -1;
    private int lastMouseY = -1;
    
    // NPC drag system
    private Customer selectedCustomer = null;
    private boolean isDraggingCustomer = false;

    // Cashier UI button areas
    private Rectangle upgradePatienceRect = new Rectangle();
    private Rectangle upgradeTableCountRect = new Rectangle();
    private Rectangle upgradeTableCapacityRect = new Rectangle();
    private Rectangle exitShopRect = new Rectangle();

    // Recipe UI state & buttons
    private boolean showRecipeUI = false;
    private Rectangle recipeButtonRect = new Rectangle(870, 110, 80, 28);
    private Rectangle exitRecipeRect = new Rectangle();

    // End Level button
    private Rectangle endLevelButtonRect = new Rectangle(870, 148, 80, 28);

    // Supply UI state & buttons
    private boolean showSupplyUI = false;
    private Rectangle supplyKopiRect = new Rectangle();
    private Rectangle supplySusuRect = new Rectangle();
    private Rectangle supplyToppingRect = new Rectangle();
    private Rectangle exitSupplyUIRect = new Rectangle();

    // Topping UI state & buttons
    private boolean showToppingUI = false;
    private Rectangle toppingCaramelRect = new Rectangle();
    private Rectangle toppingChocolateRect = new Rectangle();
    private Rectangle toppingWhippedCreamRect = new Rectangle();
    private Rectangle toppingBobaRect = new Rectangle();
    private Rectangle toppingIceRect = new Rectangle();
    private Rectangle exitToppingRect = new Rectangle();

    // Timer auto-end guard
    private boolean shiftExpiredHandled = false;

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
        
        // Add mouse listener for button interactions
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePress(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseRelease(e);
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseMove(e);
            }
        });
    }

    public void resetGame() {
        InventoryManager.resetInstance();
        gameEngine = new GameEngine();
        player = gameEngine.getPlayerBarista();
        extraStations = new ArrayList<>();
        extraStations.add(new SupplyStation(180, 135));
        extraStations.add(new CashierCounter(50, 420));
        extraStations.add(new TrashBin(740, 135));

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
        hud.setShiftStartTime(System.currentTimeMillis());
        shiftExpiredHandled = false;
        initializeButtons();
        state = GameState.PLAYING;
        requestFocusInWindow();
    }

    private void initializeButtons() {
        stationButtons.clear();
        complaintButtons.clear();
        List<Station> allStations = new ArrayList<>(gameEngine.getDaftarStasiun());
        allStations.addAll(extraStations);

        for (Station s : allStations) {
            InteractableButton btn = new InteractableButton(
                s.getX() - 10, s.getY() - 32, s.getWidth() + 20, 26, s.getName()
            );
            stationButtons.put(s, btn);
        }

        // Initialize complaint buttons for all tables
        for (TableEntity table : gameEngine.getDaftarMeja()) {
            InteractableButton btn = new InteractableButton(
                table.getX(), table.getY() - 30, 80, 20, "Complain"
            );
            complaintButtons.put(table, btn);
        }
    }

    public void updatePhysics() {
        if (state != GameState.PLAYING) {
            return;
        }

        playerLogic.update(keysPressed, WIDTH, HEIGHT);
        customerLogic.update(gameEngine, difficultyManager, selectedCustomer, patienceUpgradeLevel);

        // Auto-end shift when timer hits 00:00
        if (!shiftExpiredHandled && hud.isShiftExpired(gameEngine)) {
            shiftExpiredHandled = true;
            handleEndLevel();
        }

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
                if (key == KeyEvent.VK_1) {
                    supplyChoice = "1";
                }
                if (key == KeyEvent.VK_2) {
                    supplyChoice = "2";
                }
                if (key == KeyEvent.VK_3) {
                    supplyChoice = "3";
                }

                if (key == KeyEvent.VK_E || key == KeyEvent.VK_SPACE) {
                    Station near = findNearestStation();
                    if (near instanceof ToppingStation) {
                        showToppingUI = true;
                    } else if (near instanceof SupplyStation) {
                        showSupplyUI = true;
                    } else {
                        playerLogic.tryInteract(extraStations, selectedTopping);
                    }
                }
                if (showSupplyUI) {
                    if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_E || key == KeyEvent.VK_SPACE) {
                        showSupplyUI = false;
                    }
                    break;
                }
                if (showToppingUI) {
                    if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_E || key == KeyEvent.VK_SPACE) {
                        showToppingUI = false;
                    }
                    break;
                }

                if (showRecipeUI) {
                    if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_R || key == KeyEvent.VK_E) {
                        showRecipeUI = false;
                    }
                    break;
                }

                if (key == KeyEvent.VK_ESCAPE) {
                    state = GameState.PAUSED;
                }
                if (key == KeyEvent.VK_R && !showCashierUI) {
                    showRecipeUI = true;
                }

                // Handle cashier UI input
                if (showCashierUI) {
                    if (key == KeyEvent.VK_1) {
                        // Buy patience upgrade
                        InventoryManager inv = InventoryManager.getInstance();
                        if (inv.getSaldoUang() >= 150) {
                            inv.kurangiUang(150);
                            patienceUpgradeLevel++;
                            System.out.println("Bought patience upgrade! Level: " + patienceUpgradeLevel);
                        } else {
                            System.out.println("Not enough coins!");
                        }
                    } else if (key == KeyEvent.VK_2) {
                        // Buy more tables
                        buyNewTable();
                    } else if (key == KeyEvent.VK_3) {
                        // Upgrade table capacity
                        InventoryManager inv = InventoryManager.getInstance();
                        if (inv.getSaldoUang() >= 100) {
                            boolean anyUpgraded = false;
                            for (TableEntity t : gameEngine.getDaftarMeja()) {
                                if (t.upgradeMeja()) {
                                    anyUpgraded = true;
                                }
                            }
                            if (anyUpgraded) {
                                inv.kurangiUang(100);
                                System.out.println("Upgraded all tables' capacity!");
                            } else {
                                System.out.println("All tables already at maximum capacity!");
                            }
                        } else {
                            System.out.println("Not enough coins!");
                        }
                    } else if (key == KeyEvent.VK_E || key == KeyEvent.VK_ESCAPE) {
                        // Exit cashier UI
                        showCashierUI = false;
                        // Reset interaction state on cashier
                        for (Station station : extraStations) {
                            if (station instanceof CashierCounter) {
                                ((CashierCounter) station).resetInteractionState();
                            }
                        }
                    }
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
                    hud.setShiftStartTime(System.currentTimeMillis());
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
        // Movement keys no longer used - player controlled by cursor drag only
    }

    private void handleMouseMove(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
        
        if (state != GameState.PLAYING) {
            return;
        }
        
        // Update button hover states for stations
        for (InteractableButton btn : stationButtons.values()) {
            if (btn.isHovered(lastMouseX, lastMouseY)) {
                if (btn.getState() != InteractableButton.ButtonState.PRESSED) {
                    btn.setState(InteractableButton.ButtonState.HOVER);
                }
            } else {
                if (btn.getState() != InteractableButton.ButtonState.PRESSED) {
                    btn.setState(InteractableButton.ButtonState.NORMAL);
                }
            }
        }

        // Update complaint button hover states and visibility
        for (Map.Entry<TableEntity, InteractableButton> entry : complaintButtons.entrySet()) {
            TableEntity table = entry.getKey();
            InteractableButton btn = entry.getValue();
            // Only show complaint button if last order was incorrect
            if (table.isLastOrderIncorrect()) {
                // Update position to follow table
                btn.setPosition(table.getX(), table.getY() - 30);
                if (btn.isHovered(lastMouseX, lastMouseY)) {
                    if (btn.getState() != InteractableButton.ButtonState.PRESSED) {
                        btn.setState(InteractableButton.ButtonState.HOVER);
                    }
                } else {
                    if (btn.getState() != InteractableButton.ButtonState.PRESSED) {
                        btn.setState(InteractableButton.ButtonState.NORMAL);
                    }
                }
            } else {
                // Hide complaint button by setting state to NORMAL and moving off-screen
                btn.setState(InteractableButton.ButtonState.NORMAL);
                btn.setPosition(-100, -100); // Move off-screen
            }
        }
        
        // Update dragging customer position
        if (isDraggingCustomer && selectedCustomer != null) {
            selectedCustomer.setX(lastMouseX - selectedCustomer.getWidth() / 2);
            selectedCustomer.setY(lastMouseY - selectedCustomer.getHeight() / 2);
        }
    }

    private void handleMousePress(MouseEvent e) {
        if (state != GameState.PLAYING) {
            return;
        }
        
        // If Supply UI is open, handle its button clicks and consume click
        if (showSupplyUI) {
            updateSupplyRects();
            Point clickPoint = e.getPoint();
            InventoryManager inv = InventoryManager.getInstance();
            if (supplyKopiRect.contains(clickPoint)) {
                inv.beliStokBahan("Kopi", 30, 10);
            } else if (supplySusuRect.contains(clickPoint)) {
                inv.beliStokBahan("Susu", 25, 10);
            } else if (supplyToppingRect.contains(clickPoint)) {
                inv.beliStokBahan("Topping", 35, 10);
            } else if (exitSupplyUIRect.contains(clickPoint)) {
                showSupplyUI = false;
            }
            return;
        }

        // If Topping UI is open, handle its button clicks and consume click
        if (showToppingUI) {
            updateToppingRects();
            Point clickPoint = e.getPoint();
            InventoryManager inv = InventoryManager.getInstance();
            String currentHand = player.getItemOnHand();
            
            boolean isCoffee = !currentHand.equalsIgnoreCase("None") && 
                               (currentHand.equalsIgnoreCase("Kopi") || currentHand.equalsIgnoreCase("Kopi + Susu"));
            
            if (toppingCaramelRect.contains(clickPoint) && isCoffee && inv.getStokTopping() > 0) {
                inv.kurangiStokTopping(1);
                player.setItemOnHand(currentHand + " + Caramel");
                System.out.println("Added caramel topping!");
            }
            else if (toppingChocolateRect.contains(clickPoint) && isCoffee && inv.getStokTopping() > 0) {
                inv.kurangiStokTopping(1);
                player.setItemOnHand(currentHand + " + Chocolate");
                System.out.println("Added chocolate topping!");
            }
            else if (toppingWhippedCreamRect.contains(clickPoint) && isCoffee && inv.getStokTopping() > 0) {
                inv.kurangiStokTopping(1);
                player.setItemOnHand(currentHand + " + Whipped Cream");
                System.out.println("Added whipped cream topping!");
            }
            else if (toppingBobaRect.contains(clickPoint) && isCoffee && inv.getStokTopping() > 0) {
                inv.kurangiStokTopping(1);
                player.setItemOnHand(currentHand + " + Boba");
                System.out.println("Added boba topping!");
            }
            else if (toppingIceRect.contains(clickPoint) && !currentHand.equalsIgnoreCase("None") && !currentHand.toLowerCase().contains("ice")) {
                player.setItemOnHand(currentHand + " + Ice");
                System.out.println("Added ice!");
            }
            else if (exitToppingRect.contains(clickPoint)) {
                showToppingUI = false;
            }
            return;
        }

        // If Recipe UI is open, handle close click and consume click
        if (showRecipeUI) {
            Point clickPoint = e.getPoint();
            if (exitRecipeRect.contains(clickPoint)) {
                showRecipeUI = false;
            }
            return;
        }
        
        // 1. If Cashier UI is open, handle its button clicks and consume click
        if (showCashierUI) {
            updateCashierRects();
            Point clickPoint = e.getPoint();

            if (upgradePatienceRect.contains(clickPoint)) {
                InventoryManager inv = InventoryManager.getInstance();
                if (inv.getSaldoUang() >= 150) {
                    inv.kurangiUang(150);
                    patienceUpgradeLevel++;
                    System.out.println("Bought patience upgrade! Level: " + patienceUpgradeLevel);
                } else {
                    System.out.println("Not enough coins!");
                }
                return;
            }

            if (upgradeTableCountRect.contains(clickPoint)) {
                buyNewTable();
                return;
            }

            if (upgradeTableCapacityRect.contains(clickPoint)) {
                InventoryManager inv = InventoryManager.getInstance();
                if (inv.getSaldoUang() >= 100) {
                    boolean anyUpgraded = false;
                    for (TableEntity t : gameEngine.getDaftarMeja()) {
                        if (t.upgradeMeja()) {
                            anyUpgraded = true;
                        }
                    }
                    if (anyUpgraded) {
                        inv.kurangiUang(100);
                        System.out.println("Upgraded all tables' capacity!");
                    } else {
                        System.out.println("All tables already at maximum capacity!");
                    }
                } else {
                    System.out.println("Not enough coins!");
                }
                return;
            }

            if (exitShopRect.contains(clickPoint)) {
                showCashierUI = false;
                for (Station station : extraStations) {
                    if (station instanceof CashierCounter) {
                        ((CashierCounter) station).resetInteractionState();
                    }
                }
                return;
            }

            return; // Consume click while shop UI is open
        }
        
        // Check if clicking the Recipe Button
        if (recipeButtonRect.contains(e.getPoint())) {
            showRecipeUI = true;
            return;
        }

        // Check if clicking the End Level button
        if (endLevelButtonRect.contains(e.getPoint())) {
            handleEndLevel();
            return;
        }
        
        // 2. Check if clicking on NPC in the waiting line to select/drag
        for (Customer c : customerLogic.getCustomers()) {
            if (c.getState() == Customer.CustomerState.WAITING_FOR_TABLE) {
                int bob = c.getBobOffset();
                int cx = c.getX();
                int cy = c.getY() + bob;

                BufferedImage frame = pickCustomerFrame(c);
                int imgW = frame.getWidth();
                int imgH = frame.getHeight();
                final int MARGIN = 8;

                if (e.getX() >= cx + MARGIN && e.getX() < cx + MARGIN + imgW &&
                    e.getY() >= cy + MARGIN && e.getY() < cy + MARGIN + imgH) {
                    selectedCustomer = c;
                    isDraggingCustomer = true;
                    return;
                }
            }
        }
        
        // 3. Check if clicking on a table or customer on a table (for order taking, serving, or cleaning)
        for (TableEntity table : gameEngine.getDaftarMeja()) {
            boolean clicked = false;
            // Check table bounds
            if (e.getX() >= table.getX() && e.getX() < table.getX() + table.getWidth() &&
                e.getY() >= table.getY() && e.getY() < table.getY() + table.getHeight()) {
                clicked = true;
            }

            // Or check customer bounds if there is a customer at this table
            if (!clicked && table.getCurrentCustomer() != null) {
                Customer c = table.getCurrentCustomer();
                int bob = c.getBobOffset();
                int cx = c.getX();
                int cy = c.getY() + bob;
                BufferedImage frame = pickCustomerFrame(c);
                int imgW = frame.getWidth();
                int imgH = frame.getHeight();
                final int MARGIN = 8;
                if (e.getX() >= cx + MARGIN && e.getX() < cx + MARGIN + imgW &&
                    e.getY() >= cy + MARGIN && e.getY() < cy + MARGIN + imgH) {
                    clicked = true;
                }
            }

            if (clicked) {
                // If table is dirty, clean it
                if (table.isDirty()) {
                    table.bersihkanMeja();
                    System.out.println("Meja " + table.getIdMeja() + " dibersihkan.");
                    return;
                }

                // If table is occupied
                if (table.isOccupied() && table.getCurrentCustomer() != null) {
                    Customer c = table.getCurrentCustomer();
                    if (c.getState() == Customer.CustomerState.SEATED) {
                        c.setState(Customer.CustomerState.WAITING_FOR_ORDER);
                        c.setPatience(100.0);
                        System.out.println("Menerima pesanan dari Meja " + table.getIdMeja());
                        return;
                    } else if (c.getState() == Customer.CustomerState.WAITING_FOR_ORDER) {
                        // Serve the customer
                        table.sajikanKopiKeMeja(player, gameEngine);
                        return;
                    }
                }
            }
        }
        
        // 4. Check which station button was pressed
        for (Map.Entry<Station, InteractableButton> entry : stationButtons.entrySet()) {
            InteractableButton btn = entry.getValue();
            if (btn.isHovered(e.getX(), e.getY())) {
                btn.setState(InteractableButton.ButtonState.PRESSED);
                Station station = entry.getKey();
                String sub = (station instanceof SupplyStation) ? supplyChoice : selectedTopping;
                
                if (station instanceof CashierCounter) {
                    showCashierUI = true;
                } else if (station instanceof ToppingStation) {
                    showToppingUI = true;
                } else if (station instanceof SupplyStation) {
                    showSupplyUI = true;
                } else {
                    station.interact(player, sub);
                    System.out.println("Interacted with station: " + station.getName() + ", hand: " + player.getItemOnHand());
                }
                break;
            }
        }

        // 5. Check which complaint button was pressed
        for (Map.Entry<TableEntity, InteractableButton> entry : complaintButtons.entrySet()) {
            TableEntity table = entry.getKey();
            InteractableButton btn = entry.getValue();
            // Only respond to clicks if the complaint button should be visible
            if (table.isLastOrderIncorrect() && btn.isHovered(e.getX(), e.getY())) {
                btn.setState(InteractableButton.ButtonState.PRESSED);
                // Handle complaint - make customer angry and reset the complaint flag
                handleComplaint(table);
                break;
            }
        }
    }

    private void handleComplaint(TableEntity table) {
        // Make the customer angry when they complain about incorrect order
        if (table.getCurrentCustomer() != null) {
            Customer customer = table.getCurrentCustomer();
            // Set customer to leaving angry state
            customer.setState(Customer.CustomerState.LEAVING_ANGRY);
            // Place customer at the table position before moving down to leave
            customer.setX(table.getX() + table.getWidth() / 2 - customer.getWidth() / 2);
            customer.setY(table.getY() + table.getHeight() / 2 - customer.getHeight() / 2);
            customer.setPatience(0);
            System.out.println("Customer complained about incorrect order and got angry!");
            table.kosongkanMeja(); // Release table and make it dirty
        }

        // Reset the incorrect order flag so complaint button disappears
        table.setLastOrderIncorrect(false);
    }

    private void handleMouseRelease(MouseEvent e) {
        if (state != GameState.PLAYING) {
            return;
        }
        
        // Stop dragging and snap NPC to table if released on one
        if (isDraggingCustomer && selectedCustomer != null) {
            isDraggingCustomer = false;
            
            int cx = selectedCustomer.getX() + selectedCustomer.getWidth() / 2;
            int cy = selectedCustomer.getY() + selectedCustomer.getHeight() / 2;
            
            double minDist = 50;
            TableEntity nearestTable = null;
            for (TableEntity t : gameEngine.getDaftarMeja()) {
                int tx = t.getX() + t.getWidth() / 2;
                int ty = t.getY() + t.getHeight() / 2;
                double dist = Math.hypot(cx - tx, cy - ty);
                // Only allow seating on a truly Free table (not Occupied or Dirty)
                if (dist < minDist && t.getStatus().equalsIgnoreCase("Free")) {
                    minDist = dist;
                    nearestTable = t;
                }
            }
            
            if (nearestTable != null) {
                boolean success = nearestTable.occupyMeja(selectedCustomer);
                if (success) {
                    selectedCustomer.setAssignedTableId(nearestTable.getIdMeja());
                    selectedCustomer.setState(Customer.CustomerState.SEATED);
                    selectedCustomer.setX(nearestTable.getX() + nearestTable.getWidth() / 2 - selectedCustomer.getWidth() / 2);
                    selectedCustomer.setY(nearestTable.getY() + nearestTable.getHeight() / 2 - selectedCustomer.getHeight() / 2);
                    selectedCustomer.setPatience(100.0);
                }
            }
            
            selectedCustomer = null;
        }
        
        // Transition pressed buttons to released state
        for (InteractableButton btn : stationButtons.values()) {
            if (btn.getState() == InteractableButton.ButtonState.PRESSED) {
                btn.setState(InteractableButton.ButtonState.RELEASED);
            }
        }
        for (InteractableButton btn : complaintButtons.values()) {
            if (btn.getState() == InteractableButton.ButtonState.PRESSED) {
                btn.setState(InteractableButton.ButtonState.RELEASED);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawFloor(g2);

        g2.setColor(new Color(109, 76, 65));
        g2.fill(new Rectangle2D.Float(0, 130, WIDTH, 50));
        g2.setColor(new Color(215, 204, 200));
        g2.fillRect(0, 177, WIDTH, 3);
        g2.setColor(new Color(93, 64, 55, 120));
        g2.fill(new RoundRectangle2D.Float(15, 135, 110, 440, 12, 12));

        for (TableEntity t : gameEngine.getDaftarMeja()) {
            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillOval(t.getX() - 4, t.getY() + 38, t.getWidth() + 8, t.getWidth() + 8);
            g2.drawImage(AssetManager.tableSprite, t.getX(), t.getY(), t.getWidth(), t.getHeight(), null);

            if (t.isDirty()) {
                if (AssetManager.iconCoin != null) {
                    int coinSize = 24;
                    g2.drawImage(AssetManager.iconCoin, t.getX() + (t.getWidth() - coinSize) / 2, t.getY() + (t.getHeight() - coinSize) / 2 - 8, coinSize, coinSize, null);
                } else {
                    g2.setColor(new Color(0, 0, 0, 180));
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    g2.drawString("DIRTY", t.getX() + 18, t.getY() + 15);
                }
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 8));
            g2.setColor(new Color(255, 255, 255, 180));
            g2.drawString("Cap:" + t.getKapasitasKursi(), t.getX() + 4, t.getY() + 58);
        }

        List<Station> drawStations = new ArrayList<>(gameEngine.getDaftarStasiun());
        drawStations.addAll(extraStations);
        for (Station s : drawStations) {
            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillOval(s.getX() + 2, s.getY() + 48, s.getWidth() - 4, s.getWidth() - 4);

            java.awt.image.BufferedImage img = null;
            if (s instanceof SupplyStation) img = AssetManager.stockStationSprite;
            else if (s instanceof CoffeeStation) img = AssetManager.coffeeMachineSprite;
            else if (s instanceof MilkStation) img = AssetManager.milkDispenserSprite;
            else if (s instanceof ToppingStation) img = AssetManager.toppingStationSprite;
            else if (s instanceof CashierCounter) img = AssetManager.cashierCounterSprite;
            else if (s instanceof TrashBin) img = AssetManager.trashbinSprite;

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

        // Player character hidden - gameplay is cursor-only
        
        // Draw interactive buttons
        if (state == GameState.PLAYING) {
            for (InteractableButton btn : stationButtons.values()) {
                btn.draw(g2);
            }
            // Draw complaint buttons for tables with incorrect orders
            for (Map.Entry<TableEntity, InteractableButton> entry : complaintButtons.entrySet()) {
                TableEntity table = entry.getKey();
                InteractableButton btn = entry.getValue();
                // Only draw if the complaint should be visible
                if (table.isLastOrderIncorrect()) {
                    btn.draw(g2);
                }
            }
        }
        
        hud.draw(g2, InventoryManager.getInstance(), gameEngine, player, WIDTH, selectedTopping, supplyChoice);

        // Draw Recipe Button & End Level Button
        if (state == GameState.PLAYING && !showCashierUI && !showRecipeUI) {
            // Recipe button
            boolean hovered = recipeButtonRect.contains(lastMouseX, lastMouseY);
            g2.setColor(hovered ? new Color(141, 110, 99) : new Color(109, 76, 65));
            g2.fill(new RoundRectangle2D.Float(recipeButtonRect.x, recipeButtonRect.y, recipeButtonRect.width, recipeButtonRect.height, 8, 8));
            g2.setColor(new Color(215, 204, 200));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(recipeButtonRect.x, recipeButtonRect.y, recipeButtonRect.width, recipeButtonRect.height, 8, 8));
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String recipeText = "RESEP";
            int rtw = g2.getFontMetrics().stringWidth(recipeText);
            g2.drawString(recipeText, recipeButtonRect.x + (recipeButtonRect.width - rtw) / 2, recipeButtonRect.y + 18);

            // End Level button
            boolean elHovered = endLevelButtonRect.contains(lastMouseX, lastMouseY);
            g2.setColor(elHovered ? new Color(220, 80, 60) : new Color(160, 50, 30));
            g2.fill(new RoundRectangle2D.Float(endLevelButtonRect.x, endLevelButtonRect.y, endLevelButtonRect.width, endLevelButtonRect.height, 8, 8));
            g2.setColor(new Color(255, 200, 180));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(endLevelButtonRect.x, endLevelButtonRect.y, endLevelButtonRect.width, endLevelButtonRect.height, 8, 8));
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String elText = "END";
            int eltw = g2.getFontMetrics().stringWidth(elText);
            g2.drawString(elText, endLevelButtonRect.x + (endLevelButtonRect.width - eltw) / 2, endLevelButtonRect.y + 18);
        }

        // Draw cashier UI if active
        if (showCashierUI) {
            drawCashierUI(g2);
        }

        // Draw recipe UI if active
        if (showRecipeUI) {
            drawRecipeUI(g2);
        }

        if (showSupplyUI) {
            drawSupplyUI(g2);
        }

        if (showToppingUI) {
            drawToppingUI(g2);
        }

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

    private void drawCashierUI(Graphics2D g2) {
        updateCashierRects();
        
        // Draw semi-transparent background overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        
        int panelWidth = 500;
        int panelHeight = 350;
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = (HEIGHT - panelHeight) / 2;
        
        // Draw panel background (glassmorphism/wood styled dialog box)
        SpriteDialogBox.drawCenterPanel(g2, WIDTH, HEIGHT, panelWidth, panelHeight, "CAFE UPGRADES", new String[]{});
        
        InventoryManager inv = InventoryManager.getInstance();
        
        // Title and balance
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(new Color(255, 235, 59)); // Bright yellow
        String balanceStr = "Your Coins: $" + inv.getSaldoUang();
        g2.drawString(balanceStr, panelX + (panelWidth - g2.getFontMetrics().stringWidth(balanceStr)) / 2, panelY + 65);
        
        // Let's draw the buttons!
        drawUpgradeButton(g2, upgradePatienceRect, "Upgrade Customer Patience", 
                          "Patience decays slower (+15% resistance)", 
                          "Level: " + patienceUpgradeLevel, 
                          "Cost: $150", 
                          inv.getSaldoUang() >= 150);
                          
        int tableCost = 200 + (gameEngine.getDaftarMeja().size() - 2) * 100;
        boolean canBuyTable = gameEngine.getDaftarMeja().size() < 5;
        String tableCostStr = canBuyTable ? "Cost: $" + tableCost : "MAXED";
        drawUpgradeButton(g2, upgradeTableCountRect, "Buy More Tables", 
                          "Adds a new table to your cafe (Max 5)", 
                          "Count: " + gameEngine.getDaftarMeja().size() + "/5", 
                          tableCostStr, 
                          canBuyTable && inv.getSaldoUang() >= tableCost);
                          
        // Let's find average table level
        int avgCapacity = 0;
        for (TableEntity t : gameEngine.getDaftarMeja()) {
            avgCapacity += t.getKapasitasKursi();
        }
        avgCapacity = gameEngine.getDaftarMeja().isEmpty() ? 0 : avgCapacity / gameEngine.getDaftarMeja().size();
        boolean canUpgradeCapacity = avgCapacity < 4;
        String capacityCostStr = canUpgradeCapacity ? "Cost: $100" : "MAXED";
        drawUpgradeButton(g2, upgradeTableCapacityRect, "Upgrade Table Capacity", 
                          "Increases seat capacity of all tables (Max 4)", 
                          "Avg Seats: " + avgCapacity, 
                          capacityCostStr, 
                          canUpgradeCapacity && inv.getSaldoUang() >= 100);
                          
        // Exit button
        drawExitButton(g2, exitShopRect, "Exit Shop");
    }

    private void drawRecipeUI(Graphics2D g2) {
        // Draw semi-transparent background overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        
        int panelWidth = 520;
        int panelHeight = 380;
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = (HEIGHT - panelHeight) / 2;
        
        // Draw panel background (glassmorphism/wood styled dialog box)
        SpriteDialogBox.drawCenterPanel(g2, WIDTH, HEIGHT, panelWidth, panelHeight, "RECIPE BOOK", new String[]{});
        
        // Title and description
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(new Color(255, 235, 59)); // Bright yellow
        String subtitle = "Cafe Tycoon Drink Guide";
        g2.drawString(subtitle, panelX + (panelWidth - g2.getFontMetrics().stringWidth(subtitle)) / 2, panelY + 60);
        
        String[][] recipes = {
            {"Espresso", "Kopi"},
            {"Latte", "Kopi + Susu"},
            {"Caramel Latte", "Kopi + Susu + Caramel"},
            {"Mochaccino", "Kopi + Susu + Chocolate"},
            {"Boba Coffee Latte", "Kopi + Susu + Boba"},
            {"Con Panna", "Kopi + Whipped Cream"}
        };
        
        int startX = panelX + 40;
        int startY = panelY + 95;
        int rowHeight = 36;
        int rowW = panelWidth - 80;

        for (int i = 0; i < recipes.length; i++) {
            int currentY = startY + i * rowHeight;

            // Alternating dark strip for each row
            Color rowBg = (i % 2 == 0)
                ? new Color(20, 12, 8, 180)   // darker
                : new Color(40, 25, 15, 120);  // slightly lighter
            g2.setColor(rowBg);
            g2.fillRoundRect(startX - 10, currentY - 18, rowW, 28, 6, 6);

            // Subtle left accent bar
            g2.setColor(new Color(255, 200, 50, 200));
            g2.fillRect(startX - 10, currentY - 18, 4, 28);

            // Drink name — bold bright yellow
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(new Color(255, 235, 59));
            g2.drawString(recipes[i][0], startX + 4, currentY + 2);

            // Arrow — soft orange so it stands apart from both columns
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(new Color(255, 160, 80));
            g2.drawString("➔", startX + 150, currentY + 2);

            // Ingredients — plain white, clearly different from yellow name
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(Color.WHITE);
            g2.drawString(recipes[i][1], startX + 175, currentY + 2);
        }
        
        // Draw footnote for ice
        g2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        g2.setColor(Color.BLACK);
        String footnote = "* Tambahkan Es (Ice) di stasiun topping untuk membuat versi DINGIN (Iced).";
        g2.drawString(footnote, panelX + (panelWidth - g2.getFontMetrics().stringWidth(footnote)) / 2, panelY + panelHeight - 65);

        // Exit button
        int btnW = 120;
        int btnH = 34;
        exitRecipeRect.setBounds((WIDTH - btnW) / 2, panelY + panelHeight - 50, btnW, btnH);
        drawExitButton(g2, exitRecipeRect, "Close");
    }

    private void drawToppingUI(Graphics2D g2) {
        updateToppingRects();
        
        // Draw semi-transparent background overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        
        int panelWidth = 500;
        int panelHeight = 300;
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = (HEIGHT - panelHeight) / 2;
        
        // Draw panel background (glassmorphism/wood styled dialog box)
        SpriteDialogBox.drawCenterPanel(g2, WIDTH, HEIGHT, panelWidth, panelHeight, "TOPPING COUNTER", new String[]{});
        
        InventoryManager inv = InventoryManager.getInstance();
        String currentHand = player.getItemOnHand();
        String translatedHand = player.translateItemOnHand(currentHand);
        
        // Title and stock description
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.setColor(new Color(255, 235, 59)); // Bright yellow
        String statusStr = "Holding: " + translatedHand + " | Topping Stock: " + inv.getStokTopping();
        g2.drawString(statusStr, panelX + (panelWidth - g2.getFontMetrics().stringWidth(statusStr)) / 2, panelY + 65);
        
        // Check conditions
        boolean isCoffee = !currentHand.equalsIgnoreCase("None") && 
                           (currentHand.equalsIgnoreCase("Kopi") || currentHand.equalsIgnoreCase("Kopi + Susu") || 
                            currentHand.equalsIgnoreCase("Kopi + Ice") || currentHand.equalsIgnoreCase("Kopi + Susu + Ice"));
                           
        boolean canAddCaramel = isCoffee && !currentHand.toLowerCase().contains("caramel") && inv.getStokTopping() > 0;
        boolean canAddChocolate = isCoffee && !currentHand.toLowerCase().contains("chocolate") && inv.getStokTopping() > 0;
        boolean canAddWhipped = isCoffee && !currentHand.toLowerCase().contains("whipped cream") && inv.getStokTopping() > 0;
        boolean canAddBoba = isCoffee && !currentHand.toLowerCase().contains("boba") && inv.getStokTopping() > 0;
        
        // Ice can be added to any coffee drink as long as it doesn't already have ice
        boolean canAddIce = !currentHand.equalsIgnoreCase("None") && 
                            !currentHand.toLowerCase().contains("ice");
                            
        // Draw 2x3 grid of topping choices
        drawUpgradeButton(g2, toppingCaramelRect, "Caramel", "Sweet caramel drizzle", "Stock: " + inv.getStokTopping(), "", canAddCaramel);
        drawUpgradeButton(g2, toppingChocolateRect, "Chocolate", "Rich chocolate sauce", "Stock: " + inv.getStokTopping(), "", canAddChocolate);
        drawUpgradeButton(g2, toppingWhippedCreamRect, "Whipped Cream", "Fluffy whipped cream", "Stock: " + inv.getStokTopping(), "", canAddWhipped);
        drawUpgradeButton(g2, toppingBobaRect, "Boba", "Chewy tapioca pearls", "Stock: " + inv.getStokTopping(), "", canAddBoba);
        drawUpgradeButton(g2, toppingIceRect, "Ice", "Make it cold! (Iced)", "Unlimited", "FREE", canAddIce);
        
        drawExitButton(g2, exitToppingRect, "Close");
    }

    private void updateToppingRects() {
        int panelWidth = 500;
        int panelHeight = 300;
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = (HEIGHT - panelHeight) / 2;
        
        int btnW = 210;
        int btnH = 50;
        
        toppingCaramelRect.setBounds(panelX + 30, panelY + 90, btnW, btnH);
        toppingChocolateRect.setBounds(panelX + 260, panelY + 90, btnW, btnH);
        
        toppingWhippedCreamRect.setBounds(panelX + 30, panelY + 155, btnW, btnH);
        toppingBobaRect.setBounds(panelX + 260, panelY + 155, btnW, btnH);
        
        toppingIceRect.setBounds(panelX + 30, panelY + 220, btnW, btnH);
        exitToppingRect.setBounds(panelX + 260, panelY + 220, btnW, btnH);
    }

    // ── Supply UI ─────────────────────────────────────────────────────────────

    private void drawSupplyUI(Graphics2D g2) {
        updateSupplyRects();

        // Dark overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        int panelWidth = 460;
        int panelHeight = 310;
        SpriteDialogBox.drawCenterPanel(g2, WIDTH, HEIGHT, panelWidth, panelHeight, "SUPPLY SHOP", new String[]{});

        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = (HEIGHT - panelHeight) / 2;

        InventoryManager inv = InventoryManager.getInstance();

        // Balance
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.setColor(new Color(255, 235, 59));
        String balStr = "Koin: $" + inv.getSaldoUang();
        g2.drawString(balStr, panelX + (panelWidth - g2.getFontMetrics().stringWidth(balStr)) / 2, panelY + 62);

        // Buttons
        boolean canBuyKopi    = inv.getSaldoUang() >= 30  && inv.getStokBijiKopi()  < 100;
        boolean canBuySusu    = inv.getSaldoUang() >= 25  && inv.getStokSusu()       < 100;
        boolean canBuyTopping = inv.getSaldoUang() >= 35  && inv.getStokTopping()    < 100;

        drawUpgradeButton(g2, supplyKopiRect,
            "Kopi (Coffee Beans)",
            "Tambah 10 stok kopi",
            "Stok: " + inv.getStokBijiKopi() + "/100",
            inv.getStokBijiKopi() >= 100 ? "PENUH" : "$30",
            canBuyKopi);

        drawUpgradeButton(g2, supplySusuRect,
            "Susu (Milk)",
            "Tambah 10 stok susu",
            "Stok: " + inv.getStokSusu() + "/100",
            inv.getStokSusu() >= 100 ? "PENUH" : "$25",
            canBuySusu);

        drawUpgradeButton(g2, supplyToppingRect,
            "Topping",
            "Tambah 10 stok topping (Caramel/Choc/Boba/Cream)",
            "Stok: " + inv.getStokTopping() + "/100",
            inv.getStokTopping() >= 100 ? "PENUH" : "$35",
            canBuyTopping);

        drawExitButton(g2, exitSupplyUIRect, "Tutup");
    }

    private void updateSupplyRects() {
        int panelWidth = 460;
        int panelHeight = 310;
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = (HEIGHT - panelHeight) / 2;

        int btnW = panelWidth - 60;
        int btnH = 50;
        int startX = panelX + 30;

        supplyKopiRect.setBounds(startX, panelY + 80, btnW, btnH);
        supplySusuRect.setBounds(startX, panelY + 140, btnW, btnH);
        supplyToppingRect.setBounds(startX, panelY + 200, btnW, btnH);
        exitSupplyUIRect.setBounds(panelX + (panelWidth - 160) / 2, panelY + 262, 160, 36);
    }

    private void updateCashierRects() {
        int panelWidth = 500;
        int panelHeight = 350;
        int panelX = (WIDTH - panelWidth) / 2;
        int panelY = (HEIGHT - panelHeight) / 2;
        
        upgradePatienceRect.setBounds(panelX + 30, panelY + 90, 440, 50);
        upgradeTableCountRect.setBounds(panelX + 30, panelY + 150, 440, 50);
        upgradeTableCapacityRect.setBounds(panelX + 30, panelY + 210, 440, 50);
        exitShopRect.setBounds(panelX + 150, panelY + 280, 200, 40);
    }

    private void drawUpgradeButton(Graphics2D g2, Rectangle rect, String title, String desc, String status, String cost, boolean canAfford) {
        boolean hovered = rect.contains(lastMouseX, lastMouseY);
        
        // Background color
        if (hovered) {
            g2.setColor(canAfford ? new Color(77, 182, 172, 220) : new Color(229, 115, 115, 220)); // Soft teal or soft red
        } else {
            g2.setColor(new Color(40, 28, 22, 230)); // Dark wood/brown
        }
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);
        
        // Border
        g2.setStroke(new BasicStroke(hovered ? 2.5f : 1.5f));
        g2.setColor(hovered ? (canAfford ? new Color(0, 150, 136) : Color.RED) : new Color(141, 110, 99));
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);
        
        // Title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.setColor(Color.WHITE);
        g2.drawString(title, rect.x + 15, rect.y + 22);
        
        // Description
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(224, 224, 224));
        g2.drawString(desc, rect.x + 15, rect.y + 40);
        
        // Status & Cost on the right
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.setColor(new Color(255, 224, 178));
        g2.drawString(status, rect.x + 290, rect.y + 22);
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.setColor(canAfford ? new Color(129, 199, 132) : new Color(229, 115, 115));
        g2.drawString(cost, rect.x + 290, rect.y + 40);
    }
    
    private void drawExitButton(Graphics2D g2, Rectangle rect, String text) {
        boolean hovered = rect.contains(lastMouseX, lastMouseY);
        
        if (hovered) {
            g2.setColor(new Color(229, 57, 53)); // Bright red
        } else {
            g2.setColor(new Color(183, 28, 28)); // Dark red
        }
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);
        
        g2.setStroke(new BasicStroke(hovered ? 2.0f : 1.0f));
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, textX, textY);
    }

    /**
     * Mengakhiri level lebih awal: hentikan spawn, lalu cek hasil (LEVEL_WON /
     * LEVEL_FAILED / GAME_WON). Customer yang sedang ada masih diproses sampai
     * keluar sehingga pembayaran tetap masuk sebelum layar hasil muncul.
     */
    private void handleEndLevel() {
        // Stop further customer spawning
        gameEngine.forceEndLevel();
        // Evaluate immediately (active customers may still be walking out)
        GameEngine.LevelResult result = gameEngine.cekKondisiEndLevel(0);
        switch (result) {
            case LEVEL_WON:
                state = GameState.LEVEL_WON;
                customerLogic.clearCustomers();
                break;
            case LEVEL_FAILED:
                state = GameState.LEVEL_FAILED;
                customerLogic.clearCustomers();
                break;
            case GAME_WON:
                state = GameState.GAME_WON;
                customerLogic.clearCustomers();
                break;
            default:
                // Still IN_PROGRESS means there are seated customers; just stop new spawns
                System.out.println("Level berakhir lebih awal — menunggu pelanggan selesai.");
                break;
        }
    }

    private void buyNewTable() {
        InventoryManager inv = InventoryManager.getInstance();
        int tableCost = 200 + (gameEngine.getDaftarMeja().size() - 2) * 100; // scaling cost
        if (inv.getSaldoUang() >= tableCost) {
            if (gameEngine.getDaftarMeja().size() < 5) {
                inv.kurangiUang(tableCost);
                gameEngine.tambahMejaBaru();
                
                // Recreate all buttons at their new positions
                initializeButtons();
                
                System.out.println("Bought a new table! Total tables: " + gameEngine.getDaftarMeja().size());
            } else {
                System.out.println("Max tables reached!");
            }
        } else {
            System.out.println("Not enough coins!");
        }
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

    /**
     * Selects the current animation frame for a customer, matching the logic in
     * {@link #drawCustomer}. Centralized so the hit-test and the renderer never
     * disagree on which frame is "visible".
     */
    private BufferedImage pickCustomerFrame(Customer c) {
        boolean walking = c.getState() == Customer.CustomerState.WAITING_FOR_TABLE
                || c.getState() == Customer.CustomerState.LEAVING_HAPPY
                || c.getState() == Customer.CustomerState.LEAVING_ANGRY;

        int dir = 0; // "down" direction (facing camera)
        int frame = (int) ((System.currentTimeMillis() / 150) % 2);

        BufferedImage npcFrame = null;
        if (walking) {
            if (AssetManager.playerWalk != null && AssetManager.playerWalk[dir] != null) {
                npcFrame = AssetManager.playerWalk[dir][frame];
            }
        } else {
            if (AssetManager.playerIdle != null && AssetManager.playerIdle[dir] != null) {
                npcFrame = AssetManager.playerIdle[dir][frame];
            }
        }
        if (npcFrame == null) {
            npcFrame = AssetManager.playerSprite;
        }
        return npcFrame;
    }

    private void drawCustomer(Graphics2D g2, Customer c) {
        int bob = c.getBobOffset();
        int cx = c.getX();
        int cy = c.getY() + bob;

        // Use the actual frame size; fall back to the default player sprite.
        java.awt.image.BufferedImage npcFrame = pickCustomerFrame(c);

        final int MARGIN = 8;
        int imgW = npcFrame.getWidth();
        int imgH = npcFrame.getHeight();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Shadow – oval that sits under the sprite.
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillOval(cx + MARGIN, cy + imgH - MARGIN,
                imgW - 2 * MARGIN, imgH - 2 * MARGIN);

        // Draw the sprite at the margin‑offset position, using its natural size.
        g2.drawImage(npcFrame, cx + MARGIN, cy + MARGIN, null);

        if (c.getState() == Customer.CustomerState.WAITING_FOR_ORDER) {
            String extra = c.getPorsiTersisa() > 1 ? "Porsi x" + c.getPorsiTersisa() : null;
            SpriteDialogBox.drawNpcOrder(g2, cx + imgW / 2, cy + 12,
                    c.getName(), c.getJenisPesanan(), extra);
        } else if (c.getState() == Customer.CustomerState.SEATED) {
            SpriteDialogBox.drawNpcOrder(g2, cx + imgW / 2, cy + 12,
                    c.getName(), "Klik untuk", "pesan!");
        }

        if (selectedCustomer == c && isDraggingCustomer) {
            g2.setColor(new Color(255, 200, 100, 100));
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(cx + MARGIN, cy + MARGIN, imgW, imgH);
        }

        drawPatienceBar(g2, c, cx, cy, imgW);
    }

    /**
     * Draws a shrinking patience bar above the customer's head, with a small
     * emotion face to its left. The bar fills from the left and changes color
     * as patience drains: green {@code > 60%}, orange {@code > 30%}, red
     * otherwise. The face tracks the same thresholds (happy / neutral / sad).
     * Sits flush above the sprite so it is visible in the queue and while seated.
     */
    private void drawPatienceBar(Graphics2D g2, Customer c, int cx, int cy, int imgW) {
        double ratio = Math.max(0.0, Math.min(1.0, c.getPatience() / c.getMaxPatience()));

        final int barW = 36;
        final int barH = 5;
        // Center the bar above the sprite, 1px above the top edge.
        int bx = cx + (imgW - barW) / 2;
        int by = cy - barH - 1;

        // Emotion icon – drawn first so the bar overlays the right edge cleanly
        // if the icon is unusually large. Sits to the left of the bar with a
        // 1px gap, centered vertically on it.
        final int iconSize = 12;
        final int iconGap = 1;
        int ix = bx - iconGap - iconSize;
        int iy = by + (barH - iconSize) / 2;
        BufferedImage emotion = pickEmotionIcon(ratio);
        if (emotion != null) {
            g2.drawImage(emotion, ix, iy, iconSize, iconSize, null);
        }

        // Trough (dark background, slight transparency for a softer look).
        g2.setColor(new Color(40, 28, 22, 200));
        g2.fillRect(bx, by, barW, barH);

        // Fill shrinks from the right; width 0 means the customer is fully angry.
        int fillW = (int) Math.round(barW * ratio);
        if (fillW > 0) {
            Color fill;
            if (ratio > 0.6) {
                fill = new Color(76, 175, 80);   // green
            } else if (ratio > 0.3) {
                fill = new Color(255, 152, 0);   // orange
            } else {
                fill = new Color(211, 47, 47);   // red
            }
            g2.setColor(fill);
            g2.fillRect(bx, by, fillW, barH);
        }

        // 1px outline so the bar stays visible against light floors.
        g2.setColor(new Color(20, 12, 8));
        g2.drawRect(bx, by, barW, barH);
    }

    /**
     * Maps a patience ratio (0.0–1.0) to an emotion face. Thresholds mirror
     * the bar color in {@link #drawPatienceBar}. Returns {@code null} if no
     * matching icon asset is loaded – the bar still draws on its own.
     */
    private BufferedImage pickEmotionIcon(double ratio) {
        if (ratio > 0.6) {
            return AssetManager.iconHappy;
        } else if (ratio > 0.3) {
            return AssetManager.iconNeutral;
        } else {
            return AssetManager.iconSad;
        }
    }
}
