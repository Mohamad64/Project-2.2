package project.Database;

import java.util.*;

import com.mongodb.QueryBuilder;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCollection;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Aggregates.*;

public class SkillsDatabase {

    private MongoClient local;
    private MongoDatabase skill;

    public SkillsDatabase(){
        local = new MongoClient("localhost", 27017);
    }

    public void useSkill(String skillName){
        skill = local.getDatabase(skillName);
    }

    protected Bson joinCollections(String leftCollection, String rightCollection, String keyName){
        return lookup(rightCollection, "_id", keyName, rightCollection);
    }

    protected Bson contains(String key, Object value) {
        return match(eq(key,value));
    }

    protected List<Document> queryCollection(String collectionName,  List<Bson> pipeline){
        MongoCollection<Document> collection = skill.getCollection(collectionName);
        return collection.aggregate(pipeline).into(new ArrayList<>());
    }

    protected MongoIterable<String> getDatabaseNames(){
        return local.listDatabaseNames();
    }

    @Override
    protected void finalize(){
        local.close();
    }

    public static void main(String[] args){
        SkillsDatabase db = new SkillsDatabase();

        //testing commands to get data from the skill
        ArrayList<Bson> pipeline = new ArrayList<Bson>();

        db.useSkill("calendar");

        //chaining the necessary commands to combine collections
        pipeline.add(db.joinCollections("courses", "lectures", "course_id"));
        pipeline.add(db.contains("course-name","Mathematical Modelling"));
        pipeline.add(db.contains("lectures.room-id",0));

        //output retrieved documents in JSON format
        List<Document> results = db.queryCollection("courses", pipeline);
        System.out.println(results.toString());
    }
}
