public class PieceKnight extends Piece {
    private static final float KNIGHT_VALUE = (float) 3.05;
    public PieceKnight(int x, int y, boolean isWhite) {
        super(x, y, isWhite, KNIGHT_VALUE,  (isWhite ? "white" : "black") + "_knight.png");
    }
}
