public class PiecePawn extends Piece {
    private static final float PAWN_VALUE = 1;

    public PiecePawn(int x, int y, boolean isWhite) {
        super(x, y, isWhite, PAWN_VALUE, (isWhite ? "white" : "black") + "_pawn.png");
    }

    @Override
    public boolean isValidMove(int nxp, int nyp) {
        if (!super.isValidMove(nxp, nyp))
            return false;

        // TODO Add en passant
        boolean isCapturing = Board.board[nxp][nyp] != null && Board.board[nxp][nyp].IS_WHITE != IS_WHITE;
        boolean isStraight = nxp == xp && !isCapturing;
        boolean isDiagonal = Math.abs(nxp - xp) == 1 && isCapturing;

        if (!((nyp - yp == (IS_WHITE ? -1 : 1) && (isStraight || isDiagonal)) ||
                (!hasMoved && nyp - yp == (IS_WHITE ? -2 : 2) && isStraight && !moveOverPiece(nxp, nyp))))
            return false;

        return true;
    }
}
