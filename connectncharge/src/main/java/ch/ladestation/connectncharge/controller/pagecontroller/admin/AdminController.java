package ch.ladestation.connectncharge.controller.pagecontroller.admin;

import ch.ladestation.connectncharge.controller.ApplicationController;
import ch.ladestation.connectncharge.controller.pagecontroller.PageController;
import ch.ladestation.connectncharge.controller.pagecontroller.StageHandler;
import ch.ladestation.connectncharge.model.game.gamelogic.Game;
import ch.ladestation.connectncharge.model.text.FilePath;
import ch.ladestation.connectncharge.util.mvcbase.ControllerBase;
import ch.ladestation.connectncharge.util.mvcbase.ViewMixin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.List;

public class AdminController implements ViewMixin<Game, ControllerBase<Game>>, PageController {

    private static final String ADMIN_CODE = "123456";
    @FXML
    private TextField textField1;
    @FXML
    private TextField textField2;
    @FXML
    private TextField textField3;
    @FXML
    private TextField textField4;
    @FXML
    private TextField textField5;
    @FXML
    private TextField textField6;
    @FXML
    private GridPane keypadGridPane;
    @FXML
    private AnchorPane codeBackground;
    @FXML
    private Button deleteButton;
    @FXML
    private Button stopButton;
    @FXML
    private Label errorMessage;
    private TextField[] textFields;

    public void initialize() {
        textFields = new TextField[] {textField1, textField2, textField3, textField4, textField5, textField6};
        keypadGridPane.setVisible(false);
        errorMessage.setVisible(false);
    }

    @FXML
    private void showKeypad() {
        keypadGridPane.setVisible(true);
    }

    @FXML
    private void hideKeypad() {
        keypadGridPane.setVisible(false);
    }

    @FXML
    private void onKeyPressed(MouseEvent event) {
        Button pressedButton = (Button) event.getSource();
        String inputNumber = pressedButton.getText();

        for (TextField textField : textFields) {
            if (textField.getText().isEmpty()) {
                textField.setText(inputNumber);
                break;
            }
        }

        if (areAllTextFieldsFilled()) {
            checkCode(event);
        }
    }

    @FXML
    private void onDeleteKeyPressed() {
        for (int i = textFields.length - 1; i >= 0; i--) {
            if (!textFields[i].getText().isEmpty()) {
                textFields[i].setText("");
                break;
            }
        }
        errorMessage.setVisible(false);
    }

    private boolean areAllTextFieldsFilled() {
        for (TextField textField : textFields) {
            if (textField.getText().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void checkCode(MouseEvent event) {
        String enteredCode =
            String.join("", textField1.getText(), textField2.getText(), textField3.getText(), textField4.getText(),
                textField5.getText(), textField6.getText());

        if (enteredCode.equals(ADMIN_CODE)) {
            StageHandler.openStage(FilePath.ADMINHOMEPAGE.getFilePath());
        } else {
            errorMessage.setText("Falscher Code");
            errorMessage.setVisible(true);
            for (TextField textField : textFields) {
                textField.setText("");
            }
        }
    }


    @FXML
    private void onStopButtonClicked(ActionEvent event) {
        String fxmlPath = StageHandler.getLastFxmlPath() != null ? StageHandler.getLastFxmlPath()
            : FilePath.HOMEPAGE.getFilePath();
        StageHandler.openStage(fxmlPath);
    }

    @Override
    public void setController(ApplicationController controller) {
        init(controller);
    }

    @Override
    public void initializeParts() {

    }

    @Override
    public void layoutParts() {

    }

    @Override
    public List<String> getStylesheets() {
        return null;
    }
}
