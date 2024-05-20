import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.util.Random;

public class ChessGame extends Application {
    // Tracks whose turn it is
    public static boolean whitesTurn = new Random().nextBoolean();
    public static Label turn = new Label(ChessGame.whitesTurn ? "White's turn" : "Black's turn");
    // Tracks the piece that is currently being moved
    private static Piece currentPiece;
    private static Group boardRectangles;
    private static Group possibleMoves;

    public static void main(String[] args) {
        Board.setUpBoard();
        Application.launch(args);
    }

    @Override
    public void start(Stage window) {
        Pane root = new Pane();

        createBoard(root);

        turn.setLayoutX(550);
        turn.setLayoutY(50);
        root.getChildren().add(turn);

        createMenu(root);

        Scene scene = new Scene(root, 640, 512);

        window.setTitle("Chess Engine");
        window.setScene(scene);
        window.show();
    }

    /**
     * Draws the board to the screen with the pieces
     *
     * @param root the root pane
     */
    private void createBoard(Pane root) {
        boardRectangles = new Group();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                // Creates the checkered grid
                Rectangle boardRect = new Rectangle(x*64, y*64, 64, 64);
                boardRect.setFill((x + y) % 2 == 0 ? Color.BISQUE : Color.SADDLEBROWN);

                boardRectangles.getChildren().add(boardRect);

                boardRect.toBack();

                // Draws all the pieces
                if (Board.board[x][y] != null) {
                    ImageView imageView = Board.board[x][y].getImageView();

                    imageView.setX(x*64);
                    imageView.setY(y*64);

                    imageView.setFitHeight(64);
                    imageView.setFitWidth(64);


                    // Events for moving pieces
                    imageView.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
                        currentPiece = Board.board[((int)imageView.getX()) / 64][((int)imageView.getY()) / 64];

                        addPossibleMovesOverlay(root);

                        event.consume();
                    });
                    // Used for graphics only
                    imageView.addEventHandler(MouseEvent.MOUSE_DRAGGED, (event) -> {
                        imageView.toFront();
                        imageView.setX(event.getX() - 32);
                        imageView.setY(event.getY() - 32);

                        event.consume();
                    });
                    imageView.addEventHandler(MouseEvent.MOUSE_RELEASED, (event) -> {
                        currentPiece.move(((int)event.getX()) / 64, ((int)event.getY()) / 64);
                        currentPiece = null;

                        root.getChildren().remove(possibleMoves);

                        event.consume();
                    });

                    root.getChildren().add(imageView);
                }
            }
        }

        root.getChildren().add(boardRectangles);
        boardRectangles.toBack();
    }

    /**
     * Adds a crimson overlay to visually show potential moves
     *
     * @param root the root pane
     */
    private void addPossibleMovesOverlay(Pane root) {
        long validMoves = currentPiece.getValidMoves();

        possibleMoves = new Group();

        for (int ny = 0; ny < 8; ny++)
            for (int nx = 0; nx < 8; nx++)
                if ((Board.coordinateToLongBinary(nx, ny) & validMoves) != 0) {
                    Rectangle moveRect = new Rectangle(nx * 64, ny * 64, 64, 64);
                    moveRect.setFill(Color.CRIMSON);
                    possibleMoves.getChildren().add(moveRect);
                }

        root.getChildren().add(possibleMoves);
        possibleMoves.toBack();
        boardRectangles.toBack();
    }

    /**
     * Draws the menu to screen
     *
     * @param root the root pane
     */
    private void createMenu(Pane root) {
        // Translucent background
        Rectangle background = new Rectangle(0,0, 512, 512);
        background.setFill(Color.color(0.1,0.1,0.1,0.7));

        Button startButton = new Button("Start");
        startButton.setLayoutX(256);
        startButton.setLayoutY(256);

        Group menu = new Group(background, startButton);

        root.getChildren().add(menu);

        // Event to remove the menu when the start button is pressed
        startButton.addEventHandler(ActionEvent.ACTION, (event) -> root.getChildren().remove(menu));
    }
}
