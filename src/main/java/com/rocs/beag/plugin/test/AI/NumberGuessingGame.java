package com.rocs.beag.plugin.test.AI;

import java.util.Scanner;

import java.util.Scanner;
import java.util.Random;

public class NumberGuessingGame {
    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 100;
    private static Scanner scanner = new Scanner(System.in);
    private static Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Welcome to the Number Guessing Game!");
        System.out.println("I'm thinking of a number between " + MIN_NUMBER + " and " + MAX_NUMBER + ".");
        System.out.println("Can you guess it? Type 'quit' at any time to exit.\n");

        while (true) {
            playRound();
            System.out.print("\nPlay another round? (y/n): ");
            if (!scanner.nextLine().trim().toLowerCase().equals("y")) {
                break;
            }
            System.out.println();
        }

        System.out.println("Thanks for playing! Goodbye.");
        scanner.close();
    }

    private static void playRound() {
        int secretNumber = random.nextInt(MAX_NUMBER - MIN_NUMBER + 1) + MIN_NUMBER;
        int attempts = 0;
        boolean guessed = false;

        System.out.println("New round starts now!\n");

        while (!guessed) {
            System.out.print("Enter your guess (" + MIN_NUMBER + "-" + MAX_NUMBER + "): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit")) {
                System.out.println("Quitting the game. The number was " + secretNumber + ".");
                return;
            }

            try {
                int guess = Integer.parseInt(input);
                attempts++;

                if (guess < MIN_NUMBER || guess > MAX_NUMBER) {
                    System.out.println("Guess must be between " + MIN_NUMBER + " and " + MAX_NUMBER + ". Try again.");
                    attempts--; // Don't count invalid guesses
                    continue;
                }

                if (guess == secretNumber) {
                    guessed = true;
                    printWinMessage(attempts);
                } else if (guess < secretNumber) {
                    System.out.println("Too low! Try a higher number.\n");
                } else {
                    System.out.println("Too high! Try a lower number.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a whole number.\n");
            }
        }
    }

    private static void printWinMessage(int attempts) {
        System.out.println("\nCongratulations! You guessed it in " + attempts + " attempts!");
        if (attempts <= 5) {
            System.out.println("Amazing! You're a mind reader.");
        } else if (attempts <= 10) {
            System.out.println("Well done! Solid guessing skills.");
        } else {
            System.out.println("Good job! It took a bit, but you got there.");
        }
        System.out.println();
    }
}


