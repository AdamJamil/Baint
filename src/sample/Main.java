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
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("aint");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        primaryStage.getIcons().add(SwingFXUtils.toFXImage(ImageIO.read(new File("res/icon.png")), null));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
