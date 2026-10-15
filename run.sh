#!/usr/bin/env bash
# SportsPulse - Sports Performance Analytics Platform
# Cross-Platform Launcher for Unix, Linux, and macOS

set -e

# Resolve script directory portably
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Ensure runtime directories exist
mkdir -p runtime/logs runtime/reports runtime/benchmarks runtime/data bin

# Prefer standalone runnable JAR if present
if [ -f "dist/SportsPulse.jar" ]; then
    exec java -Dfile.encoding=UTF-8 -jar dist/SportsPulse.jar "$@"
elif [ -f "SportsPulse.jar" ]; then
    exec java -Dfile.encoding=UTF-8 -jar SportsPulse.jar "$@"
fi

# Fallback: compile from source
echo "[SportsPulse] Compiling Java sources..."
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
rm -f sources.txt

# Run main application
exec java -Dfile.encoding=UTF-8 -cp bin com.sportspulse.Main "$@"

