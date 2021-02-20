package project.GUI;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    public TextArea txtArea;
    public TextField txtField;
    public JFXHamburger ham;
    public JFXDrawer drawer;
    private boolean isServer = false;
    private Connections connection = isServer ? createServer() : createClient();

//    @Override
//    public void init() throws Exception {
//    }

    //    @Override
    public void stop() throws Exception {
        connection.closeConnection();
    }

    private Server createServer() {
        return new Server(55555, data -> {
            Platform.runLater(() -> {
                txtArea.appendText(data.toString() + "\n");
            });
        });
    }

    private Client createClient() {
        return new Client("127.0.0.1", 55555, data -> {
            Platform.runLater(() -> {
                txtArea.appendText(data.toString() + "\n");
            });
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            connection.startConnection();
            URL url2 = getClass().getClassLoader().getResource("/fxml/DrawerPane.fxml");
            URL url1 = Controller.class.getResource("/fxml/DrawerPane.fxml");
            Pane box = FXMLLoader.load(url1);
            drawer.setSidePane(box);
            HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(ham);
            transition.setRate(-1);
            ham.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
                transition.setRate(transition.getRate() * -1);
                transition.play();

                if (drawer.isOpened()) {
                    drawer.close();
                } else {
                    drawer.open();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTxtFieldAction() {

        txtField.setOnAction(event -> {
            String message = isServer ? "Server: " : "Client: ";
            message += txtField.getText();
            txtField.clear();

            txtArea.appendText(message + "\n");

            try {
                connection.send(message);
            } catch (Exception e) {
                txtArea.appendText("Failed to send\n");
            }
        });
    }

    public void setHamAction() {
    }
}
