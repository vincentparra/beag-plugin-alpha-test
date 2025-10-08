package com.rocs.beag.plugin.test.AI;

import java.io.FileWriter;
import java.io.IOException;

public class Paper {

    // Define the main method, which is executed from the command line
    public static void main(String[] args) {

        // 1. Check for required arguments (FileName and Content)
        if (args.length < 2) {
            System.out.println("❌ Error: Missing arguments.");
            System.out.println("Usage: java com.myapp.PaperGenerator <fileName.txt> \"<content>\"");
            System.out.println("Example: java com.myapp.PaperGenerator report.md \"# Project Report\\nStatus: Complete\"");
            return;
        }

        // Assign arguments to variables
        String fileName = args[0];
        String content = args[1];

        // 2. Write the content to the file using try-with-resources
        // The file will be created in the directory where the 'java' command is run.
        try (FileWriter writer = new FileWriter(fileName)) {

            writer.write(content);

            System.out.println("✅ Success! Paper created.");
            System.out.println("File Name: " + fileName);
            System.out.println("Content Size: " + content.length() + " characters.");

        } catch (IOException e) {
            System.err.println("❌ An error occurred while attempting to write to file: " + fileName);
            e.printStackTrace();
        }
    }
}