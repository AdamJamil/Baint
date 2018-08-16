package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class Main extends Application
{
    static ImageInfo imageInfo;
    static int displayWidth = 800;
    static int displayHeight = 600;
    static int dragOffsetX = 0;
    static int dragOffsetY = 0;

    private static boolean mouseHeld = false;
    private static double mousePressedX, mousePressedY;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("aint");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        primaryStage.getIcons().add(SwingFXUtils.toFXImage(ImageIO.read(new File("res/icon.png")), null));

        primaryStage.getScene().setOnMousePressed(e ->
        {
            mouseHeld = true;
            mousePressedX = e.getX();
            mousePressedY = e.getY();
        });

        primaryStage.getScene().setOnMouseDragged(e ->
        {
            if (mouseHeld)
            {
                dragOffsetX += e.getX() - mousePressedX;
                dragOffsetY += e.getY() - mousePressedY;
                mousePressedX = e.getX();
                mousePressedY = e.getY();

                if (imageInfo == null || imageInfo.displayImage == null)
                    return;

                dragOffsetX = Math.max(0, Math.min(dragOffsetX, imageInfo.displayImage.getWidth()));
                dragOffsetY = Math.max(0, Math.min(dragOffsetY, imageInfo.displayImage.getHeight()));
            }
        });

        primaryStage.getScene().setOnMouseReleased((e) -> mouseHeld = false);
        //primaryStage.getScene().getStylesheets().add(Main.class.getResource("res/Stylesheet.css").toExternalForm());
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
