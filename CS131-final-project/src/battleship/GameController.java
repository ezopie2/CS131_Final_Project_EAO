package battleship;

import java.util.*;

public class GameController {
    private final Board playerBoard;
    private final Board computerBoard;
    private final IPlayer player;
    private final IPlayer opponent;
    private final Scanner scanner;

    public GameController(int rows, int cols, IPlayer p1, IPlayer p2, Scanner scanner) {
        this.playerBoard = new Board(rows, cols);
        this.computerBoard = new Board(rows, cols);
        this.player = p1;
        this.opponent = p2;
        this.scanner = scanner;
    }

    public void setupAndPlay() {
        System.out.println("\n--- Battleship Setup ---");

        // Place ships for both players
        System.out.println("\nPlacing your ships:");
        placeShips(playerBoard, player, true);

        System.out.println("\nPlacing opponent ships...");
        placeShips(computerBoard, opponent, false);

        System.out.println("\nAll ships placed. Let the battle begin!\n");
        playGame();
    }

    // Handles ship placement
    private void placeShips(Board board, IPlayer player, boolean showBoard) {
        for (ShipType type : ShipType.values()) {
            boolean placed = false;
            while (!placed) {
                if (player instanceof HumanPlayerConsole human) {
                    System.out.println(board.toDisplayString(showBoard));
                    System.out.printf("Place %s (size %d). Enter row col orientation(H/V): ",
                            type.name(), type.size);
                    String line = scanner.nextLine().trim();
                    try {
                        String[] parts = line.split("\\s+");
                        int row = Integer.parseInt(parts[0]);
                        int col = Integer.parseInt(parts[1]);
                        boolean horizontal = parts[2].equalsIgnoreCase("H");
                        placed = board.placeShip(type, new Pos(row, col), horizontal);
                        if (!placed)
                            System.out.println("❌ Invalid placement, try again.");
                    } catch (Exception e) {
                        System.out.println("❌ Format: row col H/V (example: 2 3 H)");
                    }
                } else {
                    // Computer places ships randomly
                    Random rand = new Random();
                    int row = rand.nextInt(board.rows());
                    int col = rand.nextInt(board.cols());
                    boolean horizontal = rand.nextBoolean();
                    placed = board.placeShip(type, new Pos(row, col), horizontal);
                }
            }
        }
    }

    // Main game loop
    private void playGame() {
        while (true) {
            // Show both boards (computer ships hidden)
            System.out.println("\nYour Board:");
            System.out.println(playerBoard.toDisplayString(true));

            System.out.println("Enemy Board:");
            System.out.println(computerBoard.toDisplayString(false)); // hides computer ships

            // Player fires
            Pos target = getPlayerTarget();
            FireResult result = computerBoard.fireAt(target);
            System.out.println(result);

            if (computerBoard.allShipsSunk()) {
                System.out.println("\n🎉 You win! You sank all enemy ships!");
                break;
            }

            // Computer's turn
            System.out.println("\nComputer's turn...");
            Pos compTarget = randomTarget();
            System.out.println("Computer fires at " + compTarget);
            FireResult compResult = playerBoard.fireAt(compTarget);
            System.out.println(compResult);

            if (playerBoard.allShipsSunk()) {
                System.out.println("\n💥 You lose! The computer sank all your ships.");
                break;
            }
        }
    }

    private Pos getPlayerTarget() {
        while (true) {
            System.out.print("Enter target row col: ");
            String line = scanner.nextLine().trim();
            try {
                String[] parts = line.split("\\s+");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                return new Pos(row, col);
            } catch (Exception e) {
                System.out.println("❌ Invalid format. Example: 3 4");
            }
        }
    }

    private Pos randomTarget() {
        Random rand = new Random();
        int row = rand.nextInt(playerBoard.rows());
        int col = rand.nextInt(playerBoard.cols());
        return new Pos(row, col);
    }
}
