package com.rocs.beag.plugin.test.AI;

import java.util.ArrayList;
import java.util.Scanner;

public class InventoryTracker {

    // --- Inner class for items ---
    static class Item {
        private String name;
        private int quantity;
        private double price;

        public Item(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void displayItem() {
            System.out.printf("%-20s | %-10d | $%-10.2f%n", name, quantity, price);
        }
    }

    // --- Main program ---
    private static ArrayList<Item> inventory = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;

        do {
            System.out.println("\n=== INVENTORY TRACKER ===");
            System.out.println("1. Add Item");
            System.out.println("2. View Inventory");
            System.out.println("3. Update Item");
            System.out.println("4. Remove Item");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            choice = getIntInput();

            switch (choice) {
                case 1 -> addItem();
                case 2 -> viewInventory();
                case 3 -> updateItem();
                case 4 -> removeItem();
                case 5 -> System.out.println("Exiting... Goodbye!");
                default -> System.out.println("Invalid option. Try again!");
            }
        } while (choice != 5);

        scanner.close();
    }

    private static void addItem() {
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter quantity: ");
        int qty = getIntInput();
        System.out.print("Enter price: ");
        double price = getDoubleInput();

        inventory.add(new Item(name, qty, price));
        System.out.println("✅ Item added successfully!");
    }

    private static void viewInventory() {
        if (inventory.isEmpty()) {
            System.out.println("⚠️ Inventory is empty!");
            return;
        }

        System.out.println("\n--- INVENTORY LIST ---");
        System.out.printf("%-20s | %-10s | %-10s%n", "Item Name", "Quantity", "Price");
        System.out.println("-----------------------------------------------");

        for (Item item : inventory) {
            item.displayItem();
        }
    }

    private static void updateItem() {
        System.out.print("Enter the name of the item to update: ");
        String name = scanner.nextLine();

        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(name)) {
                System.out.print("Enter new quantity: ");
                item.setQuantity(getIntInput());
                System.out.print("Enter new price: ");
                item.setPrice(getDoubleInput());
                System.out.println("✅ Item updated successfully!");
                return;
            }
        }

        System.out.println("⚠️ Item not found!");
    }

    private static void removeItem() {
        System.out.print("Enter the name of the item to remove: ");
        String name = scanner.nextLine();

        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getName().equalsIgnoreCase(name)) {
                inventory.remove(i);
                System.out.println("🗑️ Item removed successfully!");
                return;
            }
        }

        System.out.println("⚠️ Item not found!");
    }

    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine(); // clear buffer
        return val;
    }

    private static double getDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.print("Please enter a valid price: ");
            scanner.next();
        }
        double val = scanner.nextDouble();
        scanner.nextLine(); // clear buffer
        return val;
    }
}
