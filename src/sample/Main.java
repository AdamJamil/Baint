package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;

public class Main extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("aint");
        primaryStage.getIcons().add(SwingFXUtils.toFXImage(ImageIO.read(new File("res/icon.png")), null));

        ((Controller) loader.getController()).init(primaryStage);

        //primaryStage.getScene().getStylesheets().add(Main.class.getResource("res/Stylesheet.css").toExternalForm());
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
