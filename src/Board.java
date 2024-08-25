import java.util.LinkedList;

public class Board {
    // 2-by-2 array of Pieces representing the board
    public Piece[][] board;
    // List of all pieces in play
    public LinkedList<Piece> pieces;

    public Board() {
    }

    public Board(Board b) {
        pieces = new LinkedList<>();
        for (Piece each : b.pieces)
            if (each instanceof PiecePawn)
                pieces.add(new PiecePawn(each, this));
            else if (each instanceof PieceKnight)
                pieces.add(new PieceKnight(each, this));
            else if (each instanceof PieceBishop)
                pieces.add(new PieceBishop(each, this));
            else if (each instanceof PieceRook)
                pieces.add(new PieceRook(each, this));
            else if (each instanceof PieceQueen)
                pieces.add(new PieceQueen(each, this));
            else if (each instanceof PieceKing)
                pieces.add(new PieceKing(each, this));
            else
                pieces.add(new Piece(each, this));

        board = new Piece[8][8];
        for (Piece each : pieces)
            board[each.xp][each.yp] = each;
    }

    /**
     * Instantiates the board with pieces. White always starts on the bottom
     */
    public void setUpBoard() {
        board = new Piece[8][8];
        pieces = new LinkedList<>();

        boolean isWhite;
        int xp;
        int yp;

        for (int i = 0; i < 16; i++) {
            isWhite = i < 8;
            xp = i % 8;
            yp = (isWhite ? 1 : 6);

            board[xp][yp] = new PiecePawn(xp, yp, !isWhite, ChessGame.gameBoard);
        }

        for (int i = 0; i < 4; i++) {
            isWhite = i < 2;
            xp = (i % 2 == 0 ? 0 : 7);
            yp = (isWhite ? 0 : 7);

            board[xp][yp] = new PieceRook(xp, yp, !isWhite, ChessGame.gameBoard);
        }

        for (int i = 0; i < 4; i++) {
            isWhite = i < 2;
            xp = (i % 2 == 0 ? 1 : 6);
            yp = (isWhite ? 0 : 7);

            board[xp][yp] = new PieceKnight(xp, yp, !isWhite, ChessGame.gameBoard);
        }

        for (int i = 0; i < 4; i++) {
            isWhite = i < 2;
            xp = (i % 2 == 0 ? 2 : 5);
            yp = (isWhite ? 0 : 7);

            board[xp][yp] = new PieceBishop(xp, yp, !isWhite, ChessGame.gameBoard);
        }

        board[4][7] = new PieceKing(4, 7, true, ChessGame.gameBoard);
        board[4][0] = new PieceKing(4, 0, false, ChessGame.gameBoard);
        board[3][7] = new PieceQueen(3, 7, true, ChessGame.gameBoard);
        board[3][0] = new PieceQueen(3, 0, false, ChessGame.gameBoard);
    }

    /**
     * Updates board with a moved piece. Set the new coordinate off the board to remove the piece at the old coordinate
     *
     * @param xp old x position
     * @param yp old y position
     * @param nxp new x position
     * @param nyp new y position
     *
     * @throws IndexOutOfBoundsException if the old coordinate is off the board
     */
    public void updateBoard(int xp, int yp, int nxp, int nyp) {
        if (!isOnBoard(xp, yp))
            throw new IndexOutOfBoundsException("The start coordinates are not on the board");

        Piece p = board[xp][yp];
        board[xp][yp] = null;

        if (isOnBoard(nxp, nyp))
            board[nxp][nyp] = p;
    }

    /**
     * Removes a piece from the board
     *
     * @param p the piece to be removed
     */
    public void kill(Piece p) {
        p.getImageView().setImage(null);
        pieces.remove(p);
        updateBoard(p.xp, p.yp, -1, -1);
    }

    /**
     * Boolean method that checks if a move is on the board
     *
     * @param xp the x position
     * @param yp the y position
     * @return true if both the x and y coordinates are between 0 and 7
     */
    public boolean isOnBoard(int xp, int yp) {
        return 0 <= xp && xp <= 7 && 0 <= yp && yp <= 7;
    }

    /**
     * Calculates a bitboard representing all the squares which a player attacks
     *
     * @param isWhite the player
     * @return a bitboard representing all the squares which a player attacks
     */
    public long getAttackingSquaresByAPlayer(boolean isWhite) {
        long out = 0;

        for (Piece each : pieces)
            if (isWhite == each.IS_WHITE)
                out = out | each.getAttackingSquares();

        return out;
    }

    /**
     * Checks to see if there is a checkmate
     *
     * @param isWhite are we checking for white or black to be mated?
     *
     * @return true if there is a checkmate
     */
    public boolean checkCheckmate(boolean isWhite) {
        LinkedList<Piece> tempPieces = new LinkedList<>(pieces);

        for (Piece each : tempPieces)
            if (each.IS_WHITE != isWhite && each.getValidMoves() != 0)
                return false;

        return true;
    }
}
