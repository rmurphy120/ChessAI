import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.Random;

public class ChessGame {
    // Tracks the piece that is currently being moved
    private static Piece currentPiece;
    public static boolean whitesTurn = new Random().nextBoolean();

    public static void main(String[] args) {
        Board.setUpBoard();
        createGraphics();
    }

    private static void createGraphics() {
        // Draws the board to a window
        JFrame frame = new JFrame();
        frame.setBounds(10, 10, 512, 512);
        frame.setUndecorated(true);

        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                boolean white = true;
                for (int x = 0; x < 8; x++) {
                    for (int y = 0; y < 8; y++) {
                        if (white)
                            g.setColor(Color.getHSBColor((float) 30 / 360, (float) 35 / 100, (float) 64 / 100));
                        else
                            g.setColor(Color.getHSBColor((float) 28 / 360, (float) 84 / 100, (float) 34 / 100));

                        g.fillRect(x * 64, y * 64, 64, 64);

                        if (Board.board[x][y] != null && !Board.board[x][y].equals(currentPiece))
                            g.drawImage(Board.board[x][y].getSprite(), x * 64, y * 64, this);

                        white = !white;
                    }
                    white = !white;
                }
                if (currentPiece != null)
                    g.drawImage(currentPiece.getSprite(), currentPiece.getX(), currentPiece.getY(), this);
            }
        };

        frame.add(panel);

        frame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentPiece != null) {
                    currentPiece.setCoordinates(e.getX() - 32, e.getY() - 32);
                    frame.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        frame.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                currentPiece = Board.board[e.getX() / 64][e.getY() / 64];
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentPiece != null) {
                    currentPiece.move(e.getX() / 64, e.getY() / 64);
                    currentPiece = null;
                    frame.repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }
}
