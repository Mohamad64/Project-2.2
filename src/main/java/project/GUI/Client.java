package project.GUI;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.Serializable;
import java.util.function.Consumer;

public class Client extends Connections {

    private String ip;
    private int port;
    public static ImageView image;
    public static String Name;
    public static String email="";

    public Client(String ip, int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.ip = ip;
        this.port = port;
        Name = "";
    }

    @Override
    protected boolean isServer() {
        return false;
    }

    @Override
    protected String getIP() {
        return ip;
    }

    @Override
    protected int getPort() {
        return port;
    }

    public static void setName(String name){
        Name = name;
    }

    public static String getName() {
        return Name;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Client.email = email;
    }

    public static void setImage(Image im){
        Client.image.setImage(im);
    }
}
