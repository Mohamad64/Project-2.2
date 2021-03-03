package project.Database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoIterable;

public class SkillsDatabase {

    private MongoClient local;

    public SkillsDatabase(){
        local = new MongoClient("localhost", 27017);
    }

    public MongoIterable<String> getDatabaseNames(){
        return local.listDatabaseNames();
    }

    @Override
    protected void finalize(){
        local.close();
    }
}
