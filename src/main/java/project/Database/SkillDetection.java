package project.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


public class SkillDetection {

    public SkillDetection() {

    }

    String collectionName;

    public List<Document> parseInfo(String inquiry) {
        ArrayList<Bson> pipeline = new ArrayList<Bson>();
        SkillsDatabase db = new SkillsDatabase();
        String[] keywords = inquiry.split(" ");
        for(int i = 0; i< keywords.length; i++){
            if(keywords[i].equals("course")) {
                collectionName = "courses";
            } else if(keywords[i].equals("time")) {
                collectionName = "lectures";
            } else if(keywords[i].contains("_date_")) {
                keywords[i] = keywords[i].replaceAll("_date_", "");
                pipeline.add(db.filter("date", keywords[i]));
            } else if(keywords[i].contains("_weekday_")) {
                keywords[i] = keywords[i].replaceAll("_weekday_", "");
                keywords[i] = getDate(Integer.parseInt(keywords[i]));
                pipeline.add(db.filter("date", keywords[i]));
            }
        }
        db.useSkill("calendar");
        return db.queryCollection(collectionName, pipeline);
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
        System.out.print(test.parseInfo("course 2021-03-05_date_").toString());
    }
}
