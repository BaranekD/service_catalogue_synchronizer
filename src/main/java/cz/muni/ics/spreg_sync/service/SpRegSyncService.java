package cz.muni.ics.spreg_sync.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bson.Document;

public interface SpRegSyncService {

    Document sync() throws JsonProcessingException;

}
