import os
import subprocess

content = '''**Name:** Augustina Momoh  
**Email:** augustina.momoh@miva.edu.ng  
**Department:** Cybersecurity  
**Matric No:** 2024/C/CYB/1174  
**Phone/ID:** 301757220  
**Course Code:** COS 202  

# TINA\\'S TRAVEL LIBRARY SYSTEM REPORT

## 1. Project Overview
I built "Tina\\'s Library" as a desktop application to help track travel resources, manage how travelers rent them, and provide an overview of the system\\'s inventory. I used Java and organized the code into clean modules: `model`, `controller`, `gui`, and `utils`. To make it work like a real-world tool, I applied object-oriented principles, built a modern dashboard layout, and implemented recursive algorithms to satisfy the project\\'s requirements.

## 2. Key Features I Implemented
*   **Flexible Asset Catalog:** I set up the system to handle different types of travel media—like Travel Guides, Maps, and Magazines. They all share a core `TravelResource` abstract base class.
*   **Borrowing (Rentals):** I built automated workflows for checking items in and out to specific Travelers via the Rentals dashboard.
*   **Undo Action:** I added a "history stack" so if an admin makes a mistake (like accidentally adding or deleting a resource), they can easily undo their last change.
*   **Recursive Operations:** I implemented recursive algorithms to tally total resources by category and to compute overdue late fees dynamically.
*   **Modern Dashboard UI:** Instead of a basic tabbed window, I implemented a `BorderLayout` with a sleek side navigation menu and a `CardLayout` to switch between Overview, Inventory, and Rentals panels seamlessly.

### Application Screenshots
![Overview Dashboard](insert_screenshot_1_here.png)
![Inventory Management](insert_screenshot_2_here.png)
![Rentals & Travelers](insert_screenshot_3_here.png)

## 3. Data Structures Utilized & Justification

| Data Structure | Component Location | Technical Justification |
| :--- | :--- | :--- |
| **ArrayList** | `TravelLibrarySystem.java` | I chose this for the main inventory and traveler lists because it\\'s incredibly fast for reading data. Since library users spend most of their time browsing lists, the `ArrayList` makes the experience feel smooth and responsive. |
| **Stack** | `TravelLibrarySystem.java` | This is perfect for the "undo" feature. By tracking actions (`UndoCommand`) in a stack, I can just "pop" the most recent one off the top to reverse it. |
| **Array** | `TravelLibrarySystem.java` | Used specifically for the `accessCache` to maintain a fixed-size (10) history of recently accessed items, ensuring O(1) space complexity for the recent view feature. |

## 4. Algorithms Chosen & Analysis

**Recursion Logic**
*   **Category Counting:** I implemented a recursive function `countCategoryRecursive` to iterate through the inventory and count items matching a specific type.
*   **Late Fee Computation:** I implemented `computeLateFeesRecursive` to calculate fines on a per-day basis ($1.50/day) recursively, breaking the problem down day-by-day.

## 5. Challenges & How I Solved Them
*   **The Interface Navigation Problem:** Managing multiple screens without opening new windows was tricky.
    *   *My Solution:* I used Java Swing\\'s `CardLayout` in the `MainDashboard` class. This allowed me to keep a static side-navigation bar while dynamically swapping out the main content panels (Overview, Inventory, Rentals) smoothly.
*   **The Inheritance Mapping Problem:** Differentiating the attributes of different travel items cleanly.
    *   *My Solution:* I used polymorphic abstract methods like `getExtraLabel1()` and `getExtraAttribute1()` in the `TravelResource` base class. This allowed the GUI table to dynamically pull the right properties whether the item was a Guide (Country/Audience) or a Map (Region/Scale) without writing messy if/else type-checks.

---

## Appendix: Full Source Code
'''

try:
    with open('all_code.txt', 'w', encoding='utf-8') as out_f:
        for root, dirs, files in os.walk('src_v2'):
            for file in files:
                if file.endswith('.java'):
                    filepath = os.path.join(root, file)
                    out_f.write(f'\\n### {filepath}\\n')
                    out_f.write('```java\\n')
                    with open(filepath, 'r', encoding='utf-8') as in_f:
                        out_f.write(in_f.read())
                    out_f.write('\\n```\\n')
except Exception as e:
    print(f"Error reading java files: {e}")

try:
    with open('all_code.txt', 'r', encoding='utf-8') as f:
        all_code = f.read()
except:
    all_code = 'Error loading all_code.txt'

content += '\\n' + all_code

with open('Augustina_Momoh_COS202_Report.md', 'w', encoding='utf-8') as f:
    f.write(content)

print("Report generated successfully for v2.")
