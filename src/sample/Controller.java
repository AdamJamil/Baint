package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Controller
{
    @FXML Button newButton;
    @FXML ImageView imageView;

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
    }

    static void dragImage()
    {
        Main.imageInfo.croppedDisplayImage = Main.imageInfo.displayImage.getSubimage(Main.dragOffsetX, Main.dragOffsetY, Main.displayWidth, Main.displayHeight);

        // TODO: 8/16/2018 set croppedDisplayImage to UI
    }

    @FXML private void amCarPressed()
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
