package battleship;
import java.util.Objects;

public final class Pos {
	public final int row;
	public final int col;
	
	public Pos(int row, int col) {
		this.row = row;
		this.col = col;
	}
	public Pos offset(int dr, int dc) {
		return new Pos(row + dr, col + dc);
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Pos)) return false;
		Pos pos = (Pos) o;
		return row == pos.row && col == pos.col;
	}
	@Override
	public int hashCode() { return Objects.hash(row, col); }
	
	@Override
	public String toString() { return "(" + row + "," + col + ")"; }
}
