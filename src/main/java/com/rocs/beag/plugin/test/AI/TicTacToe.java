package com.rocs.beag.plugin.test.AI;

import java.util.Scanner;

/**
 * TicTacToe - A simple command-line 2-player game
 *
 * Features:
 *  - 3x3 board
 *  - Player X and Player O take turns
 *  - Detects win or draw
 *  - Replay option
 *
 * Author: Your Name
 */
public class TicTacToe {

    private static final char EMPTY = ' ';
    private static final int SIZE = 3;
    private static char[][] board = new char[SIZE][SIZE];
    private static char currentPlayer = 'X';
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== TIC TAC TOE ===");
        boolean playAgain;

        do {
            initializeBoard();
            playGame();
            System.out.print("Do you want to play again? (y/n): ");
            playAgain = scanner.nextLine().trim().equalsIgnoreCase("y");
        } while (playAgain);

        System.out.println("Thanks for playing! 👋");
    }

    // Initialize empty board
    private static void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }
        currentPlayer = 'X';
    }

    // Main game loop
    private static void playGame() {
        boolean gameWon = false;
        int moves = 0;

        while (!gameWon && moves < SIZE * SIZE) {
            printBoard();
            System.out.println("Player " + currentPlayer + ", enter your move (row[1-3] and column[1-3]): ");

            int row = getValidInput("Row") - 1;
            int col = getValidInput("Column") - 1;

            if (board[row][col] != EMPTY) {
                System.out.println("That spot is already taken. Try again.");
                continue;
            }

            board[row][col] = currentPlayer;
            moves++;

            if (checkWin(currentPlayer)) {
                printBoard();
                System.out.println("🎉 Player " + currentPlayer + " wins!");
                gameWon = true;
            } else if (moves == SIZE * SIZE) {
                printBoard();
                System.out.println("It's a draw!");
            } else {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            }
        }
    }

    // Get valid number input
    private static int getValidInput(String type) {
        int value = -1;
        while (true) {
            System.out.print(type + ": ");
            String input = scanner.nextLine();
            try {
                value = Integer.parseInt(input);
                if (value >= 1 && value <= 3) break;
                else System.out.println("Enter a number between 1 and 3.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
            }
        }
        return value;
    }

    // Print the current board
    private static void printBoard() {
        System.out.println("\nCurrent Board:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(" " + (board[i][j] == EMPTY ? '-' : board[i][j]) + " ");
                if (j < SIZE - 1) System.out.print("|");
            }
            System.out.println();
            if (i < SIZE - 1) System.out.println("-----------");
        }
        System.out.println();
    }

    // Check if the current player has won
    private static boolean checkWin(char player) {
        // Check rows and columns
        for (int i = 0; i < SIZE; i++) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) ||
                    (board[0][i] == player && board[1][i] == player && board[2][i] == player)) {
                return true;
            }
        }
        // Check diagonals
        if ((board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
                (board[0][2] == player && board[1][1] == player && board[2][0] == player)) {
            return true;
        }
        return false;
    }
}
