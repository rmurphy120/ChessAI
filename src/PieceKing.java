public class PieceKing extends Piece {
    private static final float KING_VALUE = Float.MAX_VALUE;

    public PieceKing(int x, int y, boolean isWhite) {
        super(x, y, isWhite, KING_VALUE, (isWhite ? "white" : "black") + "_king.png");
    }

    @Override
    public boolean isValidMove(int nxp, int nyp) {
        if (!super.isValidMove(nxp, nyp))
            return false;

        //TODO Handle the king checking itself
        boolean isCastling = false;
        Piece rook = null;

        boolean castlingLeft = nxp - xp < 0;
        // Rook's potential new x position
        int rooknxp = castlingLeft ? nxp + 1 : nxp - 1;

        if (!hasMoved && Math.abs(nxp - xp) == 2) {
            rook = (castlingLeft ? Board.board[0][yp] : Board.board[7][yp]);

            boolean isCapturing = Board.board[nxp][nyp] != null && Board.board[nxp][nyp].IS_WHITE != IS_WHITE;

            isCastling = rook instanceof PieceRook && !rook.hasMoved && !isCapturing && !moveOverPiece(nxp, nyp) &&
                    !rook.moveOverPiece(rooknxp, yp);
        }

        if (!((Math.abs(nxp - xp) <= 1 && Math.abs(nyp - yp) <= 1) || isCastling))
            return false;

        if (isCastling)
            rook.move(rooknxp, yp);

        return true;
    }
}
