package project.Database;

public class TextEditor {

    private final static String[] weekdays = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    public static String inquiry;

    public static String inquire(String message) {

        if(message.contains("time") && message.contains("course")) {
            inquiry += "<time>";
            String[] split = message.split(" ");
            int index = 0;
            for(String s : split) {
                if(s.equals("course"))
                    break;
                index++;
            }
            inquiry+='<'+split[index+1]+'>';
            index=0;
            for (String ss:split){
                if(ss.equals("on"))
                    break;
                index++;
            }
            inquiry+='<'+split[index+1]+'>';
        }

        if(message.contains("course")||message.contains("courses")) {
            inquiry += "<course>";
            for(String s:weekdays){
                if(message.contains(s))
                    inquiry+='<'+s+'>';
            }
        }

        if(message.contains("courses") && message.contains("date")){
            inquiry+="<course>";
            String[] split = message.split(" ");
            int index=0;
            for(String s:split){
                if(s.equals("date"))
                    break;
                index++;
            }
            inquiry+='<'+split[index+1]+'>';
        }
        inquiry = inquiry.trim();
        return inquiry;
    }
}
