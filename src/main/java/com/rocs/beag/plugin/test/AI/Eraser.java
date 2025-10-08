package com.rocs.beag.plugin.test.AI;
import java.io.*;
import java.nio.file.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

/**
 * A simple command-line secure file eraser in Java.
 * This program overwrites the specified files multiple times with patterns
 * (zeros, ones, and random data) before deleting them to prevent easy recovery.
 *
 * Usage: java -cp . com.rocs.beag.plugin.test.AI.Eraser <file1> [file2 ...]
 *
 * WARNING: This permanently and securely deletes files. Use with caution!
 * Only works on files, not directories. Ensure you have write permissions.
 *
 * Secure erasure method: 3 passes (DoD-style: zeros, ones, random).
 */

public class Eraser {
    private static final int NUM_PASSES = 3;
    private static final Random random = new Random();
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -cp . com.rocs.beag.plugin.test.AI.Eraser <file1> [file2 ...]");
            System.out.println("No files specified. Exiting.");
            return;
        }
        for (String filePath : args) {
            eraseFile(filePath);
        }
    }
    private static void eraseFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            System.out.println("File does not exist: " + filePath);
            return;
        }

        if (!Files.isRegularFile(path)) {
            System.out.println("Not a regular file (skipping directory or special file): " + filePath);
            return;
        }
        try {
            long fileSize = Files.size(path);
            System.out.println("Erasing file: " + filePath + " (size: " + fileSize + " bytes)");
            // Open file channel for writing
            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
                ByteBuffer buffer = ByteBuffer.allocateDirect((int) Math.min(fileSize, 1024 * 1024)); // 1MB buffer
                // Perform multiple overwrite passes
                for (int pass = 1; pass <= NUM_PASSES; pass++) {
                    System.out.println("  Pass " + pass + "/" + NUM_PASSES);
                    channel.position(0); // Seek to start

                    switch (pass) {
                        case 1:
                            // Pass 1: Overwrite with zeros
                            overwriteWithPattern(channel, buffer, fileSize, (byte) 0x00);
                            break;
                        case 2:
                            // Pass 2: Overwrite with ones
                            overwriteWithPattern(channel, buffer, fileSize, (byte) 0xFF);
                            break;
                        case 3:
                            // Pass 3: Overwrite with random data
                            overwriteRandom(channel, buffer, fileSize);
                            break;
                    }
                    channel.force(true); // Ensure data is written to disk
                }
            }
            // Finally, delete the file
            Files.delete(path);
            System.out.println("File securely erased and deleted: " + filePath);
        } catch (IOException e) {
            System.err.println("Error erasing file " + filePath + ": " + e.getMessage());
        }
    }

    private static void overwriteWithPattern(FileChannel channel, ByteBuffer buffer, long fileSize, byte pattern)
            throws IOException {
        buffer.clear();
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put(pattern);
        }
        buffer.flip();
        long bytesWritten = 0;
        while (bytesWritten < fileSize) {
            int bytesToWrite = (int) Math.min(buffer.remaining(), fileSize - bytesWritten);
            buffer.limit(bytesToWrite);
            bytesWritten += channel.write(buffer);
            buffer.clear();
            for (int i = 0; i < buffer.capacity(); i++) {
                buffer.put(pattern);
            }
            buffer.flip();
        }
    }

    private static void overwriteRandom(FileChannel channel, ByteBuffer buffer, long fileSize) throws IOException {
        buffer.clear();
        long bytesWritten = 0;
        while (bytesWritten < fileSize) {
            int bytesToWrite = (int) Math.min(buffer.capacity(), fileSize - bytesWritten);
            random.nextBytes(buffer.array());
            buffer.limit(bytesToWrite);
            bytesWritten += channel.write(buffer);
            buffer.clear();
        }
    }
}
