package project.GUI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadUpdateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    /*
        Primary fxml components
     */
    public TextArea txtArea;
    public TextField txtField;
    public JFXHamburger ham;
    public JFXDrawer drawer;
    public ImageView image;
    public Label label;
    public Button update;
    private boolean isServer = false;
    private Connections connection = isServer ? createServer() : createClient();


    /*
     Profile fxml components
     */
    public JFXButton edit;
    public JFXTextField username;
    public JFXTextField email;
    public JFXButton confirm;
    public ImageView profile;
    public FileChooser fileChooser;
    public File file;
    public static Image im;


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
            URL url2 = getClass().getClassLoader().getResource("/fxml/Drawer.fxml");
            URL url1 = Controller.class.getResource("/fxml/Drawer.fxml");
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
            if(Client.getName().equals(""))
                Client.setName("Client: ");
            String message = isServer ? "Server: " : Client.getName()+": ";
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

    public void confirmAction(ActionEvent actionEvent) {
        Client.setName(username.getText());
        Client.setEmail(email.getText());
    }

    public void editProfilePic(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open your image");
        file = fileChooser.showOpenDialog(stage);

        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            im = SwingFXUtils.toFXImage(bufferedImage,null);
            profile.setImage(im);

        }catch (Exception e){
        }
    }

    public void updateInfo(ActionEvent actionEvent) {
        if(!Client.getEmail().equals(""))
            label.setText(Client.getEmail());
        image.setImage(im);
    }
}
