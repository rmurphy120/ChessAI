public class PiecePawn extends Piece {
    private static final float PAWN_VALUE = 1;
    public PiecePawn(int x, int y, boolean isWhite) {
        super(x, y, isWhite, PAWN_VALUE,  (isWhite ? "white" : "black") + "_pawn.png");
    }
}
