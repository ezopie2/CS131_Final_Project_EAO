package battleship;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Font;

public class BattleshipGUI {
    private static final int SIZE = 8;
    private static final Color SUNK_RED = new Color(139, 0, 0);

    private Board playerBoard;
    private Board computerBoard;

    private final JButton[][] playerButtons = new JButton[SIZE][SIZE];
    private final JButton[][] computerButtons = new JButton[SIZE][SIZE];
    private final JLabel status = new JLabel("Welcome! Place your ships to begin.");
    private final JButton orientToggle = new JButton("Orientation: Horizontal");

    private boolean setupPhase = true;
    private boolean horizontal = true;
    private final ShipType[] shipsToPlace = {
        ShipType.Carrier, ShipType.Battleship, ShipType.Cruiser,
        ShipType.Submarine, ShipType.Destroyer
    };
    private int currentShipIndex = 0;
    private boolean playerTurn = true;

    public enum Difficulty { EASY, MEDIUM, HARD }
    private Difficulty difficulty = Difficulty.MEDIUM;

    private final Random rng = new Random();

    public BattleshipGUI() {
        initGame();

        JFrame frame = new JFrame("Battleship");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 770);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(15, 15, 15, 15));
        frame.setContentPane(root);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> resetGame());
        gameMenu.add(newGameItem);

        JMenu diffMenu = new JMenu("Difficulty");
        ButtonGroup diffGroup = new ButtonGroup();
        for (Difficulty d : Difficulty.values()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(d.name());
            if (d == difficulty) item.setSelected(true);
            item.addActionListener(ev -> setDifficulty(d));
            diffGroup.add(item);
            diffMenu.add(item);
        }
        menuBar.add(gameMenu);
        menuBar.add(diffMenu);
        frame.setJMenuBar(menuBar);

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        orientToggle.addActionListener(e -> toggleOrientation());
        topBar.add(orientToggle);
        JLabel hint = new JLabel("Place ships on Your Board. Hover preview; click to place. Then fire on CPU Board.");
        topBar.add(hint);
        root.add(topBar, BorderLayout.NORTH);

        // Boards row
        JPanel boardsRow = new JPanel();
        boardsRow.setLayout(new BoxLayout(boardsRow, BoxLayout.X_AXIS));

        // Player board
        JPanel playerWrapper = new JPanel(new BorderLayout());
        JLabel playerTitle = new JLabel("Your Board", SwingConstants.CENTER);
        playerTitle.setFont(playerTitle.getFont().deriveFont(Font.BOLD, 16f));
        playerWrapper.add(playerTitle, BorderLayout.NORTH);
        JPanel playerPanel = new JPanel(new GridLayout(SIZE, SIZE, 3, 3));
        playerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        initPlayerGrid(playerPanel);
        playerWrapper.add(playerPanel, BorderLayout.CENTER);

        boardsRow.add(playerWrapper);
        boardsRow.add(Box.createRigidArea(new Dimension(60, 0)));

        // CPU board
        JPanel cpuWrapper = new JPanel(new BorderLayout());
        JLabel cpuTitle = new JLabel("CPU Board", SwingConstants.CENTER);
        cpuTitle.setFont(cpuTitle.getFont().deriveFont(Font.BOLD, 16f));
        cpuWrapper.add(cpuTitle, BorderLayout.NORTH);
        JPanel cpuPanel = new JPanel(new GridLayout(SIZE, SIZE, 3, 3));
        cpuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        initCpuGrid(cpuPanel);
        cpuWrapper.add(cpuPanel, BorderLayout.CENTER);

        boardsRow.add(cpuWrapper);

        root.add(boardsRow, BorderLayout.CENTER);

        // Status bar
        status.setFont(status.getFont().deriveFont(Font.PLAIN, 14f));
        root.add(status, BorderLayout.SOUTH);

        updatePlacementPrompt();
        frame.setVisible(true);
    }

    // --- Game setup/reset ---
    private void initGame() {
        playerBoard = new Board(SIZE, SIZE);
        computerBoard = new Board(SIZE, SIZE);
        setupPhase = true;
        currentShipIndex = 0;
        playerTurn = true;
    }

    private void resetGame() {
        initGame();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JButton pb = playerButtons[r][c];
                JButton cb = computerButtons[r][c];
                if (pb != null) {
                    pb.setText("");
                    pb.setBackground(null);
                    pb.setOpaque(false);
                    pb.setContentAreaFilled(false);
                    pb.setEnabled(true);
                }
                if (cb != null) {
                    cb.setText("");
                    cb.setBackground(null);
                    cb.setOpaque(false);
                    cb.setContentAreaFilled(false);
                    cb.setEnabled(true);
                }
            }
        }
        orientToggle.setEnabled(true);
        status.setText("New game started! Place your ships.");
        updatePlacementPrompt();
    }

    private void setDifficulty(Difficulty d) {
        difficulty = d;
        status.setText("Difficulty set to " + d.name() + ". Start a new game to apply.");
    }

    // --- Player board setup ---
    private void initPlayerGrid(JPanel container) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JButton btn = new JButton();
                playerButtons[r][c] = btn;
                final int row = r, col = c;
                btn.addActionListener(e -> {
                    if (setupPhase) placeShipAt(row, col, horizontal);
                });
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (setupPhase) previewPlacement(row, col, horizontal);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (setupPhase) clearPreview();
                    }
                });
                container.add(btn);
            }
        }
    }

    private void initCpuGrid(JPanel container) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JButton btn = new JButton();
                computerButtons[r][c] = btn;
                final int row = r, col = c;
                btn.addActionListener(e -> {
                    if (!setupPhase && playerTurn) handlePlayerFire(row, col);
                });
                container.add(btn);
            }
        }
    }

    private void toggleOrientation() {
        horizontal = !horizontal;
        orientToggle.setText("Orientation: " + (horizontal ? "Horizontal" : "Vertical"));
        updatePlacementPrompt();
    }

    private void updatePlacementPrompt() {
        if (setupPhase) {
            ShipType next = shipsToPlace[currentShipIndex];
            status.setText("Place " + next.name() + " (size " + next.size + ") - " +
                           (horizontal ? "Horizontal" : "Vertical"));
        }
    }

    // --- Placement preview ---
    private void previewPlacement(int row, int col, boolean horiz) {
        clearPreview();
        ShipType type = shipsToPlace[currentShipIndex];
        boolean valid = canPlace(type, row, col, horiz);
        Color color = valid ? new Color(0, 180, 0) : new Color(200, 50, 50);
        for (int i = 0; i < type.size; i++) {
            int rr = row + (horiz ? 0 : i);
            int cc = col + (horiz ? i : 0);
            if (inBounds(rr, cc)) {
                JButton btn = playerButtons[rr][cc];
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
                btn.setBackground(color);
            }
        }
    }

    private void clearPreview() {
        Color green = new Color(0, 180, 0);
        Color red = new Color(200, 50, 50);
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JButton btn = playerButtons[r][c];
                Color bg = btn.getBackground();
                if (bg != null && (bg.equals(green) || bg.equals(red))) {
                    btn.setBackground(null);
                    btn.setOpaque(false);
                    btn.setContentAreaFilled(false);
                }
            }
        }
    }

    // --- Ship placement ---
    private void placeShipAt(int row, int col, boolean horiz) {
        ShipType type = shipsToPlace[currentShipIndex];
        boolean placed = playerBoard.placeShip(type, new Pos(row, col), horiz);
        if (!placed) {
            status.setText("Invalid placement (collision or out-of-bounds). Try again.");
            return;
        }
        applyShipStyle(type, row, col, horiz);

        currentShipIndex++;
        clearPreview();
        if (currentShipIndex >= shipsToPlace.length) {
            setupPhase = false;
            orientToggle.setEnabled(false);
            cpuPlaceShips();
            status.setText("All ships placed! Your turn. Click CPU Board to fire.");
        } else {
            updatePlacementPrompt();
        }
    }

    private void applyShipStyle(ShipType type, int row, int col, boolean horiz) {
        Color shipColor;
        String label;
        switch (type) {
            case Carrier -> {
                shipColor = Color.DARK_GRAY; label = "C";
            }
            case Battleship -> {
                shipColor = new Color(0, 160, 0); label = "B";
            }
            case Cruiser -> {
                shipColor = new Color(255, 140, 0); label = "Cr";
            }
            case Submarine -> {
                shipColor = new Color(180, 0, 180); label = "S";
            }
            case Destroyer -> {
                shipColor = new Color(0, 170, 170); label = "D";
            }
            default -> {
                shipColor = Color.GRAY; label = "";
            }
        }
        for (int i = 0; i < type.size; i++) {
            int rr = row + (horiz ? 0 : i);
            int cc = col + (horiz ? i : 0);
            if (inBounds(rr, cc)) {
                JButton btn = playerButtons[rr][cc];
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
                btn.setBackground(shipColor);
                btn.setText(label);
                btn.setForeground(Color.BLACK);
                btn.setFont(btn.getFont().deriveFont(java.awt.Font.BOLD, 12f));
            }
        }
    }

    // --- Firing & turns ---
    private void handlePlayerFire(int row, int col) {
        Pos target = new Pos(row, col);
        FireResult result = computerBoard.fireAt(target);
        if (result.alreadyTried) {
            status.setText("Already fired at " + target + ". Try a different cell.");
            return;
        }

        markShot(computerButtons[row][col], result);
        computerButtons[row][col].setEnabled(false);

        if (result.sunk) {
            status.setText("You fired at " + target + ": SUNK " + result.sunkType.name());
            highlightSunkShip(computerBoard, computerButtons, result.sunkType);
        } else {
            status.setText("You fired at " + target + ": " + result);
        }

        if (computerBoard.allShipsSunk()) {
            status.setText("You win! All enemy ships sunk!");
            disableBoard(computerButtons);
            return;
        }

        playerTurn = false;
        handleCpuFire();
        playerTurn = true;
    }

    private void handleCpuFire() {
        Pos cpuTarget = cpuPickTarget(playerBoard);
        FireResult cpuResult = playerBoard.fireAt(cpuTarget);

        JButton btn = playerButtons[cpuTarget.row][cpuTarget.col];
        markShot(btn, cpuResult);

        if (cpuResult.sunk) {
            status.setText("CPU fired at " + cpuTarget + ": SUNK " + cpuResult.sunkType.name());
            highlightSunkShip(playerBoard, playerButtons, cpuResult.sunkType);
        } else {
            status.setText("CPU fired at " + cpuTarget + ": " + cpuResult);
        }

        if (playerBoard.allShipsSunk()) {
            status.setText("You lose! CPU sank all your ships.");
            disableBoard(computerButtons);
        }
    }

    private void markShot(JButton btn, FireResult result) {
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFont(btn.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        if (result.hit) {
            btn.setText("X");
            btn.setForeground(Color.RED);
            btn.setBackground(Color.BLACK);
        } else {
            btn.setText("O");
            btn.setForeground(Color.BLUE);
            btn.setBackground(Color.WHITE);
        }
    }

    private void highlightSunkShip(Board board, JButton[][] grid, ShipType type) {
        for (Ship ship : board.getShips()) {
            if (ship.type == type && ship.isSunk()) {
                for (Pos p : ship.getPositions()) {
                    JButton b = grid[p.row][p.col];
                    b.setOpaque(true);
                    b.setContentAreaFilled(true);
                    b.setText("X");
                    b.setForeground(Color.WHITE);
                    b.setBackground(SUNK_RED);
                    b.setFont(b.getFont().deriveFont(java.awt.Font.BOLD, 16f));
                    b.setEnabled(false);
                }
            }
        }
    }

    private void disableBoard(JButton[][] board) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                board[r][c].setEnabled(false);
            }
        }
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < SIZE && c >= 0 && c < SIZE;
    }

    private boolean canPlace(ShipType type, int row, int col, boolean horiz) {
        int endRow = row + (horiz ? 0 : type.size - 1);
        int endCol = col + (horiz ? type.size - 1 : 0);
        return inBounds(endRow, endCol);
    }

    // --- CPU placement ---
    private void cpuPlaceShips() {
        for (ShipType type : shipsToPlace) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 300) {
                boolean horiz = rng.nextBoolean();
                int row = rng.nextInt(SIZE);
                int col = rng.nextInt(SIZE);
                placed = computerBoard.placeShip(type, new Pos(row, col), horiz);
                attempts++;
            }
            if (!placed) {
                outer:
                for (int r = 0; r < SIZE; r++) {
                    for (int c = 0; c < SIZE; c++) {
                        if (computerBoard.placeShip(type, new Pos(r, c), true)) { placed = true; break outer; }
                        if (computerBoard.placeShip(type, new Pos(r, c), false)) { placed = true; break outer; }
                    }
                }
            }
        }
    }

    // --- CPU targeting with difficulty ---
    private Pos cpuPickTarget(Board board) {
        return switch (difficulty) {
            case EASY -> cpuPickTargetEasy(board);
            case MEDIUM -> cpuPickTargetMedium(board);
            case HARD -> cpuPickTargetHard(board);
            default -> cpuPickTargetEasy(board);
        };
    }

    private Pos cpuPickTargetEasy(Board board) {
        @SuppressWarnings("Convert2Diamond")
        List<Pos> candidates = new ArrayList<Pos>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Pos p = new Pos(r, c);
                CellState s = board.cellState(p);
                if (s != CellState.HIT && s != CellState.MISS) {
                    candidates.add(p);
                }
            }
        }
        return candidates.get(rng.nextInt(candidates.size()));
    }

    private Pos cpuPickTargetMedium(Board board) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Pos p = new Pos(r, c);
                if (board.cellState(p) == CellState.HIT) {
                    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
                    for (int[] d : dirs) {
                        Pos n = new Pos(r + d[0], c + d[1]);
                        if (board.inBounds(n)) {
                            CellState s = board.cellState(n);
                            if (s != CellState.HIT && s != CellState.MISS) {
                                return n;
                            }
                        }
                    }
                }
            }
        }
        return cpuPickTargetEasy(board);
    }

    private Pos cpuPickTargetHard(Board board) {
        Pos hunt = tryHuntAdjacent(board);
        if (hunt != null) return hunt;

        @SuppressWarnings("Convert2Diamond")
        List<Pos> candidates = new ArrayList<Pos>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if ((r + c) % 2 == 0) {
                    Pos p = new Pos(r, c);
                    CellState s = board.cellState(p);
                    if (s != CellState.HIT && s != CellState.MISS) {
                        candidates.add(p);
                    }
                }
            }
        }
        if (!candidates.isEmpty()) {
            return candidates.get(rng.nextInt(candidates.size()));
        }
        return cpuPickTargetEasy(board);
    }

    private Pos tryHuntAdjacent(Board board) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Pos p = new Pos(r, c);
                if (board.cellState(p) == CellState.HIT) {
                    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
                    for (int[] d : dirs) {
                        Pos n = new Pos(r + d[0], c + d[1]);
                        if (board.inBounds(n)) {
                            CellState s = board.cellState(n);
                            if (s != CellState.HIT && s != CellState.MISS) {
                                return n;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BattleshipGUI::new);
    }
}