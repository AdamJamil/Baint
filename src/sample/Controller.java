package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class Controller
{
    @FXML Button newButton;
    @FXML ImageView imageView;

    @FXML private void newButtonPressed()
    {
        try
        {
            imageView.setImage(SwingFXUtils.toFXImage(ImageIO.read(new File("C:/Users/adama/Pictures/memes/be emoei.jpg")), null));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("clicky");
    }
}
