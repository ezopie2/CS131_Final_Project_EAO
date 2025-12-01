package battleship;
import java.util.Scanner;

public class Main {
    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) {
        System.out.println("Battleship (Console) - CS Project");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Board size selection (default 8x8). Press Enter to continue:");
        String sizeLine = scanner.nextLine().trim();
        int rows = 8, cols = 8;
        if (!sizeLine.isEmpty()) {
            String[] parts = sizeLine.split("\\s+");
            try {
                rows = Integer.parseInt(parts[0]);
                cols = Integer.parseInt(parts[1]);
            } catch (Exception ignored) { System.out.println("Using default 8x8."); }
        }

        System.out.println("Your game mode is human vs computer." + " Press enter to continue: ");
        int mode = 1;
        try {
            String m = scanner.nextLine().trim();
            if (!m.isEmpty()) mode = Integer.parseInt(m);
        } catch (Exception ignored) {}

        IPlayer p1, p2;
        switch (mode) {
            case 2 -> {
                System.out.print("Enter name for Player 1: ");
                String n1 = scanner.nextLine().trim();
                p1 = new HumanPlayerConsole(n1.isEmpty() ? "Player1" : n1, scanner);
                System.out.print("Enter name for Player 2: ");
                String n2 = scanner.nextLine().trim();
                p2 = new HumanPlayerConsole(n2.isEmpty() ? "Player2" : n2, scanner);
            }
            case 3 -> {
                p1 = new ComputerPlayer("CPU-1", ComputerPlayer.Difficulty.HARD);
                p2 = new ComputerPlayer("CPU-2", ComputerPlayer.Difficulty.MEDIUM);
            }
            default -> {
                System.out.print("Enter your name: ");
                String name = scanner.nextLine().trim();
                p1 = new HumanPlayerConsole(name.isEmpty() ? "Player" : name, scanner);
                System.out.println("Choose computer difficulty: 1) EASY 2) MEDIUM 3) HARD (default MEDIUM)");
                int d = 2;
                try {
                    String ds = scanner.nextLine().trim();
                    if (!ds.isEmpty()) d = Integer.parseInt(ds);
                } catch (Exception ignored) {}
                ComputerPlayer.Difficulty diff = ComputerPlayer.Difficulty.MEDIUM;
                if (d == 1) diff = ComputerPlayer.Difficulty.EASY;
                if (d == 3) diff = ComputerPlayer.Difficulty.HARD;
                p2 = new ComputerPlayer("Computer", diff);
            }
        }

        GameController controller = new GameController(rows, cols, p1, p2, scanner);
        controller.setupAndPlay();
        scanner.close();
    }
}

