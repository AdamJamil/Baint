package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public class Controller
{
    @FXML Button newButton;
    @FXML Canvas canvas;

    @FXML private void newButtonPressed()
    {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.image = new BufferedImage(800, 600, 5);
        imageInfo.image.getGraphics().setColor(Color.WHITE);
        imageInfo.image.getGraphics().drawRect(0, 0, 800, 600);
        imageInfo.displayImage = new BufferedImage(2 * Main.displayWidth + imageInfo.image.getWidth(),
                                                   2 * Main.displayHeight + imageInfo.image.getHeight(), 5);

        Main.dragOffsetX = 0;
        Main.dragOffsetY = 0;

        imageInfo.croppedDisplayImage = imageInfo.displayImage.getSubimage(Main.dragOffsetX, Main.dragOffsetY, Main.displayWidth, Main.displayHeight);

        Main.imageInfo = imageInfo;

        // TODO: 8/16/2018 set croppedDisplayImage to UI 
      
        try
        {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            Image img = SwingFXUtils.toFXImage(ImageIO.read(new File("res/icon.png")), null);
            gc.drawImage(img,0,0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    static void dragImage()
    {
        Main.imageInfo.croppedDisplayImage = Main.imageInfo.displayImage.getSubimage(Main.dragOffsetX, Main.dragOffsetY, Main.displayWidth, Main.displayHeight);

        // TODO: 8/16/2018 set croppedDisplayImage to UI
        System.out.println("clicky");
    }

}
