package ui;

import java.awt.*;
import logic.GameEngine;

public class MenuUI {

    public void drawMenu(Graphics2D g2, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(44, 24, 16), width, height, new Color(15, 10, 8));
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, width, height);

        SpriteDialogBox.drawCenterPanel(g2, width, height, 420, 200, "CAFE TYCOON",
                new String[]{
                    "Kelola kafe pixel-mu!",
                    "Proto1 GUI + Protojaden logic",
                    "",
                    "[ ENTER ] Mulai"
                });

        int alphaPulse = (int) (127 + 128 * Math.sin(System.currentTimeMillis() * 0.005));
        g2.setFont(PixelFontHelper.get(10f));
        g2.setColor(new Color(255, 255, 255, Math.max(40, Math.min(255, alphaPulse))));
        g2.drawString("Pelanggan = ayam dari Free Chicken Sprites", width / 2 - 160, height - 40);
    }

    public void drawPaused(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, width, height);
        SpriteDialogBox.drawCenterPanel(g2, width, height, 360, 140, "JEDA",
                new String[]{"ESC = lanjut", "R = ulang level"});
    }

    public void drawLevelWon(Graphics2D g2, int width, int height, GameEngine engine) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, width, height);
        SpriteDialogBox.drawCenterPanel(g2, width, height, 400, 150, "LEVEL SELESAI!",
                new String[]{
                    "Level " + engine.getCurrentLevel() + " cleared",
                    "Poin: $" + engine.getPoinAwal() + " / $" + engine.getTargetPoinLevel(),
                    "[ ENTER ] Level berikutnya"
                });
    }

    public void drawLevelFailed(Graphics2D g2, int width, int height, GameEngine engine) {
        g2.setColor(new Color(40, 10, 10, 180));
        g2.fillRect(0, 0, width, height);
        SpriteDialogBox.drawCenterPanel(g2, width, height, 380, 140, "GAGAL",
                new String[]{
                    "Target: $" + engine.getTargetPoinLevel(),
                    "Kamu: $" + engine.getPoinAwal(),
                    "[ R ] Ulangi"
                });
    }

    public void drawGameWon(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, width, height);
        SpriteDialogBox.drawCenterPanel(g2, width, height, 420, 160, "MASTER BARISTA!",
                new String[]{
                    "Semua 5 level selesai!",
                    "Terima kasih sudah main",
                    "[ R ] Menu utama"
                });
    }
}
