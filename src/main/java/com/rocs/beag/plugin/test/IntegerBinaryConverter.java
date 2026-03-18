package com.rocs.beag.plugin.test;

import java.util.Scanner;

public class IntegerBinaryConverter {

    // Integer to Binary
    public static String intToBinary(int number) {
        if (number == 0) return "0";

        StringBuilder binary = new StringBuilder();
        boolean isNegative = number < 0;
        number = Math.abs(number);

        while (number > 0) {
            binary.insert(0, number % 2);
            number /= 2;
        }

        return isNegative ? "-" + binary.toString() : binary.toString();
    }

    // Binary to Integer
    public static int binaryToInt(String binary) {
        boolean isNegative = binary.startsWith("-");
        if (isNegative) binary = binary.substring(1);

        int result = 0;
        for (int i = 0; i < binary.length(); i++) {
            char bit = binary.charAt(i);
            if (bit != '0' && bit != '1')
                throw new IllegalArgumentException("Invalid binary: " + binary);
            result = result * 2 + (bit - '0');
        }

        return isNegative ? -result : result;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Integer ↔ Binary Converter ===");
        System.out.println("1. Integer → Binary");
        System.out.println("2. Binary  → Integer");
        System.out.print("Choose (1 or 2): ");
        int choice = sc.nextInt();

        if (choice == 1) {
            System.out.print("Enter integer: ");
            int num = sc.nextInt();
            System.out.println("Binary: " + intToBinary(num));
        } else if (choice == 2) {
            System.out.print("Enter binary string: ");
            String bin = sc.next();
            System.out.println("Integer: " + binaryToInt(bin));
        } else {
            System.out.println("Invalid choice.");
        }

        sc.close();
    }
}

