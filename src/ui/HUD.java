package ui;

import java.awt.*;
import java.awt.geom.*;
import entity.Player;
import logic.InventoryManager;
import logic.DifficultyManager;

public class HUD {

    public void draw(Graphics2D g2, InventoryManager inventory, DifficultyManager difficulty, Player player, int width) {
        // High quality text rendering hints
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Draw Top Glassmorphic Panel Background
        g2.setColor(new Color(20, 20, 25, 220)); // Sleek dark slate
        g2.fill(new RoundRectangle2D.Float(10, 10, width - 20, 56, 12, 12));
        
        // Glossy Top highlight border
        g2.setColor(new Color(255, 255, 255, 30));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(10, 10, width - 20, 56, 12, 12));

        // 2. Draw Cafe Tycoon Title (Gold accent)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(new Color(255, 202, 40)); // Amber gold
        g2.drawString("CAFE TYCOON", 24, 44);

        // 3. Draw Level & Score (Semi-divider)
        g2.setColor(new Color(255, 255, 255, 100));
        g2.fillRect(170, 20, 2, 36);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(176, 190, 197));
        g2.drawString("SCORE", 188, 32);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        g2.drawString(String.format("%04d", difficulty.getScore()), 188, 52);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(176, 190, 197));
        g2.drawString("LEVEL", 255, 32);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        g2.drawString("Lv. " + difficulty.getLevel(), 255, 52);

        // 4. Draw Stock Levels (Coffee Beans & Milk)
        // Coffee Beans Stock Bar
        int barWidth = 80;
        int barHeight = 8;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(176, 190, 197));
        g2.drawString("COFFEE BEANS", 320, 28);
        
        g2.setColor(new Color(50, 40, 35));
        g2.fill(new RoundRectangle2D.Float(320, 34, barWidth, barHeight, 4, 4));
        double beanPercentage = (double) inventory.getCoffeeBeans() / inventory.getMaxCoffeeBeans();
        g2.setColor(new Color(141, 110, 99)); // Cocoa brown
        g2.fill(new RoundRectangle2D.Float(320, 34, (int)(barWidth * beanPercentage), barHeight, 4, 4));
        
        // Milk Stock Bar
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(176, 190, 197));
        g2.drawString("MILK", 420, 28);
        
        g2.setColor(new Color(30, 40, 50));
        g2.fill(new RoundRectangle2D.Float(420, 34, barWidth, barHeight, 4, 4));
        double milkPercentage = (double) inventory.getMilk() / inventory.getMaxMilk();
        g2.setColor(new Color(33, 150, 243)); // Fresh Blue
        g2.fill(new RoundRectangle2D.Float(420, 34, (int)(barWidth * milkPercentage), barHeight, 4, 4));

        // 5. Draw Reputation Stars (Hearts/Stars)
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(176, 190, 197));
        g2.drawString("SATISFACTION", 520, 28);
        
        int starX = 520;
        int starY = 34;
        for (int i = 0; i < inventory.getMaxReputation(); i++) {
            if (i < inventory.getReputation()) {
                g2.setColor(new Color(255, 193, 7)); // Shiny gold star
                drawStar(g2, starX + (i * 14), starY, 6, 12, 5);
            } else {
                g2.setColor(new Color(55, 71, 79)); // Depleted star
                drawStar(g2, starX + (i * 14), starY, 6, 12, 5);
            }
        }

        // 6. Draw Money Counter (Green glowing cash)
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(165, 214, 167));
        g2.drawString("CASH", 610, 32);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(new Color(76, 175, 80)); // Profit green
        g2.drawString("$" + inventory.getMoney(), 610, 52);

        // 7. Draw Player's Holding slot (Rightmost section)
        g2.setColor(new Color(255, 255, 255, 10));
        g2.fill(new RoundRectangle2D.Float(width - 120, 15, 100, 46, 8, 8));
        g2.setColor(new Color(255, 255, 255, 30));
        g2.draw(new RoundRectangle2D.Float(width - 120, 15, 100, 46, 8, 8));

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        g2.setColor(new Color(144, 164, 174));
        g2.drawString("HOLDING ITEM", width - 114, 27);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        
        String holdingStr = player.getCarriedItem();
        if (holdingStr.equals("None")) {
            g2.setColor(new Color(120, 144, 156));
        } else if (holdingStr.equals("Completed Cafe Drink")) {
            g2.setColor(new Color(76, 175, 80)); // Green glowing ready item
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.drawString(holdingStr, width - 114, 42);

        // Reset stroke
        g2.setStroke(new BasicStroke(1.0f));
    }

    // Helper method to draw beautiful stars
    private void drawStar(Graphics2D g, int x, int y, int innerRadius, int outerRadius, int numRays) {
        double angleInc = Math.PI / numRays;
        Path2D starPath = new Path2D.Double();
        double currAngle = -Math.PI / 2.0;
        
        starPath.moveTo(x + outerRadius * Math.cos(currAngle), y + outerRadius * Math.sin(currAngle));
        currAngle += angleInc;
        
        for (int i = 1; i < numRays * 2; i++) {
            double r = (i % 2 == 0) ? outerRadius : innerRadius;
            starPath.lineTo(x + r * Math.cos(currAngle), y + r * Math.sin(currAngle));
            currAngle += angleInc;
        }
        starPath.closePath();
        g.fill(starPath);
    }
}
