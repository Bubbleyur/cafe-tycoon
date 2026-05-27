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

    // Advanced Sprite Sheet Arrays: [Direction][Frame]
    // Directions: 0 = Down, 1 = Up, 2 = Left, 3 = Right
    // Frames: 2 frames per animation
    public static BufferedImage[][] playerIdle = new BufferedImage[4][2];
    public static BufferedImage[][] playerWalk = new BufferedImage[4][2];

    private static final String ASSETS_PATH = "src/assets/";

    public static void loadAssets() {
        // Ensure directory exists
        File directory = new File(ASSETS_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        boolean sheetsLoaded = tryLoadFromSpriteSheets();

        if (!sheetsLoaded) {
            System.out.println("Sprout Lands sprite sheets not found or incomplete. Generating high-fidelity fallbacks...");
            generateProceduralFallbacks();
        }
    }

    private static boolean tryLoadFromSpriteSheets() {
        File charFile = new File(ASSETS_PATH + "Basic Charakter Spritesheet.png");
        File interiorFile = new File(ASSETS_PATH + "Interiors_free_16x16.png");
        File emojiFile = new File(ASSETS_PATH + "Emoji_Spritesheet_Free.png");

        // Alternate paths (just in case)
        if (!charFile.exists() && new File("assets/Basic Charakter Spritesheet.png").exists()) {
            charFile = new File("assets/Basic Charakter Spritesheet.png");
        }
        if (!interiorFile.exists() && new File("assets/Interiors_free_16x16.png").exists()) {
            interiorFile = new File("assets/Interiors_free_16x16.png");
        }

        if (!charFile.exists() || !interiorFile.exists()) {
            return false;
        }

        try {
            // 1. Load Sprout Lands Character Sheet
            BufferedImage charSheet = ImageIO.read(charFile);
            int size = 48; // Sprout Lands Character grid is 48x48
            
            // Loop through all directions (Down=0, Up=1, Left=2, Right=3)
            for (int d = 0; d < 4; d++) {
                // Slicing Down/Up/Left/Right subimages
                playerIdle[d][0] = charSheet.getSubimage(0, d * size, size, size);
                playerIdle[d][1] = charSheet.getSubimage(size, d * size, size, size);
                playerWalk[d][0] = charSheet.getSubimage(size * 2, d * size, size, size);
                playerWalk[d][1] = charSheet.getSubimage(size * 3, d * size, size, size);
            }

            // Assign default compatibility sprite (Idle down, frame 0)
            playerSprite = playerIdle[0][0];

            // 2. Load Sprout Lands Interiors Sheet
            BufferedImage interiorSheet = ImageIO.read(interiorFile);
            BufferedImage emojiSheet = ImageIO.read(emojiFile);

            // Emoji sheet: uniform 16x16 grid
            tableSprite = cropTile(emojiSheet, 3, 15, 16);            // Round wood table (16x16)
            
            // Interior sheet: variable sizes - use cropTileVariable for mixed tile sizes
            coffeeMachineSprite = cropTileVariable(interiorSheet, 12, 18, 32, 48);    // Coffee machine (32x48)
            milkDispenserSprite = cropTileVariable(interiorSheet, 14, 16, 24, 40);    // Glass dispenser (24x40)
            toppingStationSprite = cropTileVariable(interiorSheet, 13, 18, 28, 44);   // Spice station (28x44)
            stockStationSprite = cropTileVariable(interiorSheet, 8, 12, 48, 56);      // Supply crate (48x56)
            cashierCounterSprite = cropTileVariable(interiorSheet, 7, 18, 44, 52);    // Counter (44x52)

            System.out.println("Successfully synchronized and sliced Sprout Lands spritesheet assets!");
            return true;

        } catch (Exception e) {
            System.err.println("Failed to slice spritesheets: " + e.getMessage() + ". Defaulting to procedural assets.");
            return false;
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
    private static BufferedImage cropTile(BufferedImage sheet, int col, int row, int size) {
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
    private static BufferedImage cropTileVariable(BufferedImage sheet, int col, int row, int width, int height) {
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
    private static BufferedImage cropTileAbsolute(BufferedImage sheet, int x, int y, int width, int height) {
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
