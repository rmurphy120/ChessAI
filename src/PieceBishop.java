public class PieceBishop extends Piece {
    private static final float BISHOP_VALUE = (float) 3.33;

    public PieceBishop(int x, int y, boolean isWhite) {
        super(x, y, isWhite, BISHOP_VALUE, (isWhite ? "white" : "black") + "_bishop.png");
    }

    @Override
    public boolean isValidMove(int nxp, int nyp) {
        if (!super.isValidMove(nxp, nyp))
            return false;

        if (!(Math.abs(nxp - xp) == Math.abs(nyp - yp)) || moveOverPiece(nxp, nyp))
            return false;

        return true;
    }
}
