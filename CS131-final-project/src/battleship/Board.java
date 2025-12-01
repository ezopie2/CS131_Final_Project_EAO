package battleship;

import java.util.*;

public class Board {
    private final int rows;
    private final int cols;
    private final CellState[][] grid;
    private final List<Ship> ships = new ArrayList<>();

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new CellState[rows][cols];
        for (int r = 0; r < rows; r++)
            Arrays.fill(grid[r], CellState.EMPTY);
    }

    public int rows() { return rows; }
    public int cols() { return cols; }

    public boolean inBounds(Pos p) {
        return p.row >= 0 && p.row < rows && p.col >= 0 && p.col < cols;
    }

    public boolean placeShip(ShipType type, Pos start, boolean horizontal) {
        List<Pos> pos = new ArrayList<>();
        for (int i = 0; i < type.size; i++) {
            Pos p = horizontal ? new Pos(start.row, start.col + i) : new Pos(start.row + i, start.col);
            if (!inBounds(p)) return false;
            if (grid[p.row][p.col] != CellState.EMPTY) return false; // avoid overlap
            pos.add(p);
        }
        for (Pos p : pos) grid[p.row][p.col] = CellState.SHIP;
        @SuppressWarnings("UnnecessaryLocalVariable")
         Ship newShip = new Ship(type, pos);
        ships.add(newShip);
        return true;
    }

    public FireResult fireAt(Pos p) {
        if (!inBounds(p)) throw new IllegalArgumentException("Out of bounds: " + p);
        CellState state = grid[p.row][p.col];
        if (state == CellState.HIT || state == CellState.MISS) return FireResult.already();

        if (state == CellState.SHIP) {
            for (Ship s : ships) {
                if (s.occupies(p)) {
                    s.registerHit(p);
                    grid[p.row][p.col] = CellState.HIT;
                    if (s.isSunk()) return FireResult.sunk(s.type);
                    return FireResult.hit();
                }
            }
        }
        grid[p.row][p.col] = CellState.MISS;
        return FireResult.miss();
    }

    public boolean allShipsSunk() {
        for (Ship s : ships) if (!s.isSunk()) return false;
        return true;
    }

    public CellState cellState(Pos p) {
        if (!inBounds(p)) throw new IllegalArgumentException("Out of bounds: " + p);
        return grid[p.row][p.col];
    }

    public List<Ship> getShips() { return Collections.unmodifiableList(ships); }


    public String toDisplayString(boolean showShips) {
    StringBuilder sb = new StringBuilder();
    sb.append("   ");
    for (int c = 0; c < cols; c++) sb.append(String.format("%2d ", c));
    sb.append("\n");

    for (int r = 0; r < rows; r++) {
        sb.append(String.format("%2d ", r));
        for (int c = 0; c < cols; c++) {
            CellState s = grid[r][c];
            char ch;

            if (null == s) ch = '?';
            else             // only show ships if "showShips" is true
                ch = switch (s) {
                case EMPTY -> '.';
                case SHIP -> showShips ? 'S' : '.';
                case MISS -> 'o';
                case HIT -> 'X';
                default -> '?';
            }; // hide ships as dots
            

            sb.append(" ").append(ch).append(" ");
        }
        sb.append("\n");
    }
    return sb.toString();
}

}
