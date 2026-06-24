package ui;

import java.awt.*;

/**
 * Represents an interactive button for stations and objects.
 * Handles button states: normal, hover, pressed, released.
 */
public class InteractableButton {
    
    public enum ButtonState {
        NORMAL,
        HOVER,
        PRESSED,
        RELEASED
    }
    
    private int x;
    private int y;
    private int width;
    private int height;
    private String label;
    private ButtonState state;
    private long pressedStartTime;
    private static final long PRESS_DURATION = 150;
    
    public InteractableButton(int x, int y, int width, int height, String label) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.state = ButtonState.NORMAL;
        this.pressedStartTime = 0;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setState(ButtonState newState) {
        if (newState == ButtonState.PRESSED && this.state != ButtonState.PRESSED) {
            this.pressedStartTime = System.currentTimeMillis();
        }
        this.state = newState;
        
        // Auto-transition from PRESSED to RELEASED after duration
        if (newState == ButtonState.PRESSED) {
            long elapsed = System.currentTimeMillis() - pressedStartTime;
            if (elapsed > PRESS_DURATION) {
                this.state = ButtonState.RELEASED;
            }
        }
    }
    
    public ButtonState getState() {
        return state;
    }
    
    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width &&
               mouseY >= y && mouseY < y + height;
    }
    
    public void draw(Graphics2D g2) {
        // Draw button background with state-based styling
        Color backgroundColor;
        Color borderColor;
        float scale = 1.0f;
        int glowAlpha = 0;
        float outlineWidth = 0.0f;
        
        switch (state) {
            case PRESSED:
                backgroundColor = new Color(100, 150, 200);
                borderColor = new Color(50, 100, 150);
                scale = 0.95f;
                glowAlpha = 200;
                outlineWidth = 3.0f;
                break;
            case HOVER:
                backgroundColor = new Color(120, 170, 220);
                borderColor = new Color(70, 120, 180);
                scale = 1.05f;
                glowAlpha = 150;
                outlineWidth = 2.5f;
                break;
            case RELEASED:
                backgroundColor = new Color(100, 160, 210);
                borderColor = new Color(60, 110, 160);
                scale = 1.0f;
                glowAlpha = 100;
                outlineWidth = 1.5f;
                break;
            default: // NORMAL
                backgroundColor = new Color(80, 140, 180);
                borderColor = new Color(40, 100, 140);
                scale = 1.0f;
                glowAlpha = 0;
                outlineWidth = 0.5f;
                break;
        }
        
        // Draw glow effect for hover/pressed states
        if (glowAlpha > 0) {
            g2.setColor(new Color(200, 220, 255, glowAlpha));
            int glowExpand = (state == ButtonState.PRESSED) ? 4 : 3;
            g2.fillRoundRect(x - glowExpand, y - glowExpand, 
                            width + glowExpand * 2, height + glowExpand * 2, 8, 8);
        }
        
        // Apply scale transform for pressed effect
        if (scale != 1.0f) {
            g2.translate(x + width / 2, y + height / 2);
            g2.scale(scale, scale);
            g2.translate(-(x + width / 2), -(y + height / 2));
        }
        
        // Draw button
        g2.setColor(backgroundColor);
        g2.fillRoundRect(x, y, width, height, 8, 8);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(x, y, width, height, 8, 8);
        
        // Draw outline effect
        if (outlineWidth > 0.0f) {
            g2.setColor(new Color(255, 255, 255, (int)(outlineWidth * 40)));
            g2.setStroke(new BasicStroke(outlineWidth));
            int outlineExpand = (int)outlineWidth + 1;
            g2.drawRoundRect(x - outlineExpand, y - outlineExpand, 
                            width + outlineExpand * 2, height + outlineExpand * 2, 10, 10);
        }
        
        // Reset scale
        if (scale != 1.0f) {
            g2.translate(x + width / 2, y + height / 2);
            g2.scale(1.0f / scale, 1.0f / scale);
            g2.translate(-(x + width / 2), -(y + height / 2));
        }
        
        // Draw label
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (width - fm.stringWidth(label)) / 2;
        int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(label, textX, textY);
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getLabel() { return label; }
}
