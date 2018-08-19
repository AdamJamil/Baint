package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
    private int zoomLevel = 0;
    private double[] zoomLevels = new double[]{0.01, 0.05, 0.1, 0.25, 0.33, 0.5, 0.75, 0.8, 0.9, 1, 1.1, 1.2, 1.4, 1.8, 2.5, 3, 3.5, 4, 5, 6, 7, 8, 9};
    
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

        imageInfo.displayImage = new BufferedImage(4 + imageInfo.image.getWidth(), 4 + imageInfo.image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

        for (int i = 0; i < imageInfo.displayImage.getWidth(); i++)
            for (int j = 0; j < imageInfo.displayImage.getHeight(); j++)
                imageInfo.displayImage.setRGB(i, j, 0x00000000);

        for (int i = 0; i < imageInfo.displayImage.getWidth(); i++)
        {
            imageInfo.displayImage.setRGB(i, 0, 0xFFD575EA);
            imageInfo.displayImage.setRGB(i, imageInfo.displayImage.getHeight() - 1, 0xFFD575EA);
        }

        for (int i = 0; i < imageInfo.displayImage.getHeight(); i++)
        {
            imageInfo.displayImage.setRGB(0, i, 0xFFD575EA);
            imageInfo.displayImage.setRGB(imageInfo.displayImage.getWidth() - 1, i, 0xFFD575EA);
        }

        //0xFFDA9BE8

        for (int i = 1; i < imageInfo.displayImage.getWidth() - 1; i++)
        {
            imageInfo.displayImage.setRGB(i, 1, 0xFFDA9BE8);
            imageInfo.displayImage.setRGB(i, imageInfo.displayImage.getHeight() - 2, 0xFFDA9BE8);
        }

        for (int i = 1; i < imageInfo.displayImage.getHeight() - 1; i++)
        {
            imageInfo.displayImage.setRGB(1, i, 0xFFDA9BE8);
            imageInfo.displayImage.setRGB(imageInfo.displayImage.getWidth() - 2, i, 0xFFDA9BE8);
        }

        imageInfo.displayImage.getGraphics().drawImage(imageInfo.image, 2, 2, null);

        dragOffsetX = (displayWidth - imageInfo.image.getWidth()) / 2;
        dragOffsetY = (displayHeight - imageInfo.image.getHeight()) / 2;

        canvas.getGraphicsContext2D().clearRect(0, 0, displayWidth, displayHeight);
        canvas.getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(imageInfo.displayImage, null), dragOffsetX, dragOffsetY);

        imageInfo.fxImage = SwingFXUtils.toFXImage(imageInfo.displayImage, null);
    }

    void dragImage()
    {
        canvas.getGraphicsContext2D().clearRect(0, 0, displayWidth, displayHeight);
        canvas.getGraphicsContext2D().drawImage(imageInfo.fxImage, dragOffsetX, dragOffsetY);

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
            dragOffsetX += e.getX() - mousePressedX;
            dragOffsetY += e.getY() - mousePressedY;
            mousePressedX = e.getX();
            mousePressedY = e.getY();

            if (imageInfo == null || imageInfo.displayImage == null)
                return;

            dragOffsetX = Math.max(-imageInfo.image.getWidth() + 50, Math.min(dragOffsetX, displayWidth - 50));
            dragOffsetY = Math.max(-imageInfo.image.getHeight() + 50, Math.min(dragOffsetY, displayHeight - 50));

            dragImage();
        }
    }

    @FXML
    private void mouseScroll(ScrollEvent e)
    {
        zoomLevel += (e.getDeltaY() > 0) ? 1 : -1;
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
        borderPane.setOnScroll(this::mouseScroll);
        canvas = canvasPane.getCanvas();

        borderPane.setCenter(canvasPane);
        canvasPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4))));

        displayHeight = (int) stage.getScene().getHeight() - 100;
        displayWidth = (int) stage.getScene().getWidth();
    }
}