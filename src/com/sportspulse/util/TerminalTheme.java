package com.sportspulse.util;

/**
 * Centralized Semantic Color Theme & Accessibility Palette for SportsPulse.
 *
 * Implements a unified sports-themed palette:
 * - Stadium Cyan / Azure (Primary sports identity)
 * - Slate Blue (Secondary structural hierarchy)
 * - Vibrant Gold / Amber (Accents & Highlights)
 * - Turf Emerald Green (Success, Verified States, Active Users)
 * - Alert Crimson (Failures, Lockouts, Exceptions)
 * - Royal Purple (Admin Elevation & High Privilege)
 * - Slate Grey (Borders, Rulers, Dim Metadata)
 *
 * Accessibility First:
 * Every semantic state pairs color with a distinct textual/glyph badge ([✓ OK], [✗ FAIL], [! WARN], [ℹ INFO])
 * so colorblind users and plain-text terminals maintain complete clarity.
 */
public final class TerminalTheme {

    private TerminalTheme() {}

    // Semantic TrueColor Foreground Codes (Electric Sports Palette)
    public static String primary()    { return Ansi.rgb(0, 229, 255); }   // Electric Stadium Cyan (#00E5FF)
    public static String secondary()  { return Ansi.rgb(41, 121, 255); }  // Stadium Azure (#2979FF)
    public static String accent()     { return Ansi.rgb(255, 214, 0); }   // Vibrant Gold (#FFD600)
    public static String success()    { return Ansi.rgb(0, 230, 118); }   // Turf Emerald (#00E676)
    public static String warning()    { return Ansi.rgb(255, 145, 0); }   // Neon Orange (#FF9100)
    public static String error()      { return Ansi.rgb(255, 23, 68); }   // Alert Crimson (#FF1744)
    public static String info()       { return Ansi.rgb(0, 176, 255); }   // Sky Cyan (#00B0FF)
    public static String admin()      { return Ansi.rgb(213, 0, 249); }   // Royal Violet (#D500F9)
    public static String user()       { return Ansi.rgb(0, 230, 118); }   // Active Emerald (#00E676)
    public static String border()     { return Ansi.rgb(84, 110, 122); }  // Slate Grey (#546E7A)
    public static String muted()      { return Ansi.rgb(144, 164, 174); } // Steel Blue (#90A4AE)
    public static String metric()     { return Ansi.BOLD + Ansi.rgb(241, 250, 238); } // Crisp Metric

    // Semantic Background Colors
    public static String headerBg()    { return Ansi.bgRgb(15, 23, 42); }   // Deep Stadium Navy (#0F172A)
    public static String highlightBg() { return Ansi.bgRgb(30, 41, 59); }   // Slate Highlight (#1E293B)
    public static String alertBg()     { return Ansi.bgRgb(88, 28, 28); }   // Deep Alert (#581C1C)

    // Option Badge Pill Helpers
    public static String optionPill(String id) {
        boolean unicode = ConsoleCapabilities.isUnicodeSupported();
        if ("0".equals(id) || "q".equalsIgnoreCase(id)) {
            String mark = unicode ? "✕" : "x";
            return Ansi.paint(Ansi.BOLD + error(), " " + mark + " [ 0 ] ");
        }
        String arrow = unicode ? "▸" : ">";
        return Ansi.paint(Ansi.BOLD + primary(), String.format(" %s [ %s ] ", arrow, id));
    }

    // Accessibility Badges
    public static String successBadge(String message) {
        String tag = ConsoleCapabilities.isUnicodeSupported() ? "[✓ OK]" : "[OK]";
        return Ansi.paint(Ansi.BOLD + success(), tag) + " " + message;
    }

    public static String errorBadge(String message) {
        String tag = ConsoleCapabilities.isUnicodeSupported() ? "[✗ FAIL]" : "[FAIL]";
        return Ansi.paint(Ansi.BOLD + error(), tag) + " " + message;
    }

    public static String warnBadge(String message) {
        String tag = ConsoleCapabilities.isUnicodeSupported() ? "[! WARN]" : "[WARN]";
        return Ansi.paint(Ansi.BOLD + warning(), tag) + " " + message;
    }

    public static String infoBadge(String message) {
        String tag = ConsoleCapabilities.isUnicodeSupported() ? "[ℹ INFO]" : "[INFO]";
        return Ansi.paint(Ansi.BOLD + info(), tag) + " " + message;
    }

    public static String adminBadge() {
        return Ansi.paint(Ansi.BOLD + admin(), "[ADMIN]");
    }

    public static String userBadge() {
        return Ansi.paint(Ansi.BOLD + user(), "[USER]");
    }

    public static String securityBadge(String status) {
        String color = "ACTIVE".equalsIgnoreCase(status) ? success() :
                      ("LOCKED".equalsIgnoreCase(status) ? error() : warning());
        return Ansi.paint(Ansi.BOLD + color, "[" + status.toUpperCase() + "]");
    }
}
