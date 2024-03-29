import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.NoSuchElementException;

/**
 * General piece class from which the specific piece classes inherit
 */
public class Piece {
    // Directory where the images are pulled
    public final static File[] IMAGE_FILES = new File("images").listFiles();

    // Board x position. Used for logic
    protected int xp;
    // Board y position. Used for logic
    protected int yp;
    // Frame x position. Used for visuals
    protected int x;
    // Frame y position. Used for visuals
    protected int y;
    // Keeps track if a piece has moved yet (Useful for castling and double pawn moves)
    protected boolean hasMoved = false;
    protected final boolean IS_WHITE;
    // Material value of the piece
    protected final float VALUE;
    // Sprite of the piece
    private Image sprite;

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
        this.x = xp * 64;
        this.y = yp * 64;
        this.IS_WHITE = isWhite;
        this.VALUE = value;

        try {
            for (File imageFile : IMAGE_FILES)
                if (imageFile.getName().equals(spriteName))
                    sprite = ImageIO.read(imageFile);
        } catch (Exception e) {
            System.out.println(e.getClass() + ": " + e.getMessage());
        }

        if (sprite == null)
            throw new NoSuchElementException("sprite not initialized");

        Board.pieces.add(this);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getSprite() {
        return sprite;
    }

    /**
     * Modifies the frame position without affecting the board position. Used for modifying visuals without logic
     *
     * @param nx the new frame x position of this Piece
     * @param ny the new frame y position of this Piece
     */
    public void setCoordinates(int nx, int ny) {
        x = nx;
        y = ny;
    }

    /**
     * Attempts to move this piece to a new board position
     *
     * @param nxp the new board x position of this Piece
     * @param nyp the new board y position of this Piece
     */
    public void move(int nxp, int nyp) {
        if (isValidMove(nxp, nyp)) {
            if (Board.board[nxp][nyp] != null)
                kill(Board.board[nxp][nyp]);

            Board.updateBoard(xp, yp, nxp, nyp);

            xp = nxp;
            yp = nyp;

            hasMoved = true;
        }

        x = xp * 64;
        y = yp * 64;
    }

    /**
     * Checks if a particular move is valid in a general sense. Overrode by child classes to handle specific cases
     *
     * @param nxp the new board x position of this Piece
     * @param nyp the new board y position of this Piece
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(int nxp, int nyp) {
        // Is it a new position?
        if (xp == nxp && yp == nyp)
            return false;

        // Is the position on the board?
        if (!Board.isOnBoard(nxp, nyp))
            return false;

        // Is there's a same color piece at that position
        if (Board.board[nxp][nyp] != null && IS_WHITE == Board.board[nxp][nyp].IS_WHITE)
            return false;

        return true;
    }

    /**
     * Removes a piece from the board
     *
     * @param p the piece to be removed
     */
    public void kill(Piece p) {
        Board.pieces.remove(p);
        Board.updateBoard(p.xp, p.yp, -1, -1);
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Piece))
            throw new IllegalArgumentException("o not apart of Piece");

        Piece p = (Piece) o;
        return p != null && x == p.x && y == p.y && IS_WHITE == p.IS_WHITE && this.getClass().equals(p.getClass());
    }
}

