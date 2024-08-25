public class PiecePawn extends Piece {
    private static final float PAWN_VALUE = 1;

    // Temporary variables for en passant
    private boolean isEnPassant = false;
    private Piece otherPawn = null;

    public PiecePawn(int x, int y, boolean isWhite, Board board) {
        super(x, y, isWhite, PAWN_VALUE, (isWhite ? "white" : "black") + "_pawn.png", board);
    }

    public PiecePawn(Piece p, Board board) {
        super(p, board);
    }

    @Override
    public boolean isValidMove(int nxp, int nyp) {
        if (!super.isValidMove(nxp, nyp))
            return false;

        boolean isCapturing = boardContainingPiece.board[nxp][nyp] != null;
        boolean isStraight = nxp == xp && !isCapturing;

        // En passant logic
        otherPawn = boardContainingPiece.board[nxp][yp];

        isEnPassant = Math.abs(nxp - xp) == 1 && nyp - yp == (IS_WHITE ? -1 : 1) && !isCapturing &&
                otherPawn instanceof PiecePawn && otherPawn.equals(enPassantable);

        // Partial diagonal capture logic
        boolean isDiagonal = Math.abs(nxp - xp) == 1 && isCapturing;

        // Double move logic
        boolean isDoubleMove = !hasMoved && nyp - yp == (IS_WHITE ? -2 : 2) && isStraight && !moveOverPiece(nxp, nyp);

        if (!((nyp - yp == (IS_WHITE ? -1 : 1) && (isStraight || isDiagonal)) || isDoubleMove || isEnPassant))
            return false;

        return true;
    }

    @Override
    public boolean isAttackingSquare(int nxp, int nyp) {
        if (!super.isAttackingSquare(nxp, nyp))
            return false;

        if (!(Math.abs(nxp - xp) == 1 && nyp - yp == (IS_WHITE ? -1 : 1)))
            return false;

        return true;
    }

    /**
     * Handles an en passant
     */
    public void handleEnPassant() {
        if (!isEnPassant)
            return;

        boardContainingPiece.kill(otherPawn);

        isEnPassant = false;
        otherPawn = null;
    }
}
