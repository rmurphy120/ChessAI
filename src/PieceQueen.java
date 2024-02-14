public class PieceQueen extends Piece {
    private static final float QUEEN_VALUE = (float) 9.5;
    public PieceQueen(int x, int y, boolean isWhite) {
        super(x, y, isWhite, QUEEN_VALUE,  (isWhite ? "white" : "black") + "_queen.png");
    }
}
