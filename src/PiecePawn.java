public class PiecePawn extends Piece {
    private static final float PAWN_VALUE = 1;

    // Temporary variables for en passant
    private boolean isEnPassant = false;
    private Piece otherPawn = null;

    public PiecePawn(int x, int y, boolean isWhite) {
        super(x, y, isWhite, PAWN_VALUE, (isWhite ? "white" : "black") + "_pawn.png");
    }

    @Override
    public boolean isValidMove(int nxp, int nyp, boolean isForAttacking) {
        if (!super.isValidMove(nxp, nyp, isForAttacking))
            return false;

        boolean isCapturing = Board.board[nxp][nyp] != null;
        boolean isStraight = nxp == xp && !isCapturing;

        // En passant logic
        otherPawn = Board.board[nxp][yp];

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
    public long getAttackingSquares() {
        long out = 0;
        int txp;
        int typ = yp + (IS_WHITE ? -1 : 1);

        for (int i = -1; i <= 1; i += 2) {
            txp = xp + i;
            if (super.isValidMove(txp, typ, true))
                out += Board.coordinateToLongBinary(txp, typ);
        }

        return out;
    }

    /**
     * Handles an en passant
     */
    public void checkEnPassant() {
        if (isEnPassant)
            Board.kill(otherPawn);

        isEnPassant = false;
        otherPawn = null;
    }
}
