import java.util.LinkedList;

public class Board {
    // 2-by-2 array of Pieces representing the board
    public static Piece[][] board;
    // List of all pieces in play
    public static LinkedList<Piece> pieces = new LinkedList<>();

    /**
     * Instantiates the board with pieces. White always starts on the bottom
     */
    public static void setUpBoard() {
        board = new Piece[8][8];

        boolean isWhite;
        int xp;
        int yp;

        for (int i = 0; i < 16; i++) {
            isWhite = i < 8;
            xp = i % 8;
            yp = (isWhite ? 1 : 6);

            board[xp][yp] = new PiecePawn(xp, yp, !isWhite);
        }

        for (int i = 0; i < 4; i++) {
            isWhite = i < 2;
            xp = (i % 2 == 0 ? 0 : 7);
            yp = (isWhite ? 0 : 7);

            board[xp][yp] = new PieceRook(xp, yp, !isWhite);
        }

        for (int i = 0; i < 4; i++) {
            isWhite = i < 2;
            xp = (i % 2 == 0 ? 1 : 6);
            yp = (isWhite ? 0 : 7);

            board[xp][yp] = new PieceKnight(xp, yp, !isWhite);
        }

        for (int i = 0; i < 4; i++) {
            isWhite = i < 2;
            xp = (i % 2 == 0 ? 2 : 5);
            yp = (isWhite ? 0 : 7);

            board[xp][yp] = new PieceBishop(xp, yp, !isWhite);
        }

        board[4][7] = new PieceKing(4, 7, true);
        board[4][0] = new PieceKing(4, 0, false);
        board[3][7] = new PieceQueen(3, 7, true);
        board[3][0] = new PieceQueen(3, 0, false);
    }

    /**
     * Updates board with a moved piece. Set the new coordinate off the board to remove the piece at the old coordinate
     *
     * @param xp old x position
     * @param yp old y position
     * @param nxp new x position
     * @param nyp new y position
     * @throws IndexOutOfBoundsException if the old coordinate is off the board
     */
    public static void updateBoard(int xp, int yp, int nxp, int nyp) {
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
    public static void kill(Piece p) {
        p.getImageView().setImage(null);
        pieces.remove(p);
        updateBoard(p.xp, p.yp, -1, -1);
    }

    /**
     * Boolean method that checks if a move is on the board
     *
     * @param xp the x position
     * @param yp the y postition
     * @return true if both the x and y coordinates are between 0 and 7
     */
    public static boolean isOnBoard(int xp, int yp) {
        return 0 <= xp && xp <= 7 && 0 <= yp && yp <= 7;
    }
}
