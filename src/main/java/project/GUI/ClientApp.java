package project.GUI;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientApp {

    public static final String server_IP = "127.0.0.1";
    private static final int port = 9090;

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket(server_IP,port);

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

        while(true) {
            System.out.println("> ");
            String command = keyboard.readLine();

            if(command.equals("quit"))
                break;

            out.println(command);

            String serverResponse = input.readLine();
            System.out.println("server says: " + serverResponse);
        }
        socket.close();
        System.exit(0);
    }
}
