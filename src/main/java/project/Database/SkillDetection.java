package project.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;


public class SkillDetection {

    String collectionName;

    public void parseInfo(String inquiry) {
        String[] keywords = inquiry.split(" ");
        for(int i = 0; i< keywords.length; i++){
            if(keywords[i].equals("course")) {
                collectionName = "courseName";
            } else if(keywords[i].equals("time")) {
                collectionName = "start-time";
            } else if(keywords[i].contains("_date_")) {
                keywords[i] = keywords[i].replaceAll("_date_", "");
                //BasicDBObject.parse("date: " + keywords[i]);
            } else if(keywords[i].contains("_weekday_")) {
                keywords[i] = keywords[i].replaceAll("_weekday_", "");
                keywords[i] = getDate(Integer.parseInt(keywords[i]));
                //BasicDBObject.parse("date: " + keywords[i]);
            }
        }
        //SkillsDatabase.queryCollection(collectionName,)
    }

    public String getDate(int weekday) {
        LocalDate needed_date = switch (weekday) {
            case 1 -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
            case 2 -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
            case 3 -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));
            case 4 -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
            case 5 -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
            case 6 -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
            case 7 -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            default -> throw new IllegalStateException("Unexpected value: " + weekday);
        };
        return needed_date.toString();
    }


}
