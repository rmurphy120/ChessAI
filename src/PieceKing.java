public class PieceKing extends Piece {
    private static final float KING_VALUE = Float.MAX_VALUE;

    // Temporary variables for castling
    private Piece rook;
    private boolean isCastling = false;
    private int rookNxp = -1;

    public PieceKing(int x, int y, boolean isWhite, Board board) {
        super(x, y, isWhite, KING_VALUE, (isWhite ? "white" : "black") + "_king.png", board);
    }

    public PieceKing(Piece p, Board board) {
        super(p, board);
    }

    @Override
    public boolean isValidMove(int nxp, int nyp) {
        if (!super.isValidMove(nxp, nyp))
            return false;

        // Castling logic
        boolean castlingLeft = nxp - xp < 0;
        // Rook's potential new x position
        rookNxp = castlingLeft ? nxp + 1 : nxp - 1;

        rook = castlingLeft ? boardContainingPiece.board[0][yp] : boardContainingPiece.board[7][yp];

        // Checks if a castle goes over a check
        boolean overCheck = false;
        for (int i = 1; i <= 2; i++)
            if (!hasMoved && putsMyKingInCheck(castlingLeft ? xp - i : xp + i, yp))
                overCheck = true;

        isCastling = !hasMoved && nyp == yp && Math.abs(nxp - xp) == 2 && rook instanceof PieceRook && !rook.hasMoved &&
                !moveOverPiece(nxp, nyp) && !rook.moveOverPiece(rookNxp, yp) && !overCheck &&
                !isInCheck();

        if ((Math.abs(nxp - xp) <= 1 && Math.abs(nyp - yp) <= 1) == isCastling)
            return false;

        return true;
    }

    @Override
    public boolean isAttackingSquare(int nxp, int nyp) {
        if (!super.isAttackingSquare(nxp, nyp))
            return false;

        if (!(Math.abs(nxp - xp) < 2 && Math.abs(nyp - yp) < 2))
            return false;

        return true;
    }

    /**
     * Handles a castle
     */
    public void handleCastle() {
        if (!isCastling)
            return;

        boardContainingPiece.updateBoard(rook.xp, rook.yp, rookNxp, rook.yp);
        rook.xp = rookNxp;
        rook.getImageView().setX(rookNxp * 64);

        rook = null;
        isCastling = false;
        rookNxp = -1;
    }

    public boolean isInCheck() {
        return (Util.coordinateToLongBinary(xp, yp) &
                boardContainingPiece.getAttackingSquaresByAPlayer(!IS_WHITE)) != 0;
    }
}
