package project.Database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoIterable;

public class SkillsDatabase {

    public static void main(String[] args){
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        try{
            MongoIterable<String> databaseNames = mongoClient.listDatabaseNames();
            for(String dbName : databaseNames){
                System.out.println(dbName);
            }
        }
        finally{
            mongoClient.close();
        }
    }
    
}
