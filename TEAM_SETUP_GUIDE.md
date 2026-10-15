# SPORTSPULSE — TEAMMATE PORTABILITY & SETUP GUIDE

Welcome to **SportsPulse** (Sports Analytics & Performance Platform).
This repository is engineered to be **100% portable, self-contained, and path-agnostic**.

Every teammate can clone, unzip, or copy this project folder anywhere on their laptop (Windows, macOS, Linux) and run it instantly in their favorite editor with **zero path configuration**.

---

## ⚡ 10-Second Quick Start (Terminal / Command Prompt)

No IDE or build tool required! Run directly from the command line:

### Windows (Command Prompt or PowerShell)
`cmd
run.bat
`
*(Or to run the 202 automated regression tests: uild.bat test)*

### macOS / Linux (Terminal / Bash / Zsh)
`ash
chmod +x run.sh build.sh
./run.sh
`
*(Or to run the 202 automated regression tests: ./build.sh test)*

### Universal Portable JAR (Any OS with Java 17+)
`ash
java -jar SportsPulse.jar
`
*(Or with flags: java -jar SportsPulse.jar --test or java -jar SportsPulse.jar --env-check)*

---

## 💻 Running in Your Favorite Editor

Pre-configured workspace metadata is included for all major Java editors:

### 1. Visual Studio Code
1. Open VS Code: File → Open Folder... → select this project folder.
2. If prompted, install the recommended **Extension Pack for Java** (Microsoft).
3. Open the **Run and Debug** view (Ctrl+Shift+D or Cmd+Shift+D).
4. Select any pre-configured target from the drop-down and click **▶ Play**:
   - **SportsPulse (Interactive Console)** — Launches the full interactive TUI application.
   - **SportsPulse (202 Automated Tests)** — Runs the full automated verification suite.
   - **SportsPulse (Empirical Benchmarks)** — Runs live empirical algorithmic benchmarks.
   - **SportsPulse (Environment Check)** — Verifies host JVM and path portability.

### 2. IntelliJ IDEA
1. Open IntelliJ: File → Open... → select this project folder.
2. IntelliJ will automatically detect the pre-configured SportsPulse.iml and .idea/ workspace.
3. In the top-right toolbar, select **SportsPulse (Interactive Console)** and click the green **▶ Run** button.
4. Or right-click src/com/sportspulse/Main.java → **Run 'Main.main()'**.

### 3. Eclipse IDE
1. Open Eclipse: File → Open Projects from File System... (or Import... → Existing Projects into Workspace).
2. Choose this directory as the directory root. Eclipse will automatically recognize .project and .classpath.
3. Click **Finish**.
4. In Project Explorer, right-click src/com/sportspulse/Main.java → Run As → **Java Application**.

### 4. Apache NetBeans / Apache Ant
- Open the project folder in NetBeans, or run from terminal:
  `ash
  ant run        # Launches application
  ant test       # Runs 202 tests
  ant jar        # Builds standalone runnable JAR
  `

---

## 🛡️ Portability Guarantees

1. **Zero Machine-Specific Paths**:
   - AppPaths.java resolves paths dynamically at runtime relative to the project root.
   - No absolute paths (C:\..., /Users/..., /home/...) exist anywhere in the code.
2. **Automatic Directory Creation**:
   - Writable directories (
untime/logs/, 
untime/reports/, 
untime/benchmarks/, 
untime/data/) are auto-created on demand.
3. **Cross-Platform UTF-8 & Terminal Safety**:
   - uild.bat and 
un.bat automatically execute chcp 65001 on Windows.
   - Box-drawing and glyph rendering automatically detect terminal capabilities to prevent broken question marks (?).
4. **Zero Third-Party Dependencies**:
   - Pure JDK 17+ standard library. No Maven or Gradle download steps required.

---

## 🧪 Self-Verification Checklist

Before presenting or submitting, teammates can verify that their setup is 100% operational:

| Check | Command | Expected Output |
| :--- | :--- | :--- |
| **Host Diagnostic** | java -jar SportsPulse.jar --env-check | Environment Status: READY |
| **Automated Tests** | java -jar SportsPulse.jar --test | Passed: 202 Failed: 0 |
| **Interactive TUI** | 
un.bat (or ./run.sh) | Styled ASCII Banner + Auth Gateway |
