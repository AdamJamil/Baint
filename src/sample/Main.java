package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;

public class Main extends Application
{
    static ImageInfo imageInfo;
    static int displayWidth = 800;
    static int displayHeight = 600;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("aint");
        primaryStage.setScene(new Scene(root, 800, 706));
        primaryStage.show();
        primaryStage.setMaximized(true);

        //primaryStage.getScene().getStylesheets().add(Main.class.getResource("res/Stylesheet.css").toExternalForm());
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
