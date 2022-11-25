package cz.muni.ics.spreg_sync.persistence.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import cz.muni.ics.spreg_sync.base.connectors.MongoConnector;
import cz.muni.ics.spreg_sync.persistence.SpRegSyncDao;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class SpRegSyncDaoImpl implements SpRegSyncDao {

    private final MongoConnector mongoconnector;

    @Autowired
    public SpRegSyncDaoImpl(MongoConnector mongoconnector) {
        this.mongoconnector = mongoconnector;
    }

    @Override
    public boolean create(Document facility, String collectionName) {
        MongoCollection<Document> collection = mongoconnector.getMongoClient().getDatabase(mongoconnector.getDbName()).getCollection(collectionName);
        InsertOneResult result = collection.insertOne(facility);

        return result.getInsertedId() != null;
    }

    @Override
    public Document find(String id, String collectionName) {
        MongoCollection<Document> collection = mongoconnector.getMongoClient().getDatabase(mongoconnector.getDbName()).getCollection(collectionName);
        return collection.find(eq("_id", id)).first();
    }

    @Override
    public void update(Document facility, String collectionName) {
        MongoCollection<Document> collection = mongoconnector.getMongoClient().getDatabase(mongoconnector.getDbName()).getCollection(collectionName);

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", facility);

        collection.updateOne(Filters.eq("_id", facility.getString("_id")), updateObject);
    }

    @Override
    public void delete(Document facility, String collectionName) {
        MongoCollection<Document> collection = mongoconnector.getMongoClient().getDatabase(mongoconnector.getDbName()).getCollection(collectionName);
        collection.deleteOne(Filters.eq("_id", facility.getString("_id")));
    }

    @Override
    public Set<Document> findAll(String collectionName) {
        MongoCollection<Document> collection = mongoconnector.getMongoClient().getDatabase(mongoconnector.getDbName()).getCollection(collectionName);
        Set<Document> docs = new HashSet<>();

        FindIterable<Document> iterDoc = collection.find();
        for (Document document : iterDoc) {
            docs.add(document);
        }

        return docs;
    }
}
