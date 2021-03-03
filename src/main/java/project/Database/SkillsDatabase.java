package project.Database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class SkillsDatabase {

    private MongoClient local;
    private MongoDatabase skill;

    public SkillsDatabase(){
        local = new MongoClient("localhost", 27017);
    }

    public void useSkill(String skillName){
        skill = local.getDatabase(skillName);
    }

    public MongoIterable<String> getDatabaseNames(){
        return local.listDatabaseNames();
    }

    @Override
    protected void finalize(){
        local.close();
    }
}
