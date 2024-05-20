public class PieceKnight extends Piece {
    private static final float KNIGHT_VALUE = (float) 3.05;

    public PieceKnight(int x, int y, boolean isWhite) {
        super(x, y, isWhite, KNIGHT_VALUE, (isWhite ? "white" : "black") + "_knight.png");
    }

    @Override
    public boolean isValidMove(int nxp, int nyp, boolean isForAttacking) {
        if (!super.isValidMove(nxp, nyp, isForAttacking))
            return false;

        if (!((Math.abs(nxp - xp ) == 2 && Math.abs(nyp - yp ) == 1) ||
                (Math.abs(nxp - xp ) == 1 && Math.abs(nyp - yp ) == 2)))
            return false;

        return true;
    }
}
