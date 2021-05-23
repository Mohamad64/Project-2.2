package project.GUI;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.CFG.CFGParser;
import project.Database.TextEditor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Initializable {


    public class Key {

        public final String x;
        public final int y;

        public Key(String x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Key))
                return false;
            Key key = (Key) o;
            return x.equals(key.x) && y == key.y;
        }

        @Override
        public String toString() {
            return "Key{" +
                    "x='" + x + '\'' +
                    ", y=" + y +
                    '}';
        }
    }

    Pane box;
    public Stage newStage = new Stage();
    public static String answer;
    public static boolean check = false;


    /*
            Primary fxml components
         */
    @FXML
    public TextArea txtArea;
    @FXML
    public TextField txtField;
    @FXML
    public JFXHamburger ham;
    @FXML
    public JFXDrawer drawer;
    @FXML
    public ImageView image;
    @FXML
    public Label label;
    @FXML
    public Label label1;
    @FXML
    public Button update;
    private boolean isServer = false;
    private Connections connection = isServer ? createServer() : createClient();
    private DateFormat df = new SimpleDateFormat("hh:mm a");


    /*
     Profile fxml components
     */
    @FXML
    public JFXButton edit;
    @FXML
    public JFXTextField username;
    @FXML
    public JFXTextField email;
    @FXML
    public JFXButton confirm;
    public ImageView profile;
    public FileChooser fileChooser;
    public File file;
    public static Image im;
    @FXML
    public Circle circle;


    /*
    Setting fxml components
     */
    @FXML
    public Button SettingConfirmBtn;
    @FXML
    public TextField keyWord;
    public static String subject = "";
//    public static String res = "";
    public static HashMap<Key,String> hashMap = new HashMap<>();
    public static JFXCheckBox[] checkBoxes = new JFXCheckBox[9];
    static int[] responsesArray = new int[9];
    @FXML
    public JFXCheckBox WhatTime;
    @FXML
    public JFXCheckBox When;
    @FXML
    public JFXCheckBox What;
    @FXML
    public JFXCheckBox How;
    @FXML
    public JFXCheckBox Why;
    @FXML
    public JFXCheckBox Where;
    @FXML
    public JFXCheckBox Do;
    @FXML
    public JFXCheckBox Can;
    @FXML
    public JFXCheckBox Is;
    @FXML
    public TextField Resp;
    @FXML
    public TextArea CFGcfg;
    //    public TextArea CFG;
    public static boolean CFGCheck = false;



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
            System.out.println(CFGCheck);
            if(!CFGCheck){
                for(int i=0;i<responsesArray.length;i++){
                    if(responsesArray[i]==1 && !subject.equals("")){
                        for(Key key : hashMap.keySet()){
                            if(key.equals(new Key(subject,i))){
                                response = hashMap.get(key);
                                break;
                            }
                        }
                        responsesArray[i]=0; subject = "";
                        break;
                    }
                    else{
                        response = TextEditor.inquire(txtField.getText());
                    }
                }
            }
            else{
                System.out.println("im here");
                response = CFGParser.getRespond(txtField.getText());
                CFGCheck = false;
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
        int position = 0;
        for(int i=0;i<checkBoxes.length;i++){
            if(checkBoxes[i].isSelected()){
                responsesArray[i]=1;
                position = i;
            }
        }
        subject = keyWord.getText();
        Key key = new Key(subject,position);
        for(Key keys:hashMap.keySet()){
            if(key.equals(keys) && !Resp.getText().equals("")){
                hashMap.replace(keys,Resp.getText());
//                res = Resp.getText();
            }
        }
        if(!Resp.getText().equals("")) {
//            res = Resp.getText();
            hashMap.put(key,Resp.getText());
        }
        if(!CFGcfg.getText().equals("")) {
            CFGParser.textField = CFGcfg.getText();
            CFGCheck = true;
            System.out.println(CFGCheck);
        }
    }
}
