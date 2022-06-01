import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending; 
public class CRUD {

	static String uri = "mongodb+srv://andrewfy:hyrHuv-gamryx-goxdu8@cluster0.02k5z.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";

	
    // Find user
    public static boolean findUser(MongoCollection<Document> collection, String username) {
        try {
            Document doc = collection.find(eq("username", username)).first();
            if(doc == null) {
                System.out.println("User not found.");
                return false;
            }
            return true;
        } catch (MongoException me) {
            System.err.println("Unable to find due to an error: " + me); 
        }
        return false;
    }

    // Check password
    public static boolean checkPassword(MongoCollection<Document> collection, String username, String password) {
        try {
            Document doc = collection.find(eq("username", username)).first();
            if(doc==null)
            {
            	return false;
            }
            if(!doc.getString("password").equals(password)) {
                //System.out.println(doc.getString("password")); 
                return false;
            }
            //System.out.println("123");
            return true;
        } catch (MongoException me) {
            System.err.println("Unable to find due to an error: " + me);
            //System.out.println("abcd");
        }
        return false;
    }

    // Get user Document
    public static Document returnUser(MongoCollection<Document> collection, String username) {
        try {
            Document doc = collection.find(eq("username", username)).first();
            return doc;
        } catch (MongoException me) {
            System.err.println("Unable to return due to an error: " + me);
        }
        return null;
    }

    // Update user after win
    public static void updateWins(MongoCollection<Document> collection, String username) {
        try {
            int newWins = 0;
            double newPercentage = 0.0;
            Document doc = collection.find(eq("username", username)).first();
            newWins = doc.getInteger("wins") + 1;
            double numWins = newWins;
            double total = newWins + doc.getInteger("losses");
            newPercentage = 100 * (numWins / total);

            Document query = new Document().append("username", username);
            Bson updates = Updates.combine(
                    Updates.set("wins", newWins),
                    Updates.set("percentage", newPercentage));

            UpdateOptions options = new UpdateOptions().upsert(true);
            UpdateResult result = collection.updateOne(query, updates, options);

        } catch (MongoException me) {
            System.err.println("Unable to update due to an error: " + me);
        }
    }

    // Update user after loss
    public static void updateLosses(MongoCollection<Document> collection, String username) {
        try {
            int newLosses = 0;
            double newPercentage = 0.0;
            Document doc = collection.find(eq("username", username)).first();
            newLosses = doc.getInteger("losses") + 1;
            double numWins = doc.getInteger("wins");
            double total = newLosses + numWins;
            newPercentage = 100 * (numWins / total);

            Document query = new Document().append("username", username);
            Bson updates = Updates.combine(
                    Updates.set("losses", newLosses),
                    Updates.set("percentage", newPercentage));

            UpdateOptions options = new UpdateOptions().upsert(true);
            UpdateResult result = collection.updateOne(query, updates, options);

        } catch (MongoException me) {
            System.err.println("Unable to update due to an error: " + me);
        }
    }

    // Add match to match history of winner
    public static void addWin(MongoCollection<Document> collection, String username, String opponent) {
        try {
            Document doc = collection.find(eq("username", username)).first();
            Random gen = new Random();
            int matchID = gen.nextInt(Integer.MAX_VALUE);
            Document query = new Document().append("username", username);
            Document toAdd = new Document().append("matchid", matchID).append("opponent", opponent).append("won", true);
            
            Bson updates = Updates.combine(
                    Updates.addToSet("history", toAdd));

            UpdateOptions options = new UpdateOptions().upsert(false);
            UpdateResult result = collection.updateOne(query, updates, options);
        


        } catch (MongoException me) {
            System.err.println("Unable to update due to an error: " + me);
        }
    }

    // Add match to match history of loser
    public static void addLoss(MongoCollection<Document> collection, String username, String opponent) {
        try { 
            Document doc = collection.find(eq("username", username)).first();
            Random gen = new Random();
            int matchID = gen.nextInt(Integer.MAX_VALUE); 
            Document query = new Document().append("username", username);
            Document toAdd = new Document().append("matchid", matchID).append("opponent", opponent).append("won", false);
            Bson updates = Updates.combine(
                    Updates.addToSet("history", toAdd));

            UpdateOptions options = new UpdateOptions().upsert(true);
            UpdateResult result = collection.updateOne(query, updates, options);

        } catch (MongoException me) {
            System.err.println("Unable to update due to an error: " + me);
        }
    }

    // Return ArrayList of match history for user
    public static List<Document> listOfMatches(MongoCollection<Document> collection, String username) {
        try {
            List<Document> listOfMatches = new ArrayList<>();
            Document doc = collection.find(eq("username", username)).first();
            listOfMatches = (List<Document>) doc.get("history");
            // collection.find(eq("username", username)).into(listOfMatches);
            return listOfMatches;
        } catch (MongoException me) {
            System.err.println("Unable to find due to an error: " + me);
        }
        return null;
    }

    // Return ArrayList of user Documents sorted by wins
    // Access individual fields in each document of the ArrayList with ArrayList.get(i).get("username") for username, etc.
    public static List<Document> sortedWins(MongoCollection<Document> collection) {
        try {
            List<Document> sortedByWins = new ArrayList<>();
            collection.find().sort(descending("wins", "percentage")).into(sortedByWins);
            return sortedByWins;
        } catch (MongoException me) {
            System.err.println("Unable to find due to an error: " + me);
        }
        return null;
    }

    // Return ArrayList of user Documents sorted by win percentage
    // Access individual fields in each document of the ArrayList with ArrayList.get(i).get("username") for username, etc.
    public static List<Document> sortedPercentage(MongoCollection<Document> collection) {
        try {
            List<Document> sortedByPercentage = new ArrayList<>();
            collection.find().sort(descending("percentage", "wins")).into(sortedByPercentage);
            return sortedByPercentage;
        } catch (MongoException me) {
            System.err.println("Unable to find due to an error: " + me);
        }
        return null;
    } 

    public static void addUser(MongoCollection<Document> collection, String username, String password) {
        try {
            InsertOneResult result = collection.insertOne(new Document()
                    .append("_id", new ObjectId())
                    .append("username", username)
                    .append("password", password)
                    .append("wins", 0)
                    .append("losses", 0)
                    .append("percentage", 0.0)
                    .append("history", Arrays.asList()));
            //System.out.println(password);
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " + me);
        }
    } 

    public static MongoCollection<Document> collection;  
    static MongoClient mongoClient; 
    
    public static void startDatabase() 
    {
    	try {
    		mongoClient = MongoClients.create(uri);
             MongoDatabase database = mongoClient.getDatabase("csci201_final"); 
             collection = database.getCollection("users");
    	}
    	catch(Exception e)
    	{
    		
    	}
    } 
}