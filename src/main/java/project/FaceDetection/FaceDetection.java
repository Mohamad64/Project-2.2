package project.FaceDetection;


import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Camera.fxml"));
            Scene scene = new Scene(loader.load());

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
