public class PieceBishop extends Piece {
    private static final float BISHOP_VALUE = (float) 3.33;

    public PieceBishop(int x, int y, boolean isWhite, Board board) {
        super(x, y, isWhite, BISHOP_VALUE, (isWhite ? "white" : "black") + "_bishop.png", board);
    }

    public PieceBishop(Piece p, Board board) {
        super(p, board);
    }

    @Override
    public boolean isValidMove(int nxp, int nyp) {
        if (!super.isValidMove(nxp, nyp))
            return false;

        if (!(Math.abs(nxp - xp) == Math.abs(nyp - yp)) || moveOverPiece(nxp, nyp))
            return false;

        return true;
    }

    @Override
    public boolean isAttackingSquare(int nxp, int nyp) {
        if (!super.isAttackingSquare(nxp, nyp))
            return false;

        if (!(Math.abs(nxp - xp) == Math.abs(nyp - yp)) || moveOverPiece(nxp, nyp))
            return false;

        return true;
    }
}
