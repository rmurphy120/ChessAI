public class PiecePawn extends Piece {
    private static final float PAWN_VALUE = 1;

    public PiecePawn(int x, int y, boolean isWhite) {
        super(x, y, isWhite, PAWN_VALUE, (isWhite ? "white" : "black") + "_pawn.png");
    }

    @Override
    public boolean isValidMove(int nxp, int nyp) {
        if (!super.isValidMove(nxp, nyp))
            return false;

        boolean isCapturing = Board.board[nxp][nyp] != null;
        boolean isStraight = nxp == xp && !isCapturing;

        // En passant logic
        Piece otherPawn = Board.board[nxp][yp];

        boolean isEnPassant = Math.abs(nxp - xp) == 1 && nyp - yp == (IS_WHITE ? -1 : 1) && !isCapturing &&
                otherPawn instanceof PiecePawn && otherPawn.equals(enPassantable);

        // Partial diagonal capture logic
        boolean isDiagonal = Math.abs(nxp - xp) == 1 && isCapturing;

        // Double move logic
        boolean isDoubleMove = !hasMoved && nyp - yp == (IS_WHITE ? -2 : 2) && isStraight && !moveOverPiece(nxp, nyp);

        if (!((nyp - yp == (IS_WHITE ? -1 : 1) && (isStraight || isDiagonal)) || isDoubleMove || isEnPassant))
            return false;

        // At this point, assumes move is valid
        if (isEnPassant)
            Board.kill(otherPawn);

        return true;
    }
}
