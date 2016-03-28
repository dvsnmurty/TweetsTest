/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intuit.tweetstest;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.client.FindIterable;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author dmurty
 */
@Controller
public class TweetsController {
    
    @RequestMapping(value = "/feed", method = RequestMethod.GET)
    protected String fetchFeeds(HttpServletRequest request, HttpServletResponse response) 
            throws ParseException, ServletException, IOException, Exception {
        
        JSONObject result = new JSONObject();
        
        String user = request.getParameter("user");
        if (user != null && ! user.isEmpty()) {
            String countStr = request.getParameter("count");
            int count;
            // If count parameter is not sent in request, set it to 100
            if (countStr != null && ! countStr.isEmpty()) {
                count = Integer.parseInt(countStr);
            } else {
                count = 100;
            }
            
            // The access to the MongoDB should itself be a part of a separate 
            // package with all the DB APIs wrapped around and published to the users.
            // Not doing it here to save some time.
            MongoClient mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("tweetsdb");
            DBCollection usersCollection = db.getCollection("userscollection");
            
            // Not checking if the user is a valid user or not here.  That's a 
            // separate code flow on itself.
            DBObject query = new BasicDBObject("user", user);
            
            // Here I am retrieving the list of users the current user is following
            DBObject userDocument = usersCollection.findOne(query);
            if (userDocument == null) {
                result.put(user, "No such user");
                result.put("isSuccess", false);
                sendResponse(response, result);
                return null;
            }
            JSONObject json = (JSONObject) new JSONParser().parse(userDocument.toString());
            JSONArray following = (JSONArray) json.get("following");
            // A sample of the following array looks like ["user4", "user3"]
            List<String> followingList = getFollowingList(following);
            
            // Once the following list is retrieved, the tweets of those users are
            // read from the db.
            JSONArray tweetsArray = retrieveTweetsFromDB(db, followingList, count);
                    
            result.put("tweets", tweetsArray);
            result.put("isSuccess", true);            
        } else {
            System.out.println("Missing user parameter in the request.  Returning error");
            result.put("Missing parameter", "user");
            result.put("isSuccess", false);
        }
        sendResponse(response, result);
        return null;
    }

    protected JSONArray retrieveTweetsFromDB(DB db, List<String> followingList, 
            int count) throws ParseException {
        JSONArray tweetsArray = new JSONArray();
        DBCollection tweetsCollection = db.getCollection("tweetscollection");
        DBObject tweetsQuery = new BasicDBObject("user", new BasicDBObject("$in", followingList));
        DBObject excludeId = new BasicDBObject("_id", 0);
        DBCursor cursor = tweetsCollection.find(tweetsQuery, excludeId).
                sort(new BasicDBObject("tweetedOn", -1)).limit(count);
        for (DBObject tweet : cursor) {
            JSONObject tweetJson = (JSONObject) new JSONParser().parse(tweet.toString());
            tweetsArray.add(tweetJson);
        }
        return tweetsArray;
    }

    protected void sendResponse(HttpServletResponse response, JSONObject result) throws IOException {
        response.setCharacterEncoding("UTF-8");
        String resultStr = result.toString();
        response.setContentType("application/json");
        response.getWriter().print(resultStr);
    }
    
    public List<String> getFollowingList(JSONArray jsonArray) {
        List<String> followingList = null;
        int length = jsonArray.size();
        if(jsonArray != null){
            followingList = new ArrayList<>();
            for(int i=0;i<length;i++){
                followingList.add((String) jsonArray.get(i));
            }
        }   
        return followingList;
    }
}
