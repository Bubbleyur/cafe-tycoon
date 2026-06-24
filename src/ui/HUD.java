package ui;

import engine.AssetManager;
import entity.Player;
import java.awt.*;
import java.awt.image.BufferedImage;
import logic.GameEngine;
import logic.InventoryManager;

public class HUD {

    private static final int HUD_Y = 8;
    private static final int HUD_H = 90;
    private long shiftStartTime = 0;
    private static final long[] SHIFT_DURATION_MS = {300000, 270000, 240000, 210000, 180000}; // 5 minutes in milliseconds

    public void setShiftStartTime(long startTime) {
        this.shiftStartTime = startTime;
    }

    /** Kembalikan true jika timer shift sudah habis (00:00). */
    public boolean isShiftExpired(GameEngine engine) {
        if (shiftStartTime == 0) return false;
        return System.currentTimeMillis() - shiftStartTime >= SHIFT_DURATION_MS[engine.getCurrentLevel() - 1];
    }

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

        drawTitle(g2, panelX + 16, HUD_Y + 24);
        drawLevelBlock(g2, panelX + 120, HUD_Y + 18, engine);
        int satisfaction = Math.min(5, Math.max(1, inv.getSaldoUang() / 200));
        drawReputation(g2, panelX + 200, HUD_Y + 28, satisfaction);
        drawMoney(g2, panelX + 290, HUD_Y + 24, inv.getSaldoUang());
        drawStockSlots(g2, panelX + 390, HUD_Y + 26, inv);
        drawHeldItem(g2, panelX + 600, HUD_Y + 12, player, topping, supplyKey);
        drawShiftTimer(g2, panelX + 820, HUD_Y + 20, engine);
        drawScoreBlock(g2, panelX + 700, HUD_Y + 18, engine);

        if (oldInterp != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterp);
        }
    }

    private void drawTitle(Graphics2D g2, int x, int y) {
        g2.setFont(PixelFontHelper.get(16f));
        g2.setColor(new Color(101, 67, 33));
        g2.drawString("CAFE", x, y);
        g2.setColor(new Color(139, 90, 43));
        g2.drawString("TYCOON", x, y + 18);
    }

    private void drawLevelBlock(Graphics2D g2, int x, int y, GameEngine engine) {
        g2.setFont(PixelFontHelper.get(10f));
        g2.setColor(new Color(101, 67, 33));
        g2.drawString("LV", x, y + 12);
        g2.setFont(PixelFontHelper.get(16f));
        g2.setColor(new Color(62, 39, 35));
        g2.drawString(String.valueOf(engine.getCurrentLevel()), x + 22, y + 14);

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
                g2.drawImage(icon, x + i * 18, y, 16, 16, null);
            }
        }
    }

    private void drawReputation(Graphics2D g2, int x, int y, int hearts) {
        for (int i = 0; i < 5; i++) {
            BufferedImage icon = i < hearts ? AssetManager.iconHeartFull : AssetManager.iconHeartEmpty;
            if (icon != null) {
                g2.drawImage(icon, x + i * 16, y, 14, 14, null);
            }
        }
    }

    private void drawMoney(Graphics2D g2, int x, int y, int saldo) {
        if (AssetManager.iconCoin != null) {
            g2.drawImage(AssetManager.iconCoin, x, y, 18, 18, null);
        }
        g2.setFont(PixelFontHelper.get(14f));
        g2.setColor(new Color(62, 39, 35));
        g2.drawString("$" + saldo, x + 22, y + 14);
    }

    private void drawStockSlots(Graphics2D g2, int x, int y, InventoryManager inv) {
        miniStock(g2, x, y, "K", inv.getStokBijiKopi(), 100, new Color(141, 110, 99));
        miniStock(g2, x + 58, y, "S", inv.getStokSusu(), 100, new Color(176, 190, 197));
        miniStock(g2, x + 116, y, "T", inv.getStokTopping(), 100, new Color(255, 167, 38));
    }

    private void miniStock(Graphics2D g2, int x, int y, String label, int val, int max, Color fill) {
        g2.setFont(PixelFontHelper.get(10f));
        g2.setColor(new Color(101, 67, 33));
        g2.drawString(label, x, y + 10);
        g2.setColor(new Color(80, 60, 45));
        g2.fillRect(x + 12, y + 2, 45, 10);
        g2.setColor(fill);
        g2.fillRect(x + 12, y + 2, (int) (45 * Math.min(1.0, val / (double) max)), 10);
        g2.setFont(PixelFontHelper.get(9f));
        g2.setColor(new Color(62, 39, 35));
        g2.drawString(String.valueOf(val), x + 12, y + 22);
    }

    private void drawScoreBlock(Graphics2D g2, int x, int y, GameEngine engine) {
        g2.setFont(PixelFontHelper.get(10f));
        g2.setColor(new Color(101, 67, 33));
        g2.drawString("SKOR", x, y + 8);
        g2.setFont(PixelFontHelper.get(12f));
        g2.setColor(new Color(76, 120, 80));
        g2.drawString("$" + engine.getPoinAwal() + "/" + engine.getTargetPoinLevel(), x, y + 24);
    }

    // Legacy: kept for compatibility but no longer called from draw()
    private void drawGuestCount(Graphics2D g2, int x, int y, GameEngine engine) {
        g2.setFont(PixelFontHelper.get(10f));
        g2.setColor(new Color(101, 67, 33));
        g2.drawString("TAMU", x, y + 8);
        g2.setFont(PixelFontHelper.get(14f));
        g2.setColor(new Color(62, 39, 35));
        g2.drawString(String.valueOf(engine.getPelangganTersisaLevel()), x + 4, y + 24);
        g2.setFont(PixelFontHelper.get(10f));
        g2.setColor(new Color(76, 120, 80));
        g2.drawString("$" + engine.getPoinAwal() + "/" + engine.getTargetPoinLevel(), x + 36, y + 24);
    }

    private void drawHeldItem(Graphics2D g2, int x, int y, Player player, String topping, String supplyKey) {
        SpriteDialogBox.draw(g2, x, y, 160, 56, "BARISTA",
                new String[]{
                    "Tangan: " + player.translateItemOnHand(player.getItemOnHand()),
                    "Upgrade di Kasir Counter"
                });
    }

    private void drawShiftTimer(Graphics2D g2, int x, int y, GameEngine engine) {
        if (shiftStartTime == 0) {
            return;
        }
        
        long elapsedMs = System.currentTimeMillis() - shiftStartTime;
        long remainingMs = SHIFT_DURATION_MS[engine.getCurrentLevel() - 1] - elapsedMs;
        
        // Calculate minutes and seconds
        long minutes = Math.max(0, remainingMs / 60000);
        long seconds = Math.max(0, (remainingMs % 60000) / 1000);
        
        // Draw timer label
        g2.setFont(PixelFontHelper.get(10f));
        g2.setColor(new Color(101, 67, 33));
        g2.drawString("SHIFT", x, y + 8);
        
        // Draw time display
        g2.setFont(PixelFontHelper.get(16f));
        
        // Color based on remaining time
        Color timeColor;
        if (remainingMs > 60000) {
            timeColor = new Color(76, 120, 80); // Green
        } else if (remainingMs > 20000) {
            timeColor = new Color(255, 152, 0); // Orange
        } else {
            timeColor = new Color(211, 47, 47); // Red
        }
        
        g2.setColor(timeColor);
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        g2.drawString(timeStr, x, y + 26);
    }
}
