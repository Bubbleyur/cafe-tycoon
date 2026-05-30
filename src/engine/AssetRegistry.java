package engine;

/**
 * Koordinat crop asset — Sprout Lands + UI custom di {@code src/assets/}.
 */
public final class AssetRegistry {

    public static final String SHEET_CHARACTER = "Basic Charakter Spritesheet.png";
    public static final String SHEET_INTERIOR = "Interiors_free_16x16.png";
    public static final String SHEET_EMOJI = "Emoji_Spritesheet_Free.png";
    public static final String SHEET_HAPPINESS = "Small Happines-Sadness icons.png";
    public static final String SHEET_CHICKEN = "Free Chicken Sprites.png";
    public static final String SHEET_DIALOG = "dialog box medium.png";
    public static final String SHEET_HUD_PANEL = "Inventory_Light_example_with_slots_2.png";
    public static final String SHEET_SPECIAL_ICONS = "Special Icons.png";
    public static final String FONT_PIXEL = "pixelFont-7-8x14-sproutLands.ttf";

    public static final int CHAR_TILE = 48;
    public static final int CHICKEN_TILE = 16;
    /** Baris 2 = outline putih tebal di Special Icons. */
    public static final int ICON_ROW = 2;
    public static final int ICON_SIZE = 16;

    public static final AssetDefinition[] STATION_SPRITES = {
        new AssetDefinition("table", SHEET_EMOJI, 3, 15, 16, 16),
        new AssetDefinition("coffee_machine", SHEET_INTERIOR, 12, 18, 32, 48),
        new AssetDefinition("milk_dispenser", SHEET_INTERIOR, 14, 16, 24, 40),
        new AssetDefinition("topping_station", SHEET_INTERIOR, 13, 18, 28, 44),
        new AssetDefinition("stock_station", SHEET_INTERIOR, 8, 12, 48, 56),
        new AssetDefinition("cashier_counter", SHEET_INTERIOR, 7, 18, 44, 52),
    };

    public static final AssetDefinition[] UI_ICONS = {
        new AssetDefinition("icon_happy", SHEET_HAPPINESS, 0, 0, 16, 16),
        new AssetDefinition("icon_sad", SHEET_HAPPINESS, 1, 0, 16, 16),
    };

    /** Special Icons — kolom 0–6, baris ICON_ROW. */
    public static final AssetDefinition[] HUD_ICONS = {
        new AssetDefinition("icon_star_full", SHEET_SPECIAL_ICONS, 0, ICON_ROW, ICON_SIZE, ICON_SIZE),
        new AssetDefinition("icon_star_half", SHEET_SPECIAL_ICONS, 1, ICON_ROW, ICON_SIZE, ICON_SIZE),
        new AssetDefinition("icon_star_empty", SHEET_SPECIAL_ICONS, 2, ICON_ROW, ICON_SIZE, ICON_SIZE),
        new AssetDefinition("icon_heart_full", SHEET_SPECIAL_ICONS, 3, ICON_ROW, ICON_SIZE, ICON_SIZE),
        new AssetDefinition("icon_heart_half", SHEET_SPECIAL_ICONS, 4, ICON_ROW, ICON_SIZE, ICON_SIZE),
        new AssetDefinition("icon_heart_empty", SHEET_SPECIAL_ICONS, 5, ICON_ROW, ICON_SIZE, ICON_SIZE),
        new AssetDefinition("icon_coin", SHEET_SPECIAL_ICONS, 6, ICON_ROW, ICON_SIZE, ICON_SIZE),
    };

    private AssetRegistry() {}
}
