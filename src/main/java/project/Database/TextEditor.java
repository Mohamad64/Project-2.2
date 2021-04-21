package project.Database;

import project.GUI.Client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TextEditor {

    private final static String[] weekdays = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    public static String inquiry;
    public static String response;

    public static String inquire(String message) {
        message.toLowerCase(Locale.ENGLISH);
        if(message.contains("time") && message.contains("course")) {
            inquiry = "time ";
            String[] split = message.split(" ");
            int index = 0;
            for(String s : split) {
                if(s.equals("course"))
                    break;
                index++;
            }
            inquiry+= split[index+1] + "_course_ ";

        } else if(message.contains("courses") && message.contains("date")){
            inquiry ="course ";
            String[] split = message.split(" ");
            int index=0;
            for(String s:split){
                if(s.equals("date"))
                    break;
                index++;
            }
            if(isValidDate(split[index + 1])) {
                inquiry+= split[index+1]+"_date_ ";
            }
        } else if(message.contains("course") && !message.contains("date")) {
            inquiry = "course ";
            for(String s:weekdays){
                if(message.contains(s))
                    inquiry+= s+"_weekday_ ";
            }
        }
        else{
            inquiry = "null";
        }
        System.out.println(inquiry);
        if(message.equals("hi")||message.equals("hello")){
            response = "hi " + Client.getName()+ "!\n"+
            "How are you today?";
        }
        else if(inquiry.equals("null")){
            response = "Sorry I don't understand your question.";
        }
        else{
            response = SkillDetection.parseInfo(inquiry);
        }
        System.out.println(response);
        return response;
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        try {
            format.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

//    public static void main(String[] args) {
//        //Which courses do I have on the date 2021-03-26 ?
//        //Which course do I have on Monday ?
//        //What time is the course Mathematical Modeling ?
//        inquire("Which courses do I have on the date 2021-03-26 ?");
//        System.out.println(inquiry);
//    }
}
