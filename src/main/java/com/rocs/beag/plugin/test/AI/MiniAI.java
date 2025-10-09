package com.rocs.beag.plugin.test.AI;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.script.*;

/**
 * MiniAI - a small single-file "AI-like" assistant for the command line.
 *
 * Features:
 *  - Keyword/pattern matching responses from an editable in-memory knowledge base
 *  - Commands for teaching (/learn), listing (/list), forgetting (/forget)
 *  - Math evaluation (/math)
 *  - Conversation memory (last N user messages)
 *  - Simple fallback small-talk and echo
 *
 * Usage examples (type into the running program):
 *   hello
 *   /learn hello => Hi! I'm MiniAI. How can I help?
 *   hello
 *   /math 12 * (3 + 4)
 *   /list
 *   /forget hello
 *   /exit
 */
public class MiniAI {
    private static final Scanner sc = new Scanner(System.in);
    private static final Map<String, String> kb = new LinkedHashMap<>(); // preserves order
    private static final Deque<String> memory = new ArrayDeque<>();
    private static final int MEMORY_LIMIT = 8;
    private static final Random rand = new Random();
    private static ScriptEngine engine;

    public static void main(String[] args) {
        initEngine();
        seedKnowledgeBase();
        println("MiniAI v1.0 — type '/help' for commands. Say '/exit' to quit.");
        while (true) {
            System.out.print("> ");
            String line = safeReadLine();
            if (line == null) break;
            line = line.trim();
            if (line.isEmpty()) continue;
            pushMemory("USER: " + line);

            // Commands
            if (line.equalsIgnoreCase("/exit") || line.equalsIgnoreCase("/quit")) {
                println("Goodbye — saved " + kb.size() + " knowledge entries (in-memory only).");
                break;
            }
            if (line.equalsIgnoreCase("/help")) {
                printHelp();
                continue;
            }
            if (line.startsWith("/learn ")) {
                handleLearn(line.substring(7));
                continue;
            }
            if (line.equals("/list")) {
                handleList();
                continue;
            }
            if (line.startsWith("/forget ")) {
                handleForget(line.substring(8));
                continue;
            }
            if (line.startsWith("/math ")) {
                handleMath(line.substring(6));
                continue;
            }
            if (line.equals("/memory")) {
                handleMemory();
                continue;
            }
            if (line.equals("/clear")) {
                handleClear();
                continue;
            }

            // Normal conversation - try to respond
            String reply = generateReply(line);
            pushMemory("AI: " + reply);
            println(reply);
        }
    }

    private static void initEngine() {
        try {
            engine = new ScriptEngineManager().getEngineByName("JavaScript");
        } catch (Exception e) {
            engine = null;
        }
    }

    private static void seedKnowledgeBase() {
        kb.put("hello", "Hey there! I'm MiniAI. You can teach me with /learn.");
        kb.put("hi", "Hi! How can I help you today?");
        kb.put("how are you", "I'm a program—I'm doing what I was coded to do : )");
        kb.put("what is your name", "I am MiniAI, a tiny command-line assistant.");
        kb.put("thanks", "You're welcome!");
        kb.put("thank you", "Glad to help!");
    }

    private static void printHelp() {
        println("Commands:");
        println("  /help                Show this help");
        println("  /learn key => value  Teach MiniAI a response. Example:");
        println("                       /learn favorite color => My favorite is digital blue.");
        println("  /list                List learned keys and responses");
        println("  /forget <key>        Remove a learned key");
        println("  /math <expr>         Evaluate arithmetic expression (e.g. /math 2*(3+4))");
        println("  /memory              Show recent conversation memory");
        println("  /clear               Clear conversation memory");
        println("  /exit or /quit       Exit the program");
    }

    private static void handleLearn(String payload) {
        // Accept either "key => value" or "key | value" or "key : value"
        String[] parts = payload.split("\\s*=>\\s*|\\s*\\|\\s*|\\s*:\\s*", 2);
        if (parts.length < 2) {
            println("Usage: /learn key => response");
            return;
        }
        String key = normalize(parts[0]);
        String val = parts[1].trim();
        if (key.isEmpty() || val.isEmpty()) {
            println("Key and response must be non-empty.");
            return;
        }
        kb.put(key, val);
        println("Learned: \"" + parts[0].trim() + "\" -> \"" + val + "\"");
    }

    private static void handleList() {
        if (kb.isEmpty()) {
            println("(knowledge base is empty)");
            return;
        }
        int i = 1;
        for (Map.Entry<String, String> e : kb.entrySet()) {
            println(String.format("%d. \"%s\" => \"%s\"", i++, e.getKey(), e.getValue()));
        }
    }

    private static void handleForget(String key) {
        key = normalize(key);
        if (kb.remove(key) != null) {
            println("Forgot \"" + key + "\"");
        } else {
            println("No such key: \"" + key + "\"");
        }
    }

    private static void handleMath(String expr) {
        expr = expr.trim();
        if (expr.isEmpty()) {
            println("Usage: /math <expression>");
            return;
        }
        try {
            if (engine != null) {
                Object res = engine.eval(expr);
                println("= " + String.valueOf(res));
            } else {
                // simple fallback: support only + - * / and parentheses using a recursive parser
                double res = evalSimple(expr);
                // strip .0 when integer
                if (res == (long) res) println("= " + (long) res);
                else println("= " + res);
            }
        } catch (Exception ex) {
            println("Could not evaluate expression: " + ex.getMessage());
        }
    }

    // Basic recursive-descent evaluator for + - * / and parentheses
    private static double evalSimple(String s) {
        Tokenizer t = new Tokenizer(s);
        double val = parseExpression(t);
        if (t.hasNext()) throw new IllegalArgumentException("Unexpected: " + t.peek());
        return val;
    }

    private static double parseExpression(Tokenizer t) {
        double val = parseTerm(t);
        while (t.hasNext() && (t.peek().equals("+") || t.peek().equals("-"))) {
            String op = t.next();
            double r = parseTerm(t);
            val = op.equals("+") ? val + r : val - r;
        }
        return val;
    }

    private static double parseTerm(Tokenizer t) {
        double val = parseFactor(t);
        while (t.hasNext() && (t.peek().equals("*") || t.peek().equals("/"))) {
            String op = t.next();
            double r = parseFactor(t);
            val = op.equals("*") ? val * r : val / r;
        }
        return val;
    }

    private static double parseFactor(Tokenizer t) {
        if (!t.hasNext()) throw new IllegalArgumentException("Unexpected end");
        String p = t.peek();
        if (p.equals("(")) {
            t.next(); // (
            double v = parseExpression(t);
            if (!t.hasNext() || !t.next().equals(")")) throw new IllegalArgumentException("Expected )");
            return v;
        }
        // unary +/-
        if (p.equals("+") || p.equals("-")) {
            String op = t.next();
            double v = parseFactor(t);
            return op.equals("-") ? -v : v;
        }
        // number
        t.next();
        try {
            return Double.parseDouble(p);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expected number but got: " + p);
        }
    }

    private static class Tokenizer {
        private final List<String> tokens = new ArrayList<>();
        private int idx = 0;
        Tokenizer(String s) {
            StringBuilder num = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (Character.isWhitespace(c)) continue;
                if (Character.isDigit(c) || c == '.') {
                    num.append(c);
                } else {
                    if (num.length() > 0) {
                        tokens.add(num.toString());
                        num.setLength(0);
                    }
                    tokens.add(String.valueOf(c));
                }
            }
            if (num.length() > 0) tokens.add(num.toString());
        }
        boolean hasNext() { return idx < tokens.size(); }
        String next() { return tokens.get(idx++); }
        String peek() { return tokens.get(idx); }
    }

    private static void handleMemory() {
        if (memory.isEmpty()) {
            println("(no memory yet)");
            return;
        }
        println("Recent conversation (newest last):");
        int i = 1;
        for (String m : memory) {
            println(String.format("%d. %s", i++, m));
        }
    }

    private static void handleClear() {
        memory.clear();
        println("Memory cleared.");
    }

    private static String generateReply(String user) {
        String norm = normalize(user);

        // 1) direct exact match
        if (kb.containsKey(norm)) {
            return kb.get(norm);
        }

        // 2) partial match -> choose the KB entry with most tokens in common
        String bestKey = null;
        int bestScore = 0;
        Set<String> userTokens = new HashSet<>(Arrays.asList(norm.split("\\s+")));
        for (String key : kb.keySet()) {
            Set<String> ktokens = new HashSet<>(Arrays.asList(key.split("\\s+")));
            int score = 0;
            for (String t : ktokens) if (userTokens.contains(t)) score++;
            if (score > bestScore) {
                bestScore = score;
                bestKey = key;
            }
        }
        if (bestKey != null && bestScore > 0) {
            return kb.get(bestKey);
        }

        // 3) pattern matching for questions
        if (matchesAny(norm, "who are you", "what are you", "identify yourself")) {
            return "I'm MiniAI, a tiny local assistant you can teach with /learn.";
        }
        if (matchesAny(norm, "time", "what time", "current time")) {
            return "I can't access the system clock in natural language here, but you can check your environment's time.";
        }
        if (norm.startsWith("tell me about ")) {
            return "I don't have web access. If you teach me with /learn <topic> => <info>, I can remember it.";
        }

        // 4) math detection (common inputs like "2 + 2")
        if (looksLikeMath(norm)) {
            try {
                if (engine != null) {
                    Object res = engine.eval(norm);
                    return String.valueOf(res);
                } else {
                    double v = evalSimple(norm);
                    if (v == (long) v) return String.valueOf((long) v);
                    return String.valueOf(v);
                }
            } catch (Exception e) {
                // fall through to fallback
            }
        }

        // 5) fallback small-talk
        String[] fallbacks = {
                "Interesting. Tell me more.",
                "I see. Can you clarify?",
                "Okay — how would you like me to help?",
                "I don't know that yet. You can teach me with /learn <key> => <response>."
        };
        return fallbacks[rand.nextInt(fallbacks.length)];
    }

    private static boolean matchesAny(String txt, String... patterns) {
        for (String p : patterns) {
            if (txt.contains(normalize(p))) return true;
        }
        return false;
    }

    private static boolean looksLikeMath(String s) {
        // fairly permissive: contains digits and math symbols, no letters
        return s.matches("^[0-9\\s\\.+\\-\\*/()]+$");
    }

    private static void pushMemory(String line) {
        memory.addLast(line);
        while (memory.size() > MEMORY_LIMIT) memory.removeFirst();
    }

    private static String normalize(String s) {
        s = s.toLowerCase(Locale.ROOT).trim();
        // remove punctuation except parentheses used in math
        s = s.replaceAll("[^a-z0-9\\s()]", "");
        // collapse spaces
        s = s.replaceAll("\\s+", " ");
        return s;
    }

    private static String safeReadLine() {
        try {
            return sc.nextLine();
        } catch (NoSuchElementException | IllegalStateException e) {
            return null;
        }
    }

    private static void println(String s) {
        System.out.println(s);
    }
}

