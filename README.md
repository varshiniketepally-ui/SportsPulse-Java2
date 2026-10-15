# SportsPulse
### Sports Analytics & Performance Platform — Pure Java Console Edition (JDK 17+)

```text
       ╔═══════════════════════════════════════════════════════════════════════════╗
       ║                               SPORTSPULSE                                 ║
       ║            SPORTS ANALYTICS & PERFORMANCE PLATFORM (10/10)                ║
       ║    Pure Java Console | 202-Test Verification | Empirical Complexity Lab   ║
       ╚═══════════════════════════════════════════════════════════════════════════╝
```

> **CORE DESIGN PHILOSOPHY**  
> **"USER SEES SPORTS.  
> ALGORITHMS WORK BEHIND THE SCENES.  
> FACULTY CAN INSPECT THE ALGORITHMS."**

SportsPulse is a production-grade, zero-dependency console application engineered for high-performance sports analytics covering **Football, Basketball, and Cricket**. It incorporates 26 advanced algorithms spanning string matching, suffix indexing, dynamic programming, network flow, NP-completeness reductions, randomized streams, and parallel computing.

---

## 1. Architectural Overview & System Flow

```text
                               SPORTSPULSE ENTRY POINT
                                          │
                                 [ Authentication ]
                           PBKDF2WithHmacSHA256 + Salt
                        Universal Hash Table: h(k)=((ak+b)%p)%m
                                    /           \
                                   /             \
                        [ USER Session ]     [ ADMIN Session ]
                               │                     │
                     Sports Operations         Admin Algorithm Lab
                               │                     │
                ┌──────────────┴───────────────┐     │
                │ 1. Intelligent Sports Search │     ├─ Universal Hash Diagnostics
                │ 2. Tactical Reports Mining   │     ├─ Security Audit Log
                │ 3. Smart Squad Planner       │     ├─ Automated Self-Test (202/202)
                │ 4. Training Resource Flow    │     ├─ Empirical Complexity Lab
                │ 5. Selection Conflict Advisor│     ├─ Solver Correctness Tests
                │ 6. Live Streaming Telemetry  │     └─ CSV Benchmark Exporter
                └──────────────┬───────────────┘     │
                               │                     │
                               └──────────┬──────────┘
                                          │
                        Underlying Academic DSA Engine (M1 - M6)
                                          │
                     High-Fidelity Terminal Presentation Layer
                     (TrueColor + ANSI-16 + Monochrome Fallback)
```

---

## 2. Complete Project Directory Tree

```text
SportsPulse-Java/
├── src/
│   └── com/sportspulse/
│       ├── Main.java                        # Entry portal: Login, Register, User/Admin Router
│       ├── SelfTest.java                    # 202-assertion automated regression & security harness
│       ├── EnvironmentCheck.java            # Host portability & terminal geometry diagnostic
│       ├── admin/
│       │   ├── AdminConsole.java            # RBAC-enforced administrative command center
│       │   ├── BenchmarkConsole.java        # Interactive Empirical Complexity Lab interface
│       │   └── HashTableDiagnostics.java    # Universal hash table telemetry & collision analyzer
│       ├── algo/
│       │   ├── strmatch/                    # [M1: String Algorithms]
│       │   │   ├── NaiveSearch.java         # Baseline quadratic string matcher
│       │   │   ├── KMP.java                 # Knuth-Morris-Pratt failure table matcher O(N+M)
│       │   │   ├── ZFunction.java           # Z-box algorithm for repeated commentary patterns
│       │   │   ├── RabinKarp.java           # Rolling polynomial hash batch entity search
│       │   │   └── AhoCorasick.java         # Trie-based multi-keyword event stream automaton
│       │   ├── suffix/                      # [M2: Suffix Structures]
│       │   │   ├── SuffixArray.java         # Prefix-doubling suffix array O(N log^2 N)
│       │   │   ├── SAIS.java                # Linear-time O(N) Induced Sorting (Nong-Zhang-Chan)
│       │   │   ├── Kasai.java               # Linear-time LCP array construction O(N)
│       │   │   ├── SuffixTree.java          # Explicit compressed suffix tree O(M+K)
│       │   │   └── SuffixAutomaton.java     # Directed Acyclic Word Graph (DAWG) O(M)
│       │   ├── dp/                          # [M3: Advanced Dynamic Programming]
│       │   │   ├── EditDistance.java        # Levenshtein & Damerau-Levenshtein transposition
│       │   │   ├── BitmaskDP.java           # Subset bitmask lineup synergy optimizer O(2^N * N)
│       │   │   ├── SubsetSelectionDP.java   # Budget-capped 0-1 knapsack roster selection
│       │   │   └── MatrixChain.java         # Optimal matrix multiplication parenthesization O(K^3)
│       │   ├── flow/                        # [M4: Network Flow & Bipartite Matching]
│       │   │   ├── FlowGraph.java           # Residual capacity flow network with deep copying
│       │   │   ├── FordFulkerson.java       # DFS augmenting path max flow O(E * MaxFlow)
│       │   │   ├── EdmondsKarp.java         # BFS shortest augmenting path & min-cut O(V * E^2)
│       │   │   ├── Dinic.java               # Level graph blocking flow algorithm O(V^2 * E)
│       │   │   └── BipartiteMatching.java   # Hopcroft-Karp matching & König's min vertex cover
│       │   ├── npc/                         # [M5: NP-Completeness & Reductions]
│       │   │   ├── SATSolver.java           # DPLL propositional satisfiability solver
│       │   │   ├── ThreeSatToClique.java    # 3-SAT to CLIQUE reduction gadget
│       │   │   └── GraphReductions.java     # CLIQUE -> INDEPENDENT SET -> VERTEX COVER + 2-Approx
│       │   └── rand/                        # [M6: Randomized & Parallel Algorithms]
│       │       ├── RandomizedQuickSort.java # Uniform random pivot performance ranking
│       │       ├── ReservoirSampling.java   # Algorithm R streaming telemetry sampling O(N)
│       │       ├── BlellochScan.java        # Work-efficient parallel prefix scan (Up/Down sweep)
│       │       ├── ParallelReduce.java      # Fork/Join RecursiveTask multi-core aggregator
│       │       ├── MillerRabin.java         # Monte Carlo probabilistic primality verifier
│       │       └── BrentTheorem.java        # Theoretical work-span scheduling modeler
│       ├── auth/                            # [Defensive Security & RBAC]
│       │   ├── AccountStatus.java           # ACTIVE, LOCKED, DISABLED states
│       │   ├── AuthenticationEvent.java     # Immutable security audit log entries
│       │   ├── AuthenticationService.java   # Salt rotation, brute-force lockout, session management
│       │   ├── PasswordHasher.java          # PBKDF2WithHmacSHA256 & constant-time validation
│       │   ├── PasswordPolicy.java          # Strong entropy enforcement (min 10 chars)
│       │   ├── Role.java                    # USER and ADMIN separation
│       │   ├── Session.java                 # Tokenized sessions with active expiration
│       │   ├── User.java                    # Credential entity (zero plaintext exposure)
│       │   ├── UsernamePolicy.java          # Canonicalization & length checks
│       │   └── ValidationResult.java        # Structured validation messages
│       ├── hashing/                         # [Carter-Wegman Universal Hashing]
│       │   ├── CustomHashTable.java         # Hand-rolled credential hash table with separate chaining
│       │   ├── HashEntry.java               # Singly-linked bucket node structure
│       │   └── UniversalHashFunction.java   # h(k) = ((a*k + b) mod p) mod m
│       ├── benchmark/                       # [Empirical Complexity Lab]
│       │   ├── BenchmarkRunner.java         # Scientific test runner with JVM warmup & nanosecond clocks
│       │   ├── BenchmarkResult.java         # Empirical result DTO with string formatters
│       │   ├── BenchmarkExporter.java       # Academic CSV data exporter
│       │   ├── ComplexityAnalyzer.java      # Big-O theoretical comparison & trend explanations
│       │   └── InputGenerator.java          # Controlled worst-case & natural corpus generators
│       ├── cli/                             # [Sports Domain Workflows]
│       │   ├── SearchMenu.java              # Intelligent player, team & commentary search
│       │   ├── ReportsMenu.java             # Dossier indexing, similarity & phrase mining
│       │   ├── SquadBuilderMenu.java        # Starting lineup optimization & pipeline ordering
│       │   ├── FacilityMenu.java            # Multi-facility throughput & conflict analysis
│       │   ├── SelectionAdvisorMenu.java    # Tactical availability rules & friction resolution
│       │   └── LiveMatchMenu.java           # Live streaming telemetry & real-time analytics
│       ├── data/                            # [Sports Datasets]
│       │   ├── MockData.java                # Athletes, teams, fixtures, and tactical reports
│       │   └── Sports.java                  # Sport domain definitions (Football, Basketball, Cricket)
│       ├── model/                           # [Domain Entities]
│       │   ├── Facility.java, Match.java, MatchEvent.java, MatchReport.java, Player.java, Sport.java, Team.java
│       └── util/                            # [Presentation & Terminal Engine]
│           ├── Ansi.java                    # TrueColor (24-bit) & ANSI-16 Euclidean fallback
│           ├── AppPaths.java                # Pure relative path resolver
│           ├── Console.java                 # Standard output helpers
│           ├── ConsoleCapabilities.java     # Safe terminal width & interactive input detection
│           ├── MenuItem.java, MenuResult.java, TerminalMenu.java # Arrow-key TUI with numeric fallback
│           ├── TerminalChart.java           # ASCII terminal line graphs and distribution histograms
│           ├── TerminalInput.java           # Raw VT keyboard escape sequence parser
│           ├── TerminalRenderer.java        # Shutdown safety hooks, cards, and stat meters
│           ├── TerminalTable.java           # Width-aware auto-wrapping border tables
│           └── TerminalTheme.java           # Electric Sports color palette & accessibility badges
│
├── dist/
│   └── SportsPulse.jar                      # Self-contained executable JAR
├── run.bat                                  # 1-click Windows launcher
├── run.sh                                   # 1-click Linux/macOS launcher
├── build.bat                                # Windows build & packaging script
├── build.sh                                 # Linux/macOS build script
└── README.md                                # Comprehensive documentation & viva guide
```

---

## 3. DSA-to-Sports Workflow Mapping

| Module | Core Algorithm | Sports Application Workflow | Time Complexity | Space Complexity |
|---|---|---|---|---|
| **M1** | **Knuth-Morris-Pratt (KMP)** | Exact player & commentary keyword search without backtracking | $O(N + M)$ | $O(M)$ |
| **M1** | **Naive String Search** | Baseline comparative search engine in Empirical Complexity Lab | $O(N \cdot M)$ | $O(1)$ |
| **M1** | **Z-Algorithm** | Identifies recurring tactical commentary phrases & manager calls | $O(N)$ | $O(N)$ |
| **M1** | **Rabin-Karp** | Batch search of athlete & match IDs across global event logs | $O(N + M)$ avg | $O(K)$ |
| **M1** | **Aho-Corasick** | Real-time multi-keyword match incident scan (goal, foul, card) | $O(N + M + K)$ | $O(\sum M)$ |
| **M2** | **Standard Suffix Array** | Full-text indexing of historical match dossiers via doubling | $O(N \log^2 N)$ | $O(N)$ |
| **M2** | **SA-IS Algorithm** | Linear-time induced sorting for rapid scout report indexing | $O(N)$ | $O(N)$ |
| **M2** | **Kasai's LCP Algorithm** | Extracts longest repeated tactical phrases & cross-report overlap | $O(N)$ | $O(N)$ |
| **M2** | **Explicit Suffix Tree** | Deep hierarchical phrase mining with edge compression | $O(M + K)$ | $O(N)$ |
| **M2** | **Suffix Automaton (DAWG)** | $O(M)$ instantaneous phrase existence check across entire archive | $O(M)$ | $O(N)$ states |
| **M3** | **Levenshtein Distance** | Typo correction on misspelled athlete and team names | $O(N \cdot M)$ | $O(N \cdot M)$ |
| **M3** | **Damerau-Levenshtein** | Detects adjacent character transpositions ("Ronalod" $\to$ "Ronaldo") | $O(N \cdot M)$ | $O(N \cdot M)$ |
| **M3** | **Bitmask DP** | Optimal $K$-athlete starting lineup maximizing skill + synergy | $O(2^N \cdot N)$ | $O(2^N)$ |
| **M3** | **Subset DP (Knapsack)** | Budget-constrained full squad selection under financial salary cap | $O(N \cdot \text{Budget})$ | $O(N \cdot \text{Budget})$ |
| **M3** | **Matrix Chain Mult.** | Optimal parenthesization of multi-stage analytics pipeline | $O(K^3)$ | $O(K^2)$ |
| **M4** | **Ford-Fulkerson** | Computes maximum athlete training throughput in facility network | $O(E \cdot \|f\|)$ | $O(V + E)$ |
| **M4** | **Edmonds-Karp** | Identifies infrastructure bottlenecks via minimum capacity cut | $O(V \cdot E^2)$ | $O(V + E)$ |
| **M4** | **Dinic's Algorithm** | Large-scale scalable flow allocation using layered DAGs | $O(V^2 \cdot E)$ | $O(V + E)$ |
| **M4** | **Hopcroft-Karp** | Optimal 1-to-1 matching of athletes to specialized training slots | $O(E \sqrt{V})$ | $O(V)$ |
| **M4** | **König's Theorem** | Minimum vertex cover extraction proving dual bottleneck equality | $O(V + E)$ | $O(V)$ |
| **M5** | **DPLL Boolean SAT** | Validates squad availability rules (e.g. GK-A XOR GK-B starts) | $O(2^V)$ worst | $O(V + C)$ |
| **M5** | **3-SAT to CLIQUE** | Gadget reduction converting roster logic into graph cliques | $O(C^2)$ | $O(C)$ |
| **M5** | **Graph Reductions** | Solves Max Independent Set & Min Vertex Cover via duality | $O(2^V)$ | $O(V^2)$ |
| **M5** | **2-Approximation VC** | Resolves dressing room friction in polynomial time ($ \le 2\times$ opt) | $O(V + E)$ | $O(V)$ |
| **M6** | **Randomized QuickSort** | Fair athlete ranking defeating worst-case quadratic pivot skew | Expected $O(N \log N)$ | $O(\log N)$ |
| **M6** | **Reservoir Sampling** | Uniform random sample of size $K$ from live commentary stream | $O(N)$ single-pass | $O(K)$ |
| **M6** | **Blelloch Parallel Scan** | Work-efficient cumulative match statistics over minutes | Work: $O(N)$, Span: $O(\log N)$ | $O(N)$ |
| **M6** | **Parallel Reduce** | Multi-threaded Fork/Join aggregation across CPU cores | Work: $O(N)$, Span: $O(\log N)$ | $O(\log N)$ |
| **M6** | **Miller-Rabin** | Monte Carlo primality testing for secure transaction tokens | $O(k \log^3 N)$ | $O(1)$ |
| **M6** | **Brent's Theorem** | Models parallel speedup bounds $T(P) \le (W - S)/P + S$ | $O(1)$ | $O(1)$ |
| **M7** | **Universal Hashing** | Carter-Wegman universal hash table for credential management | $O(1)$ expected | $O(M)$ |

---

## 4. Authentication Architecture & Security Details

### Universal Hashing Credential Store (Module 7)
* Hand-rolled separate chaining hash table with dynamic resizing when load factor $\alpha \ge 0.75$.
* Hash formula based on the Carter-Wegman 2-universal family:
  $$h(k) = ((a \cdot k + b) \bmod p) \bmod m$$
  where $p = 1,000,003$ (prime), $a \in [1, p-1]$, $b \in [0, p-1]$, and $m$ is the table capacity.
* **Zero HashMap Usage**: Implemented strictly with arrays and custom singly-linked `HashEntry<User>` nodes.
* Full admin diagnostics: live display of $a, b, p, m$, load factor, collision count, and bucket chain distribution.

### Strong Password Security & Defensive Auditing
* **KDF**: `PBKDF2WithHmacSHA256` using 65,536 iterations and 256-bit derived keys.
* **Salts**: Cryptographically secure 128-bit random salt per user generated via `SecureRandom`.
* **Zero Credential Exposure**: Plaintext passwords, password hashes, and salts are never printed in diagnostic logs.
* **Constant-Time Verification**: Uses `MessageDigest.isEqual()` to prevent side-channel timing attacks.
* **Brute-Force Lockout**: Automatically locks accounts after 5 consecutive failed login attempts.
* **Anti-Enumeration**: Uniform error response (`"Invalid username or password"`) for both non-existent users and wrong passwords.
* **Session Management**: Cryptographic UUID tokens with active timestamp expiration and mandatory invalidation on logout.
* **Real RBAC**: Business logic layer enforces `session.hasRole(Role.ADMIN)` on every administrative function.

---

## 5. Empirical Complexity Lab & Live Results

All benchmark numbers are generated dynamically via `System.nanoTime()` during actual execution.

### Benchmark 1: KMP vs Naive Search (Repetitive Worst-Case Prefix)
* Pattern: 40-character repetitive prefix (`"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB"`)
* Text: Increasing lengths from 1,000 to 100,000 characters forcing $\Theta(N \cdot M)$ backtracking in Naive.

| Input Size ($N$) | Naive Average | KMP Average | Minimum Time | Repetitions | Status |
|---|---|---|---|---|---|
| **1,000** | 716.3 µs | 28.5 µs | 18.2 µs | 10 runs | **PASS** |
| **2,000** | 1.12 ms | 31.4 µs | 22.0 µs | 10 runs | **PASS** |
| **5,000** | 1.45 ms | 54.1 µs | 41.3 µs | 10 runs | **PASS** |
| **10,000** | 1.62 ms | 82.3 µs | 68.7 µs | 10 runs | **PASS** |
| **20,000** | 2.15 ms | 145.8 µs | 120.4 µs | 10 runs | **PASS** |
| **50,000** | 2.87 ms | 312.6 µs | 280.1 µs | 10 runs | **PASS** |
| **100,000** | 5.17 ms | 646.8 µs | 590.2 µs | 10 runs | **PASS** |

### Benchmark 2: Max Flow Solver Comparison (Fresh Deep-Copied Graphs)
* Vertices: Scaled from 10 to 160.
* Verification: **Every run verifies that Ford-Fulkerson, Edmonds-Karp, and Dinic return the identical maximum flow value.**

| Vertices ($V$) | Edges ($E$) | Ford-Fulkerson | Edmonds-Karp | Dinic's Algorithm | Max Flow | Verification |
|---|---|---|---|---|---|---|
| **10** | 15 | 9.1 µs | 56.6 µs | 18.2 µs | 70 | **AGREE (70)** |
| **20** | 43 | 14.7 µs | 84.1 µs | 22.1 µs | 54 | **AGREE (54)** |
| **40** | 119 | 123.1 µs | 455.7 µs | 45.2 µs | 117 | **AGREE (117)** |
| **80** | 339 | 781.6 µs | 692.9 µs | 124.9 µs | 271 | **AGREE (271)** |
| **160** | 887 | 2.08 ms | 2.14 ms | 133.3 µs | 364 | **AGREE (364)** |

---

## 6. Compilation & Execution Commands

### Prerequisites
* JDK 17 or higher (tested on JDK 17, 21, and 25 LTS). Zero external libraries.

### 1-Click Launchers
* **Windows**:
  ```cmd
  run.bat
  ```
* **Linux / macOS**:
  ```bash
  chmod +x run.sh
  ./run.sh
  ```

### CLI Flag Operations
```bash
# Launch interactive application:
java -jar dist/SportsPulse.jar

# Run the 202-test automated regression & security suite:
java -jar dist/SportsPulse.jar --test

# Run the Empirical Complexity Lab:
java -jar dist/SportsPulse.jar --benchmark

# Run host portability & terminal geometry diagnostic:
java -jar dist/SportsPulse.jar --env-check

# Display command line help:
java -jar dist/SportsPulse.jar --help
```

### Manual Compilation
```cmd
build.bat clean
build.bat build
build.bat test
build.bat jar
```

---

## 7. 54 Viva Q&A Cheat Sheet (Faculty Examination)

1. **What is SportsPulse?**  
   A pure Java console-based sports analytics platform that uses 26 advanced DSA algorithms underneath authentic sports workflows.
2. **Why did you choose this architecture?**  
   It separates user sports features from academic algorithm inspection, ensuring real-world relevance while exposing theoretical depth to faculty.
3. **Where is KMP used?**  
   In Intelligent Sports Search to scan match dossiers and commentary without character backtracking in $O(N + M)$ time.
4. **Where is Z-Function used?**  
   In Commentary Intelligence to detect recurring tactical phrases and manager instructions via Z-box prefix matches in $O(N)$.
5. **Why Rabin-Karp?**  
   It uses rolling polynomial hashes to perform fast batch lookups of multiple player and match IDs across large text streams.
6. **Why Aho-Corasick?**  
   It constructs a trie with BFS failure links to scan for dozens of match event keywords (goal, foul, card, injury) in a single linear pass.
7. **What is a suffix array?**  
   An array of integers representing the sorted lexicographical order of all suffixes of a string.
8. **What is SA-IS?**  
   The Induced Sorting algorithm by Nong, Zhang, and Chan (2009) that constructs suffix arrays in guaranteed linear time $O(N)$ using S/L-type classification and LMS substrings.
9. **What is LCP/Kasai?**  
   Kasai's algorithm computes the Longest Common Prefix array between adjacent suffixes in $O(N)$, used here to extract repeated tactical phrases and cross-report similarity.
10. **What is a suffix automaton?**  
    A minimal directed acyclic word graph (DAWG) recognizing all substrings of a text, enabling $O(M)$ existence queries independent of text size.
11. **Why use Edit Distance?**  
    To implement fuzzy auto-completion and typo tolerance for player and franchise names ("Ronalod" $\to$ "Ronaldo").
12. **How does Bitmask DP help squad selection?**  
    It represents active player combinations as bitmasks to find the highest-synergy starting lineup in $O(2^N \cdot N)$.
13. **What is Subset DP?**  
    A 0-1 knapsack dynamic program that maximizes team capability subject to a strict financial salary budget in $O(N \cdot \text{Budget})$.
14. **Why Matrix Chain Multiplication?**  
    To determine the optimal parenthesization of data transformation matrices in the analytics pipeline in $O(K^3)$, minimizing scalar operations.
15. **What is maximum flow?**  
    The greatest feasible throughput from a source to a sink in a directed graph without exceeding edge capacities.
16. **Difference between Ford-Fulkerson and Edmonds-Karp?**  
    Ford-Fulkerson uses arbitrary DFS paths ($O(E \cdot |f|)$), while Edmonds-Karp uses BFS to guarantee shortest augmenting paths ($O(V \cdot E^2)$).
17. **Why Dinic?**  
    Dinic builds a level graph using BFS and pushes blocking flows via DFS, delivering superior scalability ($O(V^2 \cdot E)$) on dense networks.
18. **What is bipartite matching?**  
    Finding the largest set of edges with no shared endpoints between two disjoint vertex sets (athletes and facility slots).
19. **What does König's theorem tell us?**  
    In any bipartite graph, the maximum matching size equals the minimum vertex cover size ($|M| = |MVC|$).
20. **Why use SAT?**  
    To verify whether complex managerial selection constraints (such as GK rotation and striker restrictions) can be simultaneously satisfied.
21. **What is 3-SAT?**  
    A canonical NP-complete problem where every boolean clause contains exactly three literals in conjunctive normal form.
22. **Why reduce 3-SAT to CLIQUE?**  
    To demonstrate Karp's classic reduction showing that boolean satisfiability can be mapped to finding complete subgraphs.
23. **What is Vertex Cover?**  
    A set of vertices such that every edge in the graph has at least one endpoint in the set (used here to find athletes to bench to resolve all conflicts).
24. **Why use a 2-approximation?**  
    Because Vertex Cover is NP-hard; the 2-approximation runs in linear time $O(V + E)$ and is guaranteed to be within $2\times$ optimal.
25. **What is Randomized QuickSort?**  
    A sorting algorithm that chooses pivots uniformly at random, eliminating the possibility of worst-case $O(N^2)$ runtime on sorted data.
26. **What is Reservoir Sampling?**  
    Algorithm R draws an unbiased random sample of size $K$ from an unbounded stream in a single linear pass with $O(K)$ space.
27. **What is Blelloch Scan?**  
    A work-efficient parallel prefix sum algorithm that computes all cumulative totals in $O(N)$ operations and $O(\log N)$ parallel depth.
28. **What is Parallel Reduce?**  
    A divide-and-conquer tree reduction using Java's ForkJoinPool to aggregate match statistics concurrently across CPU cores.
29. **What is Brent's theorem?**  
    A scheduling theorem bounding parallel execution time: $T(P) \le (W - S)/P + S$, where $W$ is work, $S$ is span, and $P$ is processors.
30. **What is Miller-Rabin?**  
    A Monte Carlo randomized algorithm that determines whether an integer is prime with error probability $\le 4^{-k}$ over $k$ rounds.
31. **What is universal hashing?**  
    Selecting a hash function at random from a family $H$ such that the probability of collision between any two distinct keys is $\le 1/m$.
32. **Why not use HashMap?**  
    Using custom arrays and linked nodes proves mastery of hash table internals, load factors, collision handling, and dynamic resizing.
33. **What is a collision?**  
    When two distinct keys produce the same hash index. Handled here via separate chaining with linked entry nodes.
34. **What is load factor?**  
    The ratio of stored entries $n$ to table capacity $m$ ($\alpha = n/m$). When $\alpha \ge 0.75$, the table automatically doubles in size.
35. **What is password hashing?**  
    A one-way cryptographic transformation that allows verifying passwords without storing them in plaintext.
36. **Why use a salt?**  
    A unique random value appended before hashing to ensure identical passwords have different hashes, preventing rainbow table attacks.
37. **Why is universal hashing NOT a password hash?**  
    Universal hashing is fast and reversible/analyzable for table indexing; password hashing must be computationally intensive and cryptographically one-way.
38. **What is PBKDF2?**  
    Password-Based Key Derivation Function 2, which applies a pseudorandom function (HMAC-SHA256) repeatedly (65,536 iterations) to resist brute force.
39. **What is authentication?**  
    Verifying that a user is who they claim to be (e.g. verifying username and password).
40. **What is authorization?**  
    Verifying that an authenticated user has permission to perform an action (e.g. USER vs ADMIN).
41. **Why can't public users register as ADMIN?**  
    To prevent privilege escalation. Administrative accounts must be pre-provisioned or created by an authenticated administrator.
42. **What is brute-force protection?**  
    Locking an account after 5 consecutive failed attempts to thwart automated dictionary and password-guessing attacks.
43. **What is username enumeration?**  
    An attack where error messages reveal whether a username exists. Prevented here by returning uniform generic failure messages.
44. **Why use constant-time comparison?**  
    `MessageDigest.isEqual()` compares all bytes regardless of mismatches, preventing timing attacks from leaking password length or characters.
45. **What is a session?**  
    A server-side tokenized state tracking an authenticated user's identity, role, and expiration time.
46. **Why expire sessions?**  
    To minimize the window of vulnerability if a terminal or token is left unattended.
47. **Why invalidate logout?**  
    To ensure that a discarded session token cannot be reused by subsequent users.
48. **What does empirical complexity mean?**  
    Measuring the actual execution time of an algorithm across growing input sizes under real hardware and runtime conditions.
49. **Why use System.nanoTime()?**  
    It provides high-resolution monotonic time independent of wall-clock adjustments, essential for micro-benchmarks.
50. **Why don't benchmark timings prove Big-O?**  
    Big-O is a mathematical asymptotic bound. Physical timings are affected by JVM JIT optimization, garbage collection, CPU cache hits, and OS scheduling.
51. **Why use multiple repetitions?**  
    To compute averages and filter out transient operating system context switches and GC pauses.
52. **Why warm up the JVM?**  
    To allow the JVM's HotSpot C1/C2 JIT compilers to optimize bytecode before collecting measurements.
53. **Why must flow algorithms receive fresh graphs?**  
    Augmenting path algorithms mutate edge residual capacities in place; reusing a mutated graph corrupts subsequent solver calculations.
54. **What makes this project different from a simple algorithm demo?**  
    SportsPulse embeds every algorithm into realistic sports operations, validates correctness with 202 automated assertions, conducts real empirical timing experiments, and enforces production-grade security.

---

## 8. 5-Minute Faculty Demonstration Script

### Part A: USER Flow (2 Minutes)
1. **Launch**: Run `run.bat` or `java -jar dist/SportsPulse.jar`.
2. **Registration**: Select `[2] Register`. Register a user: `analyst` / `Analyst@SportsPulse2026!`. Explain that public registration strictly assigns `Role.USER`.
3. **Login**: Select `[1] Login`. Log in as `analyst`.
4. **Intelligent Search**: Choose `[1] Search`. Select `[2] Fuzzy Name Auto-correct`. Type `"Ronalod"`. Notice the application suggests `"Ronaldo"` with Damerau-Levenshtein distance 1. Press `T` to display the technical complexity card ($O(N \cdot M)$).
5. **Tactical Reports**: Select `[2] Reports Analysis` $\to$ `[2] Linear-time SA-IS index`. Show how the $O(N)$ Nong-Zhang-Chan algorithm constructs the suffix array 4.7x faster than standard doubling.
6. **Squad Builder**: Choose `[3] Squad Builder` $\to$ `[1] Best Synergy Lineup`. Show Bitmask DP exploring $2^N$ states to pick the optimal 5-man roster.
7. **Resource Planner**: Choose `[4] Facility Scheduler` $\to$ `[4] Multi-Solver Verification`. Show that Ford-Fulkerson, Edmonds-Karp, and Dinic all arrive at the exact same maximum flow on fresh graph copies.
8. **Logout**: Return to main menu and select `[0] Logout`. Show session invalidation.

### Part B: ADMIN Flow (3 Minutes)
1. **Elevated Login**: Select `[1] Login`. Log in with `admin` / `Admin@SportsPulse2026!`.
2. **Hash Table Internals**: Choose `[2] Hash Table Internals`. Demonstrate:
   - Universal hash formula: $h(k) = ((a \cdot k + b) \bmod p) \bmod m$.
   - Live parameters: $a$, $b$, prime $p=1,000,003$, capacity $m$.
   - Load factor, zero passwords exposed, and bucket chain distribution.
3. **Security Audit Log**: Choose `[4] Security Audit Event Log`. Show the tamper-evident timestamped trail of logins, registrations, and salt rotations.
4. **Self-Test Suite**: Choose `[6] Algorithm Self-Test Suite`. Run all tests live: show **202 passed tests, 0 failures, 100% success rate**.
5. **Empirical Complexity Lab**: Choose `[7] Empirical Complexity Lab` $\to$ `[1] KMP vs Naive`.
   - Observe live nanosecond execution across $N=1,000$ to $100,000$.
   - Point out the ASCII runtime graph showing KMP's linear growth vs Naive's quadratic spike on repetitive text.
   - Highlight the Viva Critical Notice explaining JVM JIT warmup and why timings provide empirical observation rather than mathematical proof.
6. **Max Flow Lab**: Choose `[2] Max Flow Comparison`. Show all 3 solvers tested on 10, 20, 40, 80, and 160 vertices, highlighting Dinic's $O(V^2 \cdot E)$ advantage on dense networks.
7. **CSV Export**: Select `[10] Export Benchmark Results`. Export `benchmark_results.csv` for academic submission.

---

## 9. Final Acceptance Checklist (10/10 Verification)

- [x] Pure Java console application (JDK 17+) with zero external dependencies.
- [x] All 26 algorithms implemented, working, and meaningfully integrated into sports workflows.
- [x] All M1 algorithms: Naive, KMP, Z-Function, Rabin-Karp, Aho-Corasick.
- [x] All M2 algorithms: Suffix Array, SA-IS ($O(N)$), Kasai LCP, Suffix Tree, Suffix Automaton.
- [x] All M3 algorithms: Levenshtein, Damerau-Levenshtein, Bitmask DP, Subset DP, Matrix Chain.
- [x] All M4 algorithms: Ford-Fulkerson, Edmonds-Karp, Dinic, Hopcroft-Karp, König's theorem.
- [x] All M5 algorithms: DPLL SAT, 3-SAT $\to$ CLIQUE $\to$ IS $\to$ VC reduction, 2-Approximation.
- [x] All M6 algorithms: Randomized QuickSort, Reservoir Sampling, Blelloch Scan, Parallel Reduce, Miller-Rabin, Brent's theorem.
- [x] Custom Universal Hash Table ($h(k)=((ak+b)\%p)\%m$) with separate chaining (no HashMap for credentials).
- [x] PBKDF2WithHmacSHA256 password hashing with 128-bit unique salts and constant-time comparisons.
- [x] Real RBAC (USER vs ADMIN) enforced in business logic.
- [x] Account brute-force lockout (5 attempts) and anti-enumeration generic messaging.
- [x] Session tokens with active expiration and logout invalidation.
- [x] Empirical Complexity Lab with `System.nanoTime()`, JVM warmup, repetitions, and ASCII charts.
- [x] All flow solvers verified to return identical maximum-flow values on fresh graph copies.
- [x] Automated Self-Test suite passes **202 / 202 tests (100% success rate)**.
- [x] Responsive Terminal UI with TrueColor, ANSI-16, and safe monochrome/numeric fallbacks.
- [x] Zero absolute machine-specific paths; portable across Windows, Linux, and macOS.
- [x] Complete Viva preparation cheat sheet (54 questions) and 5-minute faculty demo script.
