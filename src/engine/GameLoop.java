package engine;

public class GameLoop implements Runnable {

    private GamePanel gamePanel;
    private Thread gameThread;
    private boolean running = false;

    // Target Frame Rate
    private final int FPS = 60;
    private final long OPTIMAL_TIME = 1000000000 / FPS; // 16.66 ms in nanoseconds

    public GameLoop(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        gameThread = new Thread(this, "GameLoopThread");
        gameThread.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            System.err.println("GameLoop thread interrupted during shutdown: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double delta = 0;

        // Diagnostic variables for FPS counter if needed
        long timer = 0;
        int frames = 0;
        int updates = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / (double) OPTIMAL_TIME;
            timer += (now - lastTime);
            lastTime = now;

            // Perform updates if enough time has passed
            while (delta >= 1) {
                gamePanel.updatePhysics();
                updates++;
                delta--;
            }

            // Always render at maximum possible rate up to target FPS
            gamePanel.repaint();
            frames++;

            // FPS Diagnostic output every second (optional)
            if (timer >= 1000000000) {
                // System.out.println("FPS: " + frames + " | Updates: " + updates);
                frames = 0;
                updates = 0;
                timer = 0;
            }

            // High precision sleep to avoid consuming 100% CPU thread
            try {
                long currentTime = System.nanoTime();
                long sleepTime = (lastTime - currentTime + OPTIMAL_TIME) / 1000000;
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                System.err.println("GameLoop sleep interrupted: " + e.getMessage());
            }
        }
    }

    public boolean isRunning() {
        return running;
    }
}
