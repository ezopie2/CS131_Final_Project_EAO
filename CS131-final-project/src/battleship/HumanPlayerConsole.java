package battleship;

import java.util.*;

public class HumanPlayerConsole implements IPlayer {
    private final Scanner scanner;
    private final String playerName;

    public HumanPlayerConsole(String name, Scanner scanner) {
        this.playerName = name;
        this.scanner = scanner;
    }

    @Override
    public void placeShips(Board board) {
        System.out.println(playerName + ": place your ships.");
        for (ShipType type : new ShipType[]{ShipType.Carrier, ShipType.Battleship, ShipType.Cruiser, ShipType.Submarine, ShipType.Destroyer}) {
            boolean placed = false;
            while (!placed) {
                System.out.println("Board:");
                System.out.println(board.toDisplayString(true));
                System.out.printf("Place %s (size %d). Enter start row col and orientation (h or v). Example: 0 0 h%n", type.name(), type.size);
                System.out.print("> ");
                String line = scanner.nextLine().trim();
                String[] parts = line.split("\\s+");
                if (parts.length < 3) { System.out.println("Invalid input. Try again."); continue; }
                try {
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
                    String orient = parts[2].toLowerCase();
                    boolean horiz = orient.startsWith("h");
                    if (board.placeShip(type, new Pos(r, c), horiz)) {
                        placed = true;
                    } else {
                        System.out.println("Invalid placement (collision/out-of-bounds). Try again.");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Bad numbers. Try again.");
                }
            }
        }
    }

    @Override
    public Pos makeMove(Board opponentBoard) {
        while (true) {
            System.out.println("Opponent board (your hits/misses so far):");
            System.out.println(opponentBoard.toDisplayString(false));
            System.out.println(playerName + ", enter row and col to fire at (e.g., 0 0):");
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            String[] parts = line.split("\\s+");
            if (parts.length < 2) { System.out.println("Invalid input."); continue; }
            try {
                int r = Integer.parseInt(parts[0]);
                int c = Integer.parseInt(parts[1]);
                Pos p = new Pos(r, c);
                if (!opponentBoard.inBounds(p)) {
                    System.out.println("Out of bounds. Try again.");
                    continue;
                }
                // don't check already tried here; controller will handle
                return p;
            } catch (NumberFormatException ex) {
                System.out.println("Bad numbers. Try again.");
            }
        }
    }

    @Override
    public String name() { return playerName; }
}
