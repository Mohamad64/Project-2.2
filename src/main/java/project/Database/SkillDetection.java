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
import java.util.regex.Pattern;


public class SkillDetection {

    public static String parseInfo(String inquiry) {
        SkillsDatabase db = new SkillsDatabase();
        String needed = null;
        db.useSkill("calendar");
        //db.load("courses");
        //db.listing("courses", "lectures", "course_id");
        String[] keywords = inquiry.split(" ");
        for(int i = 0; i< keywords.length; i++){
            if(keywords[i].equals("course")) {
                db.load("courses");
                db.listing("lectures", "course_id", "_id");
                needed = "course_name";
            } else if(keywords[i].equals("time")) {
                db.load("lectures");
                db.listing("courses", "_id", "course_id");
                needed = "start_time";
            } else if(keywords[i].contains("_date_")) {
                keywords[i] = keywords[i].replaceAll("_date_", "");;
                System.out.println(keywords[i]);
                db.contains("lectures.start_time",keywords[i]);
            } else if(keywords[i].contains("_weekday_")) {
                keywords[i] = keywords[i].replaceAll("_weekday_", "");
                keywords[i] = getDate((keywords[i]).toLowerCase(Locale.ROOT));
                System.out.println(keywords[i]);
                db.contains("lectures.start_time",keywords[i]);
            } else if(keywords[i].contains("_course_")) {
                keywords[i] = keywords[i].replaceAll("_course_", "");
                System.out.println(keywords[i]);
                db.contains("courses.course_name", keywords[i]);
            }
        }
        List<Document> results = db.get();
        String fResult = "";
        for (Document result : results) {
            fResult += result.get(needed) + " - ";
        }
        return fResult;
    }

    public static String getDate(String weekday) {
        LocalDate needed_date = switch (weekday) {
            case "monday" -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
            case "tuesday" -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
            case "wednesday" -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));
            case "thursday" -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
            case "friday" -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
            case "saturday" -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
            case "sunday" -> LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            default -> throw new IllegalStateException("Unexpected value: " + weekday);
        };
        return needed_date.toString();
    }

//    public static void main(String[] args) {
//        String results = parseInfo("course 2021-03-26_date_");
//        System.out.println(results);
//    }
}
