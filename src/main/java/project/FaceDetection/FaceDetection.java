package project.FaceDetection;


import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;


public class FaceDetection extends Application
{
    public static Stage stage;

    @Override
    public void start(Stage primaryStage)
    {
        stage = primaryStage;
        try
        {
            // load the FXML resource
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Camera.fxml"));
//            BorderPane root = (BorderPane) loader.load();
            // set a whitesmoke background
//            root.setStyle("-fx-background-color: whitesmoke;");
            // create and style a scene
            Scene scene = new Scene(loader.load());
//            scene.getStylesheets().add(getClass().getResource("fxml/application.css").toExternalForm());
            // create the stage with the given title and the previously created
            // scene
            stage.setTitle("MaAssist");
            stage.setScene(scene);
            stage.show();

            // init the controller
            FaceDetectionController controller = loader.getController();
            controller.init();

            stage.setOnCloseRequest((new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we)
                {
                    controller.setClosed();
                }
            }));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        launch(args);
    }
}
