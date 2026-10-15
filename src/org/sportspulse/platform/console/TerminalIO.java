package org.sportspulse.platform.console;

import java.util.List;
import java.util.Scanner;

/**
 * Terminal I/O utility providing robust console input reading, ANSI styling,
 * validation loops, and ASCII table formatting.
 */
public final class TerminalIO {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static boolean ansiSupported = true;

    // ANSI Colors & Styles
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    static {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (System.console() == null && !os.contains("windows") && !os.contains("linux") && !os.contains("mac")) {
            ansiSupported = false;
        }
    }

    private TerminalIO() {
        // Utility class
    }

    public static void setAnsiSupported(boolean supported) {
        ansiSupported = supported;
    }

    public static String color(String code, String text) {
        if (!ansiSupported) {
            return text;
        }
        return code + text + RESET;
    }

    public static void println(String msg) {
        System.out.println(msg);
    }

    public static void print(String msg) {
        System.out.print(msg);
    }

    public static void printHeader(String title) {
        println("");
        println(color(CYAN, "================================================================================"));
        println(color(BOLD + CYAN, "  " + title.toUpperCase()));
        println(color(CYAN, "================================================================================"));
    }

    public static void printSubHeader(String subtitle) {
        println(color(YELLOW, "--- " + subtitle + " ---"));
    }

    public static void printSuccess(String message) {
        println(color(GREEN, "[SUCCESS] " + message));
    }

    public static void printWarning(String message) {
        println(color(YELLOW, "[WARNING] " + message));
    }

    public static void printError(String message) {
        println(color(RED, "[ERROR] " + message));
    }

    public static void printInfo(String message) {
        println(color(CYAN, "[INFO] " + message));
    }

    public static String readString(String prompt) {
        print(color(BOLD + WHITE, prompt + ": "));
        if (!SCANNER.hasNextLine()) {
            return "";
        }
        return SCANNER.nextLine().trim();
    }

    public static String readNonEmptyString(String prompt) {
        while (true) {
            String val = readString(prompt);
            if (!val.isEmpty()) {
                return val;
            }
            printError("Input cannot be blank. Please try again.");
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            String raw = readString(prompt + " [" + min + " - " + max + "]");
            try {
                int val = Integer.parseInt(raw);
                if (val >= min && val <= max) {
                    return val;
                }
                printError("Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("Invalid integer format. Please enter a valid number.");
            }
        }
    }

    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            String raw = readString(prompt + " [" + min + " - " + max + "]");
            try {
                double val = Double.parseDouble(raw);
                if (val >= min && val <= max) {
                    return val;
                }
                printError("Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("Invalid decimal format. Please enter a valid number.");
            }
        }
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            String raw = readString(prompt + " (y/n)").toLowerCase();
            if (raw.equals("y") || raw.equals("yes") || raw.equals("1") || raw.equals("true")) {
                return true;
            }
            if (raw.equals("n") || raw.equals("no") || raw.equals("0") || raw.equals("false")) {
                return false;
            }
            printError("Please enter 'y' for yes or 'n' for no.");
        }
    }

    public static void pressEnterToContinue() {
        print(color(YELLOW, "\nPress Enter to return to menu..."));
        if (SCANNER.hasNextLine()) {
            SCANNER.nextLine();
        }
    }

    public static void printTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) {
            return;
        }

        int cols = headers.length;
        int[] colWidths = new int[cols];

        for (int i = 0; i < cols; i++) {
            colWidths[i] = headers[i].length();
        }

        if (rows != null) {
            for (String[] row : rows) {
                for (int i = 0; i < cols && i < row.length; i++) {
                    if (row[i] != null && row[i].length() > colWidths[i]) {
                        colWidths[i] = Math.min(row[i].length(), 40);
                    }
                }
            }
        }

        StringBuilder sep = new StringBuilder("+");
        for (int w : colWidths) {
            sep.append("-".repeat(w + 2)).append("+");
        }
        println(sep.toString());

        StringBuilder headerLine = new StringBuilder("|");
        for (int i = 0; i < cols; i++) {
            headerLine.append(" ").append(padRight(headers[i], colWidths[i])).append(" |");
        }
        println(color(BOLD + CYAN, headerLine.toString()));
        println(sep.toString());

        if (rows == null || rows.isEmpty()) {
            println("| " + padRight("(No records found)", sep.length() - 4) + " |");
        } else {
            for (String[] row : rows) {
                StringBuilder rowLine = new StringBuilder("|");
                for (int i = 0; i < cols; i++) {
                    String val = (i < row.length && row[i] != null) ? row[i] : "";
                    if (val.length() > 40) {
                        val = val.substring(0, 37) + "...";
                    }
                    rowLine.append(" ").append(padRight(val, colWidths[i])).append(" |");
                }
                println(rowLine.toString());
            }
        }

        println(sep.toString());
    }

    private static String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() >= n) return s;
        return s + " ".repeat(n - s.length());
    }
}
