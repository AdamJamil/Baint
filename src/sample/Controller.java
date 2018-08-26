package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

public class Controller
{
    @FXML BorderPane borderPane;
    @FXML Button newButton;
    @FXML Text mouseInfo;
    Canvas canvas;
    CustomJPanel customJPanel;
    Stage stage;
    FileChooser fileChooser;

    private boolean rectangleSelect = false;
    private int rectangleSelectX = -1, rectangleSelectY = -1;
    private boolean mouseHeld = false;
    int dragOffsetX = 0, dragOffsetY = 0;
    int displayWidth, displayHeight;
    private int imageCenterX, imageCenterY;
    private double mouseX, mouseY;
    private double mousePressedX, mousePressedY;
    private File file;

    ImageInfo info;

    FlipRotateResize mule = new FlipRotateResize();
    DrawPipeline pipeline;

    @FXML private void save() throws Exception
    {
        if (file == null)
        {
            saveAs();
            return;
        }

        ImageIO.write(info.image, getFileExtension(file), file);
    }

    @FXML private void saveAs() throws Exception
    {
        file = fileChooser.showSaveDialog(stage);
        save();
    }

    @FXML private void resizeButtonPressed()
    {
        ResizeResult result = (new ResizeDialog(info.image.getWidth(), info.image.getHeight())).showAndWait().get();
        if (result.error.isEmpty())
            info.image = FlipRotateResize.applyZoom(info.image, result.newX / info.image.getWidth(),
                    result.newY / info.image.getHeight(), AffineTransformOp.TYPE_BICUBIC);
        // TODO: 8/19/2018 error handling

        dragOffsetX = imageCenterX - ((int) (info.zoom * info.image.getWidth() / 2));
        dragOffsetY = imageCenterY - ((int) (info.zoom * info.image.getHeight() / 2));

        pipeline.resetDisplayImage(info);
        info.selection = null;
    }

    @FXML private void openButtonPressed() throws Exception
    {
        loadImage(ImageIO.read(fileChooser.showOpenDialog(stage)));
    }

    @FXML private void newButtonPressed()
    {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                image.setRGB(i, j, 0xFFFFFFFF);

        loadImage(image);
    }

    void loadImage(BufferedImage input)
    {
        info = new ImageInfo();
        info.image = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
        info.image.getGraphics().drawImage(input, 0, 0, null);

        dragOffsetX = (displayWidth - input.getWidth() - 4) / 2;
        dragOffsetY = (displayHeight - input.getHeight() - 4) / 2;

        imageCenterX = displayWidth / 2;
        imageCenterY = displayHeight / 2;

        pipeline.resetDisplayImage(info);
    }

    @FXML
    private void mouseScroll(ScrollEvent e)
    {
        info.zoomLevel += (e.getDeltaY() > 0) ? 1 : -1;
        info.zoomLevel = Math.max(0, Math.min(info.zoomLevel, ImageInfo.zoomLevels.length - 1));

        info.zoom = ImageInfo.zoomLevels[info.zoomLevel];

        dragOffsetX = imageCenterX - ((int) (info.zoom * info.image.getWidth() / 2));
        dragOffsetY = imageCenterY - ((int) (info.zoom * info.image.getHeight() / 2));

        pipeline.resetDisplayImage(info);
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

            if (info == null || info.displayImage == null)
                return;

            dragOffsetX = Math.max((int) (info.zoom * -info.displayImage.getWidth() + 50), Math.min(dragOffsetX, displayWidth - 50));
            dragOffsetY = Math.max((int) (info.zoom * -info.displayImage.getHeight() + 50), Math.min(dragOffsetY, displayHeight - 50));

            imageCenterX = dragOffsetX + (info.displayImage.getWidth() / 2);
            imageCenterY = dragOffsetY + (info.displayImage.getHeight() / 2);

            customJPanel.repaint();
        }
    }

    @FXML
    private void mouseReleased(MouseEvent e)
    {
        mouseHeld = false;
    }

    @FXML
    private void mouseMoved(MouseEvent e)
    {
        if (info == null)
            return;

        mouseX = e.getX() + 2;
        mouseY = e.getY() + 2;

        if (dragOffsetX + 2 <= mouseX && mouseX < dragOffsetX + info.displayImage.getWidth() - 2)
            if (dragOffsetY + 2 <= mouseY && mouseY < dragOffsetY + info.displayImage.getHeight() - 2)
            {
                int pixelX = (int) ((mouseX - dragOffsetX - 2) / info.zoom);
                int pixelY = (int) ((mouseY - dragOffsetY - 2) / info.zoom);
                mouseInfo.setText(pixelX + ", " + pixelY);

                if (e.isAltDown())
                {
                    int color = 0xFF000000 + (int) (Math.random() * 0xFFFFFF);
                    info.image.setRGB(pixelX, pixelY, color);
                    for (double i = pixelX * info.zoom; i < (pixelX + 1) * info.zoom; i++)
                        for (double j = pixelY * info.zoom; j < (pixelY + 1) * info.zoom; j++)
                            info.displayImage.setRGB((int) i + 2, (int) j + 2, color);

                    pipeline.draw();
                }

                if (rectangleSelect)
                {
                    if (info.selection == null)
                        info.selection = new PixelSelection(this);
                    else
                        info.selection.reset();

                    if (rectangleSelectX == -1)
                    {
                        rectangleSelectX = pixelX;
                        rectangleSelectY = pixelY;
                    }

                    for (int i = Math.min(pixelX, rectangleSelectX); i <= Math.max(pixelX, rectangleSelectX); i++)
                        for (int j = Math.min(pixelY, rectangleSelectY); j <= Math.max(pixelY, rectangleSelectY); j++)
                            info.selection.addPixel(i, j);
                }
            }
    }

    private void keyPressed(KeyEvent event)
    {
        if (event.isControlDown())
            rectangleSelect = true;

        if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER)
        {
            info.selection.dispose = true;
            info.selection = null;
        }
    }

    private void keyReleased(KeyEvent event)
    {
        if (!event.isControlDown())
        {
            rectangleSelect = false;
            rectangleSelectX = -1;
        }
    }

    @FXML
    private void flipV()
    {
        mule.flipV(info);
        pipeline.processDisplayImage(info);
    }

    @FXML
    private void flipH()
    {
        mule.flipH(info);
        pipeline.processDisplayImage(info);
    }

    @FXML private void rotateCW()
    {
        mule.rotateCW(info);
        pipeline.resetDisplayImage(info);
    }

    @FXML
    private void rotateCCW()
    {
        mule.rotateCCW(info);
        pipeline.resetDisplayImage(info);
    }

    void init(Stage stage)
    {
        this.stage = stage;
        stage.show();

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG image (*.png)", ".png"),
                new FileChooser.ExtensionFilter("JPG image (*.jpg)", ".jpg"),
                new FileChooser.ExtensionFilter("JPEG image (*.jpeg)", ".jpeg"),
                new FileChooser.ExtensionFilter("GIF image (*.gif)", ".gif"));

        customJPanel = new CustomJPanel(this);
        customJPanel.swingNode.setOnMousePressed(this::mousePressed);
        customJPanel.swingNode.setOnMouseDragged(this::mouseDragged);
        customJPanel.swingNode.setOnMouseReleased(this::mouseReleased);
        customJPanel.swingNode.setOnScroll(this::mouseScroll);
        customJPanel.swingNode.setOnMouseMoved(this::mouseMoved);
        stage.getScene().setOnKeyPressed(this::keyPressed);
        stage.getScene().setOnKeyReleased(this::keyReleased);

        stage.setOnCloseRequest((e) ->
        {
            stage.getOnCloseRequest();
            if (info != null && info.selection != null)
                info.selection.dispose = true;
        });

        canvas = customJPanel.getCanvas();

        borderPane.setCenter(customJPanel.swingNode);

        displayHeight = (int) stage.getScene().getHeight() - 100;
        displayWidth = (int) stage.getScene().getWidth();

        canvas.setPreferredSize(new Dimension(displayWidth, displayHeight));

        pipeline = new DrawPipeline(customJPanel);
    }

    private String getFileExtension(File file)
    {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");

        if (lastIndexOf == -1)
            return "";

        return name.substring(lastIndexOf + 1);
    }
}