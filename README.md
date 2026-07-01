# SLCAS — Smart Library Circulation & Automation System (C++)

This is the C++ implementation of the **Smart Library Circulation & Automation System (SLCAS)** for **COS 202 (Computer Programming II)** at **MIVA Open University**. 

This project is fully designed to be compiled in **JetBrains CLion** using **CMake** and run inside console-based environments (including automated **Code Grader** sandboxes).

---

## 📂 Project Structure

The codebase is organized into modules mimicking standard packages:
* **`/src/model`** (OOP & Data Classes)
  * [Borrowable.h](src/model/Borrowable.h) — Pure virtual interface defining borrow/return contract.
  * [LibraryItem.h](src/model/LibraryItem.h) / [.cpp](src/model/LibraryItem.cpp) — Abstract base class representing generic library items.
  * [Book.h](src/model/Book.h) / [.cpp](src/model/Book.cpp) — Book subclass.
  * [Magazine.h](src/model/Magazine.h) / [.cpp](src/model/Magazine.cpp) — Magazine subclass.
  * [Journal.h](src/model/Journal.h) / [.cpp](src/model/Journal.cpp) — Academic Journal subclass.
  * [Patron.h](src/model/Patron.h) / [.cpp](src/model/Patron.cpp) — Class representing users, managing history and active loans.
* **`/src/controller`** (Business Logic & Algorithms)
  * [BorrowController.h](src/controller/BorrowController.h) / [.cpp](src/controller/BorrowController.cpp) — Handles transactions and maintains the waitlist queue.
  * [SearchEngine.h](src/controller/SearchEngine.h) / [.cpp](src/controller/SearchEngine.cpp) — Linear, Binary prefix, and Recursive searches.
  * [SortEngine.h](src/controller/SortEngine.h) / [.cpp](src/controller/SortEngine.cpp) — Selection, Insertion, Merge, and Quick Sort implementations.
  * [LibraryManager.h](src/controller/LibraryManager.h) / [.cpp](src/controller/LibraryManager.cpp) — Central coordinator managing vectors, undo stacks, and arrays.
* **`/src/utils`** (Helper Utilities)
  * [IDGenerator.h](src/utils/IDGenerator.h) / [.cpp](src/utils/IDGenerator.cpp) — Thread-safe formatted unique IDs.
  * [FileHandler.h](src/utils/FileHandler.h) / [.cpp](src/utils/FileHandler.cpp) — CSV file reading and writing.
* **`CMakeLists.txt`** — Compilation configuration for CLion / CMake.
* **`src/main.cpp`** — User CLI menu entrypoint.

---

## 🛠️ Step-by-Step Compilation & Setup (JetBrains CLion)

1. **Prerequisites:** Make sure you have a C++ compiler installed (MinGW-w64 for Windows, GCC/Clang for macOS/Linux).
2. **Open Project:**
   * Open JetBrains CLion.
   * Go to **File | Open** and select the root directory `Smart Lib/` (or the folder containing `CMakeLists.txt`).
   * CLion will automatically detect the `CMakeLists.txt` file and load the project.
3. **Configure Toolchain:**
   * Go to **File | Settings | Build, Execution, Deployment | Toolchains**.
   * Verify that MinGW (or another toolchain) is detected and set as default.
4. **Compile & Run:**
   * Click the **Build** hammer icon at the top toolbar, or press **Ctrl+F9** to compile.
   * Click the **Run** green play button, or press **Shift+F10** to execute the dashboard.

---

## ⚠️ Critical C++ Traps & Common Errors to Look Out For

During C++ development, several silent compiler traps and runtime errors occur. Below is a detailed list of what you must look out for and how the code resolves them.

### 1. The `std::cin` Newline Trapping Bug (Leftover buffer)
* **The Problem:** When you read an integer using `std::cin >> variable;`, the user presses Enter, leaving a `\n` character in the keyboard buffer. If you subsequently call `std::getline()`, it reads this trailing `\n` as an empty line immediately, skipping the prompt.
* **The Solution:** Always call `std::cin.ignore()` or clear the buffer before using `std::getline()`. In `main.cpp`, we call:
  ```cpp
  std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
  ```

### 2. The Infinite Input Loop (Fail State)
* **The Problem:** If you expect a numeric choice (`std::cin >> choice;`) but the user inputs letters (e.g. `"abc"`), `std::cin` enters a "fail state". All future input statements will be skipped instantly, resulting in an infinite printing loop.
* **The Solution:** Check for failure, clear the error flag, and flush the stream:
  ```cpp
  if (std::cin.fail()) {
      std::cin.clear(); // Clear error flags
      std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n'); // Discard bad inputs
  }
  ```

### 3. Windows Carriage Return (`\r`) parsing in File I/O
* **The Problem:** Windows text files use CRLF (`\r\n`) for newlines. When using `std::getline(stream, line)` to read file lines, the trailing `\r` (carriage return) remains at the end of the string. A check like `if (type == "Book")` will fail silently because the string is actually `"Book\r"`.
* **The Solution:** Trim the `\r` manually from the end of every line read from files:
  ```cpp
  if (!line.empty() && line.back() == '\r') {
      line.pop_back();
  }
  ```

### 4. Delimiter Collision inside CSV files
* **The Problem:** If a user enters a comma inside a book title (e.g. `"Design Patterns, Elements of Reusable Code"`), standard CSV splits will interpret this comma as a field separator, misaligning all subsequent columns on load.
* **The Solution:** During serialization, we substitute all commas (`,`) with semicolons (`;`). On loading, we restore them back to commas using the helper `_restoreCommas()`.

### 5. Memory Leakage (Pointer Ownership)
* **The Problem:** In C++, instances created using `new` (like polymorphic subclasses `new Book(...)` or `new Patron(...)`) live on the heap. If they are erased from vectors or the program terminates, their memory remains occupied, causing leaks.
* **The Solution:**
  * **Destructor Cleanup:** `~LibraryManager` contains a `clearAllData()` routine deleting every allocated pointer.
  * **Undo Ownership:** When an item is deleted, it is removed from the vector but kept in the `std::stack<UndoAction>` to support recovery. If the stack is cleared or the manager terminates, the orphaned pointer is safely `deleted`.

---

## 📈 Verifying Requirements & Outcomes
* **Inheritance & Abstraction:** `LibraryItem` is derived from pure interface `Borrowable` and contains virtual methods.
* **Polymorphism:** The table drawer calls `item->getType()` and `item->getInfo()`, invoking the subclass overrides dynamically.
* **Search/Sort Engines:** Four sorts and three searches are implemented using pure arrays/vectors without standard library sorting algorithms.
* **Recursive Routines:** Category counting (`_countCategoriesRecursive`) and overdue charges (`_computeCharge`) are computed recursively.
* **Queues & Stacks:** Waitlist is a FIFO `std::queue` (dequeues on return); Undo stack is a LIFO `std::stack`.
