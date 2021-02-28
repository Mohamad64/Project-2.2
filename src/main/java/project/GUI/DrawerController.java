package project.GUI;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.crypto.Cipher;
import java.io.IOException;
import java.net.URL;

public class DrawerController {
    public JFXButton profile;
    public JFXButton settings;
    public JFXButton bot;
    public static Stage newStage = new Stage();
    public static ImageView image;
//    public Color color = Controller.colorSel.getValue();

    public void StageChanger() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/fxml/"+"profile" + ".fxml"));
        Scene type2ViewScene = new Scene(fxmlLoader.load());
        newStage.setScene(type2ViewScene);
        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                System.exit(0);

            }
        });
    }

    public void profileAction(ActionEvent actionEvent) throws IOException {
        StageChanger();
        MainApp.stage.hide();
        newStage.showAndWait();
    }

    public void botAction(ActionEvent actionEvent) throws IOException {
        newStage.close();
        MainApp.stage.show();
    }

    public void SettingAction(ActionEvent actionEvent) throws IOException {
        StageChanger2();
        MainApp.stage.hide();
        newStage.showAndWait();
    }

    private void StageChanger2() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/fxml/"+"settings" + ".fxml"));
        Scene type3ViewScene = new Scene(fxmlLoader.load());
        newStage.setScene(type3ViewScene);
        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                System.exit(0);
            }
        });
    }
}
