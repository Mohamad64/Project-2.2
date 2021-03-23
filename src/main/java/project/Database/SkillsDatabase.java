package project.Database;

import java.util.*;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCollection;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;

public class SkillsDatabase {

    private MongoClient local;
    private MongoDatabase skill;

    private MongoCollection<Document> collection;
    private ArrayList<Bson> commands;

    public SkillsDatabase(){
        local = new MongoClient("localhost", 27017);
        commands = new ArrayList<Bson>();
    }

    public void useSkill(String skillName){
        skill = local.getDatabase(skillName);
    }

    public void load(String collectionName){
        this.collection = skill.getCollection(collectionName);
    }

    protected void listing(String rightCollection, String rKeyName, String lKeyName){
        this.commands.add(lookup(rightCollection, lKeyName, rKeyName, rightCollection));
    }

    protected void contains(String key, String value) {
        this.commands.add(match(regex(key, value + ".*")));
    }

    protected void show(String fieldName) {
        this.commands.add(project(fields(excludeId(), include(fieldName))));
    }

    protected List<Document> get(){
        List<Document> results =  this.collection.aggregate(commands).into(new ArrayList<>());
        this.commands = new ArrayList<Bson>();
        return results;
    }

    protected MongoIterable<String> getDatabaseNames(){
        return local.listDatabaseNames();
    }

    @Override
    protected void finalize(){
        local.close();
    }

    public static String reply(String message){
        SkillsDatabase db = new SkillsDatabase();

        db.useSkill("calendar");
        
        //look at the courses
        db.load("courses");

        //chaining the necessary commands to combine collections
        //db.listing("courses", "lectures", "course_id");
        //commands.add(db.contains("course-name","Mathematical Modelling"));
        db.contains("lectures.start_time","2021-03-05T13:45:00Z");

        //optionally only show the fields you specify
        db.show("course-name");

        List<Document> results = db.get();
        return (String) results.get(0).get("course-name");
    }

    public static void main(String[] args){
        SkillsDatabase db = new SkillsDatabase();

        db.useSkill("calendar");
        
        //look at the courses
        db.load("courses");

        //chaining the necessary commands to combine collections
        //db.listing("courses", "lectures", "course_id");
        //commands.add(db.contains("course-name","Mathematical Modelling"));
        db.contains("lectures.start_time","2021-03-05");

        //optionally only show the fields you specify
        //db.show("course_name");

        //output retrieved documents in JSON format
        List<Document> results = db.get();
        for(int i = 0; i<results.size();i++){
            System.out.println(results.get(i).get("course_name").toString());
        }
    }
}
