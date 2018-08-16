package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class Controller
{
    @FXML Button newButton;
    @FXML Canvas canvas;

    @FXML private void newButtonPressed()
    {
        try
        {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            //Image img = ;
            gc.drawImage(SwingFXUtils.toFXImage(ImageIO.read(new File("res/icon.png")), null),0,0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("clicky");
    }

}
