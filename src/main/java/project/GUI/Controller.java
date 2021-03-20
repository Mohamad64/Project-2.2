package project.GUI;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.Database.TextEditor;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadUpdateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    Pane box;
    private final static String[] weekdays = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    public static String inquiry;
    public Stage newStage = new Stage();


    /*
        Primary fxml components
     */
    public TextArea txtArea;
    public TextField txtField;
    public JFXHamburger ham;
    public JFXDrawer drawer;
    public ImageView image;
    public Label label;
    public Label label1;
    public Button update;
    private boolean isServer = false;
    private Connections connection = isServer ? createServer() : createClient();
    private DateFormat df = new SimpleDateFormat("hh:mm");


    /*
     Profile fxml components
     */
    public JFXButton edit;
    public Button backButton;
    public JFXTextField username;
    public JFXTextField email;
    public JFXButton confirm;
    public ImageView profile;
    public FileChooser fileChooser;
    public File file;
    public static Image im;
    public Circle circle;


    /*
    Setting fxml components
     */
//    public static JFXColorPicker colorSel;



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
//        colorSel.setValue(Color.web(" #E9B637"));

        try {
            connection.startConnection();
            URL url2 = getClass().getClassLoader().getResource("/fxml/Drawer.fxml");
            URL url1 = Controller.class.getResource("/fxml/Drawer.fxml");
            box = FXMLLoader.load(url1);
//            box.setBackground(new Background(new BackgroundFill(Paint.valueOf(colorSel.toString()), CornerRadii.EMPTY, Insets.EMPTY)));
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
            TextEditor.inquire(message);
            txtField.clear();

            txtArea.appendText(message + "\n");
            String strDate = df.format(new Date().getTime());
            txtArea.appendText("\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+
                    "\t"+strDate);
            txtArea.appendText("\n");


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
            circle.setFill(new ImagePattern(im));

        }catch (Exception e){
        }
    }

    public void updateInfo(ActionEvent actionEvent) {
        if(!Client.getEmail().equals(""))
            label.setText(Client.getEmail());
        if(!Client.getName().equals(""))
            label1.setText(Client.getName());
        image.setImage(im);
    }

    public void getBackAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/fxml/"+"primary" + ".fxml"));
        Scene mainViewScene = new Scene(fxmlLoader.load());
        newStage.setScene(mainViewScene);
        MainApp.stage.hide();
        newStage.showAndWait();
    }

//    public void changeColor(ActionEvent actionEvent) {
//        Color selectedColor = colorSel.getValue();
//        box.setBackground(new Background(new BackgroundFill(Paint.valueOf(colorSel.toString()), CornerRadii.EMPTY, Insets.EMPTY)));
//
//    }
}
