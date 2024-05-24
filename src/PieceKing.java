public class PieceKing extends Piece {
    private static final float KING_VALUE = Float.MAX_VALUE;

    // Temporary variables for castling
    private Piece rook;
    private boolean isCastling = false;
    private int rookNxp = -1;

    public PieceKing(int x, int y, boolean isWhite) {
        super(x, y, isWhite, KING_VALUE, (isWhite ? "white" : "black") + "_king.png");
    }

    @Override
    public boolean isValidMove(int nxp, int nyp, boolean isForAttacking) {
        if (!super.isValidMove(nxp, nyp, isForAttacking))
            return false;

        // Castling logic
        boolean castlingLeft = nxp - xp < 0;
        // Rook's potential new x position
        rookNxp = castlingLeft ? nxp + 1 : nxp - 1;

        rook = castlingLeft ? Board.board[0][yp] : Board.board[7][yp];

        // Checks if a castle goes over a check
        boolean overCheck = false;
        for (int i = 1; i <= 2; i++)
            if (!isForAttacking && !hasMoved && putsMyKingInCheck(castlingLeft ? xp - i : xp + i, yp))
                overCheck = true;

        isCastling = !hasMoved && nyp == yp && Math.abs(nxp - xp) == 2 && rook instanceof PieceRook && !rook.hasMoved &&
                !moveOverPiece(nxp, nyp) && !rook.moveOverPiece(rookNxp, yp) && !overCheck && !isInCheck();

        if ((Math.abs(nxp - xp) <= 1 && Math.abs(nyp - yp) <= 1) == isCastling)
            return false;

        return true;
    }

    /**
     * Handles a castle
     */
    public void checkCastle() {
        if (!isCastling)
            return;

        Board.updateBoard(rook.xp, rook.yp, rookNxp, rook.yp);
        rook.xp = rookNxp;
        rook.getImageView().setX(rookNxp * 64);

        rook = null;
        isCastling = false;
        rookNxp = -1;
    }

    public boolean isInCheck() {
        return (Board.coordinateToLongBinary(xp, yp) & Board.getAttackingSquaresByAPlayer(!IS_WHITE)) != 0;
    }
}
