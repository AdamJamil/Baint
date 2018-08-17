package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import java.awt.image.BufferedImage;

public class Controller
{
    @FXML Button newButton;
    @FXML Canvas canvas;

    private static boolean mouseHeld = false;
    private static double mousePressedX, mousePressedY;
    static int dragOffsetX = 0;
    static int dragOffsetY = 0;

    @FXML private void newButtonPressed()
    {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.image = new BufferedImage(800, 600, BufferedImage.TYPE_4BYTE_ABGR);

        for (int i = 0; i < 800; i++)
            for (int j = 0; j < 600; j++)
                imageInfo.image.setRGB(i, j, 0xFFFFFFFF);

        imageInfo.displayImage = new BufferedImage(2 * Main.displayWidth + imageInfo.image.getWidth(),
                                                   2 * Main.displayHeight + imageInfo.image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

        for (int i = 0; i < imageInfo.displayImage.getWidth(); i++)
            for (int j = 0; j < imageInfo.displayImage.getHeight(); j++)
                imageInfo.displayImage.setRGB(i, j, 0x00000000);

        for (int i = -2; i < imageInfo.image.getWidth() + 1; i++)
        {
            imageInfo.displayImage.setRGB(i + Main.displayWidth, Main.displayHeight - 2, 0xFFD575EA);
            imageInfo.displayImage.setRGB(i + Main.displayWidth, Main.displayHeight + 1 + imageInfo.image.getHeight(), 0xFFD575EA);
        }

        for (int i = -2; i < imageInfo.image.getHeight() + 2; i++)
        {
            imageInfo.displayImage.setRGB(Main.displayWidth - 2, i + Main.displayHeight, 0xFFD575EA);
            imageInfo.displayImage.setRGB(Main.displayWidth + 1 + imageInfo.image.getWidth(), i + Main.displayHeight, 0xFFD575EA);
        }

        //DA9BE8

        for (int i = -1; i < imageInfo.image.getWidth(); i++)
        {
            imageInfo.displayImage.setRGB(i + Main.displayWidth, Main.displayHeight - 1, 0xFFDA9BE8);
            imageInfo.displayImage.setRGB(i + Main.displayWidth, Main.displayHeight + imageInfo.image.getHeight(), 0xFFDA9BE8);
        }

        for (int i = -1; i < imageInfo.image.getHeight() + 1; i++)
        {
            imageInfo.displayImage.setRGB(Main.displayWidth - 1, i + Main.displayHeight, 0xFFDA9BE8);
            imageInfo.displayImage.setRGB(Main.displayWidth + imageInfo.image.getWidth(), i + Main.displayHeight, 0xFFDA9BE8);
        }

        imageInfo.displayImage.getGraphics().drawImage(imageInfo.image, Main.displayWidth, Main.displayHeight, null);

        dragOffsetX = 0;
        dragOffsetY = 0;

        imageInfo.croppedDisplayImage = new BufferedImage(Main.displayWidth, Main.displayHeight, BufferedImage.TYPE_4BYTE_ABGR);

        imageInfo.displayImage.getSubimage(dragOffsetX, dragOffsetY, Main.displayWidth, Main.displayHeight).copyData(imageInfo.croppedDisplayImage.getRaster());

        Main.imageInfo = imageInfo;

        canvas.setWidth(800);
        canvas.setHeight(600);
        canvas.getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(imageInfo.croppedDisplayImage, null), 0, 0);
    }

    void dragImage()
    {
        Main.imageInfo.croppedDisplayImage.getGraphics().clearRect(0, 0, 800, 600);
        Main.imageInfo.displayImage.getSubimage(dragOffsetX, dragOffsetY, Main.displayWidth, Main.displayHeight).copyData(Main.imageInfo.croppedDisplayImage.getRaster());

        canvas.getGraphicsContext2D().clearRect(0, 0, Main.displayWidth, Main.displayHeight);
        canvas.getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(Main.imageInfo.croppedDisplayImage, null), 0, 0);

        System.out.println(dragOffsetX + ", " + dragOffsetY);
    }

    @FXML
    private void mousePressed(MouseEvent e)
    {
        mouseHeld = true;
        mousePressedX = e.getX();
        mousePressedY = e.getY();
    }

    @FXML
    private void mouseDragged(MouseEvent e)
    {
        if (mouseHeld)
        {
            dragOffsetX -= e.getX() - mousePressedX;
            dragOffsetY -= e.getY() - mousePressedY;
            mousePressedX = e.getX();
            mousePressedY = e.getY();

            if (Main.imageInfo == null || Main.imageInfo.displayImage == null)
                return;

            dragOffsetX = Math.max(50, Math.min(dragOffsetX, Main.imageInfo.displayImage.getWidth() - Main.displayWidth - 50));
            dragOffsetY = Math.max(50, Math.min(dragOffsetY, Main.imageInfo.displayImage.getHeight() - Main.displayHeight - 50));

            dragImage();
        }
    }

    @FXML
    private void mouseReleased()
    {
        mouseHeld = false;
    }
}
