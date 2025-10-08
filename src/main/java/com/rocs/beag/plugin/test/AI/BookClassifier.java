package com.rocs.beag.plugin.test.AI;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * BookClassifier - A simple command-line program
 * that classifies books into categories based on keywords.
 *
 * Features:
 *  - Add a book
 *  - Automatically classify based on title keywords
 *  - View all books and their categories
 *  - Exit program
 *
 * Author: Your Name
 */
public class BookClassifier {

    // Represents one book entry
    static class Book {
        private String title;
        private String category;

        public Book(String title, String category) {
            this.title = title;
            this.category = category;
        }

        public String getTitle() {
            return title;
        }

        public String getCategory() {
            return category;
        }

        @Override
        public String toString() {
            return "\"" + title + "\" → " + category;
        }
    }

    private final List<Book> books = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    // Start the program
    public void start() {
        System.out.println("=== Book Classifier ===");

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Exit");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addBook();
                    break;
                case "2":
                    showBooks();
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Add and classify a new book
    private void addBook() {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Title cannot be empty!");
            return;
        }

        String category = classifyBook(title);
        books.add(new Book(title, category));
        System.out.println("Book added and classified as: " + category);
    }

    // Display all books
    private void showBooks() {
        if (books.isEmpty()) {
            System.out.println("No books classified yet.");
            return;
        }

        System.out.println("\n--- Book List ---");
        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + ". " + books.get(i));
        }
    }

    // Simple keyword-based classification
    private String classifyBook(String title) {
        String lower = title.toLowerCase();

        if (lower.contains("science") || lower.contains("physics") || lower.contains("chemistry"))
            return "Science";
        else if (lower.contains("computer") || lower.contains("programming") || lower.contains("technology"))
            return "Technology";
        else if (lower.contains("history") || lower.contains("war") || lower.contains("ancient"))
            return "History";
        else if (lower.contains("novel") || lower.contains("story") || lower.contains("poem"))
            return "Literature";
        else if (lower.contains("business") || lower.contains("finance") || lower.contains("management"))
            return "Business";
        else
            return "Unclassified";
    }

    // Main method
    public static void main(String[] args) {
        new BookClassifier().start();
    }
}