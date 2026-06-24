package ui;

import engine.AssetRegistry;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;

public final class PixelFontHelper {

    private static Font pixelFont;
    private static boolean loaded;

    private PixelFontHelper() {}

    public static Font get(float size) {
        if (!loaded) {
            load();
        }
        if (pixelFont != null) {
            return pixelFont.deriveFont(size);
        }
        return new Font("Monospaced", Font.BOLD, Math.round(size));
    }

    private static void load() {
        loaded = true;
        String[] bases = {"src/assets/", "assets/"};
        for (String base : bases) {
            File f = new File(base + AssetRegistry.FONT_PIXEL);
            if (!f.isFile()) {
                continue;
            }
            try {
                pixelFont = Font.createFont(Font.TRUETYPE_FONT, f);
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .registerFont(pixelFont);
                return;
            } catch (Exception e) {
                System.err.println("Pixel font load failed: " + e.getMessage());
            }
        }
    }
}
