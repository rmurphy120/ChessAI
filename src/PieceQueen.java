public class PieceQueen extends Piece {
    private static final float QUEEN_VALUE = (float) 9.5;

    public PieceQueen(int x, int y, boolean isWhite, Board board) {
        super(x, y, isWhite, QUEEN_VALUE, (isWhite ? "white" : "black") + "_queen.png", board);
    }

    public PieceQueen(Piece p, Board board) {
        super(p, board);
    }

    @Override
    public boolean isValidMove(int nxp, int nyp) {
        if (!super.isValidMove(nxp, nyp))
            return false;

        if (!(Math.abs(nxp - xp) == Math.abs(nyp - yp) || nxp == xp || nyp == yp) || moveOverPiece(nxp, nyp))
            return false;

        return true;
    }

    @Override
    public boolean isAttackingSquare(int nxp, int nyp) {
        if (!super.isAttackingSquare(nxp, nyp))
            return false;

        if (!(Math.abs(nxp - xp) == Math.abs(nyp - yp) || nxp == xp || nyp == yp) || moveOverPiece(nxp, nyp))
            return false;

        return true;
    }
}
