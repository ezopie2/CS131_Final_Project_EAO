package battleship;

public enum ShipType {
 Carrier(5), Battleship(4), Cruiser(3), Submarine(3), Destroyer(2);
	public final int size;
	ShipType(int size) { this.size = size; }
}
