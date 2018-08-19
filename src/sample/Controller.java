package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Controller
{
    @FXML BorderPane borderPane;
    @FXML Button newButton;
    Canvas canvas;
    CanvasPane canvasPane;
    Stage stage;

    private boolean mouseHeld = false;
    private double mousePressedX, mousePressedY;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    
    private int displayWidth, displayHeight;

    ImageInfo imageInfo;

    @FXML private void openButtonPressed() throws Exception
    {
        File file = new FileChooser().showOpenDialog(stage);
        loadImage(ImageIO.read(file));
    }
    
    @FXML private void newButtonPressed()
    {
        BufferedImage image = new BufferedImage(displayWidth / 2, displayHeight / 2, BufferedImage.TYPE_4BYTE_ABGR);

        for (int i = 0; i < displayWidth / 2; i++)
            for (int j = 0; j < displayHeight / 2; j++)
                image.setRGB(i, j, 0xFFFFFFFF);

        loadImage(image);
    }

    void loadImage(BufferedImage input)
    {
        imageInfo = new ImageInfo();
        imageInfo.image = input;

        imageInfo.displayImage = new BufferedImage(2 * displayWidth + imageInfo.image.getWidth(),
                2 * displayHeight + imageInfo.image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

        for (int i = 0; i < imageInfo.displayImage.getWidth(); i++)
            for (int j = 0; j < imageInfo.displayImage.getHeight(); j++)
                imageInfo.displayImage.setRGB(i, j, 0x00000000);

        for (int i = -2; i < imageInfo.image.getWidth() + 1; i++)
        {
            imageInfo.displayImage.setRGB(i + displayWidth, displayHeight - 2, 0xFFD575EA);
            imageInfo.displayImage.setRGB(i + displayWidth, displayHeight + 1 + imageInfo.image.getHeight(), 0xFFD575EA);
        }

        for (int i = -2; i < imageInfo.image.getHeight() + 2; i++)
        {
            imageInfo.displayImage.setRGB(displayWidth - 2, i + displayHeight, 0xFFD575EA);
            imageInfo.displayImage.setRGB(displayWidth + 1 + imageInfo.image.getWidth(), i + displayHeight, 0xFFD575EA);
        }

        //DA9BE8

        for (int i = -1; i < imageInfo.image.getWidth(); i++)
        {
            imageInfo.displayImage.setRGB(i + displayWidth, displayHeight - 1, 0xFFDA9BE8);
            imageInfo.displayImage.setRGB(i + displayWidth, displayHeight + imageInfo.image.getHeight(), 0xFFDA9BE8);
        }

        for (int i = -1; i < imageInfo.image.getHeight() + 1; i++)
        {
            imageInfo.displayImage.setRGB(displayWidth - 1, i + displayHeight, 0xFFDA9BE8);
            imageInfo.displayImage.setRGB(displayWidth + imageInfo.image.getWidth(), i + displayHeight, 0xFFDA9BE8);
        }

        dragOffsetX = -(displayWidth - imageInfo.displayImage.getWidth()) / 2;
        dragOffsetY = -(displayHeight - imageInfo.displayImage.getHeight()) / 2;

        imageInfo.displayImage.getGraphics().drawImage(imageInfo.image, displayWidth, displayHeight, null);
        imageInfo.croppedDisplayImage = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_4BYTE_ABGR);
        imageInfo.displayImage.getSubimage(dragOffsetX, dragOffsetY, displayWidth, displayHeight).copyData(imageInfo.croppedDisplayImage.getRaster());

        canvas.getGraphicsContext2D().clearRect(0, 0, displayWidth, displayHeight);
        canvas.getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(imageInfo.croppedDisplayImage, null), 0, 0);

    }

    void dragImage()
    {
        imageInfo.croppedDisplayImage.getGraphics().clearRect(0, 0, displayWidth, displayHeight);
        imageInfo.displayImage.getSubimage(dragOffsetX, dragOffsetY, displayWidth, displayHeight).copyData(imageInfo.croppedDisplayImage.getRaster());

        canvas.getGraphicsContext2D().clearRect(0, 0, displayWidth, displayHeight);
        canvas.getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(imageInfo.croppedDisplayImage, null), 0, 0);

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

            if (imageInfo == null || imageInfo.displayImage == null)
                return;

            dragOffsetX = Math.max(50, Math.min(dragOffsetX, imageInfo.displayImage.getWidth() - displayWidth - 50));
            dragOffsetY = Math.max(50, Math.min(dragOffsetY, imageInfo.displayImage.getHeight() - displayHeight - 50));

            dragImage();
        }
    }

    @FXML
    private void mouseReleased(MouseEvent e)
    {
        mouseHeld = false;
    }

    void init(Stage stage)
    {
        this.stage = stage;
        stage.show();

        canvasPane = new CanvasPane(stage.getScene().getWidth(), stage.getScene().getHeight() - 100);
        borderPane.setOnMousePressed(this::mousePressed);
        borderPane.setOnMouseDragged(this::mouseDragged);
        borderPane.setOnMouseReleased(this::mouseReleased);
        canvas = canvasPane.getCanvas();

        borderPane.setCenter(canvasPane);
        canvasPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4))));

        displayHeight = (int) stage.getScene().getHeight() - 100;
        displayWidth = (int) stage.getScene().getWidth();
    }
}