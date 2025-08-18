import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

public class ChessGame extends Application {

    // Directory where the images are pulled
    public final static File[] IMAGE_FILES = new File("images").listFiles();

    public static Board gameBoard;
    // Tracks whose turn it is
    public static boolean whitesTurn;
    public static Label turn;

    // Tracks the piece that is currently being moved
    private static Piece currentPiece;
    // Tracks the number of clicks to avoid breaking due to multiple clicks
    private static int numberOfClicks;

    // Groups used for the UI
    private static Group boardRectangles;
    private static Group possibleMoves;
    private static Group menu;
    private static Group pawnPromotionMenu;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage window) {
        Pane root = new Pane();

        reset(root);

        Scene scene = new Scene(root, 640, 512);

        window.setTitle("Chess Engine");
        window.setScene(scene);
        window.show();
    }

    public void reset(Pane root) {
        root.getChildren().clear();

        gameBoard = new Board();
        gameBoard.setUpBoard();

        createBoard(root);

        whitesTurn = new Random().nextBoolean();
        turn = new Label(ChessGame.whitesTurn ? "White's turn" : "Black's turn");
        turn.setLayoutX(550);
        turn.setLayoutY(50);
        root.getChildren().add(turn);

        createMenu(root);
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
                Rectangle boardRect = new Rectangle(x * 64, y * 64, 64, 64);
                boardRect.setFill((x + y) % 2 == 0 ? Color.BISQUE : Color.SADDLEBROWN);

                boardRectangles.getChildren().add(boardRect);

                boardRect.toBack();

                if (gameBoard.board[x][y] == null)
                    continue;

                drawPiece(root, x, y);
            }
        }

        root.getChildren().add(boardRectangles);
        boardRectangles.toBack();
    }

    private void drawPiece(Pane root, int x, int y) {
        ImageView sprite = gameBoard.board[x][y].getImageView();

        sprite.setX(x * 64);
        sprite.setY(y * 64);

        sprite.setFitHeight(64);
        sprite.setFitWidth(64);


        // Events for moving pieces
        sprite.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
            if (numberOfClicks == 0) {
                currentPiece = gameBoard.board[((int) sprite.getX()) / 64][((int) sprite.getY()) / 64];

                addPossibleMovesOverlay(root);
            }

            numberOfClicks++;

            event.consume();
        });
        // Used for graphics only
        sprite.addEventHandler(MouseEvent.MOUSE_DRAGGED, (event) -> {
            if (numberOfClicks > 0) {
                sprite.toFront();
                sprite.setX(event.getX() - 32);
                sprite.setY(event.getY() - 32);
            }

            event.consume();
        });
        sprite.addEventHandler(MouseEvent.MOUSE_RELEASED, (event) -> {
            if (numberOfClicks == 1) {
                if (currentPiece.move(((int) event.getX()) / 64, ((int) event.getY()) / 64)) {
                    // Valid move

                    EndState es = gameBoard.isOver(currentPiece.IS_WHITE);
                    if (es != EndState.IN_PROGRESS) {
                        // Game ended

                        reset(root);

                        String label = " Play again?";
                        switch (es) {
                            case WHITE_WIN -> label = "White won!" + label;
                            case BLACK_WIN -> label = "Black won!" + label;
                            case DRAW -> label = "Tie." + label;
                        }

                        Label endText = new Label(label);

                        endText.setLayoutX(220);
                        endText.setLayoutY(200);
                        endText.setTextFill(Color.WHITE);

                        menu.getChildren().add(endText);
                    } else if (currentPiece instanceof PiecePawn &&
                            currentPiece.yp == (currentPiece.IS_WHITE ? 0 : 7))
                        createPawnPromotionMenu(root);
                }

                currentPiece = null;

                root.getChildren().remove(possibleMoves);
            }

            numberOfClicks--;

            event.consume();
        });

        root.getChildren().add(sprite);
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
                if ((Util.coordinateToLongBinary(nx, ny) & validMoves) != 0) {
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
        Rectangle background = new Rectangle(0, 0, 512, 512);
        background.setFill(Color.color(0.1, 0.1, 0.1, 0.7));

        Button startButton = new Button("Start");
        startButton.setLayoutX(235);
        startButton.setLayoutY(240);

        menu = new Group(background, startButton);

        root.getChildren().add(menu);

        // Event to remove the menu when the start button is pressed
        startButton.addEventHandler(ActionEvent.ACTION, (event) -> root.getChildren().remove(menu));
    }

    private void createPawnPromotionMenu(Pane root) {
        // Translucent background
        Rectangle background = new Rectangle(0, 0, 512, 512);
        background.setFill(Color.color(0.1, 0.1, 0.1, 0.7));

        HBox options = new HBox();
        options.setLayoutX(200);
        options.setLayoutY(240);
        options.setBackground(Background.fill(Color.WHITE));

        int xp = currentPiece.xp;
        int yp = currentPiece.yp;
        boolean isWhite = currentPiece.IS_WHITE;
        Piece pawnPromoted = currentPiece;

        try {
            ImageView q = ChessGame.getImageView((isWhite ? "white" : "black") + "_queen.png");
            ImageView kn = ChessGame.getImageView((isWhite ? "white" : "black") + "_knight.png");

            q.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                gameBoard.kill(pawnPromoted);

                gameBoard.board[xp][yp] = new PieceQueen(xp, yp, isWhite, gameBoard);
                drawPiece(root, xp, yp);

                root.getChildren().remove(pawnPromotionMenu);
            });
            kn.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                gameBoard.kill(pawnPromoted);

                gameBoard.board[xp][yp] = new PieceKnight(xp, yp, isWhite, gameBoard);
                drawPiece(root, xp, yp);

                root.getChildren().remove(pawnPromotionMenu);
            });

            options.getChildren().add(q);
            options.getChildren().add(kn);
        } catch (Exception e) {
            System.out.println(e.getClass() + ": " + e.getMessage());
        }

        pawnPromotionMenu = new Group(background, options);
        root.getChildren().add(pawnPromotionMenu);
    }

    public static ImageView getImageView(String spriteName) throws Exception {
        for (File imageFile : ChessGame.IMAGE_FILES)
            if (imageFile.getName().equals(spriteName)) {
                FileInputStream inStream = new FileInputStream(imageFile);
                return new ImageView(new Image(inStream));
            }

        throw new IllegalArgumentException("Sprite name not in images");
    }
}
