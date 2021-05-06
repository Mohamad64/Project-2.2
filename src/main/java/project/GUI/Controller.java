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
import project.Database.SkillDetection;
import project.Database.TextEditor;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadUpdateListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Initializable {

    Pane box;
    public Stage newStage = new Stage();
    public static String answer;
    public static boolean check = false;


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
    private DateFormat df = new SimpleDateFormat("hh:mm a");


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
    public Circle circle;


    /*
    Setting fxml components
     */
    public HashMap<JFXCheckBox,String> hashMap;
    List<JFXCheckBox> checkBoxes = new ArrayList<>();
    public JFXCheckBox WhatTime;
    public JFXCheckBox When;
    public JFXCheckBox What;
    public JFXCheckBox How;
    public JFXCheckBox Why;
    public JFXCheckBox Where;
    public JFXCheckBox Do;
    public JFXCheckBox Can;
    public JFXCheckBox Is;


    //    @Override
//    public void stop() throws Exception {
//        connection.closeConnection();
//    }

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

        hashMap = new HashMap<>();

        checkBoxes.add(WhatTime);
        checkBoxes.add(When);
        checkBoxes.add(What);
        checkBoxes.add(How);
        checkBoxes.add(Where);
        checkBoxes.add(Why);
        checkBoxes.add(Do);
        checkBoxes.add(Can);

        hashMap.put(WhatTime,"might be in the afternoon, I'm not sure though");
        hashMap.put(When,"I'm not sure when is ");
        hashMap.put(What,"I don't know that. Google might have an idea");
        hashMap.put(How, Is.isSelected()?  "is great" : "I don't know how, I am not google :(");
        hashMap.put(Why,"It's good question, you seem like a curious person.");
        hashMap.put(Where,Is.isSelected()? "could be anywhere": "hmm...\n" + "I don't know where you can");
        hashMap.put(Do,"NO I DO NOT");
        hashMap.put(Can,"yes"+"subject" + "could");

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
                Client.setName("Client");
            String message = isServer ? "Server: " : Client.getName()+": ";
            message += txtField.getText();
            String response = TextEditor.inquire(txtField.getText());
            txtField.clear();

            txtArea.appendText(message + "\n\n");
            String strDate = df.format(new Date().getTime());
            txtArea.appendText("\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+
                    "\t"+strDate);
            txtArea.appendText("\n");
            check = true;

            try {
                txtArea.appendText("Server: " + response + "\n\n");
                txtArea.appendText("\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+
                        "\t"+strDate);
                txtArea.appendText("\n");
            } catch (Exception e) {
                System.out.println(e);
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

    public void setCheck(boolean check) {
        this.check = check;
    }
}
