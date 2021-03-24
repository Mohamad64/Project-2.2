package project.GUI;

import project.Database.SkillDetection;
import project.Database.TextEditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ChatApp {

    private static final int port = 9090;

    public static void main(String[] args) throws IOException {

        ServerSocket listener = new ServerSocket(port);

        System.out.println("server waiting for connections...");
        Socket client = listener.accept();
        System.out.println("connected...");

        PrintWriter out = new PrintWriter(client.getOutputStream(),true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));



        try {
            while(true) {
                if(Controller.check){
                    out.println(Controller.answer);
                }
//                String request = in.readLine();
//                out.println("answer");
//            String request = in.readLine();
//            if(request.contains("name"))
//                out.println(new Date().toString());
//            else {
//                out.println("nemidonam");
//            }
            }
        }finally {
            out.close();
            in.close();
        }
    }
}

