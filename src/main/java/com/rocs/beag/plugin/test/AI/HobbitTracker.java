package com.rocs.beag.plugin.test.AI;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class HobbitTracker {

    // --- Inner Class: Hobbit (Data Model) ---
    private class Hobbit {
        private String name;
        private LocalDate lastCompletedDate;

        public Hobbit(String name) {
            this.name = name;
            this.lastCompletedDate = null;
        }

        public void complete() {
            this.lastCompletedDate = LocalDate.now();
        }

        public boolean isCompletedToday() {
            return lastCompletedDate != null && lastCompletedDate.isEqual(LocalDate.now());
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            String status = isCompletedToday() ? "✅ COMPLETED" : "❌ PENDING";
            return String.format("[%s] %s (Last done: %s)",
                    status,
                    name,
                    lastCompletedDate != null ? lastCompletedDate.toString() : "Never");
        }
    }
    // ----------------------------------------

    private List<Hobbit> habits;
    private Scanner scanner;

    public HobbitTracker() {
        this.habits = new ArrayList<>();
        this.scanner = new Scanner(System.in);

        // Add some initial habits
        habits.add(new Hobbit("Read for 30 minutes"));
        habits.add(new Hobbit("Exercise for 1 hour"));
        habits.add(new Hobbit("Drink 8 glasses of water"));
    }

    public void run() {
        System.out.println("--- Welcome to the Hobbit Tracker CLI ---");
        String command;

        do {
            displayMenu();
            System.out.print("Enter command (view, complete, add, help, exit): ");
            command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "view":
                    viewHabits();
                    break;
                case "complete":
                    completeHabit();
                    break;
                case "add":
                    addHabit();
                    break;
                case "help":
                    displayHelp();
                    break;
                case "exit":
                    System.out.println("Goodbye! Keep up the good habits.");
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for options.");
            }
        } while (!command.equals("exit"));
    }

    private void displayMenu() {
        System.out.println("\n-------------------------------------");
        System.out.println("Commands: view | complete | add | help | exit");
        System.out.println("-------------------------------------");
    }

    private void displayHelp() {
        System.out.println("\n--- HELP ---");
        System.out.println("view:       Shows all your habits and their status for today.");
        System.out.println("complete:   Marks a specific habit as completed for today.");
        System.out.println("add:        Adds a new habit to your tracker.");
        System.out.println("exit:       Closes the application.");
    }

    private void viewHabits() {
        if (habits.isEmpty()) {
            System.out.println("You have no habits to track yet. Use 'add' to start!");
            return;
        }

        System.out.println("\n--- YOUR HABITS ---");
        for (int i = 0; i < habits.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, habits.get(i).toString());
        }
    }

    private void completeHabit() {
        if (habits.isEmpty()) {
            System.out.println("No habits to complete. Use 'add' first.");
            return;
        }

        viewHabits();
        System.out.print("Enter the number of the habit to complete: ");

        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (index >= 0 && index < habits.size()) {
                Hobbit habit = habits.get(index);
                habit.complete();
                System.out.println("\nSuccessfully completed: '" + habit.getName() + "' for today!");
            } else {
                System.out.println("Invalid number. Please choose a number from the list.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void addHabit() {
        System.out.print("Enter the name of the new habit: ");
        String name = scanner.nextLine().trim();

        if (!name.isEmpty()) {
            habits.add(new Hobbit(name));
            System.out.println("Habit added: '" + name + "'");
        } else {
            System.out.println("Habit name cannot be empty.");
        }
    }

    public static void main(String[] args) {
        new HobbitTracker().run();
    }
}
