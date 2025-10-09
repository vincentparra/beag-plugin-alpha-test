package com.rocs.beag.plugin.test.AI;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * AIChat.java
 *
 * Simple single-file command-line "AI-like" chatbot.
 * - Keyword-based intent detection (greet, calc, todo, help, exit)
 * - Small utilities: calculator, todo list (in-memory + saved to file)
 * - Learning persistence: unknown user inputs can be taught and saved to knowledge.txt
 *
 * Compile: javac AIChat.java
 * Run:     java AIChat
 *
 * Works on Java 8+.
 */
public class AIChat {
    private static final String KNOWLEDGE_FILE = "knowledge.txt";
    private static final String TODO_FILE      = "todo.txt";

    private final Map<String, String> knowledge = new HashMap<>();
    private final List<String> todoList = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        AIChat bot = new AIChat();
        bot.start();
    }

    public AIChat() {
        loadKnowledge();
        loadTodos();
    }

    private void start() {
        println("Hello! I'm MiniAI — a tiny command-line 'AI-like' assistant.");
        println("Type 'help' to see what I can do. (Type 'exit' to quit)");
        while (true) {
            System.out.print("\nYou: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            if (matches(input, "exit", "quit", "bye", "goodbye")) {
                println("MiniAI: Bye! Saving memory... see you later.");
                saveKnowledge();
                saveTodos();
                break;
            }
            handleInput(input);
        }
    }

    private void handleInput(String input) {
        // direct knowledge-base match (exact or lower-case)
        String key = input.toLowerCase();
        if (knowledge.containsKey(key)) {
            println("MiniAI: " + knowledge.get(key));
            return;
        }

        // intents
        if (matches(input, "help", "commands", "what can you do")) {
            showHelp();
            return;
        }

        if (matches(input, "hi", "hello", "hey", "hiya")) {
            println("MiniAI: Hi there! How can I help today?");
            return;
        }

        if (matches(input, "time", "what time", "current time")) {
            println("MiniAI: Current time is " + new Date().toString());
            return;
        }

        if (input.toLowerCase().startsWith("calc ") || looksLikeMath(input)) {
            String expr = input;
            if (input.toLowerCase().startsWith("calc ")) expr = input.substring(5).trim();
            handleCalc(expr);
            return;
        }

        if (input.toLowerCase().startsWith("todo") || matches(input, "add todo", "list todo", "show todo")) {
            handleTodo(input);
            return;
        }

        if (matches(input, "joke", "tell me a joke")) {
            tellJoke();
            return;
        }

        // fuzzy keyword detection
        if (input.toLowerCase().contains("weather")) {
            println("MiniAI: I don't have live weather access here, but I can store your preferred forecast source.");
            return;
        }

        // fallback: ask user to teach this input
        println("MiniAI: I don't have a ready answer for that.");
        System.out.print("Would you like to teach me the correct response? (yes/no): ");
        String teach = scanner.nextLine().trim().toLowerCase();
        if (teach.equals("yes") || teach.equals("y")) {
            System.out.print("Enter the response I should give in the future: ");
            String response = scanner.nextLine().trim();
            if (!response.isEmpty()) {
                knowledge.put(key, response);
                saveKnowledge(); // save immediately for persistence
                println("MiniAI: Thanks — I've learned that.");
            } else {
                println("MiniAI: OK, nothing saved.");
            }
        } else {
            println("MiniAI: No problem. You can teach me later with the same phrase.");
        }
    }

    private void showHelp() {
        println("MiniAI capabilities:");
        println(" - greet: say 'hi', 'hello'");
        println(" - calc: 'calc 2+2' or just '2+2*3' (supports + - * / and parentheses)");
        println(" - todo: 'todo add buy milk' | 'todo list' | 'todo remove 1'");
        println(" - teach: when I don't know, say 'yes' to teach me a response");
        println(" - save: knowledge and todos are persisted to files in this folder");
    }

    // ---------- Calculator ----------
    private void handleCalc(String expr) {
        try {
            double result = eval(expr);
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                println("MiniAI: I couldn't compute that.");
            } else {
                if (result == Math.floor(result)) {
                    println("MiniAI: " + (long) result);
                } else {
                    println("MiniAI: " + result);
                }
            }
        } catch (Exception e) {
            println("MiniAI: Error evaluating expression. Try something like: 2+3*(4-1)");
        }
    }

    // Very small expression evaluator using shunting-yard + RPN
    private double eval(String expr) throws Exception {
        String sanitized = expr.replaceAll("[^0-9+\\-*/(). ]", "");
        List<String> tokens = tokenize(sanitized);
        List<String> rpn = shuntingYard(tokens);
        return evalRPN(rpn);
    }

    private List<String> tokenize(String s) {
        List<String> t = new ArrayList<>();
        Matcher m = Pattern.compile("\\d*\\.?\\d+|[+\\-*/()]").matcher(s);
        while (m.find()) t.add(m.group());
        return t;
    }

    private int prec(String op) {
        if (op.equals("+") || op.equals("-")) return 1;
        if (op.equals("*") || op.equals("/")) return 2;
        return 0;
    }

    private boolean isOp(String s) {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
    }

    private List<String> shuntingYard(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();
        for (String tok : tokens) {
            if (tok.matches("\\d*\\.?\\d+")) {
                output.add(tok);
            } else if (isOp(tok)) {
                while (!ops.isEmpty() && isOp(ops.peek()) && prec(ops.peek()) >= prec(tok)) {
                    output.add(ops.pop());
                }
                ops.push(tok);
            } else if (tok.equals("(")) {
                ops.push(tok);
            } else if (tok.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) output.add(ops.pop());
                if (!ops.isEmpty() && ops.peek().equals("(")) ops.pop();
            }
        }
        while (!ops.isEmpty()) output.add(ops.pop());
        return output;
    }

    private double evalRPN(List<String> rpn) {
        Deque<Double> stack = new ArrayDeque<>();
        for (String tok : rpn) {
            if (tok.matches("\\d*\\.?\\d+")) {
                stack.push(Double.parseDouble(tok));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                switch (tok) {
                    case "+" -> stack.push(a + b);
                    case "-" -> stack.push(a - b);
                    case "*" -> stack.push(a * b);
                    case "/" -> stack.push(a / b);
                    default -> throw new RuntimeException("Unknown op");
                }
            }
        }
        return stack.pop();
    }

    private boolean looksLikeMath(String s) {
        return s.matches(".*\\d+.*[+\\-*/].*\\d+.*");
    }

    // ---------- Todo ----------
    private void handleTodo(String input) {
        String lower = input.toLowerCase();
        if (lower.equals("todo list") || lower.equals("todo show") || lower.equals("todo")) {
            showTodos();
            return;
        }
        if (lower.startsWith("todo add ")) {
            String item = input.substring(9).trim();
            if (!item.isEmpty()) {
                todoList.add(item);
                saveTodos();
                println("MiniAI: Added todo: \"" + item + "\"");
            } else {
                println("MiniAI: Please specify what to add.");
            }
            return;
        }
        if (lower.startsWith("todo remove ") || lower.startsWith("todo rm ")) {
            String[] parts = input.split("\\s+");
            if (parts.length >= 3) {
                try {
                    int idx = Integer.parseInt(parts[2]) - 1;
                    if (idx >= 0 && idx < todoList.size()) {
                        String removed = todoList.remove(idx);
                        saveTodos();
                        println("MiniAI: Removed: " + removed);
                    } else println("MiniAI: Index out of range.");
                } catch (NumberFormatException e) {
                    println("MiniAI: Provide a numeric index, e.g. 'todo remove 1'.");
                }
            } else println("MiniAI: Usage: 'todo remove 1'");
            return;
        }

        // convenience: "add buy milk" or "list"
        if (lower.startsWith("add ")) {
            String item = input.substring(4).trim();
            todoList.add(item);
            saveTodos();
            println("MiniAI: Added todo: \"" + item + "\"");
            return;
        }
        if (lower.equals("list") || lower.equals("show")) {
            showTodos();
            return;
        }

        println("MiniAI: Todo commands: 'todo add <item>', 'todo list', 'todo remove <index>'");
    }

    private void showTodos() {
        if (todoList.isEmpty()) {
            println("MiniAI: Your todo list is empty.");
            return;
        }
        println("MiniAI: Your todos:");
        for (int i = 0; i < todoList.size(); i++) {
            println("  " + (i + 1) + ". " + todoList.get(i));
        }
    }

    // ---------- Knowledge persistence ----------
    private void loadKnowledge() {
        Path p = Paths.get(KNOWLEDGE_FILE);
        if (!Files.exists(p)) return;
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                // simple format: key\tvalue
                int tab = line.indexOf('\t');
                if (tab > 0) {
                    String k = line.substring(0, tab);
                    String v = line.substring(tab + 1);
                    knowledge.put(k, v);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: could not load knowledge file: " + e.getMessage());
        }
    }

    private void saveKnowledge() {
        Path p = Paths.get(KNOWLEDGE_FILE);
        try (BufferedWriter bw = Files.newBufferedWriter(p)) {
            for (Map.Entry<String, String> e : knowledge.entrySet()) {
                bw.write(e.getKey().replace("\t", " ") + "\t" + e.getValue().replace("\t", " "));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Warning: could not save knowledge file: " + e.getMessage());
        }
    }

    // ---------- Todos persistence ----------
    private void loadTodos() {
        Path p = Paths.get(TODO_FILE);
        if (!Files.exists(p)) return;
        try {
            List<String> lines = Files.readAllLines(p);
            todoList.addAll(lines);
        } catch (IOException e) {
            System.err.println("Warning: could not load todo file: " + e.getMessage());
        }
    }

    private void saveTodos() {
        Path p = Paths.get(TODO_FILE);
        try {
            Files.write(p, todoList, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Warning: could not save todo file: " + e.getMessage());
        }
    }

    // ---------- Small utilities ----------
    private void tellJoke() {
        String[] jokes = {
                "Why did the programmer quit his job? Because he didn't get arrays.",
                "I would tell you a UDP joke, but you might not get it.",
                "There are only 10 types of people: those who understand binary and those who don't."
        };
        int i = Math.abs(new Random().nextInt()) % jokes.length;
        println("MiniAI: " + jokes[i]);
    }

    // ---------- Helpers ----------
    private boolean matches(String input, String... options) {
        String l = input.toLowerCase();
        for (String o : options) if (l.equals(o.toLowerCase())) return true;
        return false;
    }

    private void println(String s) {
        System.out.println(s);
    }
}

