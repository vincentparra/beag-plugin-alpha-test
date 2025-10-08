package com.rocs.beag.plugin.test.AI;

import java.util.Random;
import java.util.Scanner;

/**
 * RockPaperScissors - A simple command-line game
 *
 * Features:
 *  - Player vs Computer
 *  - Random computer choice
 *  - Win/Lose/Draw result shown each round
 *  - Option to play again or exit
 *
 * Author: Your Name
 */
public class RockPaperScissors {

    private static final String[] CHOICES = {"rock", "paper", "scissors"};
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("=== Rock, Paper, Scissors Game ===");
        boolean playAgain = true;

        while (playAgain) {
            System.out.print("\nEnter your choice (rock, paper, scissors): ");
            String playerChoice = scanner.nextLine().trim().toLowerCase();

            if (!isValidChoice(playerChoice)) {
                System.out.println("Invalid choice! Please enter rock, paper, or scissors.");
                continue;
            }

            String computerChoice = CHOICES[random.nextInt(3)];
            System.out.println("Computer chose: " + computerChoice);

            String result = determineWinner(playerChoice, computerChoice);
            System.out.println(result);

            System.out.print("\nDo you want to play again? (y/n): ");
            String again = scanner.nextLine().trim().toLowerCase();
            playAgain = again.equals("y");
        }

        System.out.println("Thanks for playing! 👋");
    }

    // Check if user input is valid
    private static boolean isValidChoice(String choice) {
        for (String valid : CHOICES) {
            if (valid.equals(choice)) return true;
        }
        return false;
    }

    // Determine the winner
    private static String determineWinner(String player, String computer) {
        if (player.equals(computer)) {
            return "It's a draw!";
        }

        switch (player) {
            case "rock":
                return (computer.equals("scissors")) ? "You win! 🪨 breaks ✂️" : "You lose! 📄 covers 🪨";
            case "paper":
                return (computer.equals("rock")) ? "You win! 📄 covers 🪨" : "You lose! ✂️ cuts 📄";
            case "scissors":
                return (computer.equals("paper")) ? "You win! ✂️ cuts 📄" : "You lose! 🪨 breaks ✂️";
            default:
                return "Error occurred.";
        }
    }
}