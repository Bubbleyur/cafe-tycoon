package ui;

import engine.AssetManager;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Dialog NPC memakai {@code dialog box medium.png} — 9-slice + ekor kiri.
 */
public final class SpriteDialogBox {

    private static final int SLICE_LEFT = 28;
    private static final int SLICE_RIGHT = 12;
    private static final int SLICE_TOP = 10;
    private static final int SLICE_BOTTOM = 10;

    private SpriteDialogBox() {}

    public static void draw(Graphics2D g2, int x, int y, int width, int height,
                            String title, String[] lines) {
        BufferedImage box = AssetManager.dialogBoxMedium;
        if (box == null) {
            drawFallback(g2, x, y, width, height, title, lines);
            return;
        }

        drawNineSlice(g2, box, x, y, width, height);

        g2.setFont(PixelFontHelper.get(11f));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        int tx = x + SLICE_LEFT + 6;
        int ty = y + SLICE_TOP + 12;

        if (title != null && !title.isEmpty()) {
            g2.setColor(new Color(101, 67, 33));
            g2.drawString(title, tx, ty);
            ty += 14;
        }

        g2.setColor(new Color(62, 39, 35));
        if (lines != null) {
            for (String line : lines) {
                if (line != null) {
                    g2.drawString(line, tx, ty);
                    ty += 13;
                }
            }
        }
    }

    /** Bubble di atas NPC — ekor menunjuk ke {@code anchorX, anchorY}. */
    public static void drawNpcOrder(Graphics2D g2, int anchorX, int anchorY,
                                    String customerName, String order, String extra) {
        String[] lines = {order};
        if (extra != null && !extra.isEmpty()) {
            lines = new String[]{order, extra};
        }
        int w = 148;
        int h = extra != null && !extra.isEmpty() ? 58 : 46;
        int bx = anchorX + 28;
        int by = anchorY - h - 8;
        if (bx + w > 790) {
            bx = anchorX - w - 12;
        }
        if (by < 78) {
            by = anchorY + 52;
        }
        draw(g2, bx, by, w, h, customerName, lines);
    }

    public static void drawCenterPanel(Graphics2D g2, int screenW, int screenH,
                                       int panelW, int panelH, String title, String[] lines) {
        int x = (screenW - panelW) / 2;
        int y = (screenH - panelH) / 2;
        draw(g2, x, y, panelW, panelH, title, lines);
    }

    private static void drawNineSlice(Graphics2D g2, BufferedImage src,
                                      int x, int y, int w, int h) {
        int sw = src.getWidth();
        int sh = src.getHeight();
        int cw = sw - SLICE_LEFT - SLICE_RIGHT;
        int ch = sh - SLICE_TOP - SLICE_BOTTOM;
        int dw = w - SLICE_LEFT - SLICE_RIGHT;
        int dh = h - SLICE_TOP - SLICE_BOTTOM;

        Object oldHint = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        g2.drawImage(src, x, y, x + SLICE_LEFT, y + SLICE_TOP, 0, 0, SLICE_LEFT, SLICE_TOP, null);
        g2.drawImage(src, x + w - SLICE_RIGHT, y, x + w, y + SLICE_TOP, sw - SLICE_RIGHT, 0, sw, SLICE_TOP, null);
        g2.drawImage(src, x, y + h - SLICE_BOTTOM, x + SLICE_LEFT, y + h, 0, sh - SLICE_BOTTOM, SLICE_LEFT, sh, null);
        g2.drawImage(src, x + w - SLICE_RIGHT, y + h - SLICE_BOTTOM, x + w, y + h,
                sw - SLICE_RIGHT, sh - SLICE_BOTTOM, sw, sh, null);

        g2.drawImage(src, x + SLICE_LEFT, y, x + w - SLICE_RIGHT, y + SLICE_TOP,
                SLICE_LEFT, 0, SLICE_LEFT + cw, SLICE_TOP, null);
        g2.drawImage(src, x + SLICE_LEFT, y + h - SLICE_BOTTOM, x + w - SLICE_RIGHT, y + h,
                SLICE_LEFT, sh - SLICE_BOTTOM, SLICE_LEFT + cw, sh, null);
        g2.drawImage(src, x, y + SLICE_TOP, x + SLICE_LEFT, y + h - SLICE_BOTTOM,
                0, SLICE_TOP, SLICE_LEFT, SLICE_TOP + ch, null);
        g2.drawImage(src, x + w - SLICE_RIGHT, y + SLICE_TOP, x + w, y + h - SLICE_BOTTOM,
                sw - SLICE_RIGHT, SLICE_TOP, sw, SLICE_TOP + ch, null);

        g2.drawImage(src, x + SLICE_LEFT, y + SLICE_TOP, x + w - SLICE_RIGHT, y + h - SLICE_BOTTOM,
                SLICE_LEFT, SLICE_TOP, SLICE_LEFT + cw, SLICE_TOP + ch, null);

        if (oldHint != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldHint);
        }
    }

    private static void drawFallback(Graphics2D g2, int x, int y, int w, int h,
                                     String title, String[] lines) {
        g2.setColor(new Color(255, 248, 220));
        g2.fillRoundRect(x, y, w, h, 8, 8);
        g2.setColor(new Color(101, 67, 33));
        g2.drawRoundRect(x, y, w, h, 8, 8);
        g2.setFont(PixelFontHelper.get(11f));
        int tx = x + 10;
        int ty = y + 16;
        if (title != null) {
            g2.drawString(title, tx, ty);
            ty += 14;
        }
        g2.setColor(new Color(62, 39, 35));
        if (lines != null) {
            for (String line : lines) {
                if (line != null) {
                    g2.drawString(line, tx, ty);
                    ty += 13;
                }
            }
        }
    }
}
