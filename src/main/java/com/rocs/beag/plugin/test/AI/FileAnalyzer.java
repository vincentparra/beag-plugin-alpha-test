package com.rocs.beag.plugin.test.AI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileAnalyzer {

    public static void main(String[] args) {
        // 1. Check for command-line argument
        if (args.length == 0) {
            System.err.println("Error: Please provide a file path as a command-line argument.");
            System.err.println("Usage: java FileAnalyzer <path/to/file>");
            System.exit(1);
        }

        String filePath = args[0];
        Path file = Path.of(filePath);

        // 2. Validate file existence and readability
        if (!Files.exists(file) || !Files.isReadable(file)) {
            System.err.println("Error: File not found or cannot be read at path: " + filePath);
            System.exit(1);
        }

        // 3. Initialize counters
        long lineCount = 0;
        long wordCount = 0;
        long charCount = 0;

        // 4. Process the file line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            System.out.println("--- Analyzing File: " + file.getFileName() + " ---");

            while ((line = reader.readLine()) != null) {
                lineCount++;

                // Count characters (including spaces, but excluding the newline character)
                charCount += line.length();

                // Simple word count: split the line by whitespace
                if (!line.trim().isEmpty()) {
                    String[] words = line.trim().split("\\s+");
                    wordCount += words.length;
                }
            }

            // Note: The total file size in bytes (including newlines) is often checked too.
            long totalBytes = Files.size(file);

            // 5. Print results
            System.out.println("\n[Analysis Complete]");
            System.out.printf("Lines:       %d\n", lineCount);
            System.out.printf("Words:       %d\n", wordCount);
            System.out.printf("Characters:  %d\n", charCount);
            System.out.printf("Total Bytes: %d\n", totalBytes);

        } catch (IOException e) {
            System.err.println("An unexpected error occurred while reading the file: " + e.getMessage());
            System.exit(1);
        }
    }
}
