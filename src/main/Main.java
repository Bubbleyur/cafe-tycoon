package main;

import javax.swing.*;
import engine.GamePanel;
import engine.GameLoop;

public class Main {

    public static void main(String[] args) {
        // Run JFrame creation on the Event Dispatch Thread (EDT) for thread-safe UI rendering
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Cafe Tycoon");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            // Create main frontend render-focused panel
            GamePanel gamePanel = new GamePanel();
            window.add(gamePanel);
            window.pack();
            
            // Center the frame on the screen
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            // Start the separate repaint and physics loop thread
            GameLoop gameLoop = new GameLoop(gamePanel);
            gameLoop.start();
        });
    }
}
