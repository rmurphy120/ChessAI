public class PieceKing extends Piece {
    private static final float KING_VALUE = Float.MAX_VALUE;
    public PieceKing(int x, int y, boolean isWhite) {
        super(x, y, isWhite, KING_VALUE,  (isWhite ? "white" : "black") + "_king.png");
    }
}
