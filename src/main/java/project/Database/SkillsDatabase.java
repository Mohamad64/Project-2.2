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

    protected Bson contains(String key, String keyName) {
        return match(eq(key,keyName));
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
        /*NOTE: This should output the following when the mockup database is connected
        [Document{{_id=1, course-name=Human Computer Interaction, lectures=[Document{{_id=2, course_id=1, room-id=0, start_time=2021-03-01T16:15:00Z, end-time=2021-03-01T18:15:00Z}},
         Document{{_id=4, course_id=1, room-id=0, start_time=2021-03-02T13:45:00Z, end-time=2021-03-02T15:45:00Z}}]}}]
        */

        //testing commands to get data from the skill
        ArrayList<Bson> pipeline = new ArrayList<Bson>();

        db.useSkill("calendar");

        //chaining the necessary commands to combine collections
        pipeline.add(db.joinCollections("courses", "lectures", "course_id"));
        pipeline.add(db.contains("course-name","Human Computer Interaction"));

        //output retrieved documents in JSON format
        List<Document> results = db.queryCollection("courses", pipeline);
        System.out.println(results.toString());
    }
}
