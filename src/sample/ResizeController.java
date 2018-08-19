package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class ResizeController
{
    @FXML RadioButton percentRadio, pixelRadio;
    @FXML TextField percentText, widthText, heightText;
    @FXML GridPane gridPane;
    Node doneButton;

    @FXML private void percentSelect()
    {
        pixelRadio.selectedProperty().setValue(false);
        percentRadio.focusTraversableProperty().setValue(false);
        pixelRadio.focusTraversableProperty().setValue(true);
        widthText.setDisable(true);
        heightText.setDisable(true);
        percentText.setDisable(false);
    }

    @FXML private void pixelSelect()
    {
        percentRadio.selectedProperty().setValue(false);
        pixelRadio.focusTraversableProperty().setValue(false);
        percentRadio.focusTraversableProperty().setValue(true);
        percentText.setDisable(true);
        widthText.setDisable(false);
        heightText.setDisable(false);
    }

    @FXML void initialize()
    {
        pixelRadio.selectedProperty().setValue(true);
        pixelRadio.focusTraversableProperty().setValue(false);
        percentText.setDisable(true);
        Platform.runLater(() -> widthText.requestFocus());
    }

    @FXML
    private void onKeyPressed(KeyEvent e)
    {
        if (e.getCode().equals(KeyCode.ENTER))
            ((Button) doneButton).fire();
    }

    void setResultConverter(ResizeDialog dialog, int width, int height)
    {
        ButtonType buttonType = new ButtonType("done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonType);
        doneButton = dialog.getDialogPane().lookupButton(buttonType);
        doneButton.visibleProperty().setValue(false);
        widthText.setText("" + width);
        heightText.setText("" + height);

        dialog.setResultConverter(v ->
        {
            if (v.equals(buttonType))
            {
                if (pixelRadio.selectedProperty().getValue())
                {
                    if (widthText.getText().matches(".*\\d+.*") && heightText.getText().matches(".*\\d+.*"))
                        return new ResizeResult(Double.parseDouble(widthText.getText()), Double.parseDouble(heightText.getText()));
                    else
                        return new ResizeResult("didn't receive numerical arguments");
                }
                else
                {
                    if (percentText.getText().matches(".*\\d+.*"))
                    {
                        double scaleFactor = Double.parseDouble(percentText.getText()) / 100;
                        return new ResizeResult(scaleFactor * width, scaleFactor * height);
                    }
                    else
                        return new ResizeResult("didn't receive numerical argument");
                }
            }
            return new ResizeResult("unknown error lmao");
        });
    }
}
