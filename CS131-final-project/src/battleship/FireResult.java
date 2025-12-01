package battleship;

public class FireResult {
    public final boolean hit;
    public final boolean sunk;
    public final ShipType sunkType;
    public final boolean alreadyTried;
    public Object sunkShip;

    private FireResult(boolean hit, boolean sunk, ShipType sunkType, boolean alreadyTried) {
        this.hit = hit;
        this.sunk = sunk;
        this.sunkType = sunkType;
        this.alreadyTried = alreadyTried;
    }

    public static FireResult miss() { return new FireResult(false, false, null, false); }
    public static FireResult hit()  { return new FireResult(true, false, null, false); }
    public static FireResult sunk(ShipType type) { return new FireResult(true, true, type, false); }
    public static FireResult already() { return new FireResult(false, false, null, true); }

    @Override
    public String toString() {
        if (alreadyTried) return "Already tried";
        if (sunk) return "SUNK " + sunkType.name();
        if (hit) return "HIT";
        return "MISS";
    }
}
