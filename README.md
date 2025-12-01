# CS131_Final_Project_EAO
---
## Description
This is a console-based implementation of the classic **Battleship** game in Java. Players can compete against a computer AI or another human player.  
Demonstrates object-oriented programming, interfaces, polymorphism, and exception handling.

---

## Features
- Supports **Human vs Computer**, **Human vs Human**, and **Computer vs Computer** modes  
- Customizable board size (default 8x8)  
- Ships: Carrier (5), Battleship (4), Cruiser (3), Submarine (3), Destroyer (2)  
- Computer AI difficulty levels:  
  - **Easy**: random moves  
  - **Medium**: hunt & target after hits  
  - **Hard**: checkerboard targeting  
- Tracks hits, misses, and sunk ships  
- Prevents invalid moves, overlapping ships, and out-of-bounds placement  
- Console display of boards and moves  

---

## Getting Started

### Requirements
- Java Development Kit (JDK 8+)  
- Terminal or console  

### Running the Game
1. Clone the repository or download the source code  
2. Compile all `.java` files:
```bash
javac battleship/*.java

