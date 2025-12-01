package battleship;

public interface IPlayer {
    void placeShips(Board board);
    Pos makeMove(Board opponentBoard);
    String name();
}
