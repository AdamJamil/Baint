package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.nio.Buffer;

public class Controller
{
    @FXML
    BorderPane borderPane;
    @FXML
    Button newButton;
    @FXML
    Text mouseInfo;
    Canvas canvas;
    CustomJPanel customJPanel;
    Stage stage;

    private boolean mouseHeld = false;
    private double mouseX, mouseY;
    private double mousePressedX, mousePressedY;
    int dragOffsetX = 0, dragOffsetY = 0;
    private int zoomLevel = 14;
    private double[] zoomLevels = new double[]{0.01, 0.05, 0.1, 0.25, 0.33, 0.5, 0.75, 0.8, 0.9, 1, 1.1, 1.2, 1.4, 1.8, 2.5, 3, 3.5, 4, 5, 6, 7, 8, 9};
    private int imageCenterX, imageCenterY;
    private double zoom = zoomLevels[zoomLevel];
    private BufferedImage zoomImage;

    int displayWidth, displayHeight;

    ImageInfo imageInfo;

    @FXML
    private void resizeButtonPressed()
    {
        ResizeResult result = (new ResizeDialog(imageInfo.image.getWidth(), imageInfo.image.getHeight())).showAndWait().get();
        if (result.error.isEmpty())
            imageInfo.image = applyZoom(imageInfo.image, result.newX / imageInfo.image.getWidth(),
                    result.newY / imageInfo.image.getHeight(), AffineTransformOp.TYPE_BICUBIC);
        // TODO: 8/19/2018 error handling

        dragOffsetX = imageCenterX - ((int) (zoom * imageInfo.image.getWidth() / 2));
        dragOffsetY = imageCenterY - ((int) (zoom * imageInfo.image.getHeight() / 2));

        processDisplayImage();
    }

    @FXML
    private void openButtonPressed() throws Exception
    {
        File file = new FileChooser().showOpenDialog(stage);
        loadImage(ImageIO.read(file));
    }

    @FXML
    private void newButtonPressed()
    {
        BufferedImage image = new BufferedImage(displayWidth / 2, displayHeight / 2, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < displayWidth / 2; i++)
            for (int j = 0; j < displayHeight / 2; j++)
                image.setRGB(i, j, 0xFFFFFFFF);

        loadImage(image);
    }

    void loadImage(BufferedImage input)
    {
        imageInfo = new ImageInfo();
        imageInfo.image = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
        imageInfo.image.getGraphics().drawImage(input, 0, 0, null);

        dragOffsetX = (displayWidth - input.getWidth() - 4) / 2;
        dragOffsetY = (displayHeight - input.getHeight() - 4) / 2;

        resetCenter();

        processDisplayImage();
    }

    void processDisplayImage()
    {
        if (zoomImage == null || zoomImage.getWidth() != (int) (zoom * imageInfo.image.getWidth()))
            zoomImage = new BufferedImage((int) (imageInfo.image.getWidth() * zoom), (int) (imageInfo.image.getHeight() * zoom), BufferedImage.TYPE_INT_ARGB);

        if (imageInfo.displayImage == null)
            resetDisplayImage();

        resizePixels(((DataBufferInt) imageInfo.image.getRaster().getDataBuffer()).getData(), ((DataBufferInt) imageInfo.displayImage.getRaster().getDataBuffer()).getData(),
                imageInfo.image.getWidth(), imageInfo.image.getHeight(), (int) (imageInfo.image.getWidth() * zoom), (int) (imageInfo.image.getHeight() * zoom));

        customJPanel.repaint();
    }

    BufferedImage applyZoom(BufferedImage input, double scaleFactorX, double scaleFactorY, int renderingType)
    {
        if (scaleFactorX == 1 && scaleFactorY == 1)
            return input;
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.scale(scaleFactorX, scaleFactorY);
        AffineTransformOp op = new AffineTransformOp(affineTransform, renderingType);
        return op.filter(input, null);
    }

    @FXML
    private void mouseScroll(ScrollEvent e)
    {
        zoomLevel += (e.getDeltaY() > 0) ? 1 : -1;
        zoomLevel = Math.max(0, Math.min(zoomLevel, 22));

        zoom = zoomLevels[zoomLevel];

        dragOffsetX = imageCenterX - ((int) (zoom * imageInfo.image.getWidth() / 2));
        dragOffsetY = imageCenterY - ((int) (zoom * imageInfo.image.getHeight() / 2));

        resetDisplayImage();

        processDisplayImage();
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

            dragOffsetX = Math.max((int) (zoom * -imageInfo.displayImage.getWidth() + 50), Math.min(dragOffsetX, displayWidth - 50));
            dragOffsetY = Math.max((int) (zoom * -imageInfo.displayImage.getHeight() + 50), Math.min(dragOffsetY, displayHeight - 50));

            imageCenterX = dragOffsetX + (imageInfo.displayImage.getWidth() / 2);
            imageCenterY = dragOffsetY + (imageInfo.displayImage.getHeight() / 2);

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
        if (imageInfo == null)
            return;

        mouseX = e.getX();
        mouseY = e.getY();

        if (dragOffsetX + 2 <= mouseX && mouseX < dragOffsetX + imageInfo.displayImage.getWidth() - 2)
            if (dragOffsetY + 2 <= mouseY && mouseY < dragOffsetY + imageInfo.displayImage.getHeight() - 2)
            {
                int pixelX = (int) ((mouseX - dragOffsetX - 2) / zoomLevels[zoomLevel]);
                int pixelY = (int) ((mouseY - dragOffsetY - 2) / zoomLevels[zoomLevel]);
                mouseInfo.setText(pixelX + ", " + pixelY);

                if (e.isAltDown())
                {
                    imageInfo.image.setRGB(pixelX, pixelY, 0xFF000000);
                    for (double i = pixelX * zoom; i <= (pixelX + 1) * zoom; i++)
                        for (double j = pixelY * zoom; j <= (pixelY + 1) * zoom; j++)
                            imageInfo.displayImage.setRGB((int) i, (int) j, 0xFF000000);

                    customJPanel.repaint();
                }
            }
    }

    void resizePixels(int[] in, int[] out, int w1, int h1, int w2, int h2)
    {
        int x_ratio = ((w1 << 16) / w2) + 1;
        int y_ratio = ((h1 << 16) / h2) + 1;
        int x2, y2 = 0;

        for (int i = 0; i < h2; i++)
        {
            x2 = 0;
            for (int j = 0; j < w2; j++)
            {
                out[((i + 2) * (w2 + 4)) + j + 2] = in[((y2 >> 16) * w1) + (x2 >> 16)];
                x2 += x_ratio;
            }
            y2 += y_ratio;
        }
    }

    @FXML
    private void flipV()
    {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.concatenate(AffineTransform.getScaleInstance(1, -1));
        affineTransform.concatenate(AffineTransform.getTranslateInstance(0, -imageInfo.image.getHeight()));
        AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        imageInfo.image = affineTransformOp.filter(imageInfo.image, null);

        processDisplayImage();
    }

    @FXML
    private void flipH()
    {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.concatenate(AffineTransform.getScaleInstance(-1, 1));
        affineTransform.concatenate(AffineTransform.getTranslateInstance(-imageInfo.image.getWidth(), 0));
        AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        imageInfo.image = affineTransformOp.filter(imageInfo.image, null);

        processDisplayImage();
    }

    @FXML
    private void rotateCW()
    {
        performRotation();

        resetDisplayImage();
        processDisplayImage();
    }

    private void resetDisplayImage()
    {
        imageInfo.displayImage = new BufferedImage(4 + (int) (zoom * imageInfo.image.getWidth()), 4 + (int) (zoom * imageInfo.image.getHeight()), BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = imageInfo.displayImage.getGraphics();
        graphics.setColor(new java.awt.Color(0xFFD575EA));
        graphics.drawRect(0, 0, imageInfo.displayImage.getWidth() - 1, imageInfo.displayImage.getHeight() - 1);
        graphics.setColor(new java.awt.Color(0xFFDA9BE8));
        graphics.drawRect(1, 1, imageInfo.displayImage.getWidth() - 3, imageInfo.displayImage.getHeight() - 3);
    }

    @FXML
    private void rotateCCW()
    {
        //loop unrolling for efficiency
        rotateCW();
        rotateCW();
        rotateCW();

        resetDisplayImage();
        processDisplayImage();
    }

    private void performRotation()
    {
        BufferedImage temp = new BufferedImage(imageInfo.image.getHeight(), imageInfo.image.getWidth(), BufferedImage.TYPE_INT_ARGB);

        int[] in = ((DataBufferInt) imageInfo.image.getRaster().getDataBuffer()).getData();
        int[] out = ((DataBufferInt) temp.getRaster().getDataBuffer()).getData();

        int width = imageInfo.image.getWidth();
        int height = imageInfo.image.getHeight();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                out[(i * height) + j] = in[i + (j * width)];

        imageInfo.image = temp;

        flipH();
    }

    void resetCenter()
    {
        imageCenterX = displayWidth / 2;
        imageCenterY = displayHeight / 2;
    }

    void init(Stage stage)
    {
        this.stage = stage;
        stage.show();

        customJPanel = new CustomJPanel(this);
        customJPanel.swingNode.setOnMousePressed(this::mousePressed);
        customJPanel.swingNode.setOnMouseDragged(this::mouseDragged);
        customJPanel.swingNode.setOnMouseReleased(this::mouseReleased);
        customJPanel.swingNode.setOnScroll(this::mouseScroll);
        customJPanel.swingNode.setOnMouseMoved(this::mouseMoved);

        canvas = customJPanel.getCanvas();

        borderPane.setCenter(customJPanel.swingNode);

        displayHeight = (int) stage.getScene().getHeight() - 100;
        displayWidth = (int) stage.getScene().getWidth();

        canvas.setPreferredSize(new Dimension(displayWidth, displayHeight));
    }
}