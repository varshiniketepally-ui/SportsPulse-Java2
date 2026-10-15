#!/usr/bin/env bash
# SportsPulse - Unified Build & Test Automation for Unix/Linux/macOS
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

ACTION="${1:-build}"

compile() {
    mkdir -p bin runtime/logs runtime/reports runtime/benchmarks runtime/data
    find src -name "*.java" > sources.txt
    javac -encoding UTF-8 -d bin @sources.txt
    rm -f sources.txt
}

case "$ACTION" in
    build)
        compile
        echo "Build OK -> bin/"
        ;;
    test)
        compile
        java -Dfile.encoding=UTF-8 -cp bin com.sportspulse.SelfTest "$@"
        ;;
    run)
        compile
        java -Dfile.encoding=UTF-8 -cp bin com.sportspulse.Main "$@"
        ;;
    benchmark|bench)
        compile
        java -Dfile.encoding=UTF-8 -cp bin com.sportspulse.benchmark.BenchmarkRunner "$@"
        ;;
    check)
        compile
        java -Dfile.encoding=UTF-8 -cp bin com.sportspulse.EnvironmentCheck "$@"
        ;;
    jar)
        compile
        mkdir -p dist bin/META-INF
        cat << 'EOF' > bin/META-INF/MANIFEST.MF
Manifest-Version: 1.0
Main-Class: com.sportspulse.Main
Implementation-Title: SportsPulse
EOF
        jar cfm dist/SportsPulse.jar bin/META-INF/MANIFEST.MF -C bin .
        cp -f dist/SportsPulse.jar SportsPulse.jar
        echo "Built standalone runnable JAR -> dist/SportsPulse.jar and SportsPulse.jar"
        ;;
    clean)
        rm -rf bin build dist out sources.txt
        echo "Cleaned build artifacts."
        ;;
    *)
        echo "Usage: $0 {build|test|run|benchmark|check|jar|clean}"
        exit 1
        ;;
esac
