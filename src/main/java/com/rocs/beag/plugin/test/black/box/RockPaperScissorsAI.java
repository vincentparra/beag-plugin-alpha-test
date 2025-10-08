package com.rocs.beag.plugin.test.black.box;

import java.util.*;

public class RockPaperScissorsAI {
    private String name = "AI Bot";
    private List<String> opponentLastMoves = new ArrayList<>(); // Tracks opponent's last 3 moves
    private Random random = new Random();
    private int playerScore = 0;
    private int aiScore = 0;
    private String playerLastMove = null;

    // Define winning counters: key is opponent's move, value is what beats it
    private Map<String, String> counters = new HashMap<>();

    public RockPaperScissorsAI() {
        counters.put("rock", "paper");
        counters.put("paper", "scissors");
        counters.put("scissors", "rock");
    }

    /**
     * Generates the AI's move. Biases toward countering the opponent's last move if available.
     * @param opponentLastMove The player's previous move (if known)
     * @return The AI's chosen move as a string
     */
    public String getMove(String opponentLastMove) {
        if (opponentLastMove != null) {
            opponentLastMoves.add(opponentLastMove);
            if (opponentLastMoves.size() > 3) {
                opponentLastMoves.remove(0); // Keep only last 3 moves
            }
        }

        // 70% chance to counter the last known move if available
        if (opponentLastMove != null && random.nextDouble() < 0.7) {
            return counters.get(opponentLastMove);
        }

        // Fallback: Random choice with 40% bias toward 'rock'
        String[] moves = {"rock", "paper", "scissors"};
        if (random.nextDouble() < 0.4) {
            return "rock";
        }
        return moves[random.nextInt(moves.length)];
    }

    /**
     * Determines the winner of a round.
     * @param playerMove Player's move
     * @param aiMove AI's move
     * @return 1 if player wins, -1 if AI wins, 0 if tie
     */
    private int determineWinner(String playerMove, String aiMove) {
        if (playerMove.equals(aiMove)) {
            return 0; // Tie
        }

        // Player wins if: rock > scissors, paper > rock, scissors > paper
        if ((playerMove.equals("rock") && aiMove.equals("scissors")) ||
                (playerMove.equals("paper") && aiMove.equals("rock")) ||
                (playerMove.equals("scissors") && aiMove.equals("paper"))) {
            return 1;
        }

        return -1; // AI wins
    }

    /**
     * Runs the interactive game loop.
     */
    public void playGame() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Rock-Paper-Scissors vs " + name + "!");
        System.out.println("Type 'rock', 'paper', or 'scissors' to play. Type 'quit' to exit.");
        System.out.println("Enter 'score' to see current score.\n");

        while (true) {
            System.out.print("Your move: ");
            String playerMove = scanner.nextLine().trim().toLowerCase();

            if (playerMove.equals("quit")) {
                break;
            } else if (playerMove.equals("score")) {
                System.out.println("Score - You: " + playerScore + ", AI: " + aiScore + "\n");
                continue;
            } else if (!playerMove.equals("rock") && !playerMove.equals("paper") && !playerMove.equals("scissors")) {
                System.out.println("Invalid move! Try 'rock', 'paper', or 'scissors'.\n");
                continue;
            }

            String aiMove = getMove(playerLastMove);
            playerLastMove = playerMove;

            System.out.println("You chose: " + playerMove);
            System.out.println(name + " chose: " + aiMove);

            int winner = determineWinner(playerMove, aiMove);
            if (winner == 0) {
                System.out.println("It's a tie!");
            } else if (winner == 1) {
                System.out.println("You win this round!");
                playerScore++;
            } else {
                System.out.println(name + " wins this round!");
                aiScore++;
            }

            System.out.println("------------------------\n");
        }

        System.out.println("Final Score - You: " + playerScore + ", AI: " + aiScore);
        System.out.println("Thanks for playing!");
        scanner.close();
    }

    // Main method to run the game
    public static void main(String[] args) {
        RockPaperScissorsAI game = new RockPaperScissorsAI();
        game.playGame();
    }
}
