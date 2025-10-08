package com.rocs.beag.plugin.test.AI;

import java.io.*;
import java.net.*;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RunOnline - small command-line utility that provides a few safe "online" actions:
 * 1) Check connectivity (isReachable)
 * 2) HTTP GET (show first N lines)
 * 3) Download file (with progress)
 * 4) DNS lookup
 * 5) Simple traceroute-like probe (best-effort)
 *
 * Usage:
 *   javac RunOnline.java
 *   java RunOnline
 *
 * Notes:
 * - Uses only standard Java SE APIs; should run on Java 8+.
 * - Traceroute approach is a best-effort using DatagramSocket TTL where supported.
 */
public class RunOnline {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        println("=== RunOnline (CLI) ===");
        while (true) {
            println("\nSelect an action:");
            println("1) Check connectivity (host reachable)");
            println("2) HTTP GET (show first N lines)");
            println("3) Download file");
            println("4) DNS lookup (resolve host)");
            println("5) Simple traceroute (best-effort)");
            println("0) Exit");
            print("Choice: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": checkConnectivity(); break;
                    case "2": httpGetShow(); break;
                    case "3": downloadFile(); break;
                    case "4": dnsLookup(); break;
                    case "5": traceroute(); break;
                    case "0": println("Bye!"); return;
                    default: println("Unknown option. Try again.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static void checkConnectivity() {
        print("Enter hostname or IP (e.g. google.com): ");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) {
            println("Cancelled.");
            return;
        }
        try {
            InetAddress addr = InetAddress.getByName(host);
            println("Resolved: " + addr.getHostAddress());
            print("Timeout (ms, default 2000): ");
            String t = scanner.nextLine().trim();
            int timeout = t.isEmpty() ? 2000 : Integer.parseInt(t);
            boolean reachable = addr.isReachable(timeout);
            println("Reachable: " + reachable + " (timeout " + timeout + "ms)");
        } catch (IOException e) {
            println("Could not reach/resolve host: " + e.getMessage());
        }
    }

    private static void httpGetShow() {
        print("Enter URL (include http:// or https://): ");
        String urlStr = scanner.nextLine().trim();
        if (urlStr.isEmpty()) { println("Cancelled."); return; }
        print("Show how many body lines? (default 20): ");
        String nStr = scanner.nextLine().trim();
        int lines = nStr.isEmpty() ? 20 : Integer.parseInt(nStr);

        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "RunOnline/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int code = conn.getResponseCode();
            println("\nHTTP/1.x " + code + " " + conn.getResponseMessage());
            // print some headers
            Map<String, List<String>> headers = conn.getHeaderFields();
            println("Headers:");
            headers.entrySet().stream().limit(10).forEach(e -> println(e.getKey() + ": " + e.getValue()));

            InputStream is = (code >= 400) ? conn.getErrorStream() : conn.getInputStream();
            if (is == null) { println("[no body]"); return; }
            reader = new BufferedReader(new InputStreamReader(is));
            println("\n--- Body (first " + lines + " lines) ---");
            for (int i = 0; i < lines; i++) {
                String line = reader.readLine();
                if (line == null) break;
                println(line);
            }
            println("--- end ---");
        } catch (MalformedURLException mue) {
            println("Invalid URL: " + mue.getMessage());
        } catch (IOException ioe) {
            println("I/O error: " + ioe.getMessage());
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
            if (conn != null) conn.disconnect();
        }
    }

    private static void downloadFile() {
        print("Enter file URL: ");
        String urlStr = scanner.nextLine().trim();
        if (urlStr.isEmpty()) { println("Cancelled."); return; }
        print("Save as (file path): ");
        String outPath = scanner.nextLine().trim();
        if (outPath.isEmpty()) {
            println("No output file provided — cancelled.");
            return;
        }

        InputStream in = null;
        ReadableByteChannel rbc = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "RunOnline/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(15000);

            int code = conn.getResponseCode();
            if (code >= 400) {
                println("Server returned HTTP " + code + ": " + conn.getResponseMessage());
                return;
            }
            int contentLength = conn.getContentLength();
            in = conn.getInputStream();

            Path out = Paths.get(outPath);
            Files.createDirectories(out.getParent() == null ? Paths.get(".") : out.getParent());

            // Write with a buffer and show simple progress if content length known
            try (BufferedOutputStream bout = new BufferedOutputStream(Files.newOutputStream(out))) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                long total = 0;
                long lastPrint = System.nanoTime();
                while ((bytesRead = in.read(buffer)) != -1) {
                    bout.write(buffer, 0, bytesRead);
                    total += bytesRead;
                    // update progress every 300ms
                    if (System.nanoTime() - lastPrint > TimeUnit.MILLISECONDS.toNanos(300)) {
                        lastPrint = System.nanoTime();
                        if (contentLength > 0) {
                            int pct = (int) (total * 100 / contentLength);
                            print("\rDownloaded: " + humanReadableByteCount(total) + " / " + humanReadableByteCount(contentLength) + " (" + pct + "%)   ");
                        } else {
                            print("\rDownloaded: " + humanReadableByteCount(total) + "   ");
                        }
                    }
                }
                println("\rDownload finished. Total: " + humanReadableByteCount(total));
            }
        } catch (IOException e) {
            println("Download failed: " + e.getMessage());
        } finally {
            try { if (in != null) in.close(); } catch (IOException ignored) {}
            try { if (rbc != null) rbc.close(); } catch (IOException ignored) {}
        }
    }

    private static void dnsLookup() {
        print("Enter hostname (e.g. example.com): ");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) { println("Cancelled."); return; }
        try {
            InetAddress[] addrs = InetAddress.getAllByName(host);
            println("Found " + addrs.length + " address(es):");
            for (InetAddress a : addrs) {
                println(" - " + a.getHostAddress() + (a.isLoopbackAddress() ? " (loopback)" : ""));
            }
        } catch (UnknownHostException e) {
            println("Could not resolve host: " + e.getMessage());
        }
    }

    /**
     * Very simple traceroute-like function:
     * Attempts to send UDP packets with increasing TTL and waits for ICMP Time Exceeded responses.
     * This is best-effort: VM, OS, privileges and network middleboxes affect behavior.
     */
    private static void traceroute() {
        print("Enter target host (e.g. google.com): ");
        String target = scanner.nextLine().trim();
        if (target.isEmpty()) { println("Cancelled."); return; }
        int maxHops = 30;
        print("Max hops (default 30): ");
        String maxStr = scanner.nextLine().trim();
        if (!maxStr.isEmpty()) maxHops = Integer.parseInt(maxStr);

        println("Traceroute to " + target + " (max " + maxHops + " hops)");
        try {
            InetAddress dest = InetAddress.getByName(target);
            for (int ttl = 1; ttl <= maxHops; ttl++) {
                long start = System.nanoTime();
                String hopAddr = "*";
                try {
                    // create UDP socket and set TTL (Time To Live)
                    DatagramSocket ds = new DatagramSocket();
                    ds.setSoTimeout(2000);

                    // using reflection for setTimeToLive on DatagramSocket not available; use DatagramPacket + java.net.MulticastSocket TTL trick not applicable.
                    // We use the underlying InetAddress reachability check with TTL via isReachable where possible:
                    boolean reachable = dest.isReachable(null, ttl, 2000);
                    hopAddr = reachable ? dest.getHostAddress() : "*";
                    ds.close();
                } catch (IOException ioe) {
                    // fallback: mark as unknown
                    hopAddr = "*";
                }
                long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                println(String.format("%2d  %s  %d ms", ttl, hopAddr, elapsedMs));
                if (hopAddr.equals(dest.getHostAddress())) {
                    println("Destination reached.");
                    break;
                }
            }
        } catch (UnknownHostException e) {
            println("Unknown host: " + e.getMessage());
        }
    }

    // small helpers
    private static void println(String s) { System.out.println(s); }
    private static void print(String s) { System.out.print(s); }

    private static String humanReadableByteCount(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}

