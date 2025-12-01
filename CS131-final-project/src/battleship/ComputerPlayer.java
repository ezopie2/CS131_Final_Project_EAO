package battleship;
import java.util.*;

public class ComputerPlayer implements IPlayer {

    public Set<Pos> getTried() {
        return tried;
    }
	
	public enum Difficulty { EASY, MEDIUM, HARD }
    private final Difficulty difficulty;
    private final String name;
    private final Random rnd = new Random();

    //for medium hunt/target
    
    private final Set<Pos> tried = new HashSet<>();
    private final Deque<Pos> targetQueue = new ArrayDeque<>();
    public ComputerPlayer(String name, Difficulty difficulty) {
        this.name = name;
        this.difficulty = difficulty;
    }
  
        @Override
    public void placeShips(Board board) {
    	
    	// simple random placement with retry
    	
    	for (ShipType type : new ShipType[] {ShipType.Carrier, ShipType.Battleship, ShipType.Battleship, ShipType.Submarine, ShipType.Destroyer}) {
    		boolean placed = false;
    		int attempts = 0;
    		while (!placed && attempts < 1000) {
    			int r = rnd.nextInt(board.rows());
    			int c = rnd.nextInt(board.cols());
    			boolean horiz = rnd.nextBoolean();
    			placed = board.placeShip(type, new Pos(r,c), horiz);
    		}
    		if (!placed) throw new IllegalStateException("Failed to place shipe: " + type);
    		
    		}
    	}
    @Override
    public Pos makeMove(Board opponentBoard) {
        if (difficulty == Difficulty.EASY) return easyMove(opponentBoard);
        if (difficulty == Difficulty.MEDIUM) return mediumMove(opponentBoard);
        return hardMove(opponentBoard);
    }

    private Pos easyMove(Board b) {
        while (true) {
            Pos p = new Pos(rnd.nextInt(b.rows()), rnd.nextInt(b.cols()));
            if (!alreadyTried(p, b)) return p;
        }
    }

    private Pos mediumMove(Board b) {
        // if have queued target cells (after a hit), pop them
        while (!targetQueue.isEmpty()) {
            Pos p = targetQueue.poll();
            if (b.inBounds(p) && !alreadyTried(p, b)) return p;
        }
        // otherwise random until hit
        for (int tries = 0; tries < 10000; tries++) {
            Pos p = new Pos(rnd.nextInt(b.rows()), rnd.nextInt(b.cols()));
            if (!alreadyTried(p, b)) return p;
        }
        // fallback
        return easyMove(b);
    }

    private Pos hardMove(Board b) {
        // very simple probability density (checker board pattern)
        for (int r = 0; r < b.rows(); r++)
            for (int c = 0; c < b.cols(); c++) {
                Pos p = new Pos(r, c);
                if (!alreadyTried(p, b) && (r + c) % 2 == 0) return p;
            }
        return mediumMove(b);
    }

    public void informResult(Pos p, FireResult res) {
        // computer can use result to change strategy (medium)
        if (difficulty == Difficulty.MEDIUM) {
            tried.add(p);
            if (res.hit && !res.sunk) {
                // enqueue neighbors
                int[][] deltas = {{-1,0},{1,0},{0,-1},{0,1}};
                for (int[] d : deltas) targetQueue.add(new Pos(p.row + d[0], p.col + d[1]));
            } else if (res.sunk) {
                // clear targetQueue as ship is sunk
                targetQueue.clear();
            }
        } else {
            tried.add(p);
        }
    }

    private boolean alreadyTried(Pos p, Board b) {
        CellState s = (CellState) b.cellState(p);
        return s == CellState.HIT || s == CellState.MISS;
    }

    @Override
    public String name() { return name + " (CPU-" + difficulty.name() + ")"; }
}