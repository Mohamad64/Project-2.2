package project.Database;

import java.util.*;

import com.mongodb.QueryBuilder;
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

    protected Bson joinCollections(String leftCollection, String rightCollection, String keyName){
        return Aggregates.lookup(rightCollection, "_id", keyName, rightCollection);
    }

    protected Bson filter(String key, String keyName) {
        QueryBuilder queryBuilder = QueryBuilder.start(key);
        return (Bson) queryBuilder.is(keyName);
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
}
