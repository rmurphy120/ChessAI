public class PieceRook extends Piece {
    private static final float ROOK_VALUE = (float) 5.63;
    public PieceRook(int x, int y, boolean isWhite) {
        super(x, y, isWhite, ROOK_VALUE,  (isWhite ? "white" : "black") + "_rook.png");
    }
}
