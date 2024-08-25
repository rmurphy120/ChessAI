public class PieceKnight extends Piece {
    private static final float KNIGHT_VALUE = (float) 3.05;

    public PieceKnight(int x, int y, boolean isWhite, Board board) {
        super(x, y, isWhite, KNIGHT_VALUE, (isWhite ? "white" : "black") + "_knight.png", board);
    }

    public PieceKnight(Piece p, Board board) {
        super(p, board);
    }

    @Override
    public boolean isValidMove(int nxp, int nyp) {
        if (!super.isValidMove(nxp, nyp))
            return false;

        if (!((Math.abs(nxp - xp ) == 2 && Math.abs(nyp - yp ) == 1) ||
                (Math.abs(nxp - xp ) == 1 && Math.abs(nyp - yp ) == 2)))
            return false;

        return true;
    }

    @Override
    public boolean isAttackingSquare(int nxp, int nyp) {
        if (!super.isAttackingSquare(nxp, nyp))
            return false;

        if (!((Math.abs(nxp - xp ) == 2 && Math.abs(nyp - yp ) == 1) ||
                (Math.abs(nxp - xp ) == 1 && Math.abs(nyp - yp ) == 2)))
            return false;

        return true;
    }

}
