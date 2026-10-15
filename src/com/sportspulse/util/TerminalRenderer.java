package com.sportspulse.util;

/**
 * Screen rendering primitives, box headers, status bars, and terminal safety for SportsPulse.
 *
 * Terminal Safety:
 * Registers a JVM shutdown hook to unconditionally restore the terminal cursor and
 * reset ANSI escape codes in case of unhandled termination, SIGINT / Ctrl+C, or abnormal exit.
 */
public final class TerminalRenderer {

    private static volatile boolean shutdownHookInstalled = false;

    static {
        installSafetyShutdownHook();
    }

    private TerminalRenderer() {}

    /**
     * Installs an unconditional JVM shutdown hook ensuring the cursor is never left
     * hidden and text styles are reset even on sudden termination (Ctrl+C / SIGINT).
     */
    public static void installSafetyShutdownHook() {
        if (!shutdownHookInstalled) {
            synchronized (TerminalRenderer.class) {
                if (!shutdownHookInstalled) {
                    try {
                        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                            System.out.print(Ansi.CURSOR_SHOW + Ansi.RESET);
                            System.out.flush();
                        }, "SportsPulse-TerminalSafetyHook"));
                        shutdownHookInstalled = true;
                    } catch (IllegalStateException ignored) {
                        // JVM already shutting down
                    }
                }
            }
        }
    }

    public static boolean isShutdownHookInstalled() {
        return shutdownHookInstalled;
    }

    /**
     * Clears the terminal screen and homes the cursor if running in an interactive ANSI terminal.
     * Safely falls back to newline spacing in pipes or automated test runs.
     */
    public static void clearScreen() {
        if (ConsoleCapabilities.isInteractive() && Ansi.ENABLED) {
            System.out.print(Ansi.CLEAR_SCREEN + Ansi.CURSOR_HOME);
            System.out.flush();
        } else {
            System.out.println();
        }
    }

    /**
     * Toggles cursor visibility. Must only be used when interactive.
     */
    public static void setCursorVisible(boolean visible) {
        if (ConsoleCapabilities.isInteractive() && Ansi.ENABLED) {
            System.out.print(visible ? Ansi.CURSOR_SHOW : Ansi.CURSOR_HIDE);
            System.out.flush();
        }
    }

    /**
     * Renders the signature SPORTSPULSE ASCII brand banner in high-intensity Stadium Cyan.
     * Uses 100% standard ASCII characters (32-126) so it never garbles across any console.
     */
    public static void renderBanner() {
        int width = ConsoleCapabilities.getTerminalWidth();
        String[] logo = {
            "  ___ ___  ___  ___ _____ ___ ___ _   _ _    ___ ___ ",
            " / __| _ \\/ _ \\| _ \\_   _/ __| _ \\ | | | |  / __| __|",
            " \\__ \\  _/ (_) |   / | | \\__ \\  _/ |_| | |__\\__ \\ _| ",
            " |___/_|  \\___/|_|_\\ |_| |___/_|  \\___/|____|___/___|"
        };
        boolean unicode = ConsoleCapabilities.isUnicodeSupported();
        String star = unicode ? "✦" : "*";
        String sub = star + " SPORTS PERFORMANCE ANALYTICS & TACTICAL INTELLIGENCE " + star;
        String meta = "Pure Java Console Platform  |  26 DSA Algorithms Engine  |  v2.0";

        System.out.println();
        for (String line : logo) {
            System.out.println(Ansi.paint(Ansi.BOLD + TerminalTheme.primary(), centerText(line, width)));
        }
        System.out.println(Ansi.paint(Ansi.BOLD + TerminalTheme.accent(), centerText(sub, width)));
        System.out.println(Ansi.paint(TerminalTheme.muted(), centerText(meta, width)));
        System.out.println();
    }

    /**
     * Renders a modern sleek box header with rounded corners and centered title/subtitle.
     * Uses smooth single-line frames (╭───╮) or clean ASCII (+---+) for guaranteed clarity.
     */
    public static void renderBoxHeader(String title, String subtitle) {
        int width = Math.min(ConsoleCapabilities.getTerminalWidth(), 80);
        boolean unicode = ConsoleCapabilities.isUnicodeSupported();

        char topLeft  = unicode ? '╭' : '+';
        char topRight = unicode ? '╮' : '+';
        char botLeft  = unicode ? '╰' : '+';
        char botRight = unicode ? '╯' : '+';
        char horiz    = unicode ? '─' : '-';
        char vert     = unicode ? '│' : '|';
        char midLeft  = unicode ? '├' : '+';
        char midRight = unicode ? '┤' : '+';

        String topBorder = topLeft + String.valueOf(horiz).repeat(Math.max(0, width - 2)) + topRight;
        String botBorder = botLeft + String.valueOf(horiz).repeat(Math.max(0, width - 2)) + botRight;

        System.out.println(Ansi.paint(TerminalTheme.border(), topBorder));

        // Title row
        String centerTitle = centerText(title, width - 4);
        System.out.println(Ansi.paint(TerminalTheme.border(), String.valueOf(vert)) + " "
                + Ansi.paint(Ansi.BOLD + TerminalTheme.primary(), centerTitle) + " "
                + Ansi.paint(TerminalTheme.border(), String.valueOf(vert)));

        if (subtitle != null && !subtitle.isBlank()) {
            String midSep = midLeft + String.valueOf(horiz).repeat(Math.max(0, width - 2)) + midRight;
            System.out.println(Ansi.paint(TerminalTheme.border(), midSep));
            String centerSub = centerText(subtitle, width - 4);
            System.out.println(Ansi.paint(TerminalTheme.border(), String.valueOf(vert)) + " "
                    + Ansi.paint(TerminalTheme.muted(), centerSub) + " "
                    + Ansi.paint(TerminalTheme.border(), String.valueOf(vert)));
        }

        System.out.println(Ansi.paint(TerminalTheme.border(), botBorder));
    }

    /**
     * Renders a high-density status bar across the terminal width.
     */
    public static void renderStatusBar(String mode, String user, String statusInfo) {
        int width = Math.min(ConsoleCapabilities.getTerminalWidth(), 80);
        boolean unicode = ConsoleCapabilities.isUnicodeSupported();
        String onlineDot = unicode ? "● " : "* ";
        String sep = unicode ? " │ " : " | ";

        String left = " " + onlineDot + "MODE: " + mode + sep + "USER: " + user;
        String right = statusInfo + sep + width + "C ";

        int pad = width - Ansi.visibleLength(left) - Ansi.visibleLength(right);
        if (pad < 1) pad = 1;

        String barContent = left + " ".repeat(pad) + right;
        String bar = Ansi.paint(Ansi.BOLD + TerminalTheme.headerBg() + TerminalTheme.accent(), barContent);
        System.out.println(bar);
    }

    public static void renderDivider(String label, int width) {
        boolean unicode = ConsoleCapabilities.isUnicodeSupported();
        char ch = unicode ? '─' : '-';
        int targetWidth = Math.min(width, 80);
        if (label == null || label.isBlank()) {
            System.out.println(Ansi.paint(TerminalTheme.border(), String.valueOf(ch).repeat(Math.max(0, targetWidth))));
        } else {
            String prefix = String.valueOf(ch).repeat(3) + " " + label + " ";
            int remaining = Math.max(0, targetWidth - prefix.length());
            System.out.println(Ansi.paint(TerminalTheme.secondary(), prefix)
                    + Ansi.paint(TerminalTheme.border(), String.valueOf(ch).repeat(remaining)));
        }
    }

    public static void renderCategoryHeader(String category) {
        int width = Math.min(ConsoleCapabilities.getTerminalWidth(), 80);
        boolean unicode = ConsoleCapabilities.isUnicodeSupported();
        char ch = unicode ? '─' : '-';
        String header = String.valueOf(ch).repeat(2) + " " + category + " ";
        int remaining = Math.max(0, width - Ansi.visibleLength(header));
        System.out.println("\n" + Ansi.paint(Ansi.BOLD + TerminalTheme.accent(), header)
                + Ansi.paint(TerminalTheme.border(), String.valueOf(ch).repeat(remaining)));
    }

    public static String centerText(String text, int width) {
        if (text == null) text = "";
        int visibleLen = Ansi.visibleLength(text);
        if (visibleLen >= width) return text;
        int leftPad = (width - visibleLen) / 2;
        int rightPad = width - visibleLen - leftPad;
        return " ".repeat(Math.max(0, leftPad)) + text + " ".repeat(Math.max(0, rightPad));
    }

    /**
     * Renders a graphical progress bar or readiness stat meter.
     * Example: [████████████░░░░] 75.0%
     */
    public static String renderProgressBar(double current, double max, int width) {
        if (max <= 0) max = 1.0;
        double ratio = Math.max(0.0, Math.min(1.0, current / max));
        int filled = (int) Math.round(ratio * width);
        int empty = Math.max(0, width - filled);
        boolean unicode = ConsoleCapabilities.isUnicodeSupported();
        char fillChar = unicode ? '█' : '#';
        char emptyChar = unicode ? '░' : '-';
        String color = ratio >= 0.8 ? TerminalTheme.success() : (ratio >= 0.5 ? TerminalTheme.accent() : TerminalTheme.error());
        return Ansi.paint(color, "[" + String.valueOf(fillChar).repeat(filled))
                + Ansi.paint(TerminalTheme.muted(), String.valueOf(emptyChar).repeat(empty) + "]")
                + String.format(" %5.1f%%", ratio * 100.0);
    }

    /**
     * Renders an elegant framed card with rounded corners and high-contrast text.
     */
    public static void renderCard(String title, java.util.List<String> lines) {
        int width = Math.min(ConsoleCapabilities.getTerminalWidth(), 76);
        boolean unicode = ConsoleCapabilities.isUnicodeSupported();
        char tl = unicode ? '╭' : '+';
        char tr = unicode ? '╮' : '+';
        char bl = unicode ? '╰' : '+';
        char br = unicode ? '╯' : '+';
        char h  = unicode ? '─' : '-';
        char v  = unicode ? '│' : '|';

        String top = tl + String.valueOf(h).repeat(width - 2) + tr;
        String bot = bl + String.valueOf(h).repeat(width - 2) + br;

        System.out.println(Ansi.paint(TerminalTheme.border(), top));
        if (title != null && !title.isBlank()) {
            String titleStr = " " + Ansi.paint(Ansi.BOLD + TerminalTheme.primary(), title) + " ";
            int titleLen = Ansi.visibleLength(titleStr);
            int pad = Math.max(0, width - 2 - titleLen);
            System.out.println(Ansi.paint(TerminalTheme.border(), String.valueOf(v)) + titleStr + " ".repeat(pad) + Ansi.paint(TerminalTheme.border(), String.valueOf(v)));
            System.out.println(Ansi.paint(TerminalTheme.border(), String.valueOf(v) + String.valueOf(h).repeat(width - 2) + String.valueOf(v)));
        }

        if (lines != null) {
            for (String line : lines) {
                int lineLen = Ansi.visibleLength(line);
                int pad = Math.max(0, width - 4 - lineLen);
                System.out.println(Ansi.paint(TerminalTheme.border(), String.valueOf(v)) + " " + line + " ".repeat(pad) + " " + Ansi.paint(TerminalTheme.border(), String.valueOf(v)));
            }
        }
        System.out.println(Ansi.paint(TerminalTheme.border(), bot));
    }
}

