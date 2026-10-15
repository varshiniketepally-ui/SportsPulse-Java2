package org.sportspulse.platform.core;

import org.sportspulse.platform.benchmark.BenchmarkService;
import org.sportspulse.platform.console.*;
import org.sportspulse.platform.domain.repository.DataFileHandler;
import org.sportspulse.platform.domain.repository.DataStore;
import org.sportspulse.platform.testing.ComprehensiveSelfTestRunner;

/**
 * Main Entry Point for SPORTSPULSE - Sports Performance Analytics Platform.
 * Pure Java Console Application.
 *
 * User-Centered Sports Performance Architecture:
 * - Users experience real sports operations (Players, Teams, Matches, Live Center, Reports, Squad Planner).
 * - Advanced M1-M6 DSA algorithms function seamlessly underneath every feature.
 * - Optional '[T] Technical Details' views and 'Advanced Analysis & DSA Lab' provide rigorous viva evidence.
 */
public class SportsPulseApp {

    public static final String APP_NAME = "SPORTSPULSE";
    public static final String APP_VERSION = "2.0-ACADEMIC";
    private static boolean running = true;

    public static void main(String[] args) {
        // Initialize portable relative storage
        DataFileHandler.initializeStorage();

        // Handle CLI arguments if provided
        if (args != null && args.length > 0) {
            String arg = args[0].trim().toLowerCase();
            if (arg.equals("--test") || arg.equals("-t")) {
                boolean passed = ComprehensiveSelfTestRunner.runAllTests();
                System.exit(passed ? 0 : 1);
                return;
            } else if (arg.equals("--benchmark") || arg.equals("-b")) {
                BenchmarkService.runAllBenchmarks();
                return;
            } else if (arg.equals("--help") || arg.equals("-h")) {
                printHelp();
                return;
            }
        }

        printBanner();
        runMainLoop();
    }

    private static void printBanner() {
        TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "================================================================================"));
        TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.CYAN, "         SPORTSPULSE - SPORTS PERFORMANCE ANALYTICS PLATFORM"));
        TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "       Pure Java Terminal Application | Advanced DSA Syllabus Engine"));
        TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "================================================================================"));
        TerminalIO.println("Welcome back, Chief Sports Analyst & Tactical Director!\n");
    }

    private static void printHelp() {
        TerminalIO.println("SportsPulse CLI Usage:");
        TerminalIO.println("  java org.sportspulse.platform.core.SportsPulseApp          Interactive console mode");
        TerminalIO.println("  java org.sportspulse.platform.core.SportsPulseApp --test   Run all automated self-tests");
        TerminalIO.println("  java org.sportspulse.platform.core.SportsPulseApp --benchmark Run algorithmic benchmark suite");
        TerminalIO.println("  java org.sportspulse.platform.core.SportsPulseApp --help   Display command line options");
    }

    private static void runMainLoop() {
        while (running) {
            TerminalIO.printHeader("SPORTSPULSE MAIN DASHBOARD");
            TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.WHITE, "  [1]  My Players (Profiles, Form & Rankings)"));
            TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.WHITE, "  [2]  My Teams & Franchises (Rosters & Tactics)"));
            TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.WHITE, "  [3]  Matches & Fixtures (Timelines & Scores)"));
            TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.WHITE, "  [4]  Competitions & League Standings"));
            TerminalIO.println(TerminalIO.color(TerminalIO.BOLD + TerminalIO.WHITE, "  [5]  Training, Conditioning & Fitness Lab"));
            TerminalIO.println(TerminalIO.color(TerminalIO.GREEN, "  [6]  Intelligent Sports Search (Unified Search Engine)"));
            TerminalIO.println(TerminalIO.color(TerminalIO.GREEN, "  [7]  Reports & Tactical Insights (Document Mining)"));
            TerminalIO.println(TerminalIO.color(TerminalIO.YELLOW, "  [8]  Smart Squad Planner (Formations & Synergy Optimization)"));
            TerminalIO.println(TerminalIO.color(TerminalIO.YELLOW, "  [9]  Training & Resource Planner (Facility Flow & Positions)"));
            TerminalIO.println(TerminalIO.color(TerminalIO.MAGENTA, "  [10] Live Match Center (Real-Time Streams & Telemetry)"));
            TerminalIO.println(TerminalIO.color(TerminalIO.MAGENTA, "  [11] Advanced Analysis & DSA Lab (Faculty / Viva Inspection)"));
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "  [12] Performance Benchmark Center"));
            TerminalIO.println(TerminalIO.color(TerminalIO.CYAN, "  [13] System Health Check"));
            TerminalIO.println(TerminalIO.color(TerminalIO.BLUE, "  [14] Data Persistence & File Export/Import"));
            TerminalIO.println(TerminalIO.color(TerminalIO.RED, "  [0]  Exit SportsPulse"));

            int choice = TerminalIO.readInt("Choose an option", 0, 14);
            routeChoice(choice);
        }
        TerminalIO.println(TerminalIO.color(TerminalIO.GREEN, "\nThank you for using SportsPulse. Analytics session saved successfully."));
    }

    private static void routeChoice(int choice) {
        switch (choice) {
            case 1 -> AthleteConsoleController.showMenu();
            case 2 -> FranchiseConsoleController.showFranchiseMenu();
            case 3 -> FixtureConsoleController.showFixtureMenu();
            case 4 -> FranchiseConsoleController.showCompetitionMenu();
            case 5 -> FixtureConsoleController.showFitnessMenu();
            case 6 -> SportsSearchController.showSearchMenu();
            case 7 -> ReportIntelligenceController.showMenu();
            case 8 -> SmartSquadPlannerController.showMenu();
            case 9 -> ResourcePlannerController.showMenu();
            case 10 -> LiveMatchCenterController.showMenu();
            case 11 -> AdvancedAnalysisController.showMenu();
            case 12 -> {
                BenchmarkService.runAllBenchmarks();
                TerminalIO.pressEnterToContinue();
            }
            case 13 -> {
                ComprehensiveSelfTestRunner.runAllTests();
                TerminalIO.pressEnterToContinue();
            }
            case 14 -> handlePersistenceMenu();
            case 0 -> running = false;
            default -> TerminalIO.printError("Invalid menu selection.");
        }
    }

    private static void handlePersistenceMenu() {
        DataStore ds = DataStore.getInstance();
        TerminalIO.printHeader("DATA PERSISTENCE & FILE STORAGE");
        TerminalIO.println("  [1] Export Current Sports State to Relative Files (data/)");
        TerminalIO.println("  [2] Import/Reload State from Relative Files (data/)");
        TerminalIO.println("  [0] Return to Dashboard");

        int c = TerminalIO.readInt("Select Option", 0, 2);
        if (c == 1) {
            boolean ok = DataFileHandler.exportAll(ds);
            if (ok) TerminalIO.printSuccess("Exported records to 'data/' folder successfully.");
            else TerminalIO.printError("File export encountered an error.");
        } else if (c == 2) {
            boolean ok = DataFileHandler.importAll(ds);
            if (ok) TerminalIO.printSuccess("Imported records from 'data/' folder successfully.");
            else TerminalIO.printWarning("No data files found to import.");
        }
        TerminalIO.pressEnterToContinue();
    }
}
