package com.rocs.beag.plugin.test.todo.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TodoListApp {

    // Model for a task
    static class Task {
        private String description;
        private boolean completed;

        public Task(String description) {
            this.description = description;
            this.completed = false;
        }

        public String getDescription() {
            return description;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void markAsDone() {
            this.completed = true;
        }

        @Override
        public String toString() {
            return (completed ? "[✔]" : "[ ]") + " " + description;
        }
    }

    // Main application logic
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Task> tasks = new ArrayList<>();
        boolean running = true;

        System.out.println("==== CLI To-Do List ====");
        System.out.println("Type a number to select an option:");

        while (running) {
            System.out.println("\n1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Mark Task as Done");
            System.out.println("4. Delete Task");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter task description: ");
                    String desc = scanner.nextLine().trim();
                    if (!desc.isEmpty()) {
                        tasks.add(new Task(desc));
                        System.out.println("✅ Task added!");
                    } else {
                        System.out.println("⚠️ Task description cannot be empty!");
                    }
                    break;

                case "2":
                    if (tasks.isEmpty()) {
                        System.out.println("📭 No tasks yet!");
                    } else {
                        System.out.println("\n--- Your Tasks ---");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println((i + 1) + ". " + tasks.get(i));
                        }
                    }
                    break;

                case "3":
                    if (tasks.isEmpty()) {
                        System.out.println("⚠️ No tasks to mark!");
                        break;
                    }
                    System.out.print("Enter task number to mark as done: ");
                    try {
                        int index = Integer.parseInt(scanner.nextLine()) - 1;
                        if (index >= 0 && index < tasks.size()) {
                            tasks.get(index).markAsDone();
                            System.out.println("✅ Task marked as done!");
                        } else {
                            System.out.println("⚠️ Invalid task number!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Please enter a valid number!");
                    }
                    break;

                case "4":
                    if (tasks.isEmpty()) {
                        System.out.println("⚠️ No tasks to delete!");
                        break;
                    }
                    System.out.print("Enter task number to delete: ");
                    try {
                        int index = Integer.parseInt(scanner.nextLine()) - 1;
                        if (index >= 0 && index < tasks.size()) {
                            tasks.remove(index);
                            System.out.println("🗑️ Task deleted!");
                        } else {
                            System.out.println("⚠️ Invalid task number!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Please enter a valid number!");
                    }
                    break;

                case "5":
                    running = false;
                    System.out.println("👋 Exiting To-Do List. Goodbye!");
                    break;

                default:
                    System.out.println("⚠️ Invalid choice! Please try again.");
            }
        }

        scanner.close();
    }
}

