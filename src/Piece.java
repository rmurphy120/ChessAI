import javafx.scene.image.ImageView;

import java.util.NoSuchElementException;

/**
 * General piece class from which the specific piece classes inherit
 */
public class Piece {
    // A piece which can be captured through en passant
    protected static PiecePawn enPassantable;

    protected final boolean IS_WHITE;
    // Material value of the piece
    protected final float VALUE;
    // Board x position. Used for logic
    protected int xp;
    // Board y position. Used for logic
    protected int yp;
    // Keeps track if a piece has moved yet (Useful for castling and double pawn moves)
    protected boolean hasMoved = false;
    protected Board boardContainingPiece;
    // ImageView of the piece (Javafx visualizer for images)
    private ImageView imageView;

    /**
     * Constructor that instantiates a piece object
     *
     * @param xp         the x coordinate of the piece
     * @param yp         the y coordinate of the piece
     * @param isWhite    whether the piece is white or not
     * @param value      material value of the piece
     * @param spriteName the exact name of the image file
     * @param boardContainingPiece board which references piece
     * @throws IndexOutOfBoundsException if the coordinates are not between 0 and 7
     * @throws NoSuchElementException    if spriteName is not found in IMAGE_FILES
     */
    public Piece(int xp, int yp, boolean isWhite, float value, String spriteName, Board boardContainingPiece) {
        if (0 > xp || xp > 7 || 0 > yp || yp > 7)
            throw new IndexOutOfBoundsException("Not a valid position");

        this.xp = xp;
        this.yp = yp;
        this.IS_WHITE = isWhite;
        this.VALUE = value;

        try {
            imageView = ChessGame.getImageView(spriteName);
        } catch (Exception e) {
            System.out.println(e.getClass() + ": " + e.getMessage());
        }

        if (imageView == null)
            throw new NoSuchElementException("sprite not initialized");

        this.boardContainingPiece = boardContainingPiece;

        boardContainingPiece.pieces.add(this);
    }

    /**
     * Constructs a deep copy of another Piece
     *
     * @param p     the other Piece
     * @param boardContainingPiece board which references piece
     */
    public Piece(Piece p, Board boardContainingPiece) {
        xp = p.xp;
        yp = p.yp;
        IS_WHITE = p.IS_WHITE;
        VALUE = p.VALUE;
        hasMoved = p.hasMoved;
        imageView = p.getImageView();
        this.boardContainingPiece = boardContainingPiece;
    }

    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Attempts to move this piece to a new board position
     *
     * @param nxp the new board x position of this Piece
     * @param nyp the new board y position of this Piece
     *
     * @return true if the move was successful, false otherwise
     */
    public boolean move(int nxp, int nyp) {
        boolean successfulMove = false;

        if (isValidMove(nxp, nyp)) {
            if (boardContainingPiece.board[nxp][nyp] != null)
                boardContainingPiece.kill(boardContainingPiece.board[nxp][nyp]);

            // If the piece moved was a king, checks if it castled
            if (this instanceof PieceKing)
                ((PieceKing) this).handleCastle();

            // If the piece moved was a pawn, checks if it performed en passant
            if (this instanceof PiecePawn)
                ((PiecePawn) this).handleEnPassant();

            // Updates enPassantable
            enPassantable = null;
            if (this instanceof PiecePawn && nyp - yp == (IS_WHITE ? -2 : 2))
                enPassantable = (PiecePawn) this;

            boardContainingPiece.updateBoard(xp, yp, nxp, nyp);

            xp = nxp;
            yp = nyp;

            hasMoved = true;

            // Changes which player's turn it is
            ChessGame.whitesTurn = !ChessGame.whitesTurn;
            ChessGame.turn.setText(ChessGame.whitesTurn ? "White's turn" : "Black's turn");

            successfulMove = true;
        }

        // Outside the if statement so graphics are reset if the move is invalid

        imageView.setX(xp * 64);
        imageView.setY(yp * 64);

        return successfulMove;
    }

    /**
     * Simulates a move and returns the board. Does NOT check the validity of the move
     * Will need to make a better simulation system for the AI (Particularly thinking about pawn promotion)
     *
     * @param nxp the new board x position of this Piece
     * @param nyp the new board y position of this Piece
     * @param currBoard the current board state
     * @return a new board based of that move
     */
    public Board simulateMove(int nxp, int nyp, Board currBoard) {
        Board simBoard = new Board(currBoard);

        if (simBoard.board[nxp][nyp] != null)
            simBoard.pieces.remove(simBoard.board[nxp][nyp]);

        simBoard.board[xp][yp].xp = nxp;
        simBoard.board[xp][yp].yp = nyp;
        simBoard.updateBoard(xp, yp, nxp, nyp);

        return simBoard;
    }

    /**
     * Checks if a particular move is valid in a general sense. Overrode by child classes to handle specific cases
     *
     * @param nxp            the new board x position of this Piece
     * @param nyp            the new board y position of this Piece
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(int nxp, int nyp) {
        // Is it a new position?
        if (xp == nxp && yp == nyp)
            return false;

        // Is the position on the board?
        if (!boardContainingPiece.isOnBoard(nxp, nyp))
            return false;

        // Is it this person's turn to move?
        if (IS_WHITE != ChessGame.whitesTurn)
            return false;

        // Is there's a same color piece at that position?
        if (boardContainingPiece.board[nxp][nyp] != null && IS_WHITE == boardContainingPiece.board[nxp][nyp].IS_WHITE)
            return false;

        if (putsMyKingInCheck(nxp, nyp))
            return false;

        return true;
    }

    public boolean isAttackingSquare(int nxp, int nyp) {
        // Is it a new position?
        if (xp == nxp && yp == nyp)
            return false;

        // Is the position on the board?
        if (!boardContainingPiece.isOnBoard(nxp, nyp))
            return false;

        return true;
    }

    /**
     * Returns true if this move puts this player's team in check
     *
     * @param nxp the new x position
     * @param nyp the new y position
     * @return true if this move puts this player's team in check
     */
    public boolean putsMyKingInCheck(int nxp, int nyp) {
        Board simBoard = simulateMove(nxp, nyp, boardContainingPiece);

        // Finds the player's king
        PieceKing piecesKing = null;

        for (Piece each : simBoard.pieces)
            if (IS_WHITE == each.IS_WHITE && each instanceof PieceKing)
                piecesKing = (PieceKing) each;

        // Checks if the players king is in check
        return piecesKing.isInCheck();
    }

    /**
     * Checks if a piece moved over another piece. Meant for moves of 2 or more vertically, horizontally, or diagonally
     *
     * @param nxp the new x position
     * @param nyp the new y position
     * @return true if it does move over another piece
     */
    protected boolean moveOverPiece(int nxp, int nyp) {
        // Temporary coordinates used to step through the path
        int txp = xp;
        int typ = yp;

        // Steps through the path, checking each square if there's a piece there
        while (txp != nxp || typ != nyp) {
            if (boardContainingPiece.board[txp][typ] != null && !boardContainingPiece.board[txp][typ].equals(this))
                return true;

            if (txp != nxp)
                // Increments or decrements depending on whether moving left or right
                txp += xp > nxp ? -1 : 1;
            if (typ != nyp)
                // Increments or decrements depending on whether moving up or down
                typ += yp > nyp ? -1 : 1;
        }

        return false;
    }

    /**
     * Calculates the squares which this piece could move. Goes through each square
     *
     * @return a bitboard representing the squares which this piece could move
     */
    public long getValidMoves() {
        long validMoves = 0;

        for (int typ = 0; typ < 8; typ++)
            for (int txp = 0; txp < 8; txp++)
                if (isValidMove(txp, typ))
                    validMoves = validMoves | Util.coordinateToLongBinary(txp, typ);

        return validMoves;
    }

    /**
     * Calculates the squares which this piece attacks. Goes through each square
     *
     * @return a bitboard representing the squares which this piece attacks
     */
    public long getAttackingSquares() {
        long out = 0;

        for (int typ = 0; typ < 8; typ++)
            for (int txp = 0; txp < 8; txp++)
                if (isAttackingSquare(txp, typ))
                    out = out | Util.coordinateToLongBinary(txp, typ);

        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Piece p))
            return false;

        return xp == p.xp && yp == p.yp && IS_WHITE == p.IS_WHITE && this.getClass().equals(p.getClass());
    }
}

