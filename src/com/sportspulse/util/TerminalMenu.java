package com.sportspulse.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * High-performance dual-mode terminal menu for SportsPulse.
 *
 * Mode 1 (Interactive TTY):
 * - Displays active selection indicator (❯) with real-time cursor highlighting.
 * - Supports keyboard navigation via arrow keys (↑/↓), vim keys (k/j, w/s), and Enter.
 * - Supports direct digit jumping (1-9, 0) without requiring arrow movement.
 * - Displays live viewport status bar and navigation command bar.
 *
 * Mode 2 (Piped / Scripted / Headless / Test Runner):
 * - 100% reliable numeric line fallback.
 * - Seamless integration with automated tests, CI scripts, and report generators.
 * - Never hangs, polls, or crashes on redirected stdin.
 */
public final class TerminalMenu {

    private final String title;
    private final String subtitle;
    private String statusMode = "ANALYTICS";
    private String statusUser = "GUEST";
    private String statusInfo = "ACTIVE";
    private final List<MenuItem> items = new ArrayList<>();
    private MenuItem exitItem = new MenuItem("0", "Exit");
    private boolean showBanner = false;

    public TerminalMenu(String title, String subtitle) {
        this.title = title != null ? title : "SPORTSPULSE";
        this.subtitle = subtitle != null ? subtitle : "";
    }

    public TerminalMenu(String title) {
        this(title, "");
    }

    public TerminalMenu setBannerVisible(boolean showBanner) {
        this.showBanner = showBanner;
        return this;
    }

    public TerminalMenu addItem(MenuItem item) {
        if (item != null) items.add(item);
        return this;
    }

    public TerminalMenu addItem(String id, String label, String description) {
        return addItem(new MenuItem(id, label, description, "", ""));
    }

    public TerminalMenu addItem(String id, String label, String description, String badge) {
        return addItem(new MenuItem(id, label, description, badge, ""));
    }

    public TerminalMenu addItem(String id, String label, String description, String badge, String category) {
        return addItem(new MenuItem(id, label, description, badge, category));
    }

    public TerminalMenu setExitOption(String id, String label) {
        this.exitItem = new MenuItem(id, label);
        return this;
    }

    public TerminalMenu setStatus(String mode, String user, String statusInfo) {
        if (mode != null) this.statusMode = mode;
        if (user != null) this.statusUser = user;
        if (statusInfo != null) this.statusInfo = statusInfo;
        return this;
    }

    public List<MenuItem> getItems() {
        return new ArrayList<>(items);
    }

    public MenuItem getExitItem() {
        return exitItem;
    }

    /**
     * Renders the menu and reads user selection.
     * Automatically adapts between interactive arrow navigation and line-mode numeric entry.
     */
    public MenuResult renderAndSelect(Scanner scanner) {
        if (!ConsoleCapabilities.isInteractive()) {
            return renderLineMode(scanner);
        }
        return renderInteractiveMode(scanner);
    }

    /**
     * Clean, predictable line-mode fallback with modern option pills and categorized cards.
     */
    private MenuResult renderLineMode(Scanner scanner) {
        if (showBanner) {
            TerminalRenderer.renderBanner();
        }
        TerminalRenderer.renderBoxHeader(title, subtitle);
        TerminalRenderer.renderStatusBar(statusMode, statusUser, statusInfo);

        String lastCategory = null;
        for (MenuItem item : items) {
            String cat = item.getCategory();
            if (cat != null && !cat.isEmpty() && !cat.equalsIgnoreCase(lastCategory)) {
                TerminalRenderer.renderCategoryHeader(cat);
                lastCategory = cat;
            }
            renderItemLine(item, false);
        }

        if (exitItem != null) {
            if (lastCategory != null && !lastCategory.isEmpty()) {
                TerminalRenderer.renderCategoryHeader("SYSTEM & SESSION");
            } else {
                System.out.println();
            }
            renderItemLine(exitItem, false);
        }

        Console.prompt("\nSelect an option: ");

        if (scanner == null || !scanner.hasNextLine()) {
            return MenuResult.exit();
        }

        String raw = scanner.nextLine().trim();
        while (!raw.isEmpty() && (raw.charAt(0) == '\uFEFF' || (raw.charAt(0) == '?' && raw.length() > 1 && Character.isDigit(raw.charAt(1))))) {
            raw = raw.substring(1).trim();
        }
        if (raw.isEmpty()) {
            return null;
        }

        if (exitItem != null && (raw.equalsIgnoreCase(exitItem.getId()) || raw.equalsIgnoreCase("q") || raw.equalsIgnoreCase("exit"))) {
            return MenuResult.cancel();
        }

        for (int i = 0; i < items.size(); i++) {
            MenuItem item = items.get(i);
            if (raw.equalsIgnoreCase(item.getId()) || raw.equalsIgnoreCase(item.getLabel())) {
                return MenuResult.select(item, i);
            }
        }

        try {
            int num = Integer.parseInt(raw);
            if (num >= 1 && num <= items.size()) {
                return MenuResult.select(items.get(num - 1), num - 1);
            }
        } catch (NumberFormatException ignored) {}

        Console.error("Invalid choice '" + raw + "'. Please select a valid option number.");
        return null;
    }

    private void renderItemLine(MenuItem item, boolean isSelected) {
        String pill = TerminalTheme.optionPill(item.getId());
        String labelStr = padRight(item.getLabel(), 28);
        String descStr = item.getDescription().isEmpty() ? "" : "(" + item.getDescription() + ")";
        String badgeStr = item.getBadge().isEmpty() ? "" : " " + item.getBadge();

        if (isSelected) {
            boolean unicode = ConsoleCapabilities.isUnicodeSupported();
            String ptr = unicode ? "❯ " : "> ";
            System.out.printf(" %s %s  %s%s%n",
                    Ansi.paint(Ansi.BOLD + TerminalTheme.accent(), ptr + pill.trim() + " "),
                    Ansi.paint(Ansi.BOLD + TerminalTheme.primary(), labelStr),
                    Ansi.paint(Ansi.BOLD + Ansi.WHITE, descStr),
                    badgeStr.isEmpty() ? "" : " " + Ansi.paint(Ansi.BOLD + TerminalTheme.accent(), badgeStr));
        } else {
            System.out.printf(" %s %s  %s%s%n",
                    pill,
                    Ansi.paint(Ansi.BOLD + Ansi.WHITE, labelStr),
                    Ansi.paint(TerminalTheme.muted(), descStr),
                    badgeStr.isEmpty() ? "" : " " + Ansi.paint(TerminalTheme.secondary(), badgeStr));
        }
    }

    /**
     * Keyboard-driven interactive navigation with real-time arrow keys and selection indicator.
     */
    private MenuResult renderInteractiveMode(Scanner scanner) {
        List<MenuItem> all = new ArrayList<>(items);
        if (exitItem != null) all.add(exitItem);

        int selectedIndex = 0;

        while (true) {
            TerminalRenderer.clearScreen();
            if (showBanner) {
                TerminalRenderer.renderBanner();
            }
            TerminalRenderer.renderBoxHeader(title, subtitle);
            TerminalRenderer.renderStatusBar(statusMode, statusUser, statusInfo);

            String lastCategory = null;
            for (int i = 0; i < all.size(); i++) {
                MenuItem item = all.get(i);
                boolean isSelected = (i == selectedIndex);
                String cat = item.getCategory();
                if (cat != null && !cat.isEmpty() && !cat.equalsIgnoreCase(lastCategory)) {
                    TerminalRenderer.renderCategoryHeader(cat);
                    lastCategory = cat;
                } else if (item == exitItem && lastCategory != null && !lastCategory.isEmpty()) {
                    TerminalRenderer.renderCategoryHeader("SYSTEM & SESSION");
                    lastCategory = "SYSTEM & SESSION";
                }
                renderItemLine(item, isSelected);
            }

            System.out.println();
            TerminalRenderer.renderDivider("", ConsoleCapabilities.getTerminalWidth());
            System.out.println(Ansi.paint(TerminalTheme.muted(), "  [↑/↓ / w/s] Navigate  [Enter] Select  [1-9] Direct Jump  [0/q] Back"));
            Console.prompt("Select option: ");

            TerminalInput.ParsedInput input = TerminalInput.readInput(scanner);
            if (input.isEof()) {
                return MenuResult.exit();
            }

            if (input.isUp()) {
                selectedIndex = (selectedIndex - 1 + all.size()) % all.size();
                continue;
            }

            if (input.isDown()) {
                selectedIndex = (selectedIndex + 1) % all.size();
                continue;
            }

            if (input.isEnter()) {
                MenuItem chosen = all.get(selectedIndex);
                if (chosen == exitItem) return MenuResult.cancel();
                return MenuResult.select(chosen, selectedIndex);
            }

            if (input.isBack()) {
                return MenuResult.cancel();
            }

            if (input.isDigit()) {
                String d = input.getDigit();
                if (exitItem != null && d.equals(exitItem.getId())) {
                    return MenuResult.cancel();
                }
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getId().equals(d)) {
                        return MenuResult.select(items.get(i), i);
                    }
                }
            }

            // Direct textual match fallback
            String raw = input.getRaw().trim();
            if (exitItem != null && raw.equalsIgnoreCase(exitItem.getId())) {
                return MenuResult.cancel();
            }
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getId().equalsIgnoreCase(raw) || items.get(i).getLabel().equalsIgnoreCase(raw)) {
                    return MenuResult.select(items.get(i), i);
                }
            }
        }
    }

    private static String padRight(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s;
        return s + " ".repeat(width - s.length());
    }
}
