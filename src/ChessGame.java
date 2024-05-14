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
    public static boolean whitesTurn = new Random().nextBoolean();
    public static Label turn = new Label(ChessGame.whitesTurn ? "White's turn" : "Black's turn");
    // Tracks the piece that is currently being moved
    private static Piece currentPiece;

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

    private void createBoard(Pane root) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Rectangle rectangle = new Rectangle(x*64, y*64, 64, 64);
                rectangle.setFill((x + y) % 2 == 0 ? Color.BISQUE : Color.SADDLEBROWN);

                root.getChildren().add(rectangle);

                rectangle.toBack();

                if (Board.board[x][y] != null) {
                    //Setting the image view
                    ImageView imageView = Board.board[x][y].getImageView();

                    //Setting the position of the image
                    imageView.setX(x*64);
                    imageView.setY(y*64);

                    //setting the fit height and width of the image view
                    imageView.setFitHeight(64);
                    imageView.setFitWidth(64);

                    imageView.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
                        currentPiece = Board.board[((int)imageView.getX()) / 64][((int)imageView.getY()) / 64];
                    });
                    imageView.addEventHandler(MouseEvent.MOUSE_DRAGGED, (event) -> {
                        imageView.toFront();
                        imageView.setX(event.getX() - 32);
                        imageView.setY(event.getY() - 32);
                    });
                    imageView.addEventHandler(MouseEvent.MOUSE_RELEASED, (event) -> {
                        currentPiece.move(((int)event.getX()) / 64, ((int)event.getY()) / 64);
                        currentPiece = null;
                    });

                    root.getChildren().add(imageView);
                }
            }
        }
    }

    private void createMenu(Pane root) {
        Rectangle background = new Rectangle(0,0, 512, 512);
        background.setFill(Color.color(0.1,0.1,0.1,0.7));

        Button startButton = new Button("Start");
        startButton.setLayoutX(256);
        startButton.setLayoutY(256);

        Group menu = new Group(background, startButton);

        root.getChildren().add(menu);

        startButton.addEventHandler(ActionEvent.ACTION, (event) -> {
            root.getChildren().remove(menu);
        });
    }
}
