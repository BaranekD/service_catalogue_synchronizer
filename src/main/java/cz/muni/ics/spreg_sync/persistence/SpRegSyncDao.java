package cz.muni.ics.spreg_sync.persistence;

import org.bson.Document;

import java.util.Set;

public interface SpRegSyncDao {

    boolean create(Document facility, String collection);

    Document find(String id, String collection);

    void update(Document facility, String collection);

    void delete(Document facility, String collection);

    Set<Document> findAll(String collection);
}
