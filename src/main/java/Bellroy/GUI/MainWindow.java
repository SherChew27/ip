package Bellroy.GUI;

import Bellroy.Bellroy;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Bellroy bellroy;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private Image BellroyImage = new Image(this.getClass().getResourceAsStream("/images/Duo.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().addAll(
                DialogBox.getBellroyDialog(Ui.welcomeMessage(), BellroyImage)
        );
    }

    /** Injects the Duke instance */
    public void setBellroy(Bellroy b) {
        bellroy = b;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = bellroy.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBellroyDialog(response, BellroyImage)
        );
        userInput.clear();
    }
}

