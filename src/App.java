import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class App extends JPanel implements Runnable {

    // Ukuran window
    final int WIDTH = 800;
    final int HEIGHT = 600;

    // Asset PNG
    BufferedImage player;

    // Posisi player
    int x = 100;
    int y = 100;

    Thread gameThread;

    public App() {

        // Set ukuran panel
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        // Load asset PNG
        try {
            player = ImageIO.read(new File("assets/player.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start game loop
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Render object
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Render gambar PNG
        g2.drawImage(player, x, y, 64, 64, null);

        g2.dispose();
    }

    // Game loop
    @Override
    public void run() {

        while (true) {

            // Update object
            x++;

            // Reset posisi
            if (x > WIDTH) {
                x = -64;
            }

            // Repaint screen
            repaint();

            // FPS delay
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Main method
    public static void main(String[] args) {

        JFrame window = new JFrame("2D Game");

        App gamePanel = new App();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}