package ui;

import engine.AssetManager;
import entity.Player;
import java.awt.*;
import java.awt.image.BufferedImage;
import logic.GameEngine;
import logic.InventoryManager;

public class HUD {

    private static final int HUD_Y = 4;
    private static final int HUD_H = 76;

    public void draw(Graphics2D g2, InventoryManager inv, GameEngine engine, Player player,
                     int width, String topping, String supplyKey) {

        Object oldInterp = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        int panelX = 8;
        int panelW = width - 16;

        if (AssetManager.hudPanel != null) {
            g2.drawImage(AssetManager.hudPanel, panelX, HUD_Y, panelX + panelW, HUD_Y + HUD_H,
                    0, 0, AssetManager.hudPanel.getWidth(), AssetManager.hudPanel.getHeight(), null);
        } else {
            g2.setColor(new Color(210, 180, 140, 230));
            g2.fillRoundRect(panelX, HUD_Y, panelW, HUD_H, 10, 10);
            g2.setColor(new Color(101, 67, 33));
            g2.drawRoundRect(panelX, HUD_Y, panelW, HUD_H, 10, 10);
        }

        drawTitle(g2, panelX + 14, HUD_Y + 18);
        drawLevelBlock(g2, panelX + 130, HUD_Y + 14, engine);
        int satisfaction = Math.min(5, Math.max(1, inv.getSaldoUang() / 200));
        drawReputation(g2, panelX + 200, HUD_Y + 22, satisfaction);
        drawMoney(g2, panelX + 280, HUD_Y + 18, inv.getSaldoUang());
        drawStockSlots(g2, panelX + 370, HUD_Y + 20, inv);
        drawGuestCount(g2, panelX + 560, HUD_Y + 18, engine);
        drawHeldItem(g2, width - 200, HUD_Y + 12, player, topping, supplyKey);

        if (oldInterp != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterp);
        }
    }

    private void drawTitle(Graphics2D g2, int x, int y) {
        g2.setFont(PixelFontHelper.get(14f));
        g2.setColor(new Color(101, 67, 33));
        g2.drawString("CAFE", x, y);
        g2.setColor(new Color(139, 90, 43));
        g2.drawString("TYCOON", x, y + 14);
    }

    private void drawLevelBlock(Graphics2D g2, int x, int y, GameEngine engine) {
        g2.setFont(PixelFontHelper.get(9f));
        g2.setColor(new Color(101, 67, 33));
        g2.drawString("LV", x, y + 10);
        g2.setFont(PixelFontHelper.get(13f));
        g2.setColor(new Color(62, 39, 35));
        g2.drawString(String.valueOf(engine.getCurrentLevel()), x + 18, y + 12);

        int stars = Math.min(5, engine.getCurrentLevel());
        drawStars(g2, x + 42, y + 2, stars, 5);
    }

    private void drawStars(Graphics2D g2, int x, int y, int filled, int max) {
        for (int i = 0; i < max; i++) {
            BufferedImage icon = AssetManager.iconStarEmpty;
            if (i < filled) {
                icon = AssetManager.iconStarFull;
            } else if (i == filled && filled < max) {
                icon = AssetManager.iconStarHalf;
            }
            if (icon != null) {
                g2.drawImage(icon, x + i * 14, y, 12, 12, null);
            }
        }
    }

    private void drawReputation(Graphics2D g2, int x, int y, int hearts) {
        for (int i = 0; i < 5; i++) {
            BufferedImage icon = i < hearts ? AssetManager.iconHeartFull : AssetManager.iconHeartEmpty;
            if (icon != null) {
                g2.drawImage(icon, x + i * 13, y, 11, 11, null);
            }
        }
    }

    private void drawMoney(Graphics2D g2, int x, int y, int saldo) {
        if (AssetManager.iconCoin != null) {
            g2.drawImage(AssetManager.iconCoin, x, y, 14, 14, null);
        }
        g2.setFont(PixelFontHelper.get(12f));
        g2.setColor(new Color(62, 39, 35));
        g2.drawString("$" + saldo, x + 18, y + 11);
    }

    private void drawStockSlots(Graphics2D g2, int x, int y, InventoryManager inv) {
        miniStock(g2, x, y, "K", inv.getStokBijiKopi(), 100, new Color(141, 110, 99));
        miniStock(g2, x + 52, y, "S", inv.getStokSusu(), 100, new Color(176, 190, 197));
        miniStock(g2, x + 104, y, "T", inv.getStokTopping(), 100, new Color(255, 167, 38));
    }

    private void miniStock(Graphics2D g2, int x, int y, String label, int val, int max, Color fill) {
        g2.setFont(PixelFontHelper.get(8f));
        g2.setColor(new Color(101, 67, 33));
        g2.drawString(label, x, y + 8);
        g2.setColor(new Color(80, 60, 45));
        g2.fillRect(x + 10, y + 2, 38, 7);
        g2.setColor(fill);
        g2.fillRect(x + 10, y + 2, (int) (38 * Math.min(1.0, val / (double) max)), 7);
        g2.setFont(PixelFontHelper.get(7f));
        g2.setColor(new Color(62, 39, 35));
        g2.drawString(String.valueOf(val), x + 10, y + 18);
    }

    private void drawGuestCount(Graphics2D g2, int x, int y, GameEngine engine) {
        g2.setFont(PixelFontHelper.get(8f));
        g2.setColor(new Color(101, 67, 33));
        g2.drawString("TAMU", x, y + 8);
        g2.setFont(PixelFontHelper.get(12f));
        g2.setColor(new Color(62, 39, 35));
        g2.drawString(String.valueOf(engine.getPelangganTersisaLevel()), x + 4, y + 22);
        g2.setFont(PixelFontHelper.get(8f));
        g2.setColor(new Color(76, 120, 80));
        g2.drawString("$" + engine.getPoinAwal() + "/" + engine.getTargetPoinLevel(), x + 32, y + 22);
    }

    private void drawHeldItem(Graphics2D g2, int x, int y, Player player, String topping, String supplyKey) {
        SpriteDialogBox.draw(g2, x, y, 188, 52, "BARISTA",
                new String[]{
                    "Tangan: " + player.getItemOnHand(),
                    "Top[1-4]: " + topping,
                    "Beli[1-3]: bahan"
                });
    }
}
