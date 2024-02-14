import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * General piece class from which the specific piece classes inherit
 */
public class Piece {
    // Directory where the images are pulled
    public final static File[] IMAGE_FILES = new File("images").listFiles();
    // List of all pieces in play
    public static LinkedList<Piece> pieces = new LinkedList<>();
    private int x;
    private int y;
    private final boolean IS_WHITE;
    // Material value of the piece
    private final float VALUE;
    // Sprite of the piece
    private Image sprite;

    /**
     * Constructor that instantiates a piece object
     *
     * @param x the x coordinate of the piece
     * @param y the y coordinate of the piece
     * @param isWhite whether the piece is white or not
     * @param value material value of the piece
     * @param spriteName the exact name of the image file
     *
     * @throws IndexOutOfBoundsException if the coordinates are not between 0 and 7
     * @throws NoSuchElementException if spriteName is not found in IMAGE_FILES
     */
    public Piece (int x, int y, boolean isWhite, float value, String spriteName) {
        if(0 > x || x > 7 || 0 > y || y > 7)
            throw new IndexOutOfBoundsException("Not a valid position");

        this.x = x;
        this.y = y;
        this.IS_WHITE = isWhite;
        this.VALUE = value;

        try {
            for (File imageFile : IMAGE_FILES)
                if (imageFile.getName().equals(spriteName))
                    sprite = ImageIO.read(imageFile);
        } catch (Exception e) {
            System.out.println(e.getClass() + ": " + e.getMessage());
        }

        if(sprite == null)
            throw new NoSuchElementException("sprite not initialized");

        pieces.add(this);
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public boolean isWhite() {
        return IS_WHITE;
    }
    public Image getSprite() {
        return sprite;
    }

    public void move(int x, int y) {
        // Loops over pieces and kills any in the new spot
        pieces.stream().filter(piece -> piece.x == x && piece.y == y).forEach(Piece::kill);

        this.x = x;
        this.y = y;
    }

    public void kill() {
        pieces.remove(this);
    }
}

