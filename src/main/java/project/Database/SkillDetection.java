package project.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


public class SkillDetection {

    public SkillDetection() {

    }

    String collectionName;

    public List<Document> parseInfo(String inquiry) {
        SkillsDatabase db = new SkillsDatabase();
        db.useSkill("calendar");
        //db.load("courses");
        //db.listing("courses", "lectures", "course_id");
        String[] keywords = inquiry.split(" ");
        for(int i = 0; i< keywords.length; i++){
            if(keywords[i].equals("course")) {
                db.load("courses");
                db.listing("courses", "lectures", "course_id", "_id");
            } else if(keywords[i].equals("time")) {
                db.load("lectures");
                db.listing("lectures", "courses", "_id", "course_id");
            } else if(keywords[i].contains("_date_")) {
                keywords[i] = keywords[i].replaceAll("_date_", "");
                db.contains("lectures.start_time",keywords[i]);
            } else if(keywords[i].contains("_weekday_")) {
                keywords[i] = keywords[i].replaceAll("_weekday_", "");
                keywords[i] = getDate(Integer.parseInt(keywords[i]));
                db.contains("lectures.start_time",keywords[i]);
            } else if(keywords[i].contains("_course_")) {
                keywords[i] = keywords[i].replaceAll("_course_", "");
                System.out.println(keywords[i]);
                db.contains("courses.course_name", keywords[i]);
            }
        }
        List<Document> results = db.get();
        return results;
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

    public static void main(String[] args) {
        SkillDetection test = new SkillDetection();
        List<Document> results = test.parseInfo("time");
        for(int i = 0; i<results.size();i++){
            System.out.println(results.get(i));
        }
    }
}
