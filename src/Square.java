public class Square {
    private int xp;
    private int yp;
    private final boolean IS_SQUARE_WHITE;
    private Piece piece;

    public Square (int xp, int yp, boolean isSquareWhite, Piece piece) {
        this.xp = xp;
        this.yp = yp;
        this.IS_SQUARE_WHITE = isSquareWhite;
        this.piece = piece;
    }

    public int getXp() {
        return xp;
    }

    public int getYp() {
        return yp;
    }

    public boolean IS_SQUARE_WHITE() {
        return IS_SQUARE_WHITE;
    }

    public Piece getPiece() {
        return piece;
    }
}
