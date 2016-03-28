Notes:
======
Please install mongodb first as I used MongoDB to store and retrieve the tweets of various users. 
No special configuration is required in a brand new installation of the mongodb. 

The name of the db is "tweetsdb"

Two collections are created for this purpose: 
First is "userscollection" that holds the user related information
like what's the user id, who are his followers and whom he is following.  Here is the 
sample document that gets written:
{ "_id" : ObjectId("56f8594a5e02ae06f5a743b7"), "followers" : [ "user7", "user5" ], "following" : [ "user6", "user8" ], "user" : "user1" } 

Second is"tweetscollection" where the tweets are stored along with the creation date of a tweet,
the id of the user who created this tweet and the creation time.  Here is the sample document 
that gets written:
 "_id" : ObjectId("56f859d15e02ae06f95b89dc"), "tweetId" : "user2|tweet9", "user" : "user2", "text" : "tweet number9", "tweetedOn" : "Sun Mar 27 15:08:17 PDT 2016" }

The following GET request can be used to retrieve the feed:
http://localhost:8080/TweetsTest/feed?user=user1&count=10

In the above, count parameter is optional and if not included in the GET request, then a default of 
100 is assumed as per the ask.

If user parameter is missing or the user parameter contains an invalid user id, 
then a negative response is sent back.

Please note that I didn't use JPA for the db related operations and accessed mongodb
using boilerplate code.

******************************************************************************************

Instructions:
1) In the source folder, run the following files in the order mentioned:
	a) PopulateUsers.java   -- Creates user document including the fields mentioned above
	b) PopulateTweets.java  -- Introduces tweets for the users.
	
2) Just to save some time, I added only 10 tweets per user.  This can be easily tweaked 
by modifying the line 37 in PopulateTweets.java to whatever number that the reviewer want.

3) Just to make the time of the tweet creation easily noticeable, I introduced a 3 second
sleep between each tweet insertion.  This can be modified by changing the value in line 53 
of PopulateTweets.java