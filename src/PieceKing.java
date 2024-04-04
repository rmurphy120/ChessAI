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

        // Castling logic
        boolean castlingLeft = nxp - xp < 0;
        // Rook's potential new x position
        int rooknxp = castlingLeft ? nxp + 1 : nxp - 1;

        Piece rook = (castlingLeft ? Board.board[0][yp] : Board.board[7][yp]);

        boolean isCastling = !hasMoved && Math.abs(nxp - xp) == 2 && rook instanceof PieceRook && !rook.hasMoved &&
                !moveOverPiece(nxp, nyp) && !rook.moveOverPiece(rooknxp, yp);

        if (!((Math.abs(nxp - xp) <= 1 && Math.abs(nyp - yp) <= 1) || isCastling))
            return false;

        // At this point, assumes move is valid
        if (isCastling) {
            rook.move(rooknxp, yp);
            // Adjusts for the fact that move is being called twice, so whose turn it is doesn't change
            ChessGame.whitesTurn = !ChessGame.whitesTurn;
        }

        return true;
    }
}
