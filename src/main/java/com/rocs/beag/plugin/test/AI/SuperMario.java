package com.rocs.beag.plugin.test.AI;

/*
 SuperMario.java
 Simple ASCII SuperMario-like command-line game.

 Controls:
   a : move left
   d : move right
   w or space : jump
   q : quit

 Save:   javac SuperMario.java
 Run:    java SuperMario
*/

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SuperMario {
    // Game constants
    static final int WIDTH = 60;      // visible width
    static final int HEIGHT = 20;     // visible height
    static final int GROUND_Y = HEIGHT - 3;
    static final int WORLD_LENGTH = 300; // total world width
    static final char PLAYER_CHAR = 'M';
    static final char GROUND_CHAR = '=';
    static final char EMPTY = ' ';
    static final char COIN = 'o';
    static final char BLOCK = '#';
    static final char ENEMY = 'E';
    static final int FPS = 20;

    // Player state
    static class Player {
        int x;      // world x (0..WORLD_LENGTH-1)
        int y;      // vertical position (0 at top)
        double vy;  // vertical velocity
        boolean onGround;
        int lives = 3;
        int score = 0;
    }

    // Input reader (stores last pressed key)
    static class InputReader extends Thread {
        private final AtomicReference<Character> lastKey;
        private volatile boolean running = true;
        InputReader(AtomicReference<Character> lastKey) { this.lastKey = lastKey; setDaemon(true); }
        public void run() {
            try {
                while (running) {
                    int r = System.in.read();
                    if (r == -1) break;
                    char c = (char) r;
                    if (c == '\r' || c == '\n') continue;
                    lastKey.set(c);
                }
            } catch (IOException ignored) {}
        }
        void shutdown() { running = false; this.interrupt(); }
    }

    public static void main(String[] args) throws Exception {
        // Setup world arrays
        char[][] world = new char[HEIGHT][WORLD_LENGTH];
        for (char[] row : world) Arrays.fill(row, EMPTY);

        // Create ground
        for (int x = 0; x < WORLD_LENGTH; x++) {
            for (int y = GROUND_Y; y < HEIGHT; y++) world[y][x] = GROUND_CHAR;
        }

        Random rnd = new Random();
        // scatter blocks, coins, and enemies
        for (int x = 10; x < WORLD_LENGTH - 5; x++) {
            if (rnd.nextDouble() < 0.06) { // blocks
                world[GROUND_Y - 2][x] = BLOCK;
            }
            if (rnd.nextDouble() < 0.08) { // coins in air
                world[GROUND_Y - 4][x] = COIN;
            }
            if (rnd.nextDouble() < 0.03) { // enemies on ground
                world[GROUND_Y - 1][x] = ENEMY;
            }
            // occasional pillar
            if (rnd.nextDouble() < 0.02) {
                world[GROUND_Y - 1][x] = BLOCK;
                world[GROUND_Y - 2][x] = BLOCK;
            }
        }

        Player player = new Player();
        player.x = 2; player.y = GROUND_Y - 1; player.vy = 0; player.onGround = true;

        AtomicReference<Character> lastKey = new AtomicReference<>((char)0);
        InputReader input = new InputReader(lastKey);
        input.start();

        int cameraX = 0;

        long frameTime = 1000 / FPS;
        boolean running = true;
        long lastTickPrint = System.currentTimeMillis();

        // Game loop
        while (running) {
            long t0 = System.currentTimeMillis();

            // handle input
            Character key = lastKey.getAndSet((char)0);
            boolean left = false, right = false, jump = false;
            if (key != null && key != 0) {
                char k = Character.toLowerCase(key);
                if (k == 'a') left = true;
                if (k == 'd') right = true;
                if (k == 'w' || k == ' ') jump = true;
                if (k == 'q') { running = false; break; }
            }

            // Horizontal movement
            if (left) {
                int targetX = Math.max(0, player.x - 1);
                if (!isSolid(world, targetX, player.y)) player.x = targetX;
            }
            if (right) {
                int targetX = Math.min(WORLD_LENGTH - 1, player.x + 1);
                if (!isSolid(world, targetX, player.y)) player.x = targetX;
            }

            // Jumping
            if (jump && player.onGround) {
                player.vy = -6.0; // jump impulse
                player.onGround = false;
            }

            // Apply gravity
            player.vy += 0.5; // gravity
            double nextYFloat = player.y + player.vy;
            int nextY = (int) Math.round(nextYFloat);

            // Vertical collision simple check
            if (player.vy > 0) {
                // falling
                if (isSolid(world, player.x, nextY)) {
                    // land on top
                    player.y = findTop(world, player.x, player.y, nextY);
                    player.vy = 0;
                    player.onGround = true;
                } else {
                    player.y = nextY;
                    player.onGround = false;
                }
            } else if (player.vy < 0) {
                // rising
                if (isSolid(world, player.x, nextY)) {
                    // hit head - stop upward movement
                    player.vy = 0;
                    player.onGround = false;
                    // optional: bump blocks - if block and has coin above it? simple: convert block hit to coin
                    if (world[nextY][player.x] == BLOCK) {
                        // bump effect: convert to empty and spawn coin above if empty
                        world[nextY][player.x] = EMPTY;
                        if (nextY - 1 >= 0 && world[nextY - 1][player.x] == EMPTY) {
                            world[nextY - 1][player.x] = COIN;
                        }
                    }
                } else {
                    player.y = nextY;
                    player.onGround = false;
                }
            }

            // Collect coins and enemy collisions
            if (world[player.y][player.x] == COIN) {
                player.score += 10;
                world[player.y][player.x] = EMPTY;
            }
            if (world[player.y][player.x] == ENEMY) {
                // If falling (vy>0) and moving down, stomp enemy
                if (player.vy > 0) {
                    player.score += 50;
                    world[player.y][player.x] = EMPTY;
                    player.vy = -3; // bounce up
                } else {
                    // take damage
                    player.lives--;
                    // respawn at start of screen
                    player.x = Math.max(0, cameraX + 2);
                    player.y = GROUND_Y - 1;
                    player.vy = 0;
                    player.onGround = true;
                    if (player.lives <= 0) {
                        running = false;
                    }
                }
            }

            // Move enemies (very simple: some enemies move left/right randomly)
            for (int x = cameraX; x < Math.min(WORLD_LENGTH, cameraX + WIDTH); x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    if (world[y][x] == ENEMY) {
                        if (rnd.nextDouble() < 0.02) {
                            int dir = rnd.nextBoolean() ? 1 : -1;
                            int nx = x + dir;
                            if (nx >= 0 && nx < WORLD_LENGTH && world[y][nx] == EMPTY && !isSolid(world, nx, y+1)) {
                                world[y][x] = EMPTY;
                                world[y][nx] = ENEMY;
                            }
                        }
                    }
                }
            }

            // scroll camera to keep player roughly centered
            int desiredCam = player.x - WIDTH / 3;
            cameraX = clamp(desiredCam, 0, Math.max(0, WORLD_LENGTH - WIDTH));

            // render frame to console
            StringBuilder sb = new StringBuilder();
            sb.append("\u001b[H\u001b[2J"); // clear screen (ANSI)
            sb.append("SuperMario ASCII  | Score: ").append(player.score).append("  Lives: ").append(player.lives)
                    .append("  Pos: ").append(player.x).append("/").append(WORLD_LENGTH - 1).append("\n");
            for (int row = 0; row < HEIGHT; row++) {
                for (int col = cameraX; col < cameraX + WIDTH; col++) {
                    if (row == player.y && col == player.x) {
                        sb.append(PLAYER_CHAR);
                    } else {
                        sb.append(world[row][col]);
                    }
                }
                sb.append('\n');
            }
            sb.append("\nControls: a:left  d:right  w/space:jump  q:quit\n");
            System.out.print(sb.toString());

            // Win condition: reach near end
            if (player.x >= WORLD_LENGTH - 4) {
                System.out.println("\nYOU WIN! Final score: " + player.score);
                break;
            }

            // frame timing
            long t1 = System.currentTimeMillis();
            long sleep = frameTime - (t1 - t0);
            if (sleep > 0) {
                Thread.sleep(sleep);
            }
        }

        input.shutdown();
        System.out.println("\nGame over. Score: " + player.score + "  Lives: " + player.lives);
    }

    // helpers

    static boolean isSolid(char[][] world, int x, int y) {
        if (x < 0 || x >= WORLD_LENGTH || y < 0 || y >= HEIGHT) return true;
        char c = world[y][x];
        return c == GROUND_CHAR || c == BLOCK;
    }

    // find top surface when falling onto solid: return y such that world[y+1] is solid and y is just above it
    static int findTop(char[][] world, int x, int fromY, int attemptedY) {
        int y = attemptedY;
        if (y >= HEIGHT) y = HEIGHT - 1;
        for (int yy = Math.max(0, fromY); yy <= Math.min(y, HEIGHT - 1); yy++) {
            if (isSolid(world, x, yy + 1)) {
                return yy;
            }
        }
        // fallback: put player at attemptedY-1
        return Math.min(HEIGHT - 2, Math.max(0, attemptedY - 1));
    }

    static int clamp(int v, int a, int b) { return Math.max(a, Math.min(b, v)); }
}
