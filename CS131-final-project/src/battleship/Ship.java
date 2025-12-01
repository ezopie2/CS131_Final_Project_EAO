package battleship;

import java.util.*;

public class Ship {
	
    public final ShipType type;
    private final List<Pos> positions;
    private final Set<Pos> hits = new HashSet<>();


    Ship(ShipType type, List<Pos> pos) {
        this.type = Objects.requireNonNull(type, "type");
        Objects.requireNonNull(pos, "pos");
        this.positions = Collections.unmodifiableList(new ArrayList<>(pos));
    }

    public List<Pos> getPositions() { return positions; }

    public boolean occupies(Pos p) { return positions.contains(p); }

    public void registerHit(Pos p) {
        if (occupies(p)) hits.add(p);
    }

    public boolean isSunk() { return hits.size() >= positions.size(); }

    @Override
    public String toString() {
        return type.name() + positions.toString() + (isSunk() ? " [SUNK]" : "");
    }
}
