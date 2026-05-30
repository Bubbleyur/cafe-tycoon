package engine;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AssetManager {

    // Compatibility Static Sprites
    public static BufferedImage playerSprite;
    public static BufferedImage tableSprite;
    public static BufferedImage coffeeMachineSprite;
    public static BufferedImage milkDispenserSprite;
    public static BufferedImage toppingStationSprite;
    public static BufferedImage stockStationSprite;
    public static BufferedImage cashierCounterSprite;
    public static BufferedImage iconHappy;
    public static BufferedImage iconSad;

    // UI sprites
    public static BufferedImage dialogBoxMedium;
    public static BufferedImage hudPanel;
    public static BufferedImage iconStarFull;
    public static BufferedImage iconStarHalf;
    public static BufferedImage iconStarEmpty;
    public static BufferedImage iconHeartFull;
    public static BufferedImage iconHeartHalf;
    public static BufferedImage iconHeartEmpty;
    public static BufferedImage iconCoin;

    // NPC pelanggan — Free Chicken Sprites
    public static BufferedImage[] chickenIdle = new BufferedImage[2];
    public static BufferedImage[] chickenWalk = new BufferedImage[4];

    // Advanced Sprite Sheet Arrays: [Direction][Frame]
    // Directions: 0 = Down, 1 = Up, 2 = Left, 3 = Right
    // Frames: 2 frames per animation
    public static BufferedImage[][] playerIdle = new BufferedImage[4][2];
    public static BufferedImage[][] playerWalk = new BufferedImage[4][2];

    private static final String[] ASSET_BASE_PATHS = {
        "src/assets/",
        "assets/",
        "../src/assets/"
    };
    private static final String SPRITES_PATH = "src/assets/sprites/";

    public static void loadAssets() {
        ensureAssetDirs();
        loadUiAndNpcAssets();

        if (tryLoadPreCroppedSprites()) {
            System.out.println("Loaded pre-cropped sprites from src/assets/sprites/");
            return;
        }

        if (tryLoadFromSpriteSheets()) {
            return;
        }

        System.out.println("Sprite sheets not found. Generating procedural fallbacks...");
        generateProceduralFallbacks();
    }

    /** UI + ayam NPC — selalu dimuat (sheet atau sprites/ui). */
    public static void loadUiAndNpcAssets() {
        if (!tryLoadUiFromCropped()) {
            loadUiAndNpcFromSheets();
        }
    }

    private static boolean tryLoadUiFromCropped() {
        try {
            dialogBoxMedium = loadPng(SPRITES_PATH + "ui/dialog_box.png");
            hudPanel = loadPng(SPRITES_PATH + "ui/hud_panel.png");
            iconCoin = loadPng(SPRITES_PATH + "ui/icon_coin.png");
            iconHeartFull = loadPng(SPRITES_PATH + "ui/icon_heart_full.png");
            iconHeartHalf = loadPng(SPRITES_PATH + "ui/icon_heart_half.png");
            iconHeartEmpty = loadPng(SPRITES_PATH + "ui/icon_heart_empty.png");
            iconStarFull = loadPng(SPRITES_PATH + "ui/icon_star_full.png");
            iconStarHalf = loadPng(SPRITES_PATH + "ui/icon_star_half.png");
            iconStarEmpty = loadPng(SPRITES_PATH + "ui/icon_star_empty.png");

            for (int i = 0; i < 2; i++) {
                chickenIdle[i] = loadPng(SPRITES_PATH + "ui/chicken_idle_" + i + ".png");
            }
            for (int i = 0; i < 4; i++) {
                chickenWalk[i] = loadPng(SPRITES_PATH + "ui/chicken_walk_" + i + ".png");
            }
            return dialogBoxMedium != null && chickenIdle[0] != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static void loadUiAndNpcFromSheets() {
        try {
            File dialog = resolveSheet(AssetRegistry.SHEET_DIALOG);
            if (dialog.isFile()) {
                dialogBoxMedium = ImageIO.read(dialog);
            }

            File hud = resolveSheet(AssetRegistry.SHEET_HUD_PANEL);
            if (hud.isFile()) {
                hudPanel = ImageIO.read(hud);
            }

            for (AssetDefinition def : AssetRegistry.HUD_ICONS) {
                assignHudIcon(def.name, cropDefinition(def));
            }

            File chicken = resolveSheet(AssetRegistry.SHEET_CHICKEN);
            if (chicken.isFile()) {
                BufferedImage sheet = ImageIO.read(chicken);
                int t = AssetRegistry.CHICKEN_TILE;
                chickenIdle[0] = cropTileAbsolute(sheet, 0, 0, t, t);
                chickenIdle[1] = cropTileAbsolute(sheet, t, 0, t, t);
                for (int i = 0; i < 4; i++) {
                    chickenWalk[i] = cropTileAbsolute(sheet, i * t, t, t, t);
                }
            }
            System.out.println("Loaded UI + chicken NPC from src/assets/ sheets.");
        } catch (Exception e) {
            System.err.println("UI/NPC sheet load failed: " + e.getMessage());
        }
    }

    private static void assignHudIcon(String name, BufferedImage img) {
        switch (name) {
            case "icon_star_full": iconStarFull = img; break;
            case "icon_star_half": iconStarHalf = img; break;
            case "icon_star_empty": iconStarEmpty = img; break;
            case "icon_heart_full": iconHeartFull = img; break;
            case "icon_heart_half": iconHeartHalf = img; break;
            case "icon_heart_empty": iconHeartEmpty = img; break;
            case "icon_coin": iconCoin = img; break;
            default: break;
        }
    }

    public static BufferedImage getChickenFrame(int customerId, boolean walking, long timeMs) {
        BufferedImage[] frames = walking ? chickenWalk : chickenIdle;
        if (frames == null || frames[0] == null) {
            return null;
        }
        int count = walking ? 4 : 2;
        int idx = (int) ((timeMs / (walking ? 120 : 280) + customerId) % count);
        BufferedImage frame = frames[idx];
        if (frame == null) {
            frame = frames[0];
        }
        return frame;
    }

    /** Dipanggil {@link tools.AssetExporter} — selalu crop dari sheet proto1. */
    public static void loadAssetsForExport() throws Exception {
        ensureAssetDirs();
        if (!tryLoadFromSpriteSheets()) {
            throw new IllegalStateException("Sheets missing in src/assets/ — copy from proto1 first.");
        }
        loadUiAndNpcFromSheets();
    }

    private static void ensureAssetDirs() {
        for (String base : ASSET_BASE_PATHS) {
            File directory = new File(base);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
        new File(SPRITES_PATH).mkdirs();
    }

    private static File resolveSheet(String filename) {
        for (String base : ASSET_BASE_PATHS) {
            File f = new File(base + filename);
            if (f.isFile()) {
                return f;
            }
        }
        return new File(ASSET_BASE_PATHS[0] + filename);
    }

    private static boolean tryLoadPreCroppedSprites() {
        try {
            BufferedImage table = loadPng(SPRITES_PATH + "table.png");
            if (table == null) {
                return false;
            }
            tableSprite = table;
            coffeeMachineSprite = loadPng(SPRITES_PATH + "coffee_machine.png");
            milkDispenserSprite = loadPng(SPRITES_PATH + "milk_dispenser.png");
            toppingStationSprite = loadPng(SPRITES_PATH + "topping_station.png");
            stockStationSprite = loadPng(SPRITES_PATH + "stock_station.png");
            cashierCounterSprite = loadPng(SPRITES_PATH + "cashier_counter.png");
            iconHappy = loadPng(SPRITES_PATH + "icon_happy.png");
            iconSad = loadPng(SPRITES_PATH + "icon_sad.png");

            String[] dirs = {"down", "up", "left", "right"};
            boolean playerOk = true;
            for (int d = 0; d < 4; d++) {
                for (int f = 0; f < 2; f++) {
                    playerIdle[d][f] = loadPng(SPRITES_PATH + "player_idle_" + dirs[d] + "_" + f + ".png");
                    playerWalk[d][f] = loadPng(SPRITES_PATH + "player_walk_" + dirs[d] + "_" + f + ".png");
                    if (playerIdle[d][f] == null || playerWalk[d][f] == null) {
                        playerOk = false;
                    }
                }
            }
            if (!playerOk) {
                return false;
            }
            playerSprite = playerIdle[0][0];
            return coffeeMachineSprite != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static BufferedImage loadPng(String path) throws Exception {
        File f = new File(path);
        if (!f.isFile()) {
            return null;
        }
        return ImageIO.read(f);
    }

    private static boolean tryLoadFromSpriteSheets() {
        File charFile = resolveSheet(AssetRegistry.SHEET_CHARACTER);
        File interiorFile = resolveSheet(AssetRegistry.SHEET_INTERIOR);
        File emojiFile = resolveSheet(AssetRegistry.SHEET_EMOJI);

        if (!charFile.exists() || !interiorFile.exists() || !emojiFile.exists()) {
            System.err.println("Missing sheets in src/assets/. Expected Sprout Lands PNGs from proto1.");
            return false;
        }

        try {
            BufferedImage charSheet = ImageIO.read(charFile);
            int size = AssetRegistry.CHAR_TILE;

            for (int d = 0; d < 4; d++) {
                playerIdle[d][0] = charSheet.getSubimage(0, d * size, size, size);
                playerIdle[d][1] = charSheet.getSubimage(size, d * size, size, size);
                playerWalk[d][0] = charSheet.getSubimage(size * 2, d * size, size, size);
                playerWalk[d][1] = charSheet.getSubimage(size * 3, d * size, size, size);
            }
            playerSprite = playerIdle[0][0];

            for (AssetDefinition def : AssetRegistry.STATION_SPRITES) {
                assignStationSprite(def.name, cropDefinition(def));
            }
            for (AssetDefinition def : AssetRegistry.UI_ICONS) {
                BufferedImage icon = cropDefinition(def);
                if ("icon_happy".equals(def.name)) {
                    iconHappy = icon;
                } else if ("icon_sad".equals(def.name)) {
                    iconSad = icon;
                }
            }

            System.out.println("Cropped Sprout Lands assets from proto1 sheets (src/assets/).");
            return true;

        } catch (Exception e) {
            System.err.println("Failed to slice spritesheets: " + e.getMessage());
            return false;
        }
    }

    public static BufferedImage cropDefinition(AssetDefinition def) throws Exception {
        BufferedImage sheet = ImageIO.read(resolveSheet(def.sheetFile));
        if (def.isAbsoluteCoords) {
            return cropTileAbsolute(sheet, def.x, def.y, def.width, def.height);
        }
        // Sprout Lands interior/emoji/happiness = grid 16×16, objek bisa 32×48 px
        if (def.sheetFile.contains("Interiors")
                || def.sheetFile.contains("Emoji")
                || def.sheetFile.contains("Happines")
                || def.sheetFile.contains("Special")) {
            return cropFromBaseGrid(sheet, def.x, def.y, 16, def.width, def.height);
        }
        return cropTileVariable(sheet, def.x, def.y, def.width, def.height);
    }

    /** Grid 16×16 (atau ukuran lain): posisi = col*grid, lalu crop px berukuran width×height. */
    public static BufferedImage cropFromBaseGrid(BufferedImage sheet, int col, int row,
                                                  int gridSize, int width, int height) {
        return cropTileAbsolute(sheet, col * gridSize, row * gridSize, width, height);
    }

    private static void assignStationSprite(String name, BufferedImage img) {
        switch (name) {
            case "table": tableSprite = img; break;
            case "coffee_machine": coffeeMachineSprite = img; break;
            case "milk_dispenser": milkDispenserSprite = img; break;
            case "topping_station": toppingStationSprite = img; break;
            case "stock_station": stockStationSprite = img; break;
            case "cashier_counter": cashierCounterSprite = img; break;
            default: break;
        }
    }

    /**
     * Crops a tile from a spritesheet with uniform grid.
     * Use for sheets with equal-sized tiles (e.g., 16x16 or 48x48 throughout).
     * @param sheet The spritesheet BufferedImage
     * @param col Column index in the grid
     * @param row Row index in the grid
     * @param size Width and height of each tile (square)
     * @return The cropped tile image
     */
    public static BufferedImage cropTile(BufferedImage sheet, int col, int row, int size) {
        return cropTileVariable(sheet, col, row, size, size);
    }

    /**
     * Crops a tile from a spritesheet with variable tile sizes.
     * Use for sheets with different-sized tiles in different areas.
     * @param sheet The spritesheet BufferedImage
     * @param col Column index in the grid
     * @param row Row index in the grid
     * @param width Width of the tile to crop
     * @param height Height of the tile to crop
     * @return The cropped tile image
     */
    public static BufferedImage cropTileVariable(BufferedImage sheet, int col, int row, int width, int height) {
        int x = col * width;
        int y = row * height;
        
        if (x + width <= sheet.getWidth() && y + height <= sheet.getHeight()) {
            return sheet.getSubimage(x, y, width, height);
        }
        
        System.err.println("Warning: Tile at (" + col + "," + row + ") with size (" + width + "x" + height + ") exceeds sheet bounds!");
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Crops a tile from a spritesheet at exact pixel coordinates.
     * Use for complex/irregular spritesheets where grid doesn't apply.
     * @param sheet The spritesheet BufferedImage
     * @param x Starting X pixel coordinate
     * @param y Starting Y pixel coordinate
     * @param width Width of the tile to crop
     * @param height Height of the tile to crop
     * @return The cropped tile image
     */
    public static BufferedImage cropTileAbsolute(BufferedImage sheet, int x, int y, int width, int height) {
        if (x >= 0 && y >= 0 && x + width <= sheet.getWidth() && y + height <= sheet.getHeight()) {
            return sheet.getSubimage(x, y, width, height);
        }
        
        System.err.println("Warning: Absolute crop (" + x + "," + y + ") size (" + width + "x" + height + ") exceeds sheet bounds!");
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    private static void generateProceduralFallbacks() {
        // Generate compatibility sprites
        playerSprite = generateProceduralAsset(AssetType.PLAYER, 0, false);
        tableSprite = generateProceduralAsset(AssetType.TABLE, 0, false);
        coffeeMachineSprite = generateProceduralAsset(AssetType.COFFEE_MACHINE, 0, false);
        milkDispenserSprite = generateProceduralAsset(AssetType.MILK_DISPENSER, 0, false);
        toppingStationSprite = generateProceduralAsset(AssetType.TOPPING_STATION, 0, false);
        stockStationSprite = generateProceduralAsset(AssetType.STOCK_STATION, 0, false);
        cashierCounterSprite = generateProceduralAsset(AssetType.CASHIER_COUNTER, 0, false);

        // Generate full animation array fallbacks dynamically using bobs and tints
        for (int d = 0; d < 4; d++) {
            playerIdle[d][0] = generateProceduralAsset(AssetType.PLAYER, d, false);
            playerIdle[d][1] = generateProceduralAsset(AssetType.PLAYER, d, true); // Bobbed
            playerWalk[d][0] = generateProceduralAsset(AssetType.PLAYER, d, false);
            playerWalk[d][1] = generateProceduralAsset(AssetType.PLAYER, d, true);
        }
    }

    private enum AssetType {
        PLAYER, TABLE, COFFEE_MACHINE, MILK_DISPENSER, TOPPING_STATION, STOCK_STATION, CASHIER_COUNTER
    }

    private static BufferedImage generateProceduralAsset(AssetType type, int direction, boolean bobbed) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int bob = bobbed ? -3 : 0;

        switch (type) {
            case PLAYER:
                // Base shadow
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillOval(8, 48, 48, 12);

                // Body (Apron & Shirt)
                g2.setColor(new Color(240, 240, 240)); // White Shirt
                g2.fill(new RoundRectangle2D.Float(16, 28 + bob, 32, 24, 10, 10));
                g2.setColor(new Color(46, 125, 50)); // Green Apron
                g2.fill(new RoundRectangle2D.Float(20, 32 + bob, 24, 20, 6, 6));
                
                // Head (Skin Tone)
                g2.setColor(new Color(255, 213, 128));
                g2.fillOval(20, 10 + bob, 24, 24);

                // Hair (Brown)
                g2.setColor(new Color(109, 76, 65));
                g2.fillArc(18, 8 + bob, 28, 20, 0, 180);

                // Barista Hat (Dark Slate)
                g2.setColor(new Color(38, 50, 56));
                g2.fill(new RoundRectangle2D.Float(16, 4 + bob, 32, 8, 4, 4));
                g2.fillRect(20, 1 + bob, 24, 6);

                // Face details based on direction
                g2.setColor(new Color(33, 33, 33)); // Eyes
                if (direction == 0) { // Facing Down
                    g2.fillOval(26, 20 + bob, 3, 3);
                    g2.fillOval(35, 20 + bob, 3, 3);
                    g2.setColor(new Color(224, 117, 117)); // Smile
                    g2.drawArc(29, 22 + bob, 6, 4, 180, 180);
                } else if (direction == 2) { // Facing Left
                    g2.fillOval(23, 20 + bob, 3, 3);
                    g2.fillOval(29, 20 + bob, 3, 3);
                } else if (direction == 3) { // Facing Right
                    g2.fillOval(32, 20 + bob, 3, 3);
                    g2.fillOval(38, 20 + bob, 3, 3);
                } // Facing Up has no eyes drawn (back of head)
                break;

            case TABLE:
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillOval(4, 38, 56, 20);

                g2.setColor(new Color(141, 110, 99)); // Chair Wood Color
                g2.fill(new RoundRectangle2D.Float(6, 24, 10, 24, 4, 4));
                g2.fill(new RoundRectangle2D.Float(48, 24, 10, 24, 4, 4));

                g2.setColor(new Color(62, 39, 35));
                g2.fillRect(28, 28, 8, 22);
                g2.fill(new RoundRectangle2D.Float(20, 46, 24, 6, 4, 4));

                GradientPaint tableGrad = new GradientPaint(12, 10, new Color(188, 143, 143), 52, 28, new Color(139, 69, 19));
                g2.setPaint(tableGrad);
                g2.fillOval(12, 10, 40, 20);
                g2.setColor(new Color(222, 184, 135));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(12, 10, 40, 20);
                break;

            case COFFEE_MACHINE:
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillOval(4, 52, 56, 10);

                GradientPaint metalGrad = new GradientPaint(8, 8, new Color(207, 216, 220), 56, 50, new Color(120, 144, 156));
                g2.setPaint(metalGrad);
                g2.fill(new RoundRectangle2D.Float(8, 8, 48, 44, 8, 8));

                g2.setColor(new Color(55, 71, 79));
                g2.fillRect(12, 42, 40, 6);

                g2.setColor(new Color(38, 50, 56));
                g2.fillRect(20, 24, 8, 6);
                g2.fillRect(36, 24, 8, 6);

                g2.setColor(Color.WHITE);
                g2.fillOval(16, 14, 8, 8);
                g2.setColor(Color.BLACK);
                g2.drawOval(16, 14, 8, 8);

                g2.setColor(new Color(30, 136, 229)); 
                g2.fillRect(28, 13, 18, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Monospaced", Font.BOLD, 6));
                g2.drawString("HOT", 31, 19);

                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(21, 34, 6, 8, 2, 2));
                g2.fill(new RoundRectangle2D.Float(37, 34, 6, 8, 2, 2));
                break;

            case MILK_DISPENSER:
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillOval(6, 52, 52, 10);

                g2.setColor(new Color(121, 85, 72));
                g2.fill(new RoundRectangle2D.Float(8, 44, 48, 10, 4, 4));

                g2.setColor(new Color(255, 255, 255, 180));
                g2.fill(new RoundRectangle2D.Float(16, 12, 32, 32, 6, 6));
                
                g2.setColor(new Color(245, 245, 245));
                g2.fill(new RoundRectangle2D.Float(18, 18, 28, 24, 4, 4));

                g2.setColor(new Color(33, 150, 243));
                g2.fill(new RoundRectangle2D.Float(24, 8, 16, 5, 2, 2));

                g2.setColor(new Color(176, 190, 197));
                g2.fillRect(28, 34, 8, 4);
                g2.fillRect(32, 34, 4, 8);
                break;

            case TOPPING_STATION:
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillOval(4, 52, 56, 10);

                g2.setColor(new Color(78, 52, 46));
                g2.fill(new RoundRectangle2D.Float(6, 38, 52, 16, 4, 4));

                g2.setColor(new Color(255, 255, 255, 180));
                g2.fill(new RoundRectangle2D.Float(10, 20, 12, 18, 4, 4));
                g2.setColor(new Color(62, 39, 35));
                g2.fillRect(11, 25, 10, 12);

                g2.setColor(new Color(255, 167, 38));
                g2.fill(new RoundRectangle2D.Float(26, 14, 12, 24, 4, 4));

                g2.setColor(new Color(230, 230, 230));
                g2.fill(new RoundRectangle2D.Float(42, 18, 12, 20, 4, 4));
                break;

            case STOCK_STATION:
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillOval(4, 52, 56, 10);

                g2.setColor(new Color(93, 64, 55));
                g2.fillRect(8, 10, 6, 44);
                g2.fillRect(50, 10, 6, 44);
                g2.fillRect(8, 22, 48, 4);
                g2.fillRect(8, 38, 48, 4);
                g2.fillRect(6, 48, 52, 6);

                g2.setColor(new Color(161, 136, 127));
                g2.fillOval(14, 30, 16, 18);
                g2.fillOval(26, 31, 14, 17);
                break;

            case CASHIER_COUNTER:
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillOval(4, 52, 56, 10);

                // Counter base (wood)
                g2.setColor(new Color(139, 90, 43));
                g2.fill(new RoundRectangle2D.Float(6, 32, 52, 20, 4, 4));

                // Counter top surface
                g2.setColor(new Color(210, 180, 140));
                g2.fillRect(8, 28, 48, 6);

                // Register/drawer area
                g2.setColor(new Color(101, 67, 33));
                g2.fillRect(14, 12, 36, 16);

                // Cash drawer
                g2.setColor(new Color(184, 134, 11));
                g2.fillRect(22, 16, 20, 8);
                g2.setColor(new Color(139, 105, 20));
                g2.drawRect(22, 16, 20, 8);

                // Display/buttons area
                g2.setColor(new Color(70, 70, 70));
                g2.fillRect(16, 14, 6, 4);
                g2.fillRect(30, 14, 6, 4);
                g2.fillRect(44, 14, 6, 4);

                break;
        }

        g2.dispose();
        return img;
    }
}
