public class PieceQueen extends Piece {
    private static final float QUEEN_VALUE = (float) 9.5;

    public PieceQueen(int x, int y, boolean isWhite) {
        super(x, y, isWhite, QUEEN_VALUE, (isWhite ? "white" : "black") + "_queen.png");
    }

    @Override
    public boolean isValidMove(int nxp, int nyp, boolean isForAttacking) {
        if (!super.isValidMove(nxp, nyp, isForAttacking))
            return false;

        if (!(Math.abs(nxp - xp) == Math.abs(nyp - yp) || nxp == xp || nyp == yp) || moveOverPiece(nxp, nyp))
            return false;

        return true;
    }
}
