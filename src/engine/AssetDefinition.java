package engine;

/**
 * Defines an asset location in a spritesheet.
 * Use with AssetManager.cropTile methods for flexible asset management.
 * 
 * Example for uniform grid (16x16 tiles):
 *   new AssetDefinition("coffeeTable", "Emoji_Spritesheet_Free.png", 3, 15, 16, 16)
 * 
 * Example for variable grid (mixed sizes):
 *   new AssetDefinition("coffeeMachine", "Interiors_free_16x16.png", 12, 18, 32, 48)
 * 
 * Example for absolute pixel coords (irregular layout):
 *   new AssetDefinition("customTile", "custom.png", 120, 240, 64, 64, true)
 */
public class AssetDefinition {
    public String name;
    public String sheetFile;
    public int x;  // Column (grid) or X pixel (absolute)
    public int y;  // Row (grid) or Y pixel (absolute)
    public int width;
    public int height;
    public boolean isAbsoluteCoords;  // If true, use absolute pixel coords instead of grid

    /**
     * Constructor for grid-based tiles (most common).
     * @param name Asset identifier
     * @param sheetFile Path to spritesheet
     * @param col Column in grid
     * @param row Row in grid
     * @param width Tile width
     * @param height Tile height
     */
    public AssetDefinition(String name, String sheetFile, int col, int row, int width, int height) {
        this.name = name;
        this.sheetFile = sheetFile;
        this.x = col;
        this.y = row;
        this.width = width;
        this.height = height;
        this.isAbsoluteCoords = false;
    }

    /**
     * Constructor for absolute pixel coordinates (for irregular layouts).
     * @param name Asset identifier
     * @param sheetFile Path to spritesheet
     * @param x Pixel X coordinate
     * @param y Pixel Y coordinate
     * @param width Tile width
     * @param height Tile height
     * @param isAbsoluteCoords Must be true to use absolute coordinates
     */
    public AssetDefinition(String name, String sheetFile, int x, int y, int width, int height, boolean isAbsoluteCoords) {
        this.name = name;
        this.sheetFile = sheetFile;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isAbsoluteCoords = isAbsoluteCoords;
    }

    @Override
    public String toString() {
        String coordType = isAbsoluteCoords ? "pixel" : "grid";
        return String.format("AssetDefinition{%s from %s at (%d,%d) as %dx%d (%s)}", 
            name, sheetFile, x, y, width, height, coordType);
    }
}
