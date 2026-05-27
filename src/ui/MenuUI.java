package ui;

import java.awt.*;
import java.awt.geom.*;

public class MenuUI {

    public void drawMenu(Graphics2D g2, int width, int height) {
        // High quality rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Dark Cohesive Cafe Wallpaper Gradient
        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(44, 24, 16), width, height, new Color(15, 10, 8));
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, width, height);

        // Subtle geometric background coffee ring shapes
        g2.setColor(new Color(255, 255, 255, 5));
        g2.drawOval(width / 2 - 150, height / 2 - 150, 300, 300);
        g2.drawOval(width / 2 - 170, height / 2 - 170, 340, 340);

        // 2. Cozy Coffee Cup Steam Vector Art in center
        int cupX = width / 2;
        int cupY = height / 2 - 40;

        // Draw Steam (moving squiggly curves based on time)
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double pulse = Math.sin(System.currentTimeMillis() * 0.003);
        
        Path2D steam1 = new Path2D.Double();
        steam1.moveTo(cupX - 10, cupY - 20);
        steam1.curveTo(cupX - 5 + pulse * 5, cupY - 35, cupX - 15 - pulse * 5, cupY - 50, cupX - 10, cupY - 65);
        g2.draw(steam1);

        Path2D steam2 = new Path2D.Double();
        steam2.moveTo(cupX + 10, cupY - 20);
        steam2.curveTo(cupX + 15 - pulse * 5, cupY - 35, cupX + 5 + pulse * 5, cupY - 50, cupX + 10, cupY - 65);
        g2.draw(steam2);

        // Draw Coffee Cup Body
        g2.setColor(new Color(188, 143, 143)); // Mug color
        g2.fill(new RoundRectangle2D.Float(cupX - 25, cupY, 50, 35, 10, 10));
        // Mug Handle
        g2.setColor(new Color(160, 115, 115));
        g2.setStroke(new BasicStroke(4.0f));
        g2.drawArc(cupX + 15, cupY + 5, 20, 20, -90, 180);
        // Plate/Saucer
        g2.setColor(new Color(109, 76, 65));
        g2.fill(new RoundRectangle2D.Float(cupX - 35, cupY + 30, 70, 6, 4, 4));

        // 3. Game Title with glowing drop shadow
        g2.setFont(new Font("Segoe UI", Font.BOLD, 48));
        String title = "CAFE TYCOON";
        
        // Shadow
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(title, width / 2 - 150 + 4, height / 2 + 90 + 4);
        
        // Main Text
        g2.setColor(new Color(255, 202, 40)); // Golden glowing amber
        g2.drawString(title, width / 2 - 150, height / 2 + 90);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2.setColor(new Color(215, 204, 200));
        g2.drawString("Time Management 2D Simulator", width / 2 - 120, height / 2 + 120);

        // 4. Pulsing "PRESS ENTER TO START" Label
        int alphaPulse = (int) (127 + 128 * Math.sin(System.currentTimeMillis() * 0.005));
        g2.setColor(new Color(255, 255, 255, Math.max(20, Math.min(255, alphaPulse))));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.drawString("PRESS ENTER TO START THE CAFE", width / 2 - 150, height / 2 + 190);

        // Reset Stroke
        g2.setStroke(new BasicStroke(1.0f));
    }

    public void drawGameOver(Graphics2D g2, int width, int height, int finalScore) {
        // Red tinted screen overlay
        g2.setColor(new Color(30, 10, 10, 230));
        g2.fillRect(0, 0, width, height);

        // Draw Game Over Banner
        g2.setFont(new Font("Segoe UI", Font.BOLD, 52));
        g2.setColor(new Color(229, 57, 53)); // Vivid red
        g2.drawString("CAFE SHUT DOWN", width / 2 - 220, height / 2 - 60);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g2.setColor(new Color(176, 190, 197));
        g2.drawString("Your reputation collapsed and regulatory inspectors shut down the cafe!", width / 2 - 280, height / 2 - 20);

        // Score Frame
        g2.setColor(new Color(255, 255, 255, 10));
        g2.fill(new RoundRectangle2D.Float(width / 2 - 120, height / 2 + 20, 240, 70, 12, 12));
        g2.setColor(new Color(229, 57, 53, 100));
        g2.draw(new RoundRectangle2D.Float(width / 2 - 120, height / 2 + 20, 240, 70, 12, 12));

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(new Color(207, 216, 220));
        g2.drawString("FINAL SCORE", width / 2 - 45, height / 2 + 42);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        g2.drawString(String.format("%d Points", finalScore), width / 2 - 55, height / 2 + 75);

        // Press R to Restart
        int alphaPulse = (int) (127 + 128 * Math.sin(System.currentTimeMillis() * 0.005));
        g2.setColor(new Color(255, 255, 255, Math.max(20, Math.min(255, alphaPulse))));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.drawString("PRESS 'R' KEY TO REOPEN", width / 2 - 120, height / 2 + 150);
    }

    public void drawPaused(Graphics2D g2, int width, int height) {
        // Dark screen dim overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, width, height);

        // Blur Glassmorphic panel in center
        g2.setColor(new Color(33, 33, 40, 220));
        g2.fill(new RoundRectangle2D.Float(width / 2 - 150, height / 2 - 100, 300, 200, 16, 16));
        g2.setColor(new Color(255, 255, 255, 30));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(width / 2 - 150, height / 2 - 100, 300, 200, 16, 16));

        g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g2.setColor(new Color(255, 202, 40));
        g2.drawString("GAME PAUSED", width / 2 - 90, height / 2 - 40);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(new Color(176, 190, 197));
        g2.drawString("Take a quick coffee break!", width / 2 - 80, height / 2 - 10);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        g2.drawString("PRESS 'ESC' TO RESUME", width / 2 - 85, height / 2 + 40);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(120, 144, 156));
        g2.drawString("Press 'R' to Restart level", width / 2 - 65, height / 2 + 70);

        g2.setStroke(new BasicStroke(1.0f));
    }
}
