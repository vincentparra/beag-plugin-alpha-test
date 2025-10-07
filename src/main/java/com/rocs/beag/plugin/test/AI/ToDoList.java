package com.rocs.beag.plugin.test.AI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Simple Command-Line To-Do List Application
 * ------------------------------------------
 * Features:
 *  - Add new tasks
 *  - View all tasks
 *  - Mark tasks as completed
 *  - Delete tasks
 *  - Exit the program
 *
 * Author: Your Name
 */
public class ToDoList {

    // Inner class representing a single Task
    static class Task {
        private String description;
        private boolean isCompleted;

        public Task(String description) {
            this.description = description;
            this.isCompleted = false;
        }

        public void markDone() {
            this.isCompleted = true;
        }

        public String getDescription() {
            return description;
        }

        public boolean isCompleted() {
            return isCompleted;
        }

        @Override
        public String toString() {
            return (isCompleted ? "[✔]" : "[ ]") + " " + description;
        }
    }

    private final List<Task> tasks = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("=== Command Line To-Do List ===");

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Mark Task as Completed");
            System.out.println("4. Delete Task");
            System.out.println("5. Exit");
            System.out.print("> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addTask();
                    break;
                case "2":
                    viewTasks();
                    break;
                case "3":
                    markTask();
                    break;
                case "4":
                    deleteTask();
                    break;
                case "5":
                    System.out.println("Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void addTask() {
        System.out.print("Enter task description: ");
        String desc = scanner.nextLine().trim();
        if (desc.isEmpty()) {
            System.out.println("Task cannot be empty!");
            return;
        }
        tasks.add(new Task(desc));
        System.out.println("Task added successfully.");
    }

    private void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        System.out.println("\n--- Your Tasks ---");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    private void markTask() {
        viewTasks();
        if (tasks.isEmpty()) return;

        System.out.print("Enter task number to mark complete: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index < 0 || index >= tasks.size()) {
                System.out.println("Invalid task number.");
                return;
            }
            tasks.get(index).markDone();
            System.out.println("Task marked as completed!");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void deleteTask() {
        viewTasks();
        if (tasks.isEmpty()) return;

        System.out.print("Enter task number to delete: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index < 0 || index >= tasks.size()) {
                System.out.println("Invalid task number.");
                return;
            }
            Task removed = tasks.remove(index);
            System.out.println("Deleted task: " + removed.getDescription());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    public static void main(String[] args) {
        new ToDoList().start();
    }
}

