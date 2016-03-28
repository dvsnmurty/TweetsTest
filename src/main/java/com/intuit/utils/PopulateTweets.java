/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intuit.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author dmurty
 */
public class PopulateTweets {
    public static void main(String[] args) {
        Date now = new Date();
        System.out.println("Current date is: " + now.toString());
        
        MongoClient mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB("tweetsdb");
        DBCollection collection = db.getCollection("tweetscollection");
        WriteResult result = collection.remove(new BasicDBObject());
        
        String[] users = {"user1", "user2", "user3", "user4",
                            "user5", "user6", "user7", "user8", 
                                "user9", "user10"};
        // I am not introducing enough randomness in terms of the insertion of 
        // tweets for users at a random time orders, due to lack of time.
        for (String user : users) {
            int tweetIndex = 0;
            for (int i=1;i<=10;i++) {
                BasicDBObject document = new BasicDBObject();
                // This is a way to maintain uniqueness of the tweetid value across the system
                // Ideally, this should be the "_id" value, but due to lack of time, I am skipping
                // that part.  That would help to partition the tweets across multiple shards in a 
                // large scale system.
                String tweetId = user + "|tweet" + tweetIndex;
                document.put("tweetId", tweetId);
                document.put("user", user);
                document.put("text", "tweet number" + tweetIndex);
                document.put("tweetedOn", new Date().toString());
                System.out.println("tweet number: " + tweetIndex + "   " + document.toString());
                collection.insert(document);
                tweetIndex++;
                try {
                    // Just introducing some delay between tweets to make the testing a bit easy
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PopulateTweets.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        
        BasicDBObject indexObj = new BasicDBObject();
        indexObj.put("user", 1);
        indexObj.put("tweetedOn", -1);
        collection.createIndex(indexObj);
        
        BasicDBObject tweetIdObj = new BasicDBObject();
        tweetIdObj.put("tweetId", 1);
        collection.createIndex(tweetIdObj);
    }
}
