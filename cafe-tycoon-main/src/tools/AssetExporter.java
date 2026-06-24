package tools;

import engine.AssetDefinition;
import engine.AssetManager;
import engine.AssetRegistry;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AssetExporter {

    public static void main(String[] args) throws Exception {
        File outDir = new File("src/assets/sprites");
        File uiDir = new File("src/assets/sprites/ui");
        outDir.mkdirs();
        uiDir.mkdirs();

        AssetManager.loadAssetsForExport();
        AssetManager.loadUiAndNpcAssets();

        exportDef(outDir, AssetRegistry.STATION_SPRITES);
        exportDef(outDir, AssetRegistry.UI_ICONS);
        exportDef(uiDir, AssetRegistry.HUD_ICONS);

        exportUiFull(uiDir);
        exportChicken(uiDir);
        exportPlayerFrames(outDir);

        System.out.println("Exported to sprites/ and sprites/ui/");
    }

    private static void exportUiFull(File uiDir) throws Exception {
        if (AssetManager.dialogBoxMedium != null) {
            ImageIO.write(AssetManager.dialogBoxMedium, "png", new File(uiDir, "dialog_box.png"));
            System.out.println("  ui/dialog_box.png");
        }
        if (AssetManager.hudPanel != null) {
            ImageIO.write(AssetManager.hudPanel, "png", new File(uiDir, "hud_panel.png"));
            System.out.println("  ui/hud_panel.png");
        }
    }

    private static void exportChicken(File uiDir) throws Exception {
        for (int i = 0; i < AssetManager.chickenIdle.length; i++) {
            if (AssetManager.chickenIdle[i] != null) {
                ImageIO.write(AssetManager.chickenIdle[i], "png", new File(uiDir, "chicken_idle_" + i + ".png"));
            }
        }
        for (int i = 0; i < AssetManager.chickenWalk.length; i++) {
            if (AssetManager.chickenWalk[i] != null) {
                ImageIO.write(AssetManager.chickenWalk[i], "png", new File(uiDir, "chicken_walk_" + i + ".png"));
            }
        }
        System.out.println("  ui/chicken_*.png");
    }

    private static void exportDef(File outDir, AssetDefinition[] defs) throws Exception {
        for (AssetDefinition def : defs) {
            BufferedImage img = AssetManager.cropDefinition(def);
            if (img != null) {
                ImageIO.write(img, "png", new File(outDir, def.name + ".png"));
                System.out.println("  " + def.name + ".png");
            }
        }
    }

    private static void exportPlayerFrames(File outDir) throws Exception {
        String[] dirs = {"down", "up", "left", "right"};
        for (int d = 0; d < 4; d++) {
            for (int f = 0; f < 2; f++) {
                if (AssetManager.playerIdle[d][f] != null) {
                    ImageIO.write(AssetManager.playerIdle[d][f], "png",
                            new File(outDir, "player_idle_" + dirs[d] + "_" + f + ".png"));
                }
                if (AssetManager.playerWalk[d][f] != null) {
                    ImageIO.write(AssetManager.playerWalk[d][f], "png",
                            new File(outDir, "player_walk_" + dirs[d] + "_" + f + ".png"));
                }
            }
        }
        if (AssetManager.playerSprite != null) {
            ImageIO.write(AssetManager.playerSprite, "png", new File(outDir, "player.png"));
        }
        System.out.println("  player frames");
    }
}
