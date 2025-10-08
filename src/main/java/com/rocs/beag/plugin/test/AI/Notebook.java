package com.rocs.beag.plugin.test.AI;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Notebook - simple command-line note manager in one file.
 *
 * Compile:
 *   javac Notebook.java
 *
 * Run:
 *   java Notebook
 *
 * Notes are persisted in notebook.db (text file) in current directory.
 */
public class Notebook {
    private static final String DB_FILE = "notebook.db";
    private static final String DELIM = "\t";
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static class Note {
        String id;
        long created;
        long modified;
        String title;
        String body; // may contain newlines

        Note(String id, long created, long modified, String title, String body) {
            this.id = id;
            this.created = created;
            this.modified = modified;
            this.title = title;
            this.body = body;
        }
    }

    private final Map<String, Note> notes = new LinkedHashMap<>();

    public static void main(String[] args) {
        Notebook app = new Notebook();
        try {
            app.loadFromDisk();
        } catch (IOException e) {
            System.err.println("Warning: could not load database (" + e.getMessage() + "). Starting fresh.");
        }
        app.runREPL();
    }

    private void runREPL() {
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);
        System.out.println("Notebook — type 'help' for commands.");
        while (true) {
            System.out.print("> ");
            String line = sc.hasNextLine() ? sc.nextLine().trim() : null;
            if (line == null) break;
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0].toLowerCase(Locale.ROOT);
            String arg = parts.length > 1 ? parts[1].trim() : "";

            try {
                switch (cmd) {
                    case "help":
                        printHelp();
                        break;
                    case "add":
                        doAdd(sc);
                        break;
                    case "list":
                        doList();
                        break;
                    case "view":
                        doView(arg);
                        break;
                    case "edit":
                        doEdit(arg, sc);
                        break;
                    case "delete":
                    case "del":
                        doDelete(arg);
                        break;
                    case "search":
                        doSearch(arg);
                        break;
                    case "export":
                        doExport(arg);
                        break;
                    case "import":
                        doImport(arg);
                        break;
                    case "exit":
                    case "quit":
                        saveToDisk();
                        System.out.println("Saved. Bye!");
                        return;
                    default:
                        System.out.println("Unknown command. Type 'help' to see commands.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void printHelp() {
        System.out.println(
                "Commands:\n" +
                        "  help                 Show this help\n" +
                        "  add                  Add a new note (you'll be prompted for title and body)\n" +
                        "  list                 List all notes (id, title, created, modified)\n" +
                        "  view <id>            View a note in full\n" +
                        "  edit <id>            Edit a note's title and/or body\n" +
                        "  delete|del <id>      Delete a note\n" +
                        "  search <query>       Search notes by title or body (case-insensitive)\n" +
                        "  export <file>        Export to specified file (same format as the DB)\n" +
                        "  import <file>        Import notes from file (skips duplicates by id)\n" +
                        "  exit|quit            Save and exit\n\n" +
                        "Notes:\n" +
                        "  - When entering multi-line bodies, end by typing a single dot (.) on a line.\n" +
                        "  - IDs are UUID strings shown in 'list'."
        );
    }

    private void doAdd(Scanner sc) throws IOException {
        System.out.print("Title: ");
        String title = sc.nextLine().trim();
        if (title.isEmpty()) title = "(no title)";
        System.out.println("Enter body (end with a single '.' on its own line):");
        String body = readMultiline(sc);
        String id = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        Note n = new Note(id, now, now, sanitizeTitle(title), body);
        notes.put(id, n);
        saveToDisk();
        System.out.println("Added note id: " + id);
    }

    private void doList() {
        if (notes.isEmpty()) {
            System.out.println("(no notes)");
            return;
        }
        System.out.printf("%-36s  %-20s  %-19s  %-19s%n", "ID", "Title", "Created", "Modified");
        System.out.println(String.join("", Collections.nCopies(100, "-")));
        for (Note n : notes.values()) {
            System.out.printf("%-36s  %-20s  %-19s  %-19s%n",
                    n.id,
                    truncate(n.title, 20),
                    DF.format(new Date(n.created)),
                    DF.format(new Date(n.modified)));
        }
    }

    private void doView(String id) {
        if (id.isEmpty()) {
            System.out.println("Usage: view <id>");
            return;
        }
        Note n = notes.get(id);
        if (n == null) {
            System.out.println("Note not found: " + id);
            return;
        }
        System.out.println("ID:       " + n.id);
        System.out.println("Title:    " + n.title);
        System.out.println("Created:  " + DF.format(new Date(n.created)));
        System.out.println("Modified: " + DF.format(new Date(n.modified)));
        System.out.println("Body:\n--------------------");
        System.out.println(n.body);
        System.out.println("--------------------");
    }

    private void doEdit(String id, Scanner sc) throws IOException {
        if (id.isEmpty()) {
            System.out.println("Usage: edit <id>");
            return;
        }
        Note n = notes.get(id);
        if (n == null) {
            System.out.println("Note not found: " + id);
            return;
        }
        System.out.println("Current title: " + n.title);
        System.out.print("New title (leave blank to keep): ");
        String title = sc.nextLine();
        if (!title.trim().isEmpty()) {
            n.title = sanitizeTitle(title);
        }
        System.out.println("Current body (showing first 200 chars):");
        String preview = n.body.length() <= 200 ? n.body : n.body.substring(0, 200) + "...";
        System.out.println(preview);
        System.out.println("Enter new body (end with single '.' on its own line). Leave blank line then '.' to keep unchanged.");
        String body = readMultiline(sc);
        if (!body.equals("__KEEP__")) {
            n.body = body;
        }
        n.modified = System.currentTimeMillis();
        saveToDisk();
        System.out.println("Note updated.");
    }

    private void doDelete(String id) throws IOException {
        if (id.isEmpty()) {
            System.out.println("Usage: delete <id>");
            return;
        }
        Note n = notes.remove(id);
        if (n == null) {
            System.out.println("Note not found: " + id);
            return;
        }
        saveToDisk();
        System.out.println("Deleted note " + id);
    }

    private void doSearch(String query) {
        if (query.isEmpty()) {
            System.out.println("Usage: search <query>");
            return;
        }
        String q = query.toLowerCase(Locale.ROOT);
        List<Note> found = new ArrayList<>();
        for (Note n : notes.values()) {
            if (n.title.toLowerCase(Locale.ROOT).contains(q) ||
                    n.body.toLowerCase(Locale.ROOT).contains(q)) {
                found.add(n);
            }
        }
        if (found.isEmpty()) {
            System.out.println("No results for: " + query);
            return;
        }
        for (Note n : found) {
            System.out.println("ID: " + n.id);
            System.out.println("Title: " + n.title);
            System.out.println("Created: " + DF.format(new Date(n.created)));
            System.out.println("Modified: " + DF.format(new Date(n.modified)));
            System.out.println("----");
        }
        System.out.println(found.size() + " result(s).");
    }

    private void doExport(String file) throws IOException {
        if (file.isEmpty()) {
            System.out.println("Usage: export <file>");
            return;
        }
        Path p = Paths.get(file);
        try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
            for (Note n : notes.values()) {
                w.write(serializeNoteLine(n));
                w.newLine();
            }
        }
        System.out.println("Exported to " + file);
    }

    private void doImport(String file) throws IOException {
        if (file.isEmpty()) {
            System.out.println("Usage: import <file>");
            return;
        }
        Path p = Paths.get(file);
        if (!Files.exists(p)) {
            System.out.println("File does not exist: " + file);
            return;
        }
        int added = 0, skipped = 0;
        try (BufferedReader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                Note n = parseLineToNote(line);
                if (n == null) continue;
                if (notes.containsKey(n.id)) {
                    skipped++;
                } else {
                    notes.put(n.id, n);
                    added++;
                }
            }
        }
        saveToDisk();
        System.out.printf("Import complete. Added: %d, Skipped (duplicates): %d%n", added, skipped);
    }

    private String serializeNoteLine(Note n) {
        // Format: id \t createdMillis \t modifiedMillis \t title \t base64(body)
        String safeTitle = n.title.replace("\t", " ");
        String bodyB64 = Base64.getEncoder().encodeToString(n.body.getBytes(StandardCharsets.UTF_8));
        return String.join(DELIM,
                n.id,
                Long.toString(n.created),
                Long.toString(n.modified),
                safeTitle,
                bodyB64);
    }

    private Note parseLineToNote(String line) {
        String[] parts = line.split(DELIM, 5);
        if (parts.length < 5) return null;
        try {
            String id = parts[0];
            long created = Long.parseLong(parts[1]);
            long modified = Long.parseLong(parts[2]);
            String title = parts[3];
            String body = new String(Base64.getDecoder().decode(parts[4]), StandardCharsets.UTF_8);
            return new Note(id, created, modified, title, body);
        } catch (Exception e) {
            // skip bad line
            return null;
        }
    }

    private void loadFromDisk() throws IOException {
        Path p = Paths.get(DB_FILE);
        notes.clear();
        if (!Files.exists(p)) return;
        try (BufferedReader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                Note n = parseLineToNote(line);
                if (n != null) notes.put(n.id, n);
            }
        }
    }

    private void saveToDisk() throws IOException {
        Path p = Paths.get(DB_FILE);
        try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            for (Note n : notes.values()) {
                w.write(serializeNoteLine(n));
                w.newLine();
            }
        }
    }

    private static String readMultiline(Scanner sc) {
        StringBuilder sb = new StringBuilder();
        boolean gotAnything = false;
        while (true) {
            String line = sc.hasNextLine() ? sc.nextLine() : null;
            if (line == null) break;
            if (line.equals(".")) {
                if (!gotAnything) return "__KEEP__"; // special token: user entered '.' immediately — means keep unchanged for edit
                break;
            }
            gotAnything = true;
            sb.append(line).append(System.lineSeparator());
        }
        // remove trailing newline if present
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            sb.setLength(sb.length() - System.lineSeparator().length());
        }
        return sb.toString();
    }

    private static String sanitizeTitle(String t) {
        if (t == null) return "(no title)";
        String s = t.trim();
        return s.isEmpty() ? "(no title)" : s;
    }

    private static String truncate(String s, int len) {
        if (s == null) return "";
        return s.length() <= len ? s : s.substring(0, len - 3) + "...";
    }
}
