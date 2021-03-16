package project.Database;

import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.MongoCollection;

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

    protected MongoIterable<Document> joinCollections(String leftCollection, String rightCollection, String keyName){
        MongoCollection<Document> collection = skill.getCollection(leftCollection);
        Bson lookup = Aggregates.lookup(rightCollection, "_id", keyName, rightCollection);
        return collection.aggregate(Arrays.asList(lookup));
    }

    protected Document queryCollection(String collectionName,  BasicDBObject query){
        MongoCollection<Document> collection = skill.getCollection(collectionName);
        return collection.find(query).first();
    }

    protected MongoIterable<String> getDatabaseNames(){
        return local.listDatabaseNames();
    }

    @Override
    protected void finalize(){
        local.close();
    }
}
