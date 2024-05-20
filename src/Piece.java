import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.util.NoSuchElementException;

/**
 * General piece class from which the specific piece classes inherit
 */
public class Piece {
    // Directory where the images are pulled
    public final static File[] IMAGE_FILES = new File("images").listFiles();
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
     * @throws IndexOutOfBoundsException if the coordinates are not between 0 and 7
     * @throws NoSuchElementException    if spriteName is not found in IMAGE_FILES
     */
    public Piece(int xp, int yp, boolean isWhite, float value, String spriteName) {
        if (0 > xp || xp > 7 || 0 > yp || yp > 7)
            throw new IndexOutOfBoundsException("Not a valid position");

        this.xp = xp;
        this.yp = yp;
        this.IS_WHITE = isWhite;
        this.VALUE = value;

        try {
            for (File imageFile : IMAGE_FILES)
                if (imageFile.getName().equals(spriteName)) {
                    FileInputStream inStream = new FileInputStream(imageFile);
                    imageView = new ImageView(new Image(inStream));
                }
        } catch (Exception e) {
            System.out.println(e.getClass() + ": " + e.getMessage());
        }

        if (imageView == null)
            throw new NoSuchElementException("sprite not initialized");

        Board.pieces.add(this);
    }

    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Attempts to move this piece to a new board position
     *
     * @param nxp the new board x position of this Piece
     * @param nyp the new board y position of this Piece
     */
    public void move(int nxp, int nyp) {
        if (isValidMove(nxp, nyp, false)) {
            if (Board.board[nxp][nyp] != null)
                Board.kill(Board.board[nxp][nyp]);

            // If the piece moved was a king, checks if it castled
            if (this instanceof PieceKing)
                ((PieceKing) this).checkCastle();

            // If the piece moved was a pawn, checks if it performed en passant
            if (this instanceof PiecePawn)
                ((PiecePawn) this).checkEnPassant();

            // Updates enPassantable
            enPassantable = null;
            if (this instanceof PiecePawn && nyp - yp == (IS_WHITE ? -2 : 2))
                enPassantable = (PiecePawn) this;

            Board.updateBoard(xp, yp, nxp, nyp);

            xp = nxp;
            yp = nyp;

            hasMoved = true;

            // Changes which player's turn it is
            ChessGame.whitesTurn = !ChessGame.whitesTurn;
            ChessGame.turn.setText(ChessGame.whitesTurn ? "White's turn" : "Black's turn");
        }

        // Outside the if statement so graphics are reset if the move is invalid

        imageView.setX(xp * 64);
        imageView.setY(yp * 64);
    }

    /**
     * Checks if a particular move is valid in a general sense. Overrode by child classes to handle specific cases
     *
     * @param nxp            the new board x position of this Piece
     * @param nyp            the new board y position of this Piece
     * @param isForAttacking is the method called to find attacking squares, or valid squares
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(int nxp, int nyp, boolean isForAttacking) {
        // Is it a new position?
        if (xp == nxp && yp == nyp)
            return false;

        // Is the position on the board?
        if (!Board.isOnBoard(nxp, nyp))
            return false;

        if(isForAttacking)
            return true;

        // Method only continues to this point if function is not called for attacking

        // Is it this person's turn to move?
        if (IS_WHITE != ChessGame.whitesTurn)
            return false;

        // Is there's a same color piece at that position?
        if (Board.board[nxp][nyp] != null && IS_WHITE == Board.board[nxp][nyp].IS_WHITE)
            return false;

        if (putsMYKingInCheck(nxp, nyp))
            return false;

        return true;
    }

    /**
     * Returns true if this move puts this player's team in check
     *
     * @param nxp the new x position
     * @param nyp the new y position
     *
     * @return true if this move puts this player's team in check
     */
    public boolean putsMYKingInCheck(int nxp, int nyp) {
        // Simulates the move to test if the piece's king will be in check
        int xp = this.xp;
        int yp = this.yp;
        Piece pieceOnCheckedSquare = Board.board[nxp][nyp];

        if (pieceOnCheckedSquare != null)
            Board.pieces.remove(pieceOnCheckedSquare);
        this.xp = nxp;
        this.yp = nyp;
        Board.updateBoard(xp,yp,nxp,nyp);

        // Finds the player's king
        PieceKing piecesKing = null;

        for (Piece each : Board.pieces)
            if (IS_WHITE == each.IS_WHITE && each instanceof PieceKing)
                piecesKing = (PieceKing) each;

        // Checks if the players king is in check
        boolean out = piecesKing.isInCheck();

        this.xp = xp;
        this.yp = yp;
        Board.updateBoard(nxp,nyp,xp,yp);
        Board.board[nxp][nyp] = pieceOnCheckedSquare;
        if (pieceOnCheckedSquare != null)
            Board.pieces.add(pieceOnCheckedSquare);

        return out;
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
            if (Board.board[txp][typ] != null && !Board.board[txp][typ].equals(this))
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
                if (isValidMove(txp, typ, false))
                    validMoves = validMoves | Board.coordinateToLongBinary(txp, typ);

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
                if (isValidMove(txp, typ, true))
                    out = out | Board.coordinateToLongBinary(txp, typ);

        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Piece p))
            return false;

        return xp == p.xp && yp == p.yp && IS_WHITE == p.IS_WHITE && this.getClass().equals(p.getClass());
    }
}

