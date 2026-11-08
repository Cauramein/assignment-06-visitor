# Assignment 6: Visitor Pattern - Advanced File System

An in-memory hierarchical file system implementing the Visitor behavioral design pattern in Java 17.

---

## Features & Supported Operations

* **Tree Integrity & Invariants**: Enforces non-negative file sizes, prevents duplicate sibling names, avoids multiple parent ownership, and blocks cyclic directory references.
* **Double Dispatch**: Elements expose `accept(FileSystemVisitor)` and dispatch directly to specialized `visitFile` and `visitDirectory` methods without `instanceof` or type casts.
* **TotalSizeVisitor**: Recursively calculates the byte total of all files across nested directories.
* **SearchVisitor**: Filters elements matching a filename substring and an inclusive minimum byte size threshold, returning deterministic lexicographically sorted, root-relative paths (`/`).
* **StatisticsVisitor**: Collects total file count, directory count (including root), and maximum tree depth.

---

## Prerequisites

* **Java JDK**: version 17 or higher
* **Apache Maven**: version 3.8+

---

## Build & Execution Commands

### 1. Run Automated Test Suite (JUnit 5)
Executes all unit tests covering acceptance criteria T1 through T8:
```bash
mvn test
```
## 2. Package the Application
Compiles the project and runs the test suite:
```bash
mvn clean package
```
## 3. Run the Executable Demo
Compiles and runs the demonstration scenario in Main.java:
```bash
mvn compile exec:java -Dexec.mainClass="Main"
```

