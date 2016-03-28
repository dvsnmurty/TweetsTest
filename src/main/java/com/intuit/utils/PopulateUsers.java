/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intuit.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



/**
 *
 * @author dmurty
 */
public class PopulateUsers {
    public static void main(String[] args) {
        Date now = new Date();
        System.out.println("Current date is: " + now.toString());
        
        MongoClient mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB("tweetsdb");
        DBCollection collection = db.getCollection("userscollection");
        WriteResult result = collection.remove(new BasicDBObject());
        
        int userIndex = 1;
        for (int i=1;i<=10;i++) {
            JSONObject userDocument = new JSONObject();
            String user = "user" + userIndex;
            userDocument.put("user", user);
            
            JSONArray followerList = new JSONArray();
            Random randomGenerator = new Random();
            for (int j=0;j<3;j++) {
                int followerId = randomGenerator.nextInt(10)+1;
                // Assumption here is, a user will not be a follower on himself
                while(followerId == userIndex) {
                    followerId = randomGenerator.nextInt(10)+1;
                }
                
                String follower = "user" + followerId;
                if (! followerList.contains(follower)) {
                    followerList.add(follower);
                }
            }
            userDocument.put("followers", followerList);
            
            JSONArray followingList = new JSONArray();
            for (int k=0;k<3;k++) {
                int followingId = randomGenerator.nextInt(10)+1;
                // Assumption here is, a user will not be following his own tweets
                while(followingId == userIndex) {
                    followingId = randomGenerator.nextInt(10)+1;
                }
                
                String followingUser = "user"+followingId;
                if (! followingList.contains(followingUser)) {
                    followingList.add(followingUser);
                }
            }
            userDocument.put("following", followingList);
            System.out.println("Json string is: " + userDocument.toString());
            DBObject userDBObject = (DBObject) JSON.parse(userDocument.toString());
            collection.insert(userDBObject);
            userIndex++;
            
        }
        
//        try {
//            FileWriter file = new FileWriter("/Users/dmurty/Documents/MongoData/usersCollection.js");
//            file.write(usersArray.toJSONString());
//            file.flush();
//            file.close();
//        } catch (IOException ex) {
//            Logger.getLogger(PopulateUsers.class.getName()).log(Level.SEVERE, null, ex);
//        } 
    }
}
