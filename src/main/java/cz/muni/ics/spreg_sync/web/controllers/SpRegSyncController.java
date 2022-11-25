package cz.muni.ics.spreg_sync.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.muni.ics.spreg_sync.service.SpRegSyncService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpRegSyncController {

    private final SpRegSyncService spRegSyncService;

    @Autowired
    public SpRegSyncController(SpRegSyncService spRegSyncService) {
        this.spRegSyncService = spRegSyncService;
    }

    @GetMapping(value = "/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    public Document sync() throws JsonProcessingException {
        return spRegSyncService.sync();
    }
}
