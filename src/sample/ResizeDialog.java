package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;

class ResizeDialog extends Dialog<ResizeResult>
{
    ResizeDialog(int width, int height)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resize.fxml"));
            Parent root = loader.load();
            ResizeController controller = loader.getController();
            controller.setResultConverter(this, width, height);

            getDialogPane().setContent(root);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
