package project.GUI;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.util.ResourceBundle;

public class DrawerController implements Initializable {
    public JFXButton profile;
    public JFXButton settings;
    public JFXButton bot;
    public static Stage newStage = new Stage();
    public static ImageView image;
    private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #FFFFFF;";
    private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;";

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        profile.setStyle(IDLE_BUTTON_STYLE);
        profile.setOnMouseEntered(e -> profile.setStyle(HOVERED_BUTTON_STYLE));
        profile.setOnMouseExited(e -> profile.setStyle(IDLE_BUTTON_STYLE));

        settings.setStyle(IDLE_BUTTON_STYLE);
        settings.setOnMouseEntered(e -> settings.setStyle(HOVERED_BUTTON_STYLE));
        settings.setOnMouseExited(e -> settings.setStyle(IDLE_BUTTON_STYLE));

        bot.setStyle(IDLE_BUTTON_STYLE);
        bot.setOnMouseEntered(e -> bot.setStyle(HOVERED_BUTTON_STYLE));
        bot.setOnMouseExited(e -> bot.setStyle(IDLE_BUTTON_STYLE));

    }

}
