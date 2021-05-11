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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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
    public Button SettingConfirmBtn;
    public TextField keyWord;
    public static String subject = "";
    public HashMap<Integer,String> hashMap = new HashMap<>();
    JFXCheckBox[] checkBoxes = new JFXCheckBox[9];
    static int[] responsesArray = new int[9];
    public JFXCheckBox WhatTime;
    public JFXCheckBox When;
    public JFXCheckBox What;
    public JFXCheckBox How;
    public JFXCheckBox Why;
    public JFXCheckBox Where;
    public JFXCheckBox Do;
    public JFXCheckBox Can;
    public JFXCheckBox Is;



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

        hashMap.put(0," subject might be in the afternoon, I'm not sure though");
        hashMap.put(1,"I'm not sure when is ");
        hashMap.put(2,"I don't know that. Google might have an idea");
//        hashMap.put(3, "I don't know how, I am not google :(");
        hashMap.put(4,"It's a good question, you seem like a curious person.");
//        hashMap.put(5,"hmm...\n" + "I don't know where you can");
        hashMap.put(6,"NO I DO NOT");
        hashMap.put(7,"yes "+ "subject" + " could");
        hashMap.put(8,"I don't know "+ "subject" + " might be");


        checkBoxes[0] = WhatTime;
        checkBoxes[1] = When;
        checkBoxes[2] = What;
        checkBoxes[3] = How;
        checkBoxes[4] = Why;
        checkBoxes[5] = Where;
        checkBoxes[6] = Do;
        checkBoxes[7] = Can;
        checkBoxes[8] = Is;
        Arrays.fill(responsesArray, 0);


        try {
            connection.startConnection();
            URL url1 = Controller.class.getResource("/fxml/Drawer.fxml");
            box = FXMLLoader.load(url1);
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
            String response = "";
            if(responsesArray[3]==1 && responsesArray[8]==1){
                response = subject + " is great";
                responsesArray[3]=0;responsesArray[8]=0;
            }
            else if(responsesArray[5]==1 && responsesArray[8]==1){
                response = subject + " could be anywhere";
                responsesArray[5]=0;responsesArray[8]=0;
            }
            else if(responsesArray[3]==1 && responsesArray[7]==1){
                response = "I don't know how, I am not google :(";
                responsesArray[3]=0;responsesArray[7]=0;
            }
            else if(responsesArray[5]==1 && responsesArray[7]==1){
                response = "hmm...\n" + "I don't know where you can";
                responsesArray[5]=0;responsesArray[7]=0;
            }
            else {
                for (int i = 0; i < responsesArray.length; i++) {
                    if (responsesArray[i] == 1) {
                        response = hashMap.get(i);
                        if (response.contains("subject"))
                            response = response.replace("subject", subject);
                        responsesArray[i] = 0;
                        break;
                    } else {
                        response = TextEditor.inquire(txtField.getText());
                    }
                }
            }
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

    public void SettingConfirmBtn(ActionEvent actionEvent) {
        for(int i=0;i<checkBoxes.length;i++){
            if(checkBoxes[i].isSelected()){
                responsesArray[i]=1;
            }
        }
        subject = keyWord.getText();
    }
}
